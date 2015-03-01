package com.katrina.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainUI extends ActionBarActivity {
    private class ModuleAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
        private Context mContext;
        private ArrayList<KatrinaModule> moduleList;

        public ModuleAdapter(Context c){
            moduleList = new ArrayList<KatrinaModule>();
            mContext = c;
        }

        public void addModule(KatrinaModule m){
            moduleList.add(m);
        }

        public int getCount() {
            return moduleList.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.app_icon_ui,null);
            }

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
            }
        }
    }

    public interface Callback {
        public void onEmergency();
    }

    private GridView moduleGridView;
    private Callback cb;
    private ModuleAdapter moduleAdapter;

    private class TESTMOD implements KatrinaModule {
        @Override
        public Drawable getIconImage() {
            return null;
        }

        @Override
        public String getName() {
            return "TEST MOD";
        }

        @Override
        public boolean doAction(Context c) {
            Log.i("KatrinaModule.doAction","TEST");
            return true;
        }

        @Override
        public String getError() {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_ui);

        moduleGridView = (GridView) findViewById(R.id.moduleGridView);
        moduleAdapter = new ModuleAdapter(this);
        moduleGridView.setAdapter(moduleAdapter);
        moduleGridView.setOnItemClickListener(moduleAdapter);

        View v = findViewById(R.id.contact1);
        TextView tV = (TextView)v.findViewById(R.id.cName);
        tV.setText("TEST");

        /*TESTMOD t = new TESTMOD();

        for (int i=0;i<20;i++){
            moduleAdapter.addModule(t);
        }*/

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager manager = getPackageManager();

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            Log.i("List of Apps","Label: " + ri.loadLabel(manager).toString() + "  PackageName: " + ri.activityInfo.packageName.toString());
            ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName.toString(),ri.activityInfo.loadIcon(manager));
            moduleAdapter.addModule(app);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.contact1:
                Log.i("onButtonClick", "C1");
                break;
            case R.id.contact2:
                Log.i("onButtonClick", "C2");
                break;
            case R.id.contact3:
                Log.i("onButtonClick", "C3");
                break;
            case R.id.contact4:
                Log.i("onButtonClick", "C4");
                break;
            case R.id.contact5:
                Log.i("onButtonClick", "C5");
                break;
            case R.id.apps:
                Log.i("onButtonClick", "Apps");
                break;
            case R.id.emergency:
                if (cb != null) cb.onEmergency();
                Log.i("onButtonClick", "Emergency");
                break;
            case R.id.settings:
                Log.i("onButtonClick", "Settings");
                break;
        }
    }

    public void setCallback(Callback cb){ this.cb = cb; }
}