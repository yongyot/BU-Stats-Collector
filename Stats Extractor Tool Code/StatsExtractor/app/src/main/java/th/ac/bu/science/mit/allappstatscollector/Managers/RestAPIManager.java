package th.ac.bu.science.mit.allappstatscollector.Managers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface RestAPIManager {
    public static final Retrofit api = new Retrofit.Builder()
            .baseUrl("http://210.86.135.102:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
