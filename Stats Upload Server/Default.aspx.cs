using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;
using System.IO.Compression;


using System.Text;
using System.Diagnostics;

public partial class _Default : System.Web.UI.Page
{
    string base_path = "F:\\BU-Antivirus 3.0\\StatsUploadDatabase\\";
    protected void Page_Load(object sender, EventArgs e)
    {
        handleFile();
    }

    private void handleFile()
    {
        try
        {
            int length = (int)Request.InputStream.Length;                               //we expect some data. If there is no data. skip it.
            
            string queryMac = null;

            if (length <= 0)
            {
                try
                {
                    queryMac = Request.QueryString["id"].ToString();
                }
                catch (Exception ex)
                { 
                }
                
                Response.Write("Invalid Request");
                if (queryMac != null)
                    Logger.writeLog(" IP : " + Request.UserHostAddress + "  Invalid request from mobile device. Device Mac: " + queryMac + "\n");

                else
                    Logger.writeLog("IP : " + Request.UserHostAddress + "  Invalid requst. Unknown mac address." + "\n");
                return;
            }

            byte[] buffer = new byte[length];
            Request.InputStream.Read(buffer, 0, length);                             //read the zip file


            String fileName = Request.QueryString["id"];
            SaveZipFile(buffer, fileName + ".zip");                                //save the zip file.

            string path = unCompressZipFile(fileName);                            //extract the zip file. Path is abolute path of uncompressed file.

            FileDBManager fileDBManager = new FileDBManager();                   //insert the file name in database.
            bool result = fileDBManager.insertFile(path);

            if (result)
                Logger.writeLog(" IP : " + Request.UserHostAddress + " File inserted successfuly. File Path:\t " + path + "\n");
        }
        catch (Exception e)
        {
            Logger.writeLog("Error occured in handleFile class. Details: " + e.ToString());
        }

    }


    protected void SaveZipFile(byte[] Data, String fileName)
    {
        string Name = "F:\\BU-Antivirus 3.0\\StatsUploadDatabase\\" + fileName;
        File.WriteAllBytes(Name, Data);
    }

    protected string unCompressZipFile(String zipName)
    {
        string Name = base_path + zipName + ".zip";                 // zip file absolute path
        String timeStamp = Stopwatch.GetTimestamp().ToString();     
        string extractpath = base_path + zipName;                   //destination path for uncompressing zip file.

        ZipFile.ExtractToDirectory(Name, extractpath);              

        string newFile = base_path + zipName + "\\" + zipName + timeStamp;
        string oldFile = base_path + zipName + "\\" + zipName;      //compressed zip file name contains a file and has same name as zip file. (Eg.: test.zip contains test.stats file)
            
        if (System.IO.File.Exists(newFile)) System.IO.File.Delete(newFile); 
        System.IO.File.Move(oldFile, newFile);                      //save the file with timestamp.


        return newFile;                                             //returns absolute path of the file.

    }


}