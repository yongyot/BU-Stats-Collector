package th.ac.bu.science.mit.allappstatscollector.Managers;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.science.mit.allappstatscollector.APIs.APIVirusTotal;
import th.ac.bu.science.mit.allappstatscollector.HashGen;
import th.ac.bu.science.mit.allappstatscollector.Interfaces.RestInterface;
import th.ac.bu.science.mit.allappstatscollector.Models.AppsInfo;
import th.ac.bu.science.mit.allappstatscollector.Models.ModelVirusTotalReport;

public class VirusTotalManager {

    public interface ResponseListener {
        public void OnSuccess ();
        public void OnFailed (String reason);
    }

    ResponseListener responseListener;
    final String API_KEY = "78660282aeaedccc679bb9b2e33095916ff8d356be6e77d05ef04a284c42deff";

    public VirusTotalManager () {
        this.responseListener = null;
    }

    public void GetAllAppReport (Context context) {
        PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<AppsInfo> appList = new ArrayList<>();
        List<String> hashList = new ArrayList<>();

        for (ApplicationInfo appInfo : apps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppsInfo app = HashGen.getPackageInfo(appInfo.packageName, context);
                appList.add(app);
                hashList.add(app.hash);

//                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
//                calendar.setTimeInMillis(app.lastUpdate);
//                String date=  formatter.format(calendar.getTime());
//                data=data+app.packageName+"*"+date+"*"+app.hash+"*"+app.versionName+"*"+app.versionCode+"\n";
            }
        }

        Log.w ("myInfo", "App count " + appList.size());
        SendHashList(hashList);
    }

    void SendHashList (List<String> resourceList) {
        APIVirusTotal virusTotalAPI = RestInterface.api.create(APIVirusTotal.class);
        final Call<List<ModelVirusTotalReport>> call = virusTotalAPI.GetReport(API_KEY,
                resourceList);

        call.enqueue(new Callback<List<ModelVirusTotalReport>>() {
            @Override
            public void onResponse(Call<List<ModelVirusTotalReport>> call, Response<List<ModelVirusTotalReport>> response) {
                String toSet = response.body().size() + " All Response";

                for (int index=0; index < response.body().size(); index++) {
                    ModelVirusTotalReport report = response.body().get(index);
                    toSet += "\n"+ index + " : " + report.toString();
                }
                Log.w("myInfo", toSet);
//                responseListener.OnSuccess();
            }

            @Override
            public void onFailure(Call<List<ModelVirusTotalReport>> call, Throwable t) {
                Log.w("myInfo", "Something went wrong: " + t.getMessage());
//                responseListener.OnFailed(t.getMessage());
            }
        });
    }

    public void SendAPI (ResponseListener callback) {
        this.responseListener = callback;

        ArrayList<String> resourceList = new ArrayList<String> ();
        resourceList.add("YOOOO");
        resourceList.add("c7ede8a9100c0bf480ab764fef4961d7");

        APIVirusTotal virusTotalAPI = RestInterface.api.create(APIVirusTotal.class);
        final Call<List<ModelVirusTotalReport>> call = virusTotalAPI.GetReport(API_KEY,
                resourceList);

        call.enqueue(new Callback<List<ModelVirusTotalReport>>() {
            @Override
            public void onResponse(Call<List<ModelVirusTotalReport>> call, Response<List<ModelVirusTotalReport>> response) {
                String toSet = response.body().size() + " responses";

                for (int index=0; index < response.body().size(); index++) {
                    ModelVirusTotalReport report = response.body().get(index);
                    toSet += "\n"+ index + " : " + report.toString();
                }
                Log.w("myInfo", toSet);
                responseListener.OnSuccess();
            }

            @Override
            public void onFailure(Call<List<ModelVirusTotalReport>> call, Throwable t) {
                Log.w("myInfo", "Something went wrong: " + t.getMessage());
                responseListener.OnFailed(t.getMessage());
            }
        });
    }
}
