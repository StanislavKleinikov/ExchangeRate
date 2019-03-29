package stanislav.kleinikov.exchangerate.domain;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbrbApi {

    String PATTERN_FORMAT_DATE = "MM.dd.yyyy";
    String PATTERN_FORMAT_STRING = "%1$s.%2$s.%3$s";

    @GET("Services/XmlExRates.aspx")
    Observable<DailyExRates> loadDailyRates(@Query("ondate") String date);
}
