package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katrina.modules.KatrinaModule;
import com.katrina.modules.KatrinaModuleListener;
import com.katrina.modules.ModuleApp;

import java.util.ArrayList;

/**
 * Created by alatnet on 3/7/2015.
 */
public class ModuleAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, KatrinaModuleListener {

    /**
     * Private Class to contain a KatrinaModule and View associated
     * with it.
     */
    private class ModuleView {
        public final KatrinaModule mod;
        public final View view;

        public ModuleView(KatrinaModule m, View v){
            mod = m;
            view = v;
        }
    }

    private final Context mContext;
    private final ArrayList<ModuleView> moduleList;
    private final ArrayList<ModuleView> appList;
    private final AlertDialog.Builder aBuild;
    private final LayoutInflater inflater;

    public ModuleAdapter(Context c){
        moduleList = new ArrayList<>();
        appList = new ArrayList<>();
        mContext = c;
        aBuild = new AlertDialog.Builder(mContext);
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Add a module to the list.
    synchronized public void addModule(KatrinaModule m){
        int position = moduleList.size();
        m.setID(position);
        m.registerKMListener(this);
        m.initialize(mContext);
        //moduleViews.add(createModView(m));
        switch (m.getModuleType()){
            case MODULE:
                moduleList.add(new ModuleView(m,createModView(m)));
                break;
            //No view attached
            case BACKGROUND:
                break;
            case APP:
            case MISC:
                appList.add(new ModuleView(m,createModView(m)));
                break;
        }
    }

    //get the number of added modules.
    public int getCount() { return moduleList.size()+appList.size(); }

    private View createModView(KatrinaModule mod){
        View convertView = inflater.inflate(R.layout.app_list_button_layout, null);

        TextView textView = (TextView) convertView.findViewById(R.id.aText);
        textView.setText(mod.getName());

        Drawable iImg = mod.getIconImage();
        if (iImg != null) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.aIcon);
            imageView.setImageDrawable(iImg);
        }
        return convertView;
    }

    //Android stuff.
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position >= moduleList.size()) {
            return appList.get(position-moduleList.size()).view;
        } else {
            moduleList.get(position).mod.setID(position);
            return moduleList.get(position).view;
        }
    }

    //Android stuff.
    public long getItemId(int position) { return 0; }

    //Android stuff. Get a module from position.
    public Object getItem(int position) {
        if (position >= moduleList.size()) {
            return appList.get(position-moduleList.size()).mod;
        } else {
            moduleList.get(position).mod.setID(position);
            return moduleList.get(position).mod;
        }
    }

    //Android stuff.
    //Used to execute a module's custom code.
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        KatrinaModule mod = (KatrinaModule)getItem(position); //moduleList.get(position).mod;
        //if (!mod.doAction(mContext)) {
        //    aBuild.setMessage(mod.getError())
        //            .setTitle(mod.getName() + " Error!");
            //.setPositiveButton("OK",null);

        //    aBuild.create().show();
        //}

        if (!mod.onModuleClick(mContext)){
            aBuild.setMessage(mod.getError())
                    .setTitle(mod.getName() + " Error!")
                    .setPositiveButton("", null)
                    .setNegativeButton("", null);

            aBuild.create().show();
        }
    }

    //Android stuff.
    //Used to remove APK apps from the module list.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final KatrinaModule mod = (KatrinaModule)getItem(position); //moduleList.get(position).mod;

        if (mod.getModuleType() == KatrinaModule.MOD_TYPE.APP) {
            aBuild.setTitle("Remove " + mod.getName() + " from Home Screen.")
                    .setMessage("Are you sure you wish to remove " + mod.getName() + " from your home screen?")
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //appList.remove(position);
                            appList.remove(position-moduleList.size());
                            //moduleViews.remove(position);
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No",null);
            aBuild.create().show();
            return true;
        }else{
            if (!mod.onModuleLongClick(mContext)){
                aBuild.setMessage(mod.getError())
                        .setTitle(mod.getName() + " Error!")
                        .setPositiveButton("",null)
                        .setNegativeButton("",null);

                aBuild.create().show();
            }
            return true;
        }
    }

    @Override
    public void changeModuleIconImage(int id, Drawable drawable) {
        View modView = getView(id,null,null);
        ImageView imageView = (ImageView) modView.findViewById(R.id.aIcon);
        imageView.setImageDrawable(drawable);
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() { notifyDataSetChanged(); }
        });
    }

    synchronized public ModuleApp[] getHomeScreenApps(){
        ModuleApp[] ret = new ModuleApp[appList.size()];
        for (int i=0;i<appList.size();i++) ret[i] = (ModuleApp)appList.get(i).mod;
        return ret;
    }

    synchronized public KatrinaModule[] getModules(){
        KatrinaModule[] ret = new KatrinaModule[moduleList.size()];
        for (int i=0;i<moduleList.size();i++) ret[i] = moduleList.get(i).mod;
        return ret;
    }
}
