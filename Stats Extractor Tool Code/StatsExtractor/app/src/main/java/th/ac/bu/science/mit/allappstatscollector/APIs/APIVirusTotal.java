package th.ac.bu.science.mit.allappstatscollector.APIs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import th.ac.bu.science.mit.allappstatscollector.Models.ModelVirusTotalReport;

public interface APIVirusTotal {
    @GET("index.php/file/report")
    Call<List<ModelVirusTotalReport>> GetReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") ArrayList<String> resourceList);


}
