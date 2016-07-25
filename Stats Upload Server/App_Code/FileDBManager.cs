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
    string connectionString = "Data Source=KOMAL-HP;Initial Catalog=BUAntivirusStats;Integrated Security=True";
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
}