<%@ Page Language="C#" %>

<%@ Register TagPrefix="obout" Namespace="Obout.Grid" Assembly="obout_Grid_NET" %>
<%@ Import Namespace="System.Data" %>
<%@ Import Namespace="System.Data.OleDb" %>
<%@ Import Namespace="System.Data.SqlClient" %>
<%@ Register TagPrefix="obout" Namespace="Obout.Interface" Assembly="obout_Interface" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<script language="C#" runat="server">
    string con = ConfigurationManager.AppSettings["ConnectString"];
    void Page_load(object sender, EventArgs e)
    {
        if (!Page.IsPostBack)
        {
            grid1.FilteringSettings.FilterPosition = GridFilterPosition.Top;
            CreateGrid();
        }

    }

    void CreateGrid()
    {

        OleDbConnection myConn = new OleDbConnection(con);


        string sql = "";
        sql += " SELECT ROW_NUMBER() over(order by convert(nvarchar,[CreateOn],105)) as ID";
        sql += " ,[ID_String]";
        sql += "  ,convert(nvarchar,[CreateOn],105) as Datetime";
        sql += "  ,count(*) as Conutfile";
        sql += "   ,convert(nvarchar,max([CreateOn]),108) as LastDatetime  ";
        sql += "  from [dbo].[test_file]";
        sql += "  group by convert(nvarchar,[CreateOn],105) ,[ID_String]";

        OleDbCommand myComm = new OleDbCommand(sql, myConn);
        myConn.Open();
        OleDbDataReader myReader = myComm.ExecuteReader();

        grid1.DataSource = myReader;
        grid1.DataBind();

        myConn.Close();
    }


    void RebindGrid(object sender, EventArgs e)
    {
        CreateGrid();
    }			
</script>

<html>
<head>
    <title>ร่วมเข้าการทดสอบ</title>
    <style type="text/css">
        .tdText {
            font: 11px Verdana;
            color: #333333;
        }

        .option2 {
            font: 11px Verdana;
            color: #0033cc;
            background-color___: #f6f9fc;
            padding-left: 4px;
            padding-right: 4px;
        }

        a {
            font: 11px Verdana;
            color: #315686;
            text-decoration: underline;
        }

            a:hover {
                color: crimson;
            }
    </style>
</head>
<body>
    <form runat="server">

        <br />

        <h1>ตราจสอบข้อมูล</h1>

        <br />
        <a href="index.html">กลับสู่เมนู</a>
        <br />
        <br>
        <obout:Grid ID="grid1" runat="server" CallbackMode="true" Serialize="true" AllowGrouping="true" ShowMultiPageGroupsInfo="true"
            FolderStyle="../styles/grid/black_glass" AutoGenerateColumns="false" AllowFiltering="true" AllowAddingRecords="false"
            OnRebind="RebindGrid" GroupBy="ID_String"  ShowCollapsedGroups="true" PageSize="100" >
            <Columns>
                <obout:Column DataField="ID" Visible="false" ReadOnly="true" HeaderText="ID" Width="125" runat="server" />
                <obout:Column DataField="ID_String" HeaderText="Mac Address" Width="250" runat="server">
                    
                </obout:Column>

                <obout:Column DataField="Datetime" HeaderText="วันที่" Width="250" runat="server" AllowFilter="false">
                </obout:Column>
                <obout:Column DataField="Conutfile" HeaderText="จำนวนไฟล์" Width="250" runat="server" AllowFilter="false">
                </obout:Column>
                <obout:Column DataField="LastDatetime" HeaderText="เวลาอัพเดทล่าสุด" Width="250" runat="server" AllowFilter="false">
                </obout:Column>
            </Columns>

        </obout:Grid>

        <br />
        <br />
        <br />



    </form>
</body>
</html>
