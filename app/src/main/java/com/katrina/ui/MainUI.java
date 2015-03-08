package com.katrina.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

public class MainUI extends Activity implements View.OnClickListener /*,AdapterView.OnItemClickListener*/ {
    public interface Callback {
        public void onEmergency();
    }

    private GridView moduleGridView;
    private ModuleAdapter moduleAdapter;
    private ListView contactsListView;
    private ContactsAdapter contactsAdapter;

    private Callback cb;

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
            Log.i("TESTMOD.doAction","TEST");
            return false;
        }

        @Override
        public String getError() {
            return "TEST ERROR";
        }

        @Override
        public MOD_TYPE getModuleType() { return MOD_TYPE.MODULE; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_ui);

        moduleGridView = (GridView) findViewById(R.id.moduleGridView);
        moduleAdapter = new ModuleAdapter(this);
        moduleGridView.setAdapter(moduleAdapter);
        moduleGridView.setOnItemClickListener(moduleAdapter);
        moduleGridView.setOnItemLongClickListener(moduleAdapter);

        ModuleFactory.registerMainUI(this);

        /*contactsListView = (ListView) findViewById(R.id.contactsListView);
        contactsAdapter = new ContactsAdapter(this);
        contactsListView.setAdapter(contactsAdapter);
        contactsListView.setOnItemClickListener(contactsAdapter);*/

        /*View v = findViewById(R.id.contact1);
        TextView tV = (TextView)v.findViewById(R.id.cName);
        tV.setText("TEST");*/

        /*TESTMOD t = new TESTMOD();

        for (int i=0;i<20;i++){ moduleAdapter.addModule(t); }*/

        /*ContactInfo c1 = new ContactInfo();
        c1.name = "Test";
        c1.phone = "(777)-777-7777";
        c1.photo = null;

        contactsAdapter.add(c1);*/

        /*Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager manager = getPackageManager();

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            Log.i("List of Apps","Label: " + ri.loadLabel(manager).toString() + "  PackageName: " + ri.activityInfo.packageName.toString());
            ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName.toString(),ri.activityInfo.loadIcon(manager));
            moduleAdapter.addModule(app);
        }*/

        /*Thread appListThread = new Thread(){
            public void run(){
                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                PackageManager manager = getPackageManager();

                List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
                for(ResolveInfo ri:availableActivities){
                    Log.i("List of Apps","Label: " + ri.loadLabel(manager).toString() + "  PackageName: " + ri.activityInfo.packageName.toString());
                    ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName.toString(),ri.activityInfo.loadIcon(manager));
                    moduleAdapter.addModule(app);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { moduleAdapter.notifyDataSetChanged(); }
                });
            }
        };
        appListThread.start();*/
    }

    public void addModule(KatrinaModule mod){
        moduleAdapter.addModule(mod);
        runOnUiThread(new Runnable() {
            @Override
            public void run() { moduleAdapter.notifyDataSetChanged(); }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.contact1:
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
                break;*/
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