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

import java.util.ArrayList;

/**
 * Created by alatnet on 3/7/2015.
 */

public class ModuleAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final Context mContext;
    private final ArrayList<KatrinaModule> moduleList;
    private final AlertDialog.Builder aBuild;
    private final LayoutInflater inflater;

    public ModuleAdapter(Context c){
        moduleList = new ArrayList<>();
        mContext = c;
        aBuild = new AlertDialog.Builder(mContext);
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addModule(KatrinaModule m){
        moduleList.add(m);
    }

    public int getCount() { return moduleList.size(); }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inflater.inflate(R.layout.app_icon_ui, null);

        KatrinaModule mod = moduleList.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.aText);
        textView.setText(mod.getName());

        Drawable iImg = mod.getIconImage();
        if (iImg != null) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.aIcon);
            imageView.setImageDrawable(iImg);
        }

        return convertView;
    }

    public long getItemId(int position) { return 0; }

    public Object getItem(int position) {
        return moduleList.get(position);
    }

    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        KatrinaModule mod = moduleList.get(position);
        if (mod.doAction(mContext)) {
            Drawable iImg = mod.getIconImage();
            if (iImg != null) {
                ImageView imageView = (ImageView) v.findViewById(R.id.aIcon);
                if (imageView != null) imageView.setImageDrawable(iImg);
            }
        }else {
            aBuild.setMessage(mod.getError())
                    .setTitle(mod.getName() + " Error!");
            //.setPositiveButton("OK",null);

            aBuild.create().show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        KatrinaModule mod = moduleList.get(position);

        if (mod.getModuleType() == KatrinaModule.MOD_TYPE.APP) {
            aBuild.setTitle("Remove " + mod.getName() + " from Home Screen.")
                    .setMessage("Are you sure you wish to remove " + mod.getName() + " from your home screen?")
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moduleList.remove(position);
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() { notifyDataSetChanged(); }
                            });
                        }
                    })
                    .setNegativeButton("No",null);
            return true;
        }

        return false;
    }
}
