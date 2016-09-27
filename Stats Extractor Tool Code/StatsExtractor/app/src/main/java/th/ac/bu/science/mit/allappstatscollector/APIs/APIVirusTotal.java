package th.ac.bu.science.mit.allappstatscollector.APIs;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import th.ac.bu.science.mit.allappstatscollector.Models.ModelVirusTotalReport;

public interface APIVirusTotal {
    @GET("index.php/file/report")
    Call<List<ModelVirusTotalReport>> GetReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") List<String> resourceList
    );

    @Multipart
    @POST("index.php/file/report")
    Call<List<ModelVirusTotalReport>> GetAPKReport(
            @Part("apikey") RequestBody apikey,
            @Part("userfile\"; filename=\"pp.png\" ") RequestBody userfile
            );
}
