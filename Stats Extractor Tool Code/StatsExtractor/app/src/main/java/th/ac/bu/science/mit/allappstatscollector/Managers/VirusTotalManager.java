package th.ac.bu.science.mit.allappstatscollector.Managers;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.science.mit.allappstatscollector.APIs.APIVirusTotal;
import th.ac.bu.science.mit.allappstatscollector.HashGen;
import th.ac.bu.science.mit.allappstatscollector.Interfaces.RestInterface;
import th.ac.bu.science.mit.allappstatscollector.Models.AppsInfo;
import th.ac.bu.science.mit.allappstatscollector.Models.ModelVirusTotalReport;
import th.ac.bu.science.mit.allappstatscollector.Settings;

public class VirusTotalManager extends AsyncTask<String, String, String> {

    public interface ResponseListener {
        void OnGetReportProgress (String message, int progress);
        void OnGetReportFinished (boolean isSuccessed, List<AppsInfo> appList, String reason);
    }

    enum State {
        Idle, SendHash, SendAPK
    }

    ResponseListener responseListener;
    final String API_KEY = "78660282aeaedccc679bb9b2e33095916ff8d356be6e77d05ef04a284c42deff";
    List<AppsInfo> appList = new ArrayList<>();
    List<String> hashList = new ArrayList<>();
    List<String> apkList = new ArrayList<>();
    State state = State.Idle;
    String result;
    boolean isRunning = true;
    Context context;
    int appCount = 0;
    int currentApp = 0;
    int currentApk = 0;
    int retryCount = 0;
    int maxRetry = 1;
    int currentProgress = 0;
    long totalAPKSize;

    public VirusTotalManager (Context context, ResponseListener callback) {
        this.context = context;
        this.responseListener = callback;
    }

    //======================Async
    @Override
    protected String doInBackground(String... params) {
        GetAllHashReport();

        while (isRunning)  {
            switch (state) {
                case Idle:
                    break;

                case SendHash:
                    state = State.Idle;
                    publishProgress("Get hashcode report.");
                    SendHashList(hashList);
                    break;

                case SendAPK:
                    state = State.Idle;
                    GetAPKReport();
                    break;
            }
        }
        result = "To Return";
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.w ("myInfo", "onPostExecute " + result);
        responseListener.OnGetReportFinished(true, appList, "Successed");
    }

    @Override
    protected void onProgressUpdate(String... text) {
        responseListener.OnGetReportProgress(text[0], currentProgress);
    }

    //======================End Async

    public void GetAllHashReport() {
        String selfApk = "th.ac.bu.science.mit.allappstatscollector";
        PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : apps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !appInfo.packageName.equals(selfApk)) {
                appCount++;
            }
        }

        for (ApplicationInfo appInfo : apps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !appInfo.packageName.equals(selfApk)) {
                currentApp++;
                publishProgress("Making hashcode. " + currentApp + "/" + appCount + " application.");
                AppsInfo app = HashGen.getPackageInfo(appInfo.packageName, context);
                appList.add(app);
                hashList.add(app.hash);
            }
        }
        state = State.SendHash;
    }

    void SendHashList (List<String> resourceList) {
        APIVirusTotal apiVirusTotal = RestInterface.api.create(APIVirusTotal.class);
        final Call<List<ModelVirusTotalReport>> call = apiVirusTotal.GetReport(API_KEY,
                resourceList);

        call.enqueue(new Callback<List<ModelVirusTotalReport>>() {
            @Override
            public void onResponse(Call<List<ModelVirusTotalReport>> call, Response<List<ModelVirusTotalReport>> response) {
                for (int index=0; index < response.body().size(); index++) {
                    ModelVirusTotalReport report = response.body().get(index);

                    int appIndex = GetIndexOfHash(report.Hash());
                    if (appIndex > -1) {
                        AppsInfo app = appList.get(appIndex);
                        if (report.HasData()) {
                            app.report = report;
                        } else {
                            apkList.add(app.packageName);
                        }
                    }
                }

                if (apkList.size() > 0) {
                    currentProgress = 10;
                    totalAPKSize = GetTotalAPKSize();
                    state = State.SendAPK;
                } else {
                    isRunning = false;
                }
            }

            @Override
            public void onFailure(Call<List<ModelVirusTotalReport>> call, Throwable t) {
                Log.w("myInfo", "Something went wrong: " + t.getMessage());
                responseListener.OnGetReportFinished(false, null, t.getMessage());
            }
        });
    }

    long GetTotalAPKSize () {
        long totalSize = 0;
        for (String apk : apkList) {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(apk, 0);

                File file = new File(packageInfo.applicationInfo.sourceDir);
                totalSize += file.length();
                Log.w ("myInfo", "File size " + file.length());

            } catch (Exception ex) {
                Log.d(Settings.TAG,"Error occurred in getPackageInfo method. Details: "+ ex.toString());
            }
        }
        return totalSize;
    }

    public void GetAPKReport () {
        String strCurrentApp = (currentApk + 1) + "";
        publishProgress("Sending app(s) to server " + strCurrentApp + "/" + apkList.size());
        Log.w ("myInfo", "Sending APK " + apkList.get(currentApk));

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(apkList.get(currentApk), 0);

            SendAPK(packageInfo.applicationInfo.sourceDir);
        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error occurred in getPackageInfo method. Details: "+ ex.toString());
        }
    }


    public void GetAPKReport (Context context, String packageName, ResponseListener callback) {
        this.responseListener = callback;

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);

            SendAPK(packageInfo.applicationInfo.sourceDir);
        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error occurred in getPackageInfo method. Details: "+ ex.toString());
        }
    }

    void SendAPK (String filePath) {
        APIVirusTotal apiVirusTotal = RestInterface.api.create(APIVirusTotal.class);

        File file = new File(filePath);
        Log.w ("myInfo", "File size " + file.length());

        RequestBody apkbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody apikey = RequestBody.create(MediaType.parse("text/plain"), API_KEY);
        final Call<List<ModelVirusTotalReport>> call = apiVirusTotal.GetAPKReport(apikey,
                apkbody);

        call.enqueue(new Callback<List<ModelVirusTotalReport>>() {
            @Override
            public void onResponse(Call<List<ModelVirusTotalReport>> call, Response<List<ModelVirusTotalReport>> response) {
                String logMessage = response.body().size() + " Response";
                for (int index=0; index < response.body().size(); index++) {
                    ModelVirusTotalReport report = response.body().get(index);
                    logMessage += "\n" + report.toString();
                }

                currentApk++;
                if (currentApk < apkList.size()) {
                    state = State.SendAPK;
//                    GetAPKReport();
                } else {
                    isRunning = false;
//                    responseListener.OnGetReportFinished(true, appList, "Successed");
                }
            }

            @Override
            public void onFailure(Call<List<ModelVirusTotalReport>> call, Throwable t) {
                if (retryCount < maxRetry) {
                    retryCount++;
                    Log.w ("myInfo", "retry : " + retryCount);
                    state = State.SendAPK;
//                    GetAPKReport();
                } else {
                    currentApk++;
                    retryCount = 0;
                    if (currentApk < apkList.size()) {
                        state = State.SendAPK;
//                        GetAPKReport();
                    } else {
                        isRunning = false;
//                        responseListener.OnGetReportFinished(true, appList, "Successed");
                    }
                }
            }
        });
    }

    public int GetIndexOfHash(String hashCode)
    {
        for (int i = 0; i < appList.size(); i++)
        {
            AppsInfo app = appList.get(i);
            if (hashCode.equals(app.hash))
            {
                return i;
            }
        }

        return -1;
    }
}
