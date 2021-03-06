USE [master]
GO
/****** Object:  Database [BUAntivirusStats]    Script Date: 7/21/2016 2:31:29 PM ******/
CREATE DATABASE [BUAntivirusStats]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'BUAntivirusStats', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\BUAntivirusStats.mdf' , SIZE = 1024000KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'BUAntivirusStats_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL11.MSSQLSERVER\MSSQL\DATA\BUAntivirusStats_log.ldf' , SIZE = 6206080KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [BUAntivirusStats] SET COMPATIBILITY_LEVEL = 110
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [BUAntivirusStats].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [BUAntivirusStats] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET ARITHABORT OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [BUAntivirusStats] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [BUAntivirusStats] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [BUAntivirusStats] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET  DISABLE_BROKER 
GO
ALTER DATABASE [BUAntivirusStats] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [BUAntivirusStats] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET RECOVERY FULL 
GO
ALTER DATABASE [BUAntivirusStats] SET  MULTI_USER 
GO
ALTER DATABASE [BUAntivirusStats] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [BUAntivirusStats] SET DB_CHAINING OFF 
GO
ALTER DATABASE [BUAntivirusStats] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [BUAntivirusStats] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
EXEC sys.sp_db_vardecimal_storage_format N'BUAntivirusStats', N'ON'
GO
USE [BUAntivirusStats]
GO
/****** Object:  Table [dbo].[Applications]    Script Date: 7/21/2016 2:31:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Applications](
	[application_id] [int] IDENTITY(1,1) NOT NULL,
	[package_name] [nchar](256) NOT NULL,
	[hashcode] [nchar](40) NOT NULL,
	[version_name] [nchar](128) NOT NULL,
	[version_number] [nchar](128) NOT NULL,
 CONSTRAINT [PK_Applications] PRIMARY KEY CLUSTERED 
(
	[application_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ_AppId_PackageName] UNIQUE NONCLUSTERED 
(
	[application_id] ASC,
	[package_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[device]    Script Date: 7/21/2016 2:31:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[device](
	[device_id] [int] IDENTITY(1,1) NOT NULL,
	[device_mac] [nchar](30) NOT NULL,
 CONSTRAINT [PK_device] PRIMARY KEY CLUSTERED 
(
	[device_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UK_device_mac_device] UNIQUE NONCLUSTERED 
(
	[device_mac] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[device_application]    Script Date: 7/21/2016 2:31:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[device_application](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[device_id] [int] NOT NULL,
	[application_id] [int] NOT NULL,
	[last_modified] [datetime] NULL,
 CONSTRAINT [PK_device_application] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Files]    Script Date: 7/21/2016 2:31:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Files](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[file_name] [nchar](265) NOT NULL,
	[is_processed] [bit] NOT NULL,
 CONSTRAINT [PK_Files] PRIMARY KEY CLUSTERED 
(
	[file_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[stats]    Script Date: 7/21/2016 2:31:29 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[stats](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[logtime] [datetime] NOT NULL,
	[device_id] [int] NOT NULL,
	[application_id] [int] NOT NULL,
	[is_main_process] [bit] NOT NULL,
	[is_interacting] [bit] NOT NULL,
	[pcy] [int] NOT NULL,
	[cpu] [int] NOT NULL,
	[vss] [int] NOT NULL,
	[rss] [int] NOT NULL,
	[threads] [int] NOT NULL,
	[priority] [int] NOT NULL,
	[status] [nchar](4) NOT NULL,
	[bg_up_data] [int] NOT NULL,
	[bg_down_data] [int] NOT NULL,
	[fg_up_data] [int] NOT NULL,
	[fg_down_data] [int] NOT NULL,
	[bg_up_wifi] [int] NOT NULL,
	[bg_down_wifi] [int] NOT NULL,
	[fg_up_wifi] [int] NOT NULL,
	[fg_down_wifi] [int] NOT NULL,
	[end_logtime] [datetime] NULL,
	[repeat] [int] NULL,
	[timeDiff] [int] NULL,
	[network_type] [int] NULL,
 CONSTRAINT [PK_stats] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_Files]    Script Date: 7/21/2016 2:31:29 PM ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_Files] ON [dbo].[Files]
(
	[file_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[device_application]  WITH CHECK ADD  CONSTRAINT [FK_device_application_Applications] FOREIGN KEY([application_id])
REFERENCES [dbo].[Applications] ([application_id])
GO
ALTER TABLE [dbo].[device_application] CHECK CONSTRAINT [FK_device_application_Applications]
GO
ALTER TABLE [dbo].[device_application]  WITH CHECK ADD  CONSTRAINT [FK_device_application_device] FOREIGN KEY([device_id])
REFERENCES [dbo].[device] ([device_id])
GO
ALTER TABLE [dbo].[device_application] CHECK CONSTRAINT [FK_device_application_device]
GO
ALTER TABLE [dbo].[stats]  WITH CHECK ADD  CONSTRAINT [FK_stats_Applications] FOREIGN KEY([application_id])
REFERENCES [dbo].[Applications] ([application_id])
GO
ALTER TABLE [dbo].[stats] CHECK CONSTRAINT [FK_stats_Applications]
GO
ALTER TABLE [dbo].[stats]  WITH CHECK ADD  CONSTRAINT [FK_stats_device1] FOREIGN KEY([device_id])
REFERENCES [dbo].[device] ([device_id])
GO
ALTER TABLE [dbo].[stats] CHECK CONSTRAINT [FK_stats_device1]
GO
USE [master]
GO
ALTER DATABASE [BUAntivirusStats] SET  READ_WRITE 
GO
