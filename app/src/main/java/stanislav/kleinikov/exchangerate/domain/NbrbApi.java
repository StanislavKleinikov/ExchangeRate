package stanislav.kleinikov.exchangerate.domain;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbrbApi {

    String PATTERN_DATE = "mm.dd.yyyy";

    @GET("Services/XmlExRates.aspx")
    Call<List<Currency>> loadChanges(@Query("ondate") String date);

    @GET("Services/XmlExRates.aspx")
    Call<DailyExRates> loadDailyRates(@Query("ondate") String date);
}
