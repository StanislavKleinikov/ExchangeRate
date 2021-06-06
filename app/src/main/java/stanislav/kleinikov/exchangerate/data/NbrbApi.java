package stanislav.kleinikov.exchangerate.data;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import stanislav.kleinikov.exchangerate.domain.Currency;

public interface NbrbApi {

    @GET("rates")
    Observable<List<Currency>> loadDailyRates(@Query("ondate") String date, @Query("periodicity") int periodicity);
}
