package stanislav.kleinikov.exchangerate.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import stanislav.kleinikov.exchangerate.data.adapter.BigDecimalAdapter
import stanislav.kleinikov.exchangerate.data.adapter.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.*


object NetworkService {

    private const val BASE_URL = "https://www.nbrb.by/api/exrates/"

    private val apiDateFormatter = SimpleDateFormat("yyyy-M-d", Locale.US)

    private val moshi = Moshi.Builder()
        .add(BigDecimalAdapter())
        .add(CalendarAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val mRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    private val ratesService = mRetrofit.create(NbrbApi::class.java)

    interface NbrbApi {
        @GET("rates")
        fun loadDailyRates(
            @Query("ondate") date: String,
            @Query("periodicity") periodicity: Int
        ): Single<List<RateBean>>
    }

    fun getNbrbRates(date: Calendar): Single<List<RateBean>> {
        return ratesService.loadDailyRates(apiDateFormatter.format(date.time), 0)
    }
}