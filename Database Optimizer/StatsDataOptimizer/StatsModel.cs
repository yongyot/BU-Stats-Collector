using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatsDataOptimizer
{
    class StatsModel
    {
        public string id;
        public DateTime logtime;
        public bool is_interacting;
        public int pcy;
        public int cpu;
        public int vss;
        public int rss;
        public int threads;
        public int priority;
        public string status;
        public int bg_up_data;
        public int bg_down_data;
        public int fg_up_data;
        public int fg_down_data;
        public int bg_up_wifi;
        public int bg_down_wifi;
        public int fg_up_wifi;
        public int fg_down_wifi;

        public DateTime last_logtime;
        public int duplicate=0;

        public bool isSame(StatsModel stats)
        {
            bool result = false;

            //Console.WriteLine("Comparing two records: " + id + " and " + stats.id);

            result = is_interacting == stats.is_interacting && pcy == stats.pcy && cpu == stats.cpu && vss == stats.vss && rss == stats.rss && threads == stats.threads &&
            priority == stats.priority && status.Equals(stats.status) &&
            bg_up_data == stats.bg_up_data && bg_down_data == stats.bg_down_data && fg_up_data == stats.fg_up_data && fg_down_data == stats.fg_down_data
            && bg_up_wifi == stats.bg_up_wifi && bg_down_wifi == stats.bg_down_wifi && fg_up_wifi == stats.fg_up_wifi && fg_down_wifi == stats.fg_down_wifi
            ? true : false;
            return result;

        }

        public static StatsModel readRecord(SqlDataReader dr)
        {
            
            StatsModel currentRecord = new StatsModel();
            currentRecord.id = dr["id"].ToString();
            //reader.GetDateTime(reader.GetOrdinal("Timestamp"));
            currentRecord.logtime = dr.GetDateTime(dr.GetOrdinal("logtime"));
            currentRecord.is_interacting = Convert.ToBoolean(dr["is_interacting"]);
            currentRecord.pcy = Convert.ToInt32(dr["pcy"]);
            currentRecord.cpu = Convert.ToInt32(dr["cpu"]);
            currentRecord.vss = Convert.ToInt32(dr["vss"]);
            currentRecord.rss = Convert.ToInt32(dr["rss"]);
            currentRecord.threads = Convert.ToInt32(dr["threads"]);
            currentRecord.priority = Convert.ToInt32(dr["priority"]);
            currentRecord.status = dr["status"].ToString();
            currentRecord.bg_up_data = Convert.ToInt32(dr["bg_up_data"]) == -1 ? 0 : Convert.ToInt32(dr["bg_up_data"]); ;
            currentRecord.bg_down_data = Convert.ToInt32(dr["bg_down_data"]) == -1 ? 0 : Convert.ToInt32(dr["bg_down_data"]); ;
            currentRecord.fg_up_data = Convert.ToInt32(dr["fg_up_data"]) == -1 ? 0 : Convert.ToInt32(dr["fg_up_data"]);
            currentRecord.fg_down_data = Convert.ToInt32(dr["fg_down_data"]) == -1 ? 0 : Convert.ToInt32(dr["fg_down_data"]); ;
            currentRecord.bg_up_wifi = Convert.ToInt32(dr["bg_up_wifi"]) == -1 ? 0 : Convert.ToInt32(dr["bg_up_wifi"]); ;
            currentRecord.bg_down_wifi = Convert.ToInt32(dr["bg_down_wifi"]) == -1 ? 0 : Convert.ToInt32(dr["bg_down_wifi"]); ;
            currentRecord.fg_up_wifi = Convert.ToInt32(dr["fg_up_wifi"]) == -1 ? 0 : Convert.ToInt32(dr["fg_up_wifi"]); ;
            currentRecord.fg_down_wifi = Convert.ToInt32(dr["fg_down_wifi"]) == -1 ? 0 : Convert.ToInt32(dr["fg_down_wifi"]); ;

            
            //Console.WriteLine("Date Read: " + dt);
            return currentRecord;
        }

        public static int getTimeDiff(StatsModel currentStats, StatsModel nextStats)
        {
            DateTime dtCurrent =currentStats.logtime;
            DateTime dtNext = nextStats.logtime;

            TimeSpan diff = dtNext.Subtract(dtCurrent);
            return 0;
            //nextStats.logtime.d
        }

    }
}
