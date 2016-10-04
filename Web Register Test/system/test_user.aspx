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
            CreateGrid();
        }

    }

    void CreateGrid()
    {

        OleDbConnection myConn = new OleDbConnection(con);

        OleDbCommand myComm = new OleDbCommand("SELECT * FROM test_user", myConn);
        myConn.Open();
        OleDbDataReader myReader = myComm.ExecuteReader();

        grid1.DataSource = myReader;
        grid1.DataBind();

        myConn.Close();
    }

    void DeleteRecord(object sender, GridRecordEventArgs e)
    {
        OleDbConnection myConn = new OleDbConnection(con);
        myConn.Open();

        OleDbCommand myComm = new OleDbCommand("DELETE FROM test_user WHERE ID = " + e.Record["ID"], myConn);
        myComm.ExecuteNonQuery();
        myConn.Close();
    }
    void UpdateRecord(object sender, GridRecordEventArgs e)
    {
        OleDbConnection myConn = new OleDbConnection(con);
        myConn.Open();

        OleDbCommand myComm = new OleDbCommand("UPDATE test_user SET ID_String = '" + e.Record["ID_String"] + "', Android_Version = '" + e.Record["Android_Version"] + "', PaymentType='" + e.Record["PaymentType"] + "' , Remark = '" + e.Record["Remark"] + "'  WHERE ID = " + e.Record["ID"], myConn);

        myComm.ExecuteNonQuery();
        myConn.Close();
    }
    void InsertRecord(object sender, GridRecordEventArgs e)
    {
        OleDbConnection myConn = new OleDbConnection(con);
        myConn.Open();

        OleDbCommand myComm = new OleDbCommand("INSERT INTO test_user (ID_String, Android_Version, PaymentType,Remark,CreatedOn) VALUES('" + e.Record["ID_String"] + "', '" + e.Record["Android_Version"] + "', '" + e.Record["PaymentType"] + "','" + e.Record["Remark"] + "',sysdatetime())", myConn);

        myComm.ExecuteNonQuery();
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

        <h1>ร่วมเข้าการทดสอบ</h1>

        <br />
         <a href="index.html">กลับสู่เมนู</a>
        <br />
        <br>
        <obout:Grid ID="grid1" runat="server" CallbackMode="true" Serialize="true" PageSize="100"
            FolderStyle="../styles/grid/black_glass" AutoGenerateColumns="false" AllowFiltering="true" 
            OnRebind="RebindGrid" OnInsertCommand="InsertRecord" OnDeleteCommand="DeleteRecord" OnUpdateCommand="UpdateRecord">
            <Columns>
                <obout:Column DataField="ID" Visible="false" ReadOnly="true" HeaderText="ID" Width="125" runat="server" />
                <obout:Column DataField="ID_String" HeaderText="Mac Address" Width="250" runat="server">
                </obout:Column>
                <obout:Column DataField="Android_Version" HeaderText="Android_Version" Width="175" runat="server" />
                <obout:Column DataField="PaymentType" HeaderText="Payment Type" Width="115" runat="server" />
                <obout:Column DataField="Remark" HeaderText="Remark" Width="200" runat="server" />
                <obout:Column DataField="CreatedOn" HeaderText="Created On" Width="200"  AllowEdit="false"  DataFormatString="{0:dd-MM-yyyy HH:mm}" runat="server" />
                <obout:Column AllowEdit="true" AllowDelete="false" HeaderText="EDIT" Width="125" runat="server" />
                <obout:Column AllowEdit="false" AllowDelete="true" HeaderText="DELETE" Width="125" runat="server" />
            </Columns>

        </obout:Grid>

        <br />
        <br />
        <br />



    </form>
</body>
</html>
