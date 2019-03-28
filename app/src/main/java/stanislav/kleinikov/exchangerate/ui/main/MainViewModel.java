package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.domain.DailyExRates;
import stanislav.kleinikov.exchangerate.domain.NbrbApi;
import stanislav.kleinikov.exchangerate.domain.NetworkService;

public class MainViewModel extends ViewModel {

    public MainViewModel() {
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    Observable<List<DailyExRates>> updateExRateData() {
        Date today = new Date();
        today.setTime(today.getTime() - TimeUnit.DAYS.toMillis(1));

        SimpleDateFormat dateFormat = new SimpleDateFormat(NbrbApi.PATTERN_DATE, Locale.getDefault());

        return Observable.zip(NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(dateFormat.format(today))
                        .subscribeOn(Schedulers.io()),
                NetworkService.getInstance()
                        .getNbrbApi()
                        .loadDailyRates(dateFormat.format(new Date(today.getTime() + TimeUnit.DAYS.toMillis(1))))
                        .subscribeOn(Schedulers.io())
                , (dailyExRates, dailyExRates2) -> {
                    List<DailyExRates> list = new ArrayList<>();
                    Collections.sort(dailyExRates.getCurrencyList(), (o1, o2) -> o1.getId() - o2.getId());
                    list.add(dailyExRates);
                    if (dailyExRates2.getCurrencyList().size() > 0) {
                        Collections.sort(dailyExRates2.getCurrencyList(), (o1, o2) -> o1.getId() - o2.getId());
                        list.add(dailyExRates2);

                    }
                    return list;
                });
    }
}
