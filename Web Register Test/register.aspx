<%@ Page Language="C#" AutoEventWireup="true" CodeFile="register.aspx.cs" Inherits="register" EnableEventValidation="true" %>

<%@ Register TagPrefix="obout" Namespace="Obout.Interface" Assembly="obout_Interface" %>
<!DOCTYPE html>
<%@ Register Assembly="eWorld.UI, Version=2.0.6.2393, Culture=neutral, PublicKeyToken=24d65337282035f2"
    Namespace="eWorld.UI" TagPrefix="ew" %>
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
            <input type="hidden" id="confirm_value" name="confirm_value" />
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
                            <h3 class="post-title">ร่วมเข้าการทดสอบ
                            </h3>
                        </div>
                        <hr>
                        <div class="post-preview">
                            <div class="col-lg-8">
                                <p>

                                    <asp:UpdatePanel ID="UpdatePanel1" runat="server">
                                        <ContentTemplate>




                                            <fieldset>
                                                <div class="control-group">
                                                    <!-- Username -->
                                                    <label class="control-label" for="ID">ID<span style="color: red">*</span></label>
                                                    <div class="controls">

                                                        <ew:MaskedTextBox ID="ID" CssClass="input-xlarge" runat="server" Mask="xx:xx:xx:xx:xx:xx"></ew:MaskedTextBox>

                                                        <asp:RegularExpressionValidator ID="RegularExpressionValidator1" runat="server"
                                                            ControlToValidate="ID"
                                                            ErrorMessage="*" ForeColor="Red"
                                                            ValidationExpression="^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$">
                                                        </asp:RegularExpressionValidator>
                                                        <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server"
                                                            ControlToValidate="ID"
                                                            ErrorMessage="*"
                                                            ForeColor="Red">
                                                        </asp:RequiredFieldValidator>
                                                        <p class="help-block">กรอก ID ที่แสดงอยู่บนแอพ</p>
                                                    </div>
                                                </div>

                                                <div class="control-group">
                                                    <!-- E-mail -->
                                                    <label class="control-label" for="txt_version">Android Version<span style="color: red">*</span></label>
                                                    <div class="controls">
                                                        <asp:TextBox runat="server" MaxLength="5" ID="txt_version" CssClass="input-xlarge"></asp:TextBox>
                                                        <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server"
                                                            ControlToValidate="txt_version"
                                                            ErrorMessage="*"
                                                            ForeColor="Red">
                                                        </asp:RequiredFieldValidator>
                                                        <p class="help-block">Android Version เช่น 4.5 , 6.1</p>
                                                    </div>
                                                </div>
                                                <div class="control-group">
                                                    <label class="control-label" for="rblpaymenttype">วิธีการรับเงิน</label>
                                                    <p class="help-block">
                                                        <asp:RadioButtonList ID="rblpaymenttype" runat="server" RepeatDirection="Horizontal" CssClass="input-xlarge">
                                                            <asp:ListItem Value="เงินสด" Selected Text="เงินสด" />
                                                            <asp:ListItem Value="โอนเข้าธนาคาร" Text="โอนเข้าธนาคาร" />
                                                        </asp:RadioButtonList>
                                                    </p>
                                                </div>
                                                <div class="control-group">

                                                    <label class="control-label" for="txt_version">โปรดระบุชื่อ เบอร์โทร สถานที่รับเงินหรือรายละเอียดบัญชี<span style="color: red">*</span></label>
                                                    <div class="controls">
                                                        <asp:TextBox runat="server" ID="txt_Remark" CssClass="input-xlarge" TextMode="MultiLine" Rows="2"></asp:TextBox>
                                                        <asp:RequiredFieldValidator ID="RequiredFieldValidator3" runat="server"
                                                            ControlToValidate="txt_Remark"
                                                            ErrorMessage="*"
                                                            ForeColor="Red">
                                                        </asp:RequiredFieldValidator>

                                                    </div>
                                                </div>
                                                <div class="control-group">

                                                    <label class="control-label" for="txt_version">เข้าร่วมกลุ่มทาง Facebook <a href="https://www.facebook.com/groups/679959125489930" target="_blank">>>>BU-Stats Collector<<<</a></label>
                                                    <div class="controls">
                                                    </div>
                                                </div>

                                                <div class="control-group">
                                                    <!-- Button -->
                                                    <div class="controls">
                                                        <asp:Button runat="server" ID="btn_register" CssClass="btn btn-success" Text="Register" OnClientClick="validateAndConfirm()" OnClick="btn_register_Click" />
                                                    </div>
                                                </div>
                                            </fieldset>

                                        </ContentTemplate>

                                    </asp:UpdatePanel>
                                </p>
                            </div>
                            <div class="col-lg-4">
                                <img class="img-responsive" src="img/other/app_home.jpg" alt="">
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

                function CloseAndRefresh() {
                    window.opener.grid_Refresh();
                    setTimeout(function () { window.close(); }, 1000);
                }

                function isNumberKey(evt) {
                    var charCode = (evt.which) ? evt.which : evt.keyCode;
                    if (charCode != 46 && charCode > 31
                      && (charCode < 48 || charCode > 57))
                        return false;

                    return true;
                }

                function validateAndConfirm() {
                    Page_ClientValidate();
                    if (Page_IsValid) {


                        var confirm_value = document.getElementById("confirm_value");
                        if (confirm("Are you sure?")) {
                            confirm_value.value = "Yes";
                            return true;
                        } else {
                            confirm_value.value = "No";
                            return false;
                        }

                    }
                    return false;
                }
            </script>
        </div>
    </form>
</body>
</html>
