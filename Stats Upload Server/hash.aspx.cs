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
public partial class hash : System.Web.UI.Page
{
    string base_path = "E:\\BU-Antivirus 3.0\\StatsUploadDatabase\\";
    protected void Page_Load(object sender, EventArgs e)
    {
        handleFile();
       

    }

    private void handleFile()
    {
        try
        {
            int length = (int)Request.InputStream.Length;
            if (length <= 0)
            {
                Response.Write("Invalid Request.");
                Logger.writeLog("IP ADDRESS: "+Request.UserHostAddress+" Hash file: No data received. Invalid Request.");
                return;
            }

            byte[] buffer = new byte[length];
            Request.InputStream.Read(buffer, 0, length);

            String data = ConvertByteToString(buffer);
            Logger.writeLog("Hash data received successfully.\n");
            AppsDBManager test = new AppsDBManager(data);

        }
        catch (Exception e)
        {
            Logger.writeLog("Error occured in handleFile class. Details: " + e.ToString());
        }

    }

    public string ConvertByteToString(byte[] source)
    {
        return source != null ? System.Text.Encoding.UTF8.GetString(source) : null;
    }

}