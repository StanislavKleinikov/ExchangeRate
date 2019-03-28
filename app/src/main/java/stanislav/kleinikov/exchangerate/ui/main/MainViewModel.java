package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.util.SparseArray;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.domain.Currency;
import stanislav.kleinikov.exchangerate.domain.CurrencyBank;
import stanislav.kleinikov.exchangerate.domain.NbrbApi;
import stanislav.kleinikov.exchangerate.domain.NetworkService;

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
    Observable<List<String>> updateExRateData(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(NbrbApi.PATTERN_DATE, Locale.getDefault());

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

                    String todayDate = dailyExRates.getDate();
                    String anotherDate = dailyExRates2.getDate();

                    SparseArray<Map<String, BigDecimal>> rates = new SparseArray<>();
                    for (int i = 0; i < todayList.size(); i++) {
                        Currency todayCurrency = todayList.get(i);

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

                    Log.e(MainActivity.DEBUG_TAG, todayList.toString());
                    Log.e(MainActivity.DEBUG_TAG, anotherDayList.toString());
                    mBank.setCurrencyList(dailyExRates.getCurrencyList());
                    mBank.setRates(rates);
                    list.add(todayDate);
                    if (anotherDate != null) {
                        list.add(anotherDate);
                    }
                    return list;
                });
    }
}
