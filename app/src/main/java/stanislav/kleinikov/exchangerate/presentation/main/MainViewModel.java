package stanislav.kleinikov.exchangerate.presentation.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.application.App;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;
import stanislav.kleinikov.exchangerate.data.NetworkService;
import stanislav.kleinikov.exchangerate.domain.DailyExRates;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final CurrencyBank mBank;
    private final MutableLiveData<List<String>> mDates;

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

    Observable<List<String>> updateExRateData(Context context, Date date) {

        SimpleDateFormat apiDateFormatter = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

        Date nextDate = new Date(date.getTime() + TimeUnit.DAYS.toMillis(1));
        Date lastDate = new Date(date.getTime() - TimeUnit.DAYS.toMillis(1));

        return Observable.zip(
                NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(apiDateFormatter.format(date), 0)
                        .map(rates -> new DailyExRates(date, rates))
                        .subscribeOn(Schedulers.io()),
                NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(apiDateFormatter.format(nextDate), 0)
                        .map(rates -> new DailyExRates(nextDate, rates))
                        .onErrorResumeNext(error -> {
                                    return NetworkService.getInstance()
                                            .getNbrbApi()
                                            .loadDailyRates(apiDateFormatter.format(lastDate), 0)
                                            .map(rates -> new DailyExRates(lastDate, rates));
                                }
                        )
                        .subscribeOn(Schedulers.io())
                , (dailyExRates, dailyExRates2) -> {

                    List<String> list = new ArrayList<>();
                    List<Currency> todayList = dailyExRates.getCurrencyList();
                    List<Currency> anotherDayList = dailyExRates2.getCurrencyList();

                    SimpleDateFormat uiDateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                    String todayDate = uiDateFormatter.format(dailyExRates.getDate());
                    String anotherDate = uiDateFormatter.format(dailyExRates2.getDate());

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

                    //noinspection ComparatorCombinators
                    Collections.sort(todayList, (o1, o2) -> orderPreferences.getInt(o1.getCharCode(), 0) -
                            orderPreferences.getInt(o2.getCharCode(), 0));

                    mBank.setCurrencyList(todayList);
                    mBank.setRates(rates);
                    list.add(todayDate);
                    list.add(anotherDate);
                    Collections.sort(list, (o1, o2) -> {
                        try {
                            return Objects.requireNonNull(uiDateFormatter.parse(o1)).compareTo(uiDateFormatter.parse(o2));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    });
                    return list;
                });
    }
}
