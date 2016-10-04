using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web;

/// <summary>
/// Summary description for FileDBManager
/// </summary>
public class FileDBManager
{
    string connectionString = "Data Source=BU-MIT-HP;Initial Catalog=BUAntivirusStats;Persist Security Info=True;User ID=sa;Password=P@$$w0rd";
    SqlDataAdapter da;
    SqlConnection con;

    public FileDBManager()
    {
        //
        // TODO: Add constructor logic here
        //
        con = new SqlConnection(connectionString);
        da = new SqlDataAdapter();
    }

    public bool insertFile(String fileName)
    {
        string query = @"insert into Files(file_name, is_processed) values(@file_name, @is_processed)";
        try
        {
            da.InsertCommand = new SqlCommand(query, con);
            da.InsertCommand.Parameters.AddWithValue("@file_name", fileName);
            da.InsertCommand.Parameters.AddWithValue("@is_processed", false);
            da.InsertCommand.CommandTimeout = 300;
            con.Open();
            da.InsertCommand.ExecuteNonQuery();
            con.Close();
        }
        catch (Exception e)
        {
            Logger.writeLog("Error inserting name of file. Values: file name:- " + fileName + " Details: \n " + e.ToString());
            con.Close();
            return false;
        }
        return true;

    } 

    public List<string> getFilesList()
    {
        List<string> files = new List<string>();
        String query = "select * from Files where is_processed=@is_processed";
        SqlDataReader dr;
        con.Open();
        SqlCommand comm = new SqlCommand(query, con);
        comm.Parameters.AddWithValue("@is_processed", false);
        comm.CommandTimeout = 300;
        dr = comm.ExecuteReader();

        while (dr.Read())
        {
            string file_name = dr["file_name"].ToString();
            if (file_name != null)
                files.Add(file_name);
        }

        dr.Close();
        con.Close();

        return files;
    }

    public bool setProcessed(string fileName)
    {
        string query = @"update Files set is_processed='true' where file_name=@file_name";
        try
        {
            da.UpdateCommand = new SqlCommand(query, con);
            da.UpdateCommand.CommandTimeout = 300;
            da.UpdateCommand.Parameters.AddWithValue("@file_name", fileName);
            con.Open();
            da.UpdateCommand.ExecuteNonQuery();
            con.Close();
        }
        catch (Exception e)
        {
            Logger.writeLog("Error inserting name of file. Values: file name:- " + fileName + " Details: \n " + e.ToString());
            con.Close();
            return false;
        }
        return true;

    }

}