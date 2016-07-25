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
            string path = @"F:\BU-Antivirus 3.0\Logs\Optimize Logs.txt";            
            File.AppendAllText(path,DateTime.Now.ToString() + "\t" + message+"\n");
        }

        public static void writeLine()
        {
            string Name = @"F:\BU-Antivirus 3.0\Logs\Optimize Logs.txt";
            string data = "------****------****------****------****------****------****\n\n";
            File.AppendAllText(Name, data);
        }

    }
}
