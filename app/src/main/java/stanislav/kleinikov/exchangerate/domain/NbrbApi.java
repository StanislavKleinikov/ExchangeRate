package stanislav.kleinikov.exchangerate.domain;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbrbApi {

    String PATTERN_DATE = "MM.dd.yyyy";

    @GET("Services/XmlExRates.aspx")
    Observable<DailyExRates> loadDailyRates(@Query("ondate") String date);
}
