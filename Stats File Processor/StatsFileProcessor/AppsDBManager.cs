using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.SqlClient;


public class AppsDBManager
{
    string sourceFile = "";
    static string connectionString = "Data Source=BU-MIT-HP;Initial Catalog=BUAntivirusStats;Integrated Security=True";
    SqlDataAdapter da;
    static SqlConnection con;
    String data;

    const int UPDATED = 0;
    const int INSERTED = 1;
    const int ERROR = 2;

    public AppsDBManager(String _data)
    {
        con = new SqlConnection(connectionString);
        da = new SqlDataAdapter();
        data = _data;

        //insertApplication("test", "aa", "aa", "aa");

        //First line in data is Mac address of device. Read it and insert the device.
        using (System.IO.StringReader reader = new System.IO.StringReader(data))
        {

            string deviceMac = reader.ReadLine();
            if (deviceMac != null)
            {
                insertDevice(deviceMac);
                // Logger.writeLog("New device registerd successfully. Mac address: " + deviceMac);
            }
            else
            {
                Logger.writeLog("Received null mac address in hash file.");
            }

            string deviceId = getDeviceId(deviceMac);   //get the device id

            int insert = 0, fail = 0, update = 0;
            while (true)
            {
                string appInfo = reader.ReadLine();
                if (appInfo == null)
                    break;

                int result = insertApplication(appInfo, deviceId);
                if (result == INSERTED)
                    insert++;
                else if (result == UPDATED)
                    update++;
                else
                    fail++;

            }
            Logger.writeLog("MAC: " + deviceMac + " Apps have been inserted. Report - Inserted: " + insert + " Updated: " + update + " Failures: " + fail);
            //Logger.writeLine();
        }

    }

    public static bool insertDummyApplication(string packageName, string deviceId)
    {
        con = new SqlConnection(connectionString);

        if (packageName.Contains(':'))
        {
            string[] temp = packageName.Split(':');
            packageName = temp[0];
        }

        //string deviceId, string packageName, string lastUpdate, string hashCode, string versionName, string versionNumber
        Logger.writeLog("Inserting dummy application.");
        try
        {
            SqlDataAdapter da = new SqlDataAdapter();
            string query = @"DECLARE @DataID int;
                             insert into Applications(package_name, hashcode,version_name,version_number) values(@packageName, @hashcode, @versionName, @versionNumber);
		                     SELECT @DataID = SCOPE_IDENTITY();
			                 INSERT INTO device_application(device_id,application_id) VALUES (@deviceId,@DataID);";

            da.InsertCommand = new SqlCommand(query, con);
            da.InsertCommand.Parameters.AddWithValue("@deviceId", deviceId);
            da.InsertCommand.Parameters.AddWithValue("@hashcode", "Not available");
            da.InsertCommand.Parameters.AddWithValue("@packageName", packageName);
            da.InsertCommand.Parameters.AddWithValue("@versionName", "Not available");
            da.InsertCommand.Parameters.AddWithValue("@versionNumber", "Not available");

            con.Open();
            da.InsertCommand.ExecuteNonQuery();
            con.Close();
            Logger.writeLog("Stats recevied for the follwing package:  " + packageName + ". But package was not found in application table. Inserted this package as new application successfully.");
            return true;

        }
        catch (Exception ex)
        {
            con.Close();
            Logger.writeLog(@"Error inserting temp app data. Device ID:  " + deviceId +
                                 "package Name:  " + packageName + " Details:" + ex.ToString());
            return false;
        }

    }

    public int insertApplication(string appInfo, string deviceId)
    {

        if (checkUpdate(appInfo, deviceId))
            return UPDATED;

        int PACKAGE_NAME = 0;
        int LAST_UPDATE = 1;
        int HASH_CODE = 2;
        int VERSION_NAME = 3;
        int VERSION_CODE = 4;

        string[] tempData = appInfo.Split('*');
        if (tempData.Length != 5)
        {
            Logger.writeLog("Invalid file format.");
            return ERROR;
        }

        //string deviceId, string packageName, string lastUpdate, string hashCode, string versionName, string versionNumber

        try
        {
            SqlDataAdapter da = new SqlDataAdapter();
            string query = @"DECLARE @DataID int;
                             if not exists(select * from Applications Join device_application On device_application.application_id=Applications.application_id where hashcode=@hashcode and device_id=@deviceId)
                             Begin
                             insert into Applications(package_name, hashcode,version_name,version_number) values(@packageName, @hashcode, @versionName, @versionNumber)   
		                     SELECT @DataID = SCOPE_IDENTITY();
			                 INSERT INTO device_application(device_id,application_id,last_modified) VALUES (@deviceId,@DataID,@lastUpdate);
                             End";

            da.InsertCommand = new SqlCommand(query, con);
            da.InsertCommand.Parameters.AddWithValue("@deviceId", deviceId);
            da.InsertCommand.Parameters.AddWithValue("@hashcode", tempData[HASH_CODE]);
            da.InsertCommand.Parameters.AddWithValue("@packageName", tempData[PACKAGE_NAME]);
            da.InsertCommand.Parameters.AddWithValue("@versionName", tempData[VERSION_NAME]);
            da.InsertCommand.Parameters.AddWithValue("@versionNumber", tempData[VERSION_CODE]);
            da.InsertCommand.Parameters.AddWithValue("@lastUpdate", tempData[LAST_UPDATE]);
            con.Open();
            da.InsertCommand.ExecuteNonQuery();
            con.Close();
            return INSERTED;

        }
        catch (Exception ex)
        {
            con.Close();
            Logger.writeLog(@"Error inserting app data. Device ID:  " + deviceId +
                               "Last update time:  " + tempData[LAST_UPDATE] + " hashcode" + tempData[HASH_CODE] + " versionName: " + tempData[VERSION_NAME] + " versoin number " + tempData[VERSION_CODE] + "\nDetails:" + ex.ToString());
            return ERROR;
        }
    }



    public bool checkUpdate(string appInfo, string deviceId) //this method checks if any application has incomplete information and updates it. 
    {

        int PACKAGE_NAME = 0;

        string[] tempData = appInfo.Split('*');
        if (tempData.Length != 5)
        {
            Logger.writeLog("Invalid file format.");
            return false;
        }
        string packageName=tempData[PACKAGE_NAME];
       
        if (packageName.Contains(':'))
        {
            string[] temp = packageName.Split(':');
            packageName = temp[0];
        }


        bool result = false;

        string query = @"select Applications.application_id, device_application.id from Applications Join device_application On device_application.application_id=Applications.application_id where hashcode='Not available' and device_id=@deviceId and package_name=@packageName;";

        SqlCommand cmd = new SqlCommand(query, con);
        cmd.Parameters.AddWithValue("@deviceId", deviceId);
        cmd.Parameters.AddWithValue("@packageName", packageName);


        SqlDataReader dr;
        try
        {
            con.Open();
            dr = cmd.ExecuteReader();

            string device_applicationId = null, applicationId = null;
            if (dr.Read())
            {
                device_applicationId = dr[1].ToString();
                applicationId = dr[0].ToString();

            }
            dr.Close();
            con.Close();

            if (device_applicationId != null && applicationId != null)
            {
                //Logger.writeLog("Package " + tempData[PACKAGE_NAME]+" requires update.");
                updateApplicationInfo(appInfo, deviceId, device_applicationId, applicationId);
                //Logger.writeLog("Updated successfully.");
                return true;
            }

        }
        catch (Exception ex)
        {
            con.Close();
            Logger.writeLog("Error occured while checking for updating information. Details." + ex.ToString());
        }

        return result;
    }


    private int updateApplicationInfo(string appInfo, string deviceId, string device_applicationId, string applicationId)
    {

        int PACKAGE_NAME = 0;
        int LAST_UPDATE = 1;
        int HASH_CODE = 2;
        int VERSION_NAME = 3;
        int VERSION_CODE = 4;

        string[] tempData = appInfo.Split('*');

        string packageName = tempData[PACKAGE_NAME];

        if(packageName.Contains(":"))
        {
             packageName= packageName.Split(':')[0];
        }

        SqlDataAdapter da = new SqlDataAdapter();
        String update = @"UPDATE device_application SET last_modified = @last_modified WHERE id = @id;
                          UPDATE Applications SET hashcode = @hashcode, version_name=@version_name,version_number=@version_number  WHERE application_id = @application_id;";

        da.UpdateCommand = new SqlCommand(update, con);
        da.UpdateCommand.Parameters.AddWithValue("@last_modified", tempData[LAST_UPDATE]);
        da.UpdateCommand.Parameters.AddWithValue("@id", device_applicationId);
        da.UpdateCommand.Parameters.AddWithValue("@hashcode", tempData[HASH_CODE]);
        da.UpdateCommand.Parameters.AddWithValue("@version_name", tempData[VERSION_NAME]);
        da.UpdateCommand.Parameters.AddWithValue("@version_number", tempData[VERSION_CODE]);
        da.UpdateCommand.Parameters.AddWithValue("@application_id", applicationId);

        try
        {
            con.Open();
            da.UpdateCommand.ExecuteNonQuery();

            con.Close();

            Logger.writeLog("Updated Successfully. Package Name: " + packageName);
            return UPDATED;
        }
        catch (Exception ex)
        {
            con.Close();
            Logger.writeLog(@"Error inserting app data. Device ID:  " + deviceId +
                               "Last update time:  " + tempData[LAST_UPDATE] + " hashcode" + tempData[HASH_CODE] + " versionName: " + tempData[VERSION_NAME] + " versoin number " + tempData[VERSION_CODE] + "\nDetails:" + ex.ToString());
            return ERROR;
        }
    }



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
        }
        catch (Exception e)
        {
            Logger.writeLog("Error inserting device id. Device Mac: " + deviceMac + "\nDetails:" + e.ToString());
            return false;
        }
        con.Close();

        return true;
    }


    private string getDeviceId(string mac)
    {
        string id = null;

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