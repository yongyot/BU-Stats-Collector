using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

public partial class register : System.Web.UI.Page
{
    protected void Page_Load(object sender, EventArgs e)
    {

    }
    protected void btn_register_Click(object sender, EventArgs e)
    {
        try
        {
            string d = Request.Form["confirm_value"];
            if (d == "Yes")
            {
                BUEntities obj_BUEntities = new BUEntities();
                string id_string = ID.Text;
                bool havedata = obj_BUEntities.test_user.Any(a => a.ID_String.Equals(id_string));
                if (havedata)
                {
                    Alert(id_string + " มีอยู่ในระบบแล้ว", sender as Button);
                }
                else
                {
                    test_user obj_test_user = new test_user();
                    obj_test_user.ID_String = id_string;
                    obj_test_user.PaymentType = rblpaymenttype.SelectedValue;
                    obj_test_user.Remark = txt_Remark.Text;
                    obj_test_user.Android_Version = txt_version.Text;
                    obj_test_user.CreatedOn = DateTime.Now;
                    obj_BUEntities.test_user.Add(obj_test_user);
                    obj_BUEntities.SaveChanges();



                    Random rnd = new Random();
                    int month = rnd.Next(1, 13); // creates a number between 1 and 12
                    ScriptManager.RegisterClientScriptBlock(sender as Button, this.GetType(), "scr_" + month.ToString(), "alert('ลงทะเบียนสำเร็จ...');document.location ='index.html';", true);
                }

            }

        }
        catch (Exception ex)
        {
            Alert(ex.Message, sender as Button);

        }
    }
    protected void Alert(string in_message, Control in_control)
    {
        Random rnd = new Random();
        int month = rnd.Next(1, 13); // creates a number between 1 and 12
        ScriptManager.RegisterClientScriptBlock(in_control, this.GetType(), "scr_" + month.ToString(), "alert('" + in_message + "')", true);

    }
}