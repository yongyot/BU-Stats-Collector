using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.SqlClient;


public class DBManager
{
    string sourceFile = "";
    string connectionString = "Data Source=BU-MIT-HP;Initial Catalog=BUAntivirusStats;Integrated Security=True";
    SqlDataAdapter da;
    SqlConnection con;


    public DBManager()
    {

        con = new SqlConnection(connectionString);
        da = new SqlDataAdapter();
    }


    public int insertRecords(string _sourceFile)
    {
        try
        {
            sourceFile = _sourceFile;

            //Logger.writeLog("Starting writing in database. Device Id: " + _sourceFile);

            int count = 0;
            string line = "";
            int fail = 0;

            System.IO.StreamReader file = new System.IO.StreamReader(sourceFile);        //starting 5 lines contains meta info..

            line = file.ReadLine();         //1st line is file name. file name is mac address.stats "File Name: xx-xx-xx-xx.stats"\

            if (line.Trim().Length <= 0)
            {
                Logger.writeLog("Received Empty File. File name: " + _sourceFile);
                return -1;
            }

            int index = line.IndexOf(":");
            string deviceMac = line.Substring(index + 1, line.Length - (index + 7)).Trim();       //gives the device id.

            string deviceId = getDeviceId(deviceMac);

            if (deviceId == null)
            {
                insertDevice(deviceMac.Replace('-', ':'));
                deviceId = getDeviceId(deviceMac);
            }
            
            if(deviceId==null)
            {
                Logger.writeLog("Error: Can not locate device id. Device mac: " + deviceMac);
                return -1;
            }


            for (int i = 0; i < 4; i++)     //skip rest of the four lines.
                line = file.ReadLine();

            int c = 0;
            while (true)
            {
                if ((line = file.ReadLine()) == null)
                {
                    //Logger.writeLog("stopped reading");

                    break;
                }
                if (!String.IsNullOrEmpty(line.Trim()))
                {
                    if (c == 0)//first record...check if already exist
                    {
                        if (isDuplicate(line.TrimEnd(), deviceId))
                        {
                            Logger.writeLog("Mac Address: " + deviceMac + ". File already inserted. File Name: " + sourceFile);
                            break;
                        }
                    }

                    int code = insertLine(line.Trim(), deviceId);

                    if (code == 1)
                        count++;
                    else if (code == -2 || code == -3)
                        fail++;

                    else if (code == -1)
                    {
                        Logger.writeLog("Please refer to following file:" + _sourceFile);
                        break;
                    }
                    c++;
                }
                else
                    Logger.writeLog("Empty line." + line);
            }

            file.Close();
            Logger.writeLog("MAC: " + deviceMac + ". Finished inserting records. Details: successfully inserted: " + count + " : Failures: " + fail);
            
            return count;
        }
        catch (Exception e)
        {

            Logger.writeLog("Error while writing records. Details: " + e.ToString());
        }
        return 0;
    }

    private bool isDuplicate(string line, string deviceId)
    {
        string[] data = line.Split('|');

        string query = @"select count(*) from stats where device_id=@deviceId and logtime=@logtime";
        con.Open();
        SqlCommand comm = new SqlCommand(query, con);
        comm.Parameters.AddWithValue("@deviceId", deviceId);
        comm.Parameters.AddWithValue("@logtime", data[Index.LogTime]);
        Int32 count = (Int32)comm.ExecuteScalar();
        con.Close();
        if (count <= 0)
            return false;
        else
            return true;

    }


    private int insertLine(String line, String deviceId)
    {

        string[] data = line.Split('|');

        if (data.Length != 20)
        {
            Logger.writeLog("Invalid format of stats file.");
            return -1;  //invalid file foramat error.
        }

        data[Index.CPU] = data[(Index.CPU)].Substring(0, data[Index.CPU].IndexOf("%")); //remove %sign from cpu
        data[Index.VSS] = data[(Index.VSS)].Substring(0, data[Index.VSS].IndexOf("K")); //remove K sign from VSS
        data[Index.RSS] = data[(Index.RSS)].Substring(0, data[Index.RSS].IndexOf("K")); //remove K sign from VSS

        string application_Id = getApplicationID(data[Index.PackageName], deviceId);

        if (application_Id == null) //In some cases where we don't have application information in the applicatoin table but we have stats for it. Then insert this application as a dummy application. And update it when we receive application information.
        {
            //Logger.writeLog("Inserting dummy application. Package Name: "+data[Index.PackageName]+" Device ID: "+deviceId);
            AppsDBManager.insertDummyApplication(data[Index.PackageName], deviceId);
            application_Id = getApplicationID(data[Index.PackageName], deviceId);
        }


        if (application_Id == null)
        {
            return -3;
        }


        string query = "insert into stats(logtime, device_id, application_id, is_main_process , is_interacting, pcy, cpu, vss, rss, threads, priority, status, bg_up_data, " +
                        " bg_down_data, fg_up_data ,fg_down_data , bg_up_wifi,bg_down_wifi , fg_up_wifi, fg_down_wifi, network_type)" +
                        "values(@logtime, @device_id, @application_Id , @is_main_process , @is_interacting, @pcy ,@cpu, @vss, @rss, @threads, @priority, @status, @bg_up_data, @bg_down_data, @fg_up_data , @fg_down_data , @bg_up_wifi, @bg_down_wifi , @fg_up_wifi, @fg_down_wifi, @network_type);";


        try
        {
            da.InsertCommand = new SqlCommand(query, con);
            da.InsertCommand.Parameters.AddWithValue("@logtime", data[Index.LogTime]);
            da.InsertCommand.Parameters.AddWithValue("@device_id", deviceId);
            da.InsertCommand.Parameters.AddWithValue("@application_Id", application_Id);
            da.InsertCommand.Parameters.AddWithValue("@is_main_process", data[Index.isMainProcess]);
            da.InsertCommand.Parameters.AddWithValue("@is_interacting", data[Index.isInteracting]);
            da.InsertCommand.Parameters.AddWithValue("@pcy", data[Index.pcy]);
            da.InsertCommand.Parameters.AddWithValue("@cpu", data[Index.CPU]);
            da.InsertCommand.Parameters.AddWithValue("@vss", data[Index.VSS]);
            da.InsertCommand.Parameters.AddWithValue("@rss", data[Index.RSS]);
            da.InsertCommand.Parameters.AddWithValue("@threads", data[Index.THREADS]);
            da.InsertCommand.Parameters.AddWithValue("@priority", data[Index.Priority]);
            da.InsertCommand.Parameters.AddWithValue("@status", data[Index.Status]);
            da.InsertCommand.Parameters.AddWithValue("@bg_up_data", data[Index.BG_UP_DATA]);
            da.InsertCommand.Parameters.AddWithValue("@bg_down_data", data[Index.BG_DOWN_DATA]);
            da.InsertCommand.Parameters.AddWithValue("@fg_up_data", data[Index.FG_UP_DATA]);
            da.InsertCommand.Parameters.AddWithValue("@fg_down_data", data[Index.FG_DOWN_DATA]);
            da.InsertCommand.Parameters.AddWithValue("@bg_up_wifi", data[Index.BG_UP_WiFi]);
            da.InsertCommand.Parameters.AddWithValue("@bg_down_wifi", data[Index.BG_DOWN_WiFi]);
            da.InsertCommand.Parameters.AddWithValue("@fg_up_wifi", data[Index.FG_UP_WiFi]);
            da.InsertCommand.Parameters.AddWithValue("@fg_down_wifi", data[Index.FG_DOWN_WiFi]);
            da.InsertCommand.Parameters.AddWithValue("@network_type", data[Index.NETWORK_TYPE]);

            con.Open();
            da.InsertCommand.ExecuteNonQuery();
            con.Close();
        }
        catch (Exception e)
        {
            Logger.writeLog("Error inserting stats. Details: \n " + e.ToString());

            string temp = "Inserting Data\nLogTime|deviceId|isMainProcess|isInteracting|PCY|CPU|VSS|RSS|THREADS|Priority|Status|BG_UP_DATA| BG_DOWN_DATA| FG_UP_DATA|FG_DOWN_DATA|BG_UP_WiFi|BG_DOWN_WiFi|FG_UP_WiFi|FG_DOWN_WiFi" +
              data[Index.LogTime] + "|" + deviceId + "|" + data[Index.isMainProcess] + "|" + data[Index.isInteracting] + "|" + data[Index.pcy] + "|" + data[Index.CPU] + "|" + data[Index.VSS] + "|" + data[Index.RSS] + "|" + data[Index.THREADS] + "|" +
              data[Index.Priority] + "|" + data[Index.Status] + "|" + data[Index.BG_UP_DATA] + "|" + data[Index.BG_DOWN_DATA] + "|" + data[Index.FG_UP_DATA] + "|" + data[Index.FG_DOWN_DATA] + "|" +
              data[Index.BG_UP_WiFi] + "|" + data[Index.BG_DOWN_WiFi] + "|" + data[Index.FG_UP_WiFi] + "|" + data[Index.FG_DOWN_WiFi] + "|" + data[Index.NETWORK_TYPE];

            Logger.writeLog("Values: " + temp);


            con.Close();
            return -2;  //sql error.
        }
        return 1;

    }

    public string getApplicationID(string packageName, string deviceId)
    {
        string id = null;

        //check if package name contains colons; if yes remove it and then refer to application 
        if (packageName.Contains(':'))
        {
            string[] temp = packageName.Split(':');
            packageName = temp[0];
        }

        int cachedId = CacheAppsId.getAppIdFromCache(packageName);
        if (cachedId != -1) //app id is cached. no need to check in database.
            return cachedId + "";


        string query = @"SELECT applications.application_id FROM device_application JOIN device ON device_application.device_id	= device.device_id 
                        Join Applications ON device_application.application_id= Applications.application_id 
                        Where Applications.package_name=@packageName and device.device_id=@deviceId and  (device_application.last_modified = (SELECT Max(device_application.last_modified) 
                        FROM device_application JOIN device ON device_application.device_id	= device.device_id Join Applications ON device_application.application_id= Applications.application_id
                        Where Applications.package_name=@packageName and device.device_id=@deviceId) or device_application.last_modified  IS NULL);";

        SqlDataReader dr;
        SqlCommand cmd = new SqlCommand(query, con);
        cmd.Parameters.AddWithValue("@packageName", packageName);
        cmd.Parameters.AddWithValue("@deviceId", deviceId);
        try
        {
            con.Open();
            dr = cmd.ExecuteReader();
            if (dr.Read())
            {
                id = dr[0].ToString();
            }

            dr.Close();
            con.Close();
            /*Logger.writeLog("*****ID Package Name DeviceID ");
            Logger.writeLog("*****"+id+"  "+packageName+  "   "+deviceId );*/
        }
        catch (Exception e)
        {
            Logger.writeLog("Error while reading application id for inserting stats. Package name" + packageName + " Device ID: " + deviceId + "\nDetails:" + e.ToString());
        }

        if (id != null)
            CacheAppsId.addCache(packageName, Convert.ToInt32(id));

        return id;
    }

    /**/
    private bool insertDevice(string deviceMac)
    {
        string query = @"if not exists(select * from device where device_mac=@device_mac) insert into device(device_mac) values(@device_mac);";

        da.InsertCommand = new SqlCommand(query, con);
        da.InsertCommand.Parameters.AddWithValue("@device_mac", deviceMac);

        try
        {
            con.Open();
            da.InsertCommand.ExecuteNonQuery();
            con.Close();
            Logger.writeLog("New Device inserted. Mac address: " + deviceMac);
        }
        catch (Exception e)
        {
            Logger.writeLog("Error inserting device id. Device Mac: " + deviceMac + "\nDetails:" + e.ToString());
            return false;
        }
        con.Close();

        return true;
    }

    /**/



    private string getDeviceId(string mac)
    {
        string id = null;

        mac = mac.Replace('-', ':');

        //Logger.writeLog("Device mac request: " + mac);

        String query = "select device_id from device where device_mac = '" + mac + "';";
        SqlDataReader dr;
        SqlCommand cmd = new SqlCommand(query, con);
        try
        {
            con.Open();
            dr = cmd.ExecuteReader();
            if (dr.Read())
            {
                id = dr["device_id"].ToString();
            }

            dr.Close();
            con.Close();
        }
        catch (Exception e)
        {
            Logger.writeLog("Error reading device id:" + mac + "\nDetails:" + e.ToString());
        }
        return id;
    }
}