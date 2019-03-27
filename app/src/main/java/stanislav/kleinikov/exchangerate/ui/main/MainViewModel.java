package stanislav.kleinikov.exchangerate.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import stanislav.kleinikov.exchangerate.domain.DailyExRates;
import stanislav.kleinikov.exchangerate.domain.NbrbApi;
import stanislav.kleinikov.exchangerate.domain.NetworkService;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<DailyExRates>> mExRateData;

    public MainViewModel() {
        mExRateData = new MutableLiveData<>();
    }

    MutableLiveData<List<DailyExRates>> getExRateData() {
        return mExRateData;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    void updateExRateData() {
        Date today = new Date();
        today.setTime(today.getTime()-TimeUnit.DAYS.toMillis(1));

        SimpleDateFormat dateFormat = new SimpleDateFormat(NbrbApi.PATTERN_DATE, Locale.getDefault());
        Observable<List<DailyExRates>> listObservable = Observable.zip(NetworkService.getInstance()
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
                    Log.e(MainActivity.DEBUG_TAG,dailyExRates.toString());
                    if (dailyExRates2.getCurrencyList().size() > 0) {
                        Collections.sort(dailyExRates2.getCurrencyList(), (o1, o2) -> o1.getId() - o2.getId());
                        list.add(dailyExRates);
                        Log.e(MainActivity.DEBUG_TAG,dailyExRates2.toString());
                    }
                    return list;
                }).observeOn(AndroidSchedulers.mainThread());

        listObservable.subscribeWith(new Observer<List<DailyExRates>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<DailyExRates> s) {
                mExRateData.setValue(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(MainActivity.DEBUG_TAG,"error");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
