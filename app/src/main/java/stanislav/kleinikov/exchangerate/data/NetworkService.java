package stanislav.kleinikov.exchangerate.data;

import com.squareup.moshi.Moshi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import stanislav.kleinikov.exchangerate.util.BigDecimalAdapter;
import stanislav.kleinikov.exchangerate.util.DateAdapter;

public class NetworkService {

    private static final String BASE_URL = "https://www.nbrb.by/api/exrates/";
    private final Retrofit mRetrofit;

    private NetworkService() {
        Moshi moshi = new Moshi.Builder()
                .add(new BigDecimalAdapter())
                .add(new DateAdapter())
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static final class NetworkServiceHolder {
        private static final NetworkService INSTANCE = new NetworkService();
    }

    public static NetworkService getInstance() {
        return NetworkServiceHolder.INSTANCE;
    }

    public NbrbApi getNbrbApi() {
        return mRetrofit.create(NbrbApi.class);
    }


}
