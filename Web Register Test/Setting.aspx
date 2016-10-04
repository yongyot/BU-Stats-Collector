<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Setting.aspx.cs" Inherits="Setting" %>



<!DOCTYPE html>

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
            <div class="container" style="text-align: center">
                <div class="row">
                    <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                        <div class="post-preview">
                            <h3 class="post-title">วิธีการติดตั้ง
                            </h3>
                        </div>
                        <hr>
                        <div class="post-preview">
                            <div class="col-lg-12">
                                <p>
                                    ค้นหาชื่อ BU-Stats Collector
                                    <a href="https://play.google.com/store/apps/details?id=th.ac.bu.science.mit.allappstatscollector" target="_blank">
                                        <img src="googleplay.png" class="img-responsive" />
                                    </a>
                                </p>

                                <p>

                                    <img src="img/setting/1.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>

                                    <img src="img/setting/2.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>

                                    <img src="img/setting/3.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>

                                    <img src="img/setting/4.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>

                                    <img src="img/setting/4_1.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>

                                    <img src="img/setting/5.jpg" class="img-responsive" style="max-width: 220px; max-width: 300px" />

                                </p>
                                <p>
                                    ลงทะเบียนการเข้าร่วมทดสอบแอพ โดยนำ ID ที่แสดงผ่านหน้าจอแอพนำมาลงทะเบียน <a href="register.aspx">>>>ลงทะเบียน<<<
                                    </a>เปิดรับเพียง 100 เครื่องเท่านั้น

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



        </div>
    </form>
</body>
</html>
