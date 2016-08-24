package th.ac.bu.science.mit.allappstatscollector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import th.ac.bu.science.mit.allappstatscollector.Activities.MainActivity;

/**
 * Created by Komal on 11/2/2015.
 */
public class HashFileUploader extends AsyncTask<String, Void, String>
{
   // ProgressDialog pd;
    Context context;
    public static boolean isUploading=false;
    int serverCode;
    public HashFileUploader(Context _context)
    {
        context=_context;
    }


    synchronized public int uploadFile(String sourceFileUri) {

        String upLoadServerUri =  "http://mobile-monitoring.bu.ac.th//hash.aspx";


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
            Log.d("stats-results", "Error uploading file. Details:  Source File not exist :");
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



                Log.d("stats-results", "HTTP Response for hash file is : "+ serverResponseMessage + ": " + serverResponseCode);

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex)
            {
                ex.printStackTrace();
                Log.d("stats-results", "Error uploading hash file. Details: " + ex.getMessage(), ex);
            } catch (Exception e) {

                Log.d("stats-results", "Error hash uploading file. Details:  "+ e.getMessage());
            }

            return serverResponseCode;

        } // End else block
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(serverCode==200)
        {File file=new File(Settings.getHashFilePath());
        file.delete();}

    }

    @Override
    protected String doInBackground(String... params)
    {
        if(isInternetAvailable())
        {
            isUploading=true;
           // Log.d("stats-results","Internet Available: "+isInternetAvailable());
            //Log.d("stats-results", "Uploading file...");

            String path= Settings.getHashFilePath();
             serverCode=uploadFile(path);


            //Log.d("stats-hash", "Server Code from hashuploader: " + serverCode);
            if(serverCode==200) {

            Log.d(Settings.TAG, "Hash File uploaded successfully.");
            File file=new File(Settings.getHashFilePath());
            file.delete();
        }
            else if(serverCode==404) {

                Log.d(Settings.TAG,"Can not connect to server. Hash file can not be uploaded");

            }
            else if(serverCode==-1)
                Log.d(Settings.TAG,"Hash file does not exist.");
            else if(serverCode==504)
                Log.d(Settings.TAG,"Gateway Timeout. Couldn't upload hash file");
            else if(serverCode==0)
                Log.d(Settings.TAG,"Can not reach to server with this network.");
            else
            Log.d(Settings.TAG, "Unhandled server code. Couldn't upload hash file" + serverCode);

        }
        else
        {
            Log.d(Settings.TAG,"No internet access. Couldn't upload hash file");
        }
        isUploading=false;
        return null;
    }


    private  void showMessage(String msg)
    {
        Message message= MainActivity.handlerFileUpload.obtainMessage();
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
}
