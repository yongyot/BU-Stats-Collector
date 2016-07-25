using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data.SqlClient;
using System.Text;

namespace StatsDataOptimizer
{
    class DBManager
    {
        private SqlConnection con;
        public static string connectionString = "Data Source=BU-MIT-HP;Initial Catalog=BUAntivirusStats;Integrated Security=True";


        public DBManager()
        {
            con = new SqlConnection(connectionString);
        }

        public List<string> getAppList()
        {
            List<string> apps = new List<string>();

            string query = "select * from applications";

            con.Open();
            SqlCommand command = new SqlCommand(query, con);
            try
            {
                SqlDataReader dr = command.ExecuteReader();
                string appId;
                while (dr.Read())
                {
                    appId = dr["application_id"].ToString();
                    apps.Add(appId);
                }
                con.Close();
            }
            catch (Exception Ex)
            {
                con.Close();
                return null;

            }
            return apps;
        }

        public void optimize()
        {
            List<string> apps = getAppList();

            Logger.writeLog("Optimzing Database...");
            int totalDeleted = 0;
            int totalApps=0;
            
         
            foreach (string app in apps)
            {
            
            
                int deletedMain = optimize_app(true, app);    
                int deletedSub =  optimize_app(false, app);
                totalDeleted = totalDeleted + deletedMain+deletedSub;
                
                if (deletedMain > 0|| deletedSub>0)
                    totalApps++;
            }
            
            Logger.writeLog("\nReport: \nTotal apps optimized: "+totalApps+"\nTotal duplicate records removed: "+totalDeleted+"\nEnd of report.\n");
            Logger.writeLine();
        }


        private List<StatsModel> updateUpdateList(List<StatsModel> list, StatsModel baseStats)
        {
            for (int i = 0; i < list.Count; i++)
            {
                if (list[i].id == baseStats.id)
                    list.RemoveAt(i);
            }
            list.Add(baseStats);
            return list;

        }
        private int deleteRecord(List<string> keyList)
        {
            if (keyList.Count == 0)
                return 0;
            
            StringBuilder queries=new StringBuilder();

            foreach(string key in keyList)
                 queries.Append("delete from stats where id="+key+"\n");

            con.Open();
            SqlCommand command = new SqlCommand(queries.ToString(), con);
            int affected = command.ExecuteNonQuery();
            con.Close();
            return affected;
        }

        private int updateRecords(List<StatsModel> data)
        {
            if (data.Count == 0)
                return 0;

            StringBuilder query = new StringBuilder();

            foreach (StatsModel stats in data)
                query.Append("update stats set end_logtime='" + stats.last_logtime.ToString("MM/dd/yyyy hh:mm:ss.fff tt") + "', repeat=" + stats.duplicate + "where id=" + stats.id + "\n");

            int affected = 0;
            try
            {
                con.Open();
                SqlCommand command = new SqlCommand(query.ToString(), con);
                affected = command.ExecuteNonQuery();
                con.Close();
            }
            catch (Exception ex)
            {
                Logger.writeLog("Error Occured while updating records. Details: " + ex.ToString());
            }
            con.Close();
            return affected;
        }

        private int optimize_app(bool isMainProcess, string applicationId)
        {
            string query = "select * from stats where application_id=@application_id and is_main_process=@is_main_process order by logtime";
            con.Open();
            SqlCommand command = new SqlCommand(query, con);
            command.Parameters.AddWithValue("application_id", applicationId);//8444
            command.Parameters.AddWithValue("is_main_process", isMainProcess);

            List<string> duplicateList = new List<string>();
            List<StatsModel> updateList = new List<StatsModel>();

            try
            {
                SqlDataReader dr = command.ExecuteReader();

                StatsModel baseRecord = new StatsModel();
                StatsModel currentRecord = new StatsModel();

                if (dr.Read())
                    baseRecord = StatsModel.readRecord(dr);     //read the first record as the base record and compare it with next records.
                else
                {
                    con.Close();
                    return 0;
                }

                currentRecord = baseRecord;

                while (dr.Read())
                {
                    StatsModel nextRecord = StatsModel.readRecord(dr);                    //compare base record with current record.

                    int diff = StatsModel.getTimeDiff(currentRecord, nextRecord);
                    currentRecord = nextRecord;

                    if (baseRecord.isSame(nextRecord) && diff <= 310)   //next record is duplicate here... 310 is maximum log time difference is allowed.
                    {
                        duplicateList.Add(nextRecord.id);
                        baseRecord.duplicate++;
                        baseRecord.last_logtime = nextRecord.logtime;
                        updateList = updateUpdateList(updateList, baseRecord);
                    }
                    else
                    {
                        baseRecord = nextRecord;
                    }
                }

                con.Close();
            }
            catch (Exception ex)
            {
                con.Close();
                Logger.writeLog(ex.ToString());
            }

            int updated = updateRecords(updateList);

            int deleted  =  deleteRecord(duplicateList);

            if (duplicateList.Count > 0)
                Logger.writeLog("APP_ID:\t" + applicationId + "Optimized. MainProcess:"+isMainProcess+ "\tDuplicate Removed:  " + deleted + ".\tUpdated: " + updated);

            return deleted;
        }
    }
}
