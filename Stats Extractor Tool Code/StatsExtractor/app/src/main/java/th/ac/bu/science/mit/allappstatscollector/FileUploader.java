package th.ac.bu.science.mit.allappstatscollector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Komal on 11/2/2015.
 */
public class FileUploader extends AsyncTask<String, Void, String>
{
   // ProgressDialog pd;
    Context context;
    public static volatile boolean isUploading=false;

    public FileUploader(Context _context)
    {
        isUploading=true;
        context=_context;
    }


    synchronized public int uploadFile(String sourceFileUri) {



        //String upLoadServerUri =  "http://192.168.1.106//default.aspx";
        //String upLoadServerUri =  "http://10.109.68.7//default.aspx?id="+Settings.getOutputFileName();
        String upLoadServerUri =  "http://mobile-monitoring.bu.ac.th//default.aspx?id="+Settings.getOutputFileName(context);


        //String upLoadServerUri =  "http://210.86.135.102//default.aspx";
        int serverResponseCode = 0;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile())
        {
            Log.d(Settings.TAG, "Error uploading file. Details:  Source File not exist :");
            return -1;
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                //dos.writeBytes(twoHyphens + boundary + lineEnd);
                //dos.writeBytes("filename="+fileName +  lineEnd);

                //dos.writeBytes(lineEnd);  c

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                // dos.writeBytes(lineEnd);
                //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();


                Log.d(Settings.TAG, "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex)
            {
                ex.printStackTrace();
                Log.d(Settings.TAG, "Error uploading file. Details: " + ex.getMessage(), ex);
            } catch (Exception e) {

                Log.d(Settings.TAG, "Error uploading file. Details:  "+ e.getMessage());
                Notify.showNotification(context,"Error uploading file.");
            }

            return serverResponseCode;

        } // End else block
    }


    @Override
    protected String doInBackground(String... params)
    {
        if(isInternetAvailable())
        {
           // Log.d(Settings.TAG,"Internet Available: "+isInternetAvailable());
            //Log.d(Settings.TAG, "Uploading file...");

            StatsFileManager.compressFile(context);        //compress the file in background first..
            //Log.d(Settings.TAG,"Compressing file for uploading.");

            String path= Environment.getExternalStorageDirectory()+"/BU-Stat-Collector/"+Settings.getOutputFileName(context)+".zip";

            int serverCode=uploadFile(path);


            if(serverCode==200) {
                //Log.d(Settings.TAG, "File uploaded successfully.");
                new StatsFileManager(context).createNewFile();     //reset the file after uploading this data..
            }
            else if(serverCode==404)
                Log.d(Settings.TAG,"Can not connect to server.");

            else if(serverCode==-1)
                Log.d(Settings.TAG,"Source file does not exist.11");

            else if(serverCode==504)
                Log.d(Settings.TAG,"Gateway Timeout.");

            else
                Log.d(Settings.TAG,"Unhandled server code. " + serverCode);

            File file=new File(path);
            file.delete();  //delete the zip file
        }
        else
        {
           // Log.d(Settings.TAG,"No internet access.");
        }
        isUploading=false;
        return null;
    }



    private  void showMessage(String msg)
    {
        Message message=MainActivity.handlerFileUpload.obtainMessage();
        Bundle bundle=new Bundle();
        bundle.putString("message",msg);
        message.setData(bundle);
        MainActivity.handlerFileUpload.sendMessage(message);
    }

    public boolean isInternetAvailable()
    {
        final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if( wifi.isConnected() || mobile.isConnected())
            return true;
        else
            return false;
    }

    //write a function here to upload file using httppost...

       /* public void sendFile()
        {
            String url = "http://192.168.1.106//default.aspx";
            String path = context.getFilesDir().getAbsolutePath() + "/" + Settings.SETTINGS_CONFIG;
            //File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"yourfile");
            File file = new File(path);
            try
            {
                HttpURLConnection conn = null;
                Log.d("output"," uploading file called...");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);

                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
            }
            catch (Exception e)
            {
                Log.d("output","Exception uploading file..."+e.toString());
            }
        }*/

}
