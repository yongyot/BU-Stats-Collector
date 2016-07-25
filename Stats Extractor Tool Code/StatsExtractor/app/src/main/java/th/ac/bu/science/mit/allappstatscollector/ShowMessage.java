package th.ac.bu.science.mit.allappstatscollector;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Komal on 11/2/2015.
 */
public class ShowMessage {
    public static void message(String text, Context context)
    {
        //Toast toast = Toast.makeText(context, text, );
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
