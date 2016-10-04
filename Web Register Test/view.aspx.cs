using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data.OleDb;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class view : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }
    protected void btn_search_Click(object sender, EventArgs e)
    {

        if (IsValid)
        {

            CreateGrid(sender);


        }
    }
    void CreateGrid(object sender)
    {
        string con = ConfigurationManager.AppSettings["ConnectString"];
        OleDbConnection myConn = new OleDbConnection(con);

        string sql = "";
        sql += " SELECT ROW_NUMBER() over(order by convert(nvarchar,[CreateOn],105)) as ID";
        sql += " ,[ID_String]";
        sql += "  ,convert(nvarchar,[CreateOn],105) as Datetime";
        sql += "  ,count(*) as Conutfile";
        sql += "   ,convert(nvarchar,max([CreateOn]),108) as LastDatetime  ";
        sql += "  from [dbo].[test_file]";
        sql += "  where [ID_String]='" + txt_ID.Text + "'";
        sql += "  group by convert(nvarchar,[CreateOn],105) ,[ID_String]";

        OleDbCommand myComm = new OleDbCommand(sql, myConn);
        myConn.Open();
        OleDbDataReader myReader = myComm.ExecuteReader();

        if (!myReader.HasRows)
        {
            Repeater1.DataSource = myReader;
            Repeater1.DataBind();
            Random rnd = new Random();
            int month = rnd.Next(1, 13); // creates a number between 1 and 12
            ScriptManager.RegisterClientScriptBlock(sender as Button, this.GetType(), "scr_" + month.ToString(), "alert('ไม่พบข้อมูล')", true);
        }
        else
        {
            Repeater1.DataSource = myReader;
            Repeater1.DataBind();
        }

        myConn.Close();
    }

}