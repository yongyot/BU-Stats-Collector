using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.IO;

/// <summary>
/// Summary description for Logger
/// </summary>
public class Logger
{
    public Logger()
    {
        //
        // TODO: Add constructor logic here
        //
    }
    public static void writeLog(string message)
    {
        string Name = @"F:\\BU-Antivirus 3.0\\Logs\DBLog.txt";
        string data = DateTime.Now.ToString() + "\t" + message + "\n";
        File.AppendAllText(Name, data);
    }

    public static void writeLine()
    {
        string Name = "F:\\BU-Antivirus 3.0\\Logs\\DBLog.txt";
        string data = "------****------****------****------****------****------****\n\n";
        File.AppendAllText(Name, data);
    }

    public static void writeError(string message)
    {
        string Name = "F:\\BU-Antivirus 3.0\\StatsUploadDatabase\\ClientErros.txt";
        string data = DateTime.Now.ToString() + "\t" + message + "\n";
        File.AppendAllText(Name, data);

    }
}