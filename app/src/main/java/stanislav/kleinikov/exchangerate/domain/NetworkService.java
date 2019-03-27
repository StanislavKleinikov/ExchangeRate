package stanislav.kleinikov.exchangerate.domain;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "http://www.nbrb.by/";
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(
                        SimpleXmlConverterFactory.create(
                                new Persister(new AnnotationStrategy())))
                .build();
    }

    private static class NetworkServiceHolder {
        private static final NetworkService INSTANCE = new NetworkService();
    }

    public static NetworkService getInstance() {
        return NetworkServiceHolder.INSTANCE;
    }

    public NbrbApi getNbrbApi() {
        return mRetrofit.create(NbrbApi.class);
    }


}
