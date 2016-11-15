package th.ac.bu.science.mit.allappstatscollector.Interfaces;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface RestInterface {
    public static final Retrofit api = new Retrofit.Builder()
            .baseUrl("http://mobile-monitoring.bu.ac.th/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
