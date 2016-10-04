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
        string Name = "E:\\BU-Antivirus 3.0\\StatsUploadDatabase\\Log.txt";
        string data = DateTime.Now.ToString() + "\t" + message + "\n" + Environment.NewLine;
        File.AppendAllText(Name, data);
    }

    public static void writeLine()
    {
        string Name = "E:\\BU-Antivirus 3.0\\StatsUploadDatabase\\Log.txt";
        string data = "------****------****------****------****------****------****\n\n" + Environment.NewLine;
        File.AppendAllText(Name, data);
    }

    public static void writeError(string message)
    {
        string Name = "E:\\BU-Antivirus 3.0\\StatsUploadDatabase\\ClientErros.txt";
        string data = DateTime.Now.ToString() + "\t" + message + "\n" + Environment.NewLine;
        File.AppendAllText(Name, data);
        
    }
}