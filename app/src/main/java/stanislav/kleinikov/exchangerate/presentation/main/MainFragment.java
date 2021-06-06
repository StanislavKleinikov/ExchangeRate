package stanislav.kleinikov.exchangerate.presentation.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.presentation.settings.SettingActivity;


public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MainViewModel mViewModel;
    private List<String> mDates;
    private SharedPreferences mPreferences;

    ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    mRecyclerView.setAdapter(new CurrencyAdapter(mViewModel.getCurrencyList()));
                }
            });

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mRecyclerView = view.findViewById(R.id.main_recycler_view);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorPrimaryLight);


        MutableLiveData<List<String>> datesData = mViewModel.getDates();
        datesData.observe(getViewLifecycleOwner(), strings -> {
            if (strings != null) {
                setHasOptionsMenu(true);
                TextView firstDate = view.findViewById(R.id.first_date_tv);
                TextView secondDate = view.findViewById(R.id.second_date_tv);
                firstDate.setText(strings.get(0));
                secondDate.setText(strings.get(1));
            }
            mDates = strings;
            mRecyclerView.setAdapter(new CurrencyAdapter(mViewModel.getCurrencyList()));
        });

        if (savedInstanceState == null) {
            mRecyclerView.setAdapter(new CurrencyAdapter(new ArrayList<>()));
            mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                mViewModel.updateExRateData(getContext(), new Date())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new CurrencyObserver());
            });
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settings) {
            settingsLauncher.launch(new Intent(getContext(), SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    @Override
    public void onRefresh() {
        mViewModel.updateExRateData(getContext(), new Date()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CurrencyObserver());
    }

    private class CurrencyAdapter extends RecyclerView.Adapter<CurrencyHolder> {

        private final List<Currency> mCurrencyList = new ArrayList<>();

        private CurrencyAdapter(List<Currency> list) {
            for (Currency currency : list) {
                if (mPreferences.getBoolean(currency.getCharCode(), false)) {
                    mCurrencyList.add(currency);
                }
            }
        }

        @NonNull
        @Override
        public CurrencyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CurrencyHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull CurrencyHolder currencyHolder, int i) {
            Currency currency = mCurrencyList.get(i);
            currencyHolder.bind(currency);
        }

        @Override
        public int getItemCount() {
            return mCurrencyList.size();
        }
    }

    private class CurrencyHolder extends RecyclerView.ViewHolder {
        private final TextView mCharCodeTV;
        private final TextView mScaleTV;
        private final TextView mFirstRateTV;
        private final TextView mSecondRateTV;

        CurrencyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_currency, parent, false));
            mCharCodeTV = itemView.findViewById(R.id.char_code_tv);
            mScaleTV = itemView.findViewById(R.id.scale_tv);
            mFirstRateTV = itemView.findViewById(R.id.currency1_tv);
            mSecondRateTV = itemView.findViewById(R.id.currency2_tv);
        }

        void bind(Currency currency) {
            mCharCodeTV.setText(currency.getCharCode());
            mScaleTV.setText(String.format(getString(R.string.format_scale),
                    currency.getScale(), currency.getName()));
            Map<String, BigDecimal> currencyRates = mViewModel.getCurrencyRates(currency.getId());

            BigDecimal rate1 = currencyRates.get(mDates.get(0));
            BigDecimal rate2 = currencyRates.get(mDates.get(1));
            mFirstRateTV.setText(String.format(Locale.getDefault(), "%.4f", rate1));
            mSecondRateTV.setText(String.format(Locale.getDefault(), "%.4f", rate2));

        }
    }

    private class CurrencyObserver implements Observer<List<String>> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            // ignored
        }

        @SuppressLint("CheckResult")
        @Override
        public void onNext(@NonNull List<String> list) {
            mViewModel.getDates().setValue(list);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Toast.makeText(getContext(), getText(R.string.load_failed), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onComplete() {
            // ignored
        }
    }
}
