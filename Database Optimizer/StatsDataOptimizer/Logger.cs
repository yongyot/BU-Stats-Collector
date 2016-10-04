using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatsDataOptimizer
{
    class Logger
    {
        public static void writeLog(string message)
        {
            string path = @"E:\BU-Antivirus 3.0\Logs\Optimize Logs.txt";
            File.AppendAllText(path, DateTime.Now.ToString() + "\t" + message + "\n" + Environment.NewLine);
        }

        public static void writeLine()
        {
            string Name = @"E:\BU-Antivirus 3.0\Logs\Optimize Logs.txt";
            string data = "------****------****------****------****------****------****\n\n" + Environment.NewLine;
            File.AppendAllText(Name, data);
        }

    }
}
