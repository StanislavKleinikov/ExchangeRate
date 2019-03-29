package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.application.App;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;
import stanislav.kleinikov.exchangerate.domain.NbrbApi;
import stanislav.kleinikov.exchangerate.domain.NetworkService;

import static stanislav.kleinikov.exchangerate.domain.NbrbApi.PATTERN_FORMAT_STRING;

public class MainViewModel extends ViewModel {

    private CurrencyBank mBank;
    private MutableLiveData<List<String>> mDates;

    public MainViewModel() {
        mBank = CurrencyBank.getInstance();
        mDates = new MutableLiveData<>();
    }

    Map<String, BigDecimal> getCurrencyRates(int currencyId) {
        return mBank.getRatesById(currencyId);
    }

    List<Currency> getCurrencyList() {
        return mBank.getCurrencyList();
    }


    MutableLiveData<List<String>> getDates() {
        return mDates;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    Observable<List<String>> updateExRateData(Context context, Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(NbrbApi.PATTERN_FORMAT_DATE, Locale.getDefault());

        return Observable.zip(NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(dateFormat.format(date))
                        .subscribeOn(Schedulers.io()),
                NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(dateFormat.format(new Date(date.getTime() + TimeUnit.DAYS.toMillis(1))))
                        .subscribeOn(Schedulers.io())
                , (dailyExRates, dailyExRates2) -> {

                    List<String> list = new ArrayList<>();
                    List<Currency> todayList = dailyExRates.getCurrencyList();
                    List<Currency> anotherDayList = dailyExRates2.getCurrencyList();

                    String[] s1 = dailyExRates.getDate().split("/");

                    String[] s2 = dailyExRates2.getDate().split("/");

                    String todayDate = String.format(PATTERN_FORMAT_STRING, s1[1], s1[0], s1[2]);
                    String anotherDate = String.format(PATTERN_FORMAT_STRING, s2[1], s2[0], s2[2]);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                    SharedPreferences.Editor edit = preferences.edit();

                    SparseArray<Map<String, BigDecimal>> rates = new SparseArray<>();
                    for (int i = 0; i < todayList.size(); i++) {
                        Currency todayCurrency = todayList.get(i);
                        if (!preferences.contains(todayCurrency.getCharCode())) {
                            edit.putBoolean(todayCurrency.getCharCode(), false);
                        }

                        BigDecimal todayRate = todayCurrency.getRate();

                        BigDecimal anotherRate = new BigDecimal(0);
                        for (Currency currency : anotherDayList) {
                            if (currency.getId() == todayCurrency.getId()) {
                                anotherRate = currency.getRate();
                            }
                        }

                        Map<String, BigDecimal> map = new HashMap<>();
                        map.put(todayDate, todayRate);
                        map.put(anotherDate, anotherRate);
                        rates.put(todayList.get(i).getId(), map);
                    }

                    edit.apply();

                    SharedPreferences orderPreferences = context.getSharedPreferences(App.PREFERENCES_CURRENCY_ORDER,
                            Context.MODE_PRIVATE);


                    Collections.sort(todayList, (o1, o2) -> orderPreferences.getInt(o2.getCharCode(), 0) -
                            orderPreferences.getInt(o1.getCharCode(), 0));


                    mBank.setCurrencyList(todayList);
                    mBank.setRates(rates);
                    list.add(todayDate);
                    list.add(anotherDate);
                    return list;
                });
    }
}
