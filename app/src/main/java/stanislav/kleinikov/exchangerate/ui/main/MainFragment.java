package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.R;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.DailyExRates;
import stanislav.kleinikov.exchangerate.ui.settings.SettingActivity;


public class MainFragment extends Fragment {

    private Context mContext;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        if (savedInstanceState == null) {
            viewModel.updateExRateData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<List<DailyExRates>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<DailyExRates> s) {
                            Log.e(MainActivity.DEBUG_TAG, "ok");
                            recyclerView.setAdapter(new CurrencyAdapter(s));
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(MainActivity.DEBUG_TAG, "error");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
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

        private DailyExRates mFirstDailyExRate;
        private DailyExRates mSecondDailyExRate;

        private CurrencyAdapter(List<DailyExRates> list) {
            mFirstDailyExRate = list.get(0);
            mSecondDailyExRate = list.get(1);
        }


        @NonNull
        @Override
        public CurrencyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CurrencyHolder(layoutInflater, viewGroup);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull CurrencyHolder currencyHolder, int i) {
            Currency currency = mFirstDailyExRate.getCurrencyList().get(i);
            BigDecimal rate = mSecondDailyExRate.getCurrencyList().get(i).getRate();
            currencyHolder.bind(currency, rate);
        }

        @Override
        public int getItemCount() {
            return mFirstDailyExRate.getCurrencyList().size();
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

        void bind(Currency currency, BigDecimal rate) {
            mCurrency = currency;
            mCharCodeTV.setText(mCurrency.getCharCode());
            mScaleTV.setText(String.format(getString(R.string.format_scale),
                    currency.getScale(), currency.getName()));
            mFirstRateTV.setText(String.format(Locale.getDefault(), "%.4f", currency.getRate()));
            mSecondRateTV.setText(String.format(Locale.getDefault(), "%.4f", rate));
        }
    }
}
