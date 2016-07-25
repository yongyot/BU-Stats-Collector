using System;
using System.Collections.Generic;
using System.Linq;


/// <summary>
/// Summary description for CacheAppsId
/// </summary>
public class CacheAppsId
{
    public static HashSet<AppsModal> AppsCache = new HashSet<AppsModal>();

    public static void addCache(string packageName, int id)
    {
        AppsCache.Add(new AppsModal(packageName, id));
    }

    public static int getAppIdFromCache(string packageName)
    {
        foreach (AppsModal app in AppsCache)
        {
            if (app.packageName.Equals(packageName))
                return app.appID;
        }
        return -1;
    }

    public static void clearCache()
    {
        AppsCache.Clear();
    }

}