using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatsFileProcessor
{
    class Program
    {
        static void Main(string[] args)
        {
            FileDBManager fileDBManager = new FileDBManager();
            //bool result=fileDBManager.insertFile(@"c:\\testfilepath\\xyz.993929493243249312.stats");
            List<string> file = fileDBManager.getFilesList();
            if (file.Count() == 0)
            {
                Logger.writeLog("No unprocessed files in table.");
                return;
            }
            long records = 0;
            int totalFiles = 0;
            foreach (string f in file)
            {
                Console.WriteLine("Processing: "+f.Trim());
                DBManager dbManager = new DBManager();
                records=records+dbManager.insertRecords(f.Trim());
                totalFiles++;
                fileDBManager.setProcessed(f.Trim());
                CacheAppsId.clearCache();
            }
            Logger.writeLog(records + " Records have been inserted from " + totalFiles+" files.");
            Logger.writeLine();
            

        }
    }
}
