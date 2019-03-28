package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.ui.settings.SettingActivity;


public class MainFragment extends Fragment {

    private Context mContext;
    private MainViewModel mViewModel;
    private List<String> mDates;
    private SharedPreferences mPreferences;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        MutableLiveData<List<String>> datesData = mViewModel.getDates();
        datesData.observe(this, strings -> {
            if (strings != null) {
                setHasOptionsMenu(true);
                TextView firstDate = view.findViewById(R.id.first_date_tv);
                TextView secondDate = view.findViewById(R.id.second_date_tv);
                firstDate.setText(strings.get(0));
                secondDate.setText(strings.get(1));
            }
            mDates = strings;
            recyclerView.setAdapter(new CurrencyAdapter(mViewModel.getCurrencyList()));
        });


        if (savedInstanceState == null) {
            mViewModel.updateExRateData(new Date()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new CurrencyObserver());
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.settings:
                Intent intent = new Intent(mContext, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CurrencyAdapter extends RecyclerView.Adapter<CurrencyHolder> {

        private List<Currency> mCurrencyList;

        private CurrencyAdapter(List<Currency> list) {
            mCurrencyList = list;
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
        private TextView mCharCodeTV;
        private TextView mScaleTV;
        private TextView mFirstRateTV;
        private TextView mSecondRateTV;
        private Currency mCurrency;

        CurrencyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_currency, parent, false));
            mCharCodeTV = itemView.findViewById(R.id.char_code_tv);
            mScaleTV = itemView.findViewById(R.id.scale_tv);
            mFirstRateTV = itemView.findViewById(R.id.currency1_tv);
            mSecondRateTV = itemView.findViewById(R.id.currency2_tv);
        }

        void bind(Currency currency) {
            mCurrency = currency;
            if (!mPreferences.getBoolean(mCurrency.getCharCode(), false)) {
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            mCharCodeTV.setText(mCurrency.getCharCode());
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
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(List<String> list) {
            Log.e(MainActivity.DEBUG_TAG, list.toString());
            if (list.size() > 0 && list.size() < 2) {
                Date date = new Date();
                date.setTime(date.getTime() - TimeUnit.DAYS.toMillis(1));
                mViewModel.updateExRateData(date).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new CurrencyObserver());
            } else {
                mViewModel.getDates().setValue(list);
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Log.e(MainActivity.DEBUG_TAG, "error");
        }

        @Override
        public void onComplete() {

        }
    }
}
