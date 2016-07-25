using System;
using System.Collections.Generic;
using System.Linq;


/// <summary>
/// Summary description for AppsModal
/// </summary>
public class AppsModal
{
    public int appID;
    public string packageName;

    public AppsModal(string pack, int id)
    {
        appID = id;
        packageName = pack;
        //
        // TODO: Add constructor logic here
        //
    }
}