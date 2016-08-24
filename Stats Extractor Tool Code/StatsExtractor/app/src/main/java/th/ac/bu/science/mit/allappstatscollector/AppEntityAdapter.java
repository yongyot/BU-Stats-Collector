package th.ac.bu.science.mit.allappstatscollector;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppEntityAdapter extends ArrayAdapter<ApplicationInfo> {
    List<ApplicationInfo> appsList = null;
    Context context;
    PackageManager packageManager;
    int layoutResourceId;

    public AppEntityAdapter(Context context, int layoutResourceId,
                            List<ApplicationInfo> appsList) {
        super(context, layoutResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        this.layoutResourceId = layoutResourceId;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layoutResourceId, null);
        }

        ApplicationInfo applicationInfo = appsList.get(position);
        if (null != applicationInfo) {
            TextView appName = (TextView) view.findViewById(R.id.textAppName);
            TextView packageName = (TextView) view.findViewById(R.id.textAppInfo);
            ImageView iconview = (ImageView) view.findViewById(R.id.appIcon);

            appName.setText(applicationInfo.loadLabel(packageManager));
            packageName.setText(applicationInfo.packageName);
            iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        return view;
    }
}
