package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katrina.modules.ModuleApp;

import java.util.ArrayList;

/**
 * Created by alatnet on 3/13/2015.
 */
public class AppsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private class ModuleView {
        public final ModuleApp app;
        public final View view;

        public ModuleView(ModuleApp a, View v){
            app = a;
            view = v;
        }
    }
    private final ModuleAdapter moduleAdapter;
    private final Context mContext;
    private final ArrayList<ModuleView> appList;
    //private final ArrayList<View> appViews;
    private final AlertDialog.Builder aBuild;
    private final LayoutInflater inflater;

    public AppsAdapter(Context c, ModuleAdapter m){
        moduleAdapter = m;
        appList = new ArrayList<>();
        //appViews = new ArrayList<>();
        mContext = c;
        aBuild = new AlertDialog.Builder(mContext);
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    synchronized public void addModule(ModuleApp m){
        //appViews.add(createModView(m));
        appList.add(new ModuleView(m,createModView(m)));
    }

    private View createModView(ModuleApp mod){
        View convertView = inflater.inflate(R.layout.app_list_element_button_layout, null);

        ImageView img = (ImageView)convertView.findViewById(R.id.aImg);
        TextView txt = (TextView)convertView.findViewById(R.id.aName);

        img.setImageDrawable(mod.getIconImage());
        txt.setText(mod.getName());

        return convertView;
    }

    @Override
    public int getCount() { return appList.size(); }

    @Override
    public Object getItem(int position) { return appList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return appList.get(position).view; }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        appList.get(position).app.doAction(mContext);
        //appList.get(position).app.onModuleClick(mContext);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final ModuleApp mod = appList.get(position).app;
        aBuild.setTitle("Add to Home Screen")
                .setMessage("Do you wish to add " + mod.getName() + " to your Home Screen?")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moduleAdapter.addModule(mod);
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() { moduleAdapter.notifyDataSetChanged(); }
                        });
                    }
                });
        aBuild.create().show();
        return false;
    }
}
