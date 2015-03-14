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

import java.util.ArrayList;

/**
 * Created by alatnet on 3/7/2015.
 */

public class ModuleAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, KatrinaModuleListener {
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
    //private final ArrayList<View> moduleViews;
    private final AlertDialog.Builder aBuild;
    private final LayoutInflater inflater;

    public ModuleAdapter(Context c){
        moduleList = new ArrayList<>();
        //moduleViews = new ArrayList<>();
        mContext = c;
        aBuild = new AlertDialog.Builder(mContext);
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Add a module to the list.
    synchronized public void addModule(KatrinaModule m){
        int position = moduleList.size();
        m.setID(position);
        m.registerKMListener(this);
        //moduleViews.add(createModView(m));
        moduleList.add(new ModuleView(m,createModView(m)));
    }

    //get the number of added modules.
    public int getCount() { return moduleList.size(); }

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
        moduleList.get(position).mod.setID(position);
        return moduleList.get(position).view;
        /*View modView = null;
        if (position < moduleViews.size() && position >= 0 && moduleViews.size() != 0) modView = moduleViews.get(position);

        //create the view
        if (modView != null) {
            return modView;
        }else if (convertView == null && modView == null) {
            convertView = inflater.inflate(R.layout.app_list_button_layout, null);
            moduleViews.put(position,convertView);

            KatrinaModule mod = moduleList.get(position);

            TextView textView = (TextView) convertView.findViewById(R.id.aText);
            textView.setText(mod.getName());

            Drawable iImg = mod.getIconImage();
            if (iImg != null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.aIcon);
                imageView.setImageDrawable(iImg);
            }
        }else if (convertView == null && modView != null) { //there's already a view created, use it.
            convertView = modView;
        }

        return convertView;*/
    }

    //Android stuff.
    public long getItemId(int position) { return 0; }

    //Android stuff. Get a module from position.
    public Object getItem(int position) {
        return moduleList.get(position);
    }

    //Android stuff.
    //Used to execute a module's custom code.
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        KatrinaModule mod = moduleList.get(position).mod;
        if (!mod.doAction(mContext)) {
            aBuild.setMessage(mod.getError())
                    .setTitle(mod.getName() + " Error!");
            //.setPositiveButton("OK",null);

            aBuild.create().show();
        }
    }

    //Android stuff.
    //Used to remove APK apps from the module list.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        KatrinaModule mod = moduleList.get(position).mod;

        if (mod.getModuleType() == KatrinaModule.MOD_TYPE.APP) {
            aBuild.setTitle("Remove " + mod.getName() + " from Home Screen.")
                    .setMessage("Are you sure you wish to remove " + mod.getName() + " from your home screen?")
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moduleList.remove(position);
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
        }

        return false;
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
}
