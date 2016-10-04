<%@ Page Language="C#" AutoEventWireup="true" CodeFile="view.aspx.cs" Inherits="view" %>

<%@ Register Assembly="eWorld.UI, Version=2.0.6.2393, Culture=neutral, PublicKeyToken=24d65337282035f2"
    Namespace="eWorld.UI" TagPrefix="ew" %>
<!DOCTYPE html>
<%@ Register TagPrefix="obout" Namespace="Obout.Interface" Assembly="obout_Interface" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>BU-Stats Collector</title>
    <!-- Bootstrap Core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Theme CSS -->
    <link href="css/clean-blog.min.css" rel="stylesheet">
    <!-- Custom Fonts -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href='https://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic'
        rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'
        rel='stylesheet' type='text/css'>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->


    <style type="text/css">
        .modal {
            position: fixed;
            z-index: 999;
            height: 100%;
            width: 100%;
            top: 0;
            background-color: Black;
            filter: alpha(opacity=60);
            opacity: 0.6;
            -moz-opacity: 0.8;
        }

        .center {
            z-index: 1000;
            margin: 300px auto;
            padding: 10px;
            width: 130px;
            background-color: White;
            border-radius: 10px;
            filter: alpha(opacity=100);
            opacity: 1;
            -moz-opacity: 1;
        }

            .center img {
                height: 128px;
                width: 128px;
            }
    </style>
</head>
<body>
    <form id="form1" runat="server">
        <div>
            <asp:ScriptManager ID="ScriptManager1" runat="server"></asp:ScriptManager>
            <asp:UpdateProgress ID="UpdateProgress1" runat="server" AssociatedUpdatePanelID="UpdatePanel1">
                <ProgressTemplate>
                    <div class="modal">
                        <div class="center">
                            <img alt="" src="loader.gif" />
                        </div>
                    </div>
                </ProgressTemplate>
            </asp:UpdateProgress>

            <!-- Navigation -->
            <nav class="navbar navbar-default navbar-custom navbar-fixed-top">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header page-scroll">
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                            <span class="sr-only">Toggle navigation</span>
                            Menu <i class="fa fa-bars"></i>
                        </button>
                        <a class="navbar-brand" href="index.html">BU-Stats Collector</a>
                    </div>

                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                      <ul class="nav navbar-nav navbar-right">
                            <li>
                                <a href="index.html">เงื่อนไข</a>
                            </li>
                            <li>
                                <a href="Setting.aspx">วิธีการติดตั้ง</a>
                            </li>
                            <li>
                                <a href="register.aspx">ลงทะเบียน</a>
                            </li>
                            <li>
                                <a href="view.aspx">ตรวจสอบไฟล์</a>
                            </li>

                        </ul>
                    </div>
                    <!-- /.navbar-collapse -->
                </div>
                <!-- /.container -->
            </nav>
            <!-- Page Header -->
            <!-- Set your background image for this header on the line below. -->
            <header class="intro-header" style="background-image: url('img/unnamed.png')">
                <div class="container">
                    <div class="row">
                        <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                            <div class="site-heading">
                                <h1>BU-Stats Collector</h1>
                                <hr class="small">
                                <span class="subheading">ร่วมทดสอบการใช้งาน Application โดยมีค่าตอบแทนจำนวน 200 บาท </span>
                            </div>
                        </div>
                    </div>
                </div>
            </header>
            <!-- Main Content -->
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <div class="post-preview">
                            <h3 class="post-title">ตรวจสอบข้อมูล
                            </h3>
                        </div>
                        <hr>
                        <div class="post-preview">
                            <div class="col-lg-12">
                                <p>

                                    <asp:UpdatePanel ID="UpdatePanel1" runat="server">
                                        <ContentTemplate>
                                            ID :  
                                            <ew:MaskedTextBox ID="txt_ID" CssClass="input-xlarge" runat="server" Mask="xx:xx:xx:xx:xx:xx"></ew:MaskedTextBox>

                                            <asp:RegularExpressionValidator ID="RegularExpressionValidator1" runat="server"
                                                ControlToValidate="txt_ID"
                                                ErrorMessage="*" ForeColor="Red"
                                                ValidationExpression="^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$">
                                            </asp:RegularExpressionValidator>
                                            <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server"
                                                ControlToValidate="txt_ID"
                                                ErrorMessage="*"
                                                ForeColor="Red">
                                            </asp:RequiredFieldValidator>
                                            <asp:Button runat="server" ID="btn_search" Text="ค้นหา" Width="100px" OnClientClick="validateAndConfirm()" OnClick="btn_search_Click" />
                                            <asp:Repeater ID="Repeater1" runat="server">
                                                <HeaderTemplate>

                                                    <div class="w3-padding w3-white notranslate">
                                                        <table class="table">
                                                            <thead>
                                                                <tr>
                                                                    <th>ลำดับ</th>
                                                                    <th>วันที่</th>
                                                                    <th>จำนวนไฟล์</th>
                                                                    <th>เวลาอัพเดทล่าสุด</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                </HeaderTemplate>
                                                <ItemTemplate>


                                                    <tr class="success">
                                                        <td><%# Eval("ID") %></td>
                                                        <td><%# Eval("Datetime") %></td>
                                                        <td><%# Eval("Conutfile") %></td>
                                                        <td><%# Eval("LastDatetime") %></td>
                                                    </tr>



                                                </ItemTemplate>
                                                <FooterTemplate>
                                                    </tbody>
                                                        </table>
                                                    </div>

                                                </FooterTemplate>


                                            </asp:Repeater>


                                        </ContentTemplate>

                                    </asp:UpdatePanel>
                                </p>
                            </div>
                        </div>

                    </div>

                </div>
            </div>
            <hr>
            <!-- Footer -->
            <footer>
                <div class="container">
                    <div class="row">
                        <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                            <p class="copyright text-muted">By Team Yongyot Khamma (0801555843 บอย) &copy;2016</p>
                        </div>
                    </div>
                </div>
            </footer>
            <!-- jQuery -->

            <script src="vendor/jquery/jquery.min.js"></script>

            <!-- Bootstrap Core JavaScript -->

            <script src="vendor/bootstrap/js/bootstrap.min.js"></script>

            <!-- Contact Form JavaScript -->

            <script src="js/jqBootstrapValidation.js"></script>

            <script src="js/contact_me.js"></script>

            <!-- Theme JavaScript -->

            <script src="js/clean-blog.min.js"></script>
            <script type="text/javascript">

                function validateAndConfirm() {
                    Page_ClientValidate();
                    if (Page_IsValid) {


                        return true;

                    }
                    return false;
                }

            </script>


        </div>
    </form>
</body>
</html>
