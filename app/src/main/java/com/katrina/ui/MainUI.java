package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.katrina.modules.EmergencyListener;
import com.katrina.modules.KatrinaModule;
import com.katrina.modules.ModuleApp;
import com.katrina.util.Utilities;

import java.util.ArrayList;
import java.util.List;

//TODO Home and Back button events. Home and Back must bring screen back to home screen when in app list.

/**
 *
 */
public class MainUI extends Activity implements View.OnClickListener, View.OnLongClickListener, EmergencyListener {

    private static final String TAG = "MainUI";

    private GridView moduleGridView;  //Grid list of modules and APK apps on home screen.
    private ModuleAdapter moduleAdapter; //adapter to manage the modules displayed on the home screen.
    private ListView appsListView; //list view of applications installed on phone
    private AppsAdapter appsAdapter; //adapter to manage the list of applications
    private AlertDialog.Builder aBuild; //DIALOG BOXES EVARYWHERE!!!!
    private ContactInfo[] contactInfo = new ContactInfo[5]; //the 5 top contacts.
    private boolean firstInit = true; //check to see if this is the first time Katrina has started.

    private View mainUI = null; //home screen
    private View appUI = null; //app list

    public MainUI(){
        super();
        for(int i=0;i<contactInfo.length;i++) contactInfo[i] = new ContactInfo(); //setup the contact info.  Errors out if we dont do this.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //save state system?
        /*if (savedInstanceState != null){
        }*/

        LayoutInflater layoutInflater = getLayoutInflater();

        mainUI = layoutInflater.inflate(R.layout.activity_main_ui,null);
        appUI = layoutInflater.inflate(R.layout.activity_main_ui_appslist,null);

        setContentView(mainUI);

        aBuild = new AlertDialog.Builder(this);

        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file),Context.MODE_PRIVATE);

        //Check to see if this is the first time Katrina has started.
        firstInit = prefs.getBoolean("firstInit",true);

        if (firstInit) {
            AlertDialog.Builder aBuild = new AlertDialog.Builder(this);
            aBuild.setTitle("Setup")
                    .setMessage("Welcome to Katrina.\nWe would like to set up your main contacts that you will use either for emergency or for normal calls.")
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainUI.this,ContactsUI.class);
                            Bundle b = new Bundle();
                            b.putInt("maxSelection",5);
                            i.putExtra("data",b);
                            startActivityForResult(i, R.id.ContactSelection);
                        }
                    });
            aBuild.create().show();

            SharedPreferences.Editor e = prefs.edit();
            e.putBoolean("firstInit",false);
            e.commit();
            firstInit = false;
        }else{
            clearContacts();
            loadContacts(prefs);
        }

        //setup the module list on the home screen.
        moduleGridView = (GridView) mainUI.findViewById(R.id.moduleGridView);
        moduleAdapter = new ModuleAdapter(this);
        moduleGridView.setAdapter(moduleAdapter);
        moduleGridView.setOnItemClickListener(moduleAdapter);
        moduleGridView.setOnItemLongClickListener(moduleAdapter);

        //setup the app list.
        appsListView = (ListView) appUI.findViewById(R.id.appListView);
        appsAdapter = new AppsAdapter(this,moduleAdapter);
        appsListView.setAdapter(appsAdapter);
        appsListView.setOnItemClickListener(appsAdapter);
        appsListView.setOnItemLongClickListener(appsAdapter);

        //set contact's on long click listener.
        View contact1 = mainUI.findViewById(R.id.contact1);
        View contact2 = mainUI.findViewById(R.id.contact2);
        View contact3 = mainUI.findViewById(R.id.contact3);
        View contact4 = mainUI.findViewById(R.id.contact4);
        View contact5 = mainUI.findViewById(R.id.contact5);
        contact1.setOnLongClickListener(this);
        contact2.setOnLongClickListener(this);
        contact3.setOnLongClickListener(this);
        contact4.setOnLongClickListener(this);
        contact5.setOnLongClickListener(this);

        //View setContact = mainUI.findViewById(R.id.action_set_contacts);
        //setContact.setOnLongClickListener(this);

        //Button emergency = (Button)mainUI.findViewById(R.id.action_emergency);
        //emergency.setOnLongClickListener(this);

        //load modules.
        new ModSyncTask(this.getSharedPreferences(getString(R.string.module_file),Context.MODE_PRIVATE)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //add apps to app list
        new AppSyncTask(this,loadHomescreen(this.getSharedPreferences(getString(R.string.homescreen_file),Context.MODE_PRIVATE))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStop(){
        super.onStop();

        if (!firstInit) {
            saveContacts(this.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE));
        }else{
            firstInit = false;
        }
        //TODO save module state (i.e. isActive setActive)

        saveHomescreen(this.getSharedPreferences(getString(R.string.homescreen_file),Context.MODE_PRIVATE));
        saveModuleStates(this.getSharedPreferences(getString(R.string.module_file), Context.MODE_PRIVATE));
    }

    //i have no idea what im doing...
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
                setContentView(mainUI);
                return true;
        }
        return false;
    }

    //works ish...
    @Override
    public void onBackPressed(){
        setContentView(mainUI);
    }

    //works only on double tap...
    @Override
    protected void onUserLeaveHint(){
        setContentView(mainUI);
    }

    //Android stuff.
    //Options context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_ui, menu);
        return true;
    }

    //Android stuff.
    //Options context menu stuff
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            //case R.id.action_settings:
            //    return true;
            case R.id.action_set_contacts:
                startContactActivity();
                return true;
            case R.id.action_view_apps:
                setContentView(appUI);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //when parts of the ui are clicked on.
    //only effects those that do not use an adapter.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //case R.id.apps:
            //    setContentView(appUI);
            //    break;
            case R.id.backToMain:
                setContentView(mainUI);
                break;
        }
    }

    //using this to call a contact.
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.contact1:
                contactInfo[0].call(this);
                break;
            case R.id.contact2:
                contactInfo[1].call(this);
                break;
            case R.id.contact3:
                contactInfo[2].call(this);
                break;
            case R.id.contact4:
                contactInfo[3].call(this);
                break;
            case R.id.contact5:
                contactInfo[4].call(this);
                break;
            case R.id.action_set_contacts:
                startContactActivity();
                break;

        }
        return false;
    }

    //used to receive data from separate activities started by this activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Log.i("onActivityResult","requestCode: " + requestCode);
        Log.i("onActivityResult","resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case R.id.ContactSelection:
                    firstInit = firstInit?false:true;
                    clearContacts();
                    Bundle contacts = data.getBundleExtra("Contacts");
                    if (contacts != null){
                        int currContact = 1;
                        for (String key : contacts.keySet()){
                            final View contact;
                            TextView cName;
                            TextView cPhone;
                            ImageView cImg;
                            ImageView cPhoneIcon;
                            Bundle contactData = contacts.getBundle(key);
                            switch(currContact){
                                case 1:
                                    contact=mainUI.findViewById(R.id.contact1);
                                    Log.i("onActivityResult","Setting contact 1");
                                    break;
                                case 2:
                                    contact=mainUI.findViewById(R.id.contact2);
                                    Log.i("onActivityResult","Setting contact 2");
                                    break;
                                case 3:
                                    contact=mainUI.findViewById(R.id.contact3);
                                    Log.i("onActivityResult","Setting contact 3");
                                    break;
                                case 4:
                                    contact=mainUI.findViewById(R.id.contact4);
                                    Log.i("onActivityResult","Setting contact 4");
                                    break;
                                case 5:
                                    contact=mainUI.findViewById(R.id.contact5);
                                    Log.i("onActivityResult","Setting contact 5");
                                    break;
                                default:
                                    contact = null;
                                    break;
                            }
                            if(contact != null && contactData.getLong("id") == -1)
                            {
                                contact.setVisibility(View.GONE);
                            }
                            else if (contact != null) {
                                contact.setVisibility(View.VISIBLE);
                                cName = (TextView) contact.findViewById(R.id.cName);
                                cPhone = (TextView) contact.findViewById(R.id.cPhone);
                                cImg = (ImageView) contact.findViewById(R.id.cImg);
                                cPhoneIcon = (ImageView) contact.findViewById(R.id.phone_button);
                                String type = contactData.getString("Type");
                                cName.setText(contactData.getString("Name"));


                                cPhoneIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onLongClick(contact);
                                    }
                                });

                                if (!type.isEmpty()) {
                                    cPhone.setText(contactData.getString("Number") + " (" + type + ")");
                                }
                                else {
                                    cPhone.setText(contactData.getString("Number"));
                                }

                                String photo = contactData.getString("Photo");

                                if (photo.equals("null"))
                                {
                                    cImg.setImageResource(R.mipmap.unknown);
                                }
                                else
                                {
                                    cImg.setImageURI(Uri.parse(photo));
                                }

                                contactInfo[currContact-1].name = contactData.getString("Name");
                                contactInfo[currContact-1].phone = contactData.getString("Number");
                                contactInfo[currContact-1].type = type;

                                if (photo.equals("null"))
                                {
                                    contactInfo[currContact - 1].photo = null;
                                }
                                else
                                {
                                    contactInfo[currContact - 1].photo = Uri.parse(photo);
                                }
                            }

                            currContact++;
                        }
                    }
                    break;
            }
            saveContacts(this.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE));
        }
    }

    @Override
    public void onEmergency() {
        if (Utilities.DEBUG){
            Toast.makeText(this,"DEBUG! onEmergency Executed.",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("MainUI","EMERGENCY MODE ACTIVATED");
        //TODO Text all numbers with location. Call emergency services.

        for (ContactInfo c : contactInfo) c.text("EMERGENCY!");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:911"));
        startActivity(callIntent);
    }

    //Custom Code
    private void startContactActivity(){
        Intent intent = new Intent(MainUI.this,ContactsUI.class);

        Bundle dataBundle = new Bundle();
        Bundle contactsBundle = new Bundle();
        dataBundle.putInt("maxSelection", 5);

        for (int i=0;i<contactInfo.length;i++){
            if(contactInfo[i].id == null) {
                continue;
            }
            Bundle b = new Bundle();
            b.putLong("id", contactInfo[i].id);
            b.putString("phone", contactInfo[i].phone);
            contactsBundle.putBundle(""+i,b);
        }
        dataBundle.putBundle("contactSelected",contactsBundle);

        intent.putExtra("data",dataBundle);
        startActivityForResult(intent, R.id.ContactSelection);
    }

    private void loadContacts(SharedPreferences prefs){
        for (int i=0;i<contactInfo.length;i++){
            final View contact;
            TextView cName;
            TextView cPhone;
            ImageView cImg;
            ImageView cPhoneButton;

            contactInfo[i].name = prefs.getString("Contact"+i+"-name","Set Contact");
            contactInfo[i].phone = prefs.getString("Contact"+i+"-number","(777)-777-7777");
            contactInfo[i].type = prefs.getString("Contact"+i+"-type","");
            contactInfo[i].photo = Utilities.checkUriExists(this, Uri.parse(prefs.getString("Contact" + i + "-photo", "null")));
            contactInfo[i].id = prefs.getLong("Contact" + i + "-id", (long) -1);

            switch(i+1){
                case 1:
                    contact=mainUI.findViewById(R.id.contact1);
                    break;
                case 2:
                    contact=mainUI.findViewById(R.id.contact2);
                    break;
                case 3:
                    contact=mainUI.findViewById(R.id.contact3);
                    break;
                case 4:
                    contact=mainUI.findViewById(R.id.contact4);
                    break;
                case 5:
                    contact=mainUI.findViewById(R.id.contact5);
                    break;
                default:
                    contact = null;
                    break;
            }
            if(contact != null && contactInfo[i].type.isEmpty())
            {
                contact.setVisibility(View.GONE);
            }
            if (contact != null) {
                cName = (TextView) contact.findViewById(R.id.cName);
                cPhone = (TextView) contact.findViewById(R.id.cPhone);
                cImg = (ImageView) contact.findViewById(R.id.cImg);
                cPhoneButton = (ImageView) contact.findViewById(R.id.phone_button);

                cPhoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //OnLongClick method has code for calling user.
                        onLongClick(contact);
                    }
                });
                if (contactInfo[i].name.isEmpty()){
                    contactInfo[i].name = "";
                    contactInfo[i].phone = "";
                    contactInfo[i].type = "";
                    contactInfo[i].photo = null;
                    contactInfo[i].id = (long)-1;

                    cName.setText("Set Contact");
                    cPhone.setText("(777)-777-7777");
                    cImg.setImageResource(R.mipmap.unknown);
                }else {
                    cName.setText(contactInfo[i].name);
                    if (!contactInfo[i].type.isEmpty())
                        cPhone.setText(contactInfo[i].phone + " (" + contactInfo[i].type + ")");
                    else cPhone.setText(contactInfo[i].phone);

                    if (contactInfo[i].photo != null) cImg.setImageURI(contactInfo[i].photo);
                    else cImg.setImageResource(R.mipmap.unknown);
                }
            }
        }
    }

    private void saveContacts(SharedPreferences prefs){
        SharedPreferences.Editor e = prefs.edit();

        //save contacts
        for (int i = 0; i < contactInfo.length; i++) {

            //When app tries to save default contacts, their ids will be null
            //and cause the app to throw an exception without this code
            if(contactInfo[i].id == null)
            {
                continue;
            }
            e.putString("Contact" + i + "-name", contactInfo[i].name);
            e.putString("Contact" + i + "-number", contactInfo[i].phone);
            e.putString("Contact" + i + "-type", contactInfo[i].type);
            e.putLong("Contact" + i + "-id", contactInfo[i].id);
            if (contactInfo[i].photo != null) e.putString("Contact" + i + "-photo", contactInfo[i].photo.toString());
            else e.putString("Contact" + i + "-photo", "null");
        }

        e.apply();
    }

    private void clearContact(int slot){
        contactInfo[slot].phone = "";
        contactInfo[slot].name = "";
        contactInfo[slot].type = "";
        contactInfo[slot].photo = null;
        contactInfo[slot].id = (long)-1;

        View contact=null;
        TextView cName;
        TextView cPhone;
        ImageView cImg;

        switch(slot+1){
            case 1:
                contact=mainUI.findViewById(R.id.contact1);
                break;
            case 2:
                contact=mainUI.findViewById(R.id.contact2);
                break;
            case 3:
                contact=mainUI.findViewById(R.id.contact3);
                break;
            case 4:
                contact=mainUI.findViewById(R.id.contact4);
                break;
            case 5:
                contact=mainUI.findViewById(R.id.contact5);
                break;
        }

        if (contact != null) {
            cName = (TextView) contact.findViewById(R.id.cName);
            cPhone = (TextView) contact.findViewById(R.id.cPhone);
            cImg = (ImageView) contact.findViewById(R.id.cImg);

            cName.setText("Set Contact");
            cPhone.setText("(777)-777-7777");
            cImg.setImageResource(R.mipmap.unknown);
        }
    }

    private void clearContacts(){ for (int i=0;i<contactInfo.length;i++) clearContact(i); }

    private ArrayList<String> loadHomescreen(SharedPreferences prefs){
        Log.i("loadHomescreen","LOADING HOMESCREEN");
        int numApps = prefs.getInt("NumApps",0);
        if (numApps == 0) return null;

        ArrayList<String> ret = new ArrayList<>();

        for (int i=0;i<numApps;i++){
            String s = prefs.getString("" + i, "");
            Log.i("loadHomescreen","UID: " + s);
            ret.add(s) ;
        }

        return ret;
    }

    private void saveHomescreen(SharedPreferences prefs){
        Log.i("saveHomescreen","SAVING HOMESCREEN");
        SharedPreferences.Editor e = prefs.edit();
        e.clear();

        ModuleApp[] apps = moduleAdapter.getHomeScreenApps();
        e.putInt("NumApps", apps.length);

        for (int i=0;i<apps.length;i++) {
            String s = apps[i].getUniqueID();
            Log.i("saveHomescreen","ID: "+ i + "  UID: " + s);
            e.putString("" + i, s);
        }

        e.commit();
    }

    private void saveModuleStates(SharedPreferences prefs){
        KatrinaModule[] mods = moduleAdapter.getModules();

        SharedPreferences.Editor e = prefs.edit();
        e.clear();

        for (KatrinaModule mod : mods){
            Log.i("saveModuleStates","UID: " + mod.getUniqueID() + "  State: " + mod.isActive());
            e.putBoolean(mod.getUniqueID(),mod.isActive());
        }

        e.commit();
    }

    /**
     * AsyncTask to obtain the list of Katrina Modules. Modules
     * are set Active if set in preferences, and modules are given
     * an emergencyListener instance.
     */
    private class ModSyncTask extends AsyncTask<Void,Void,Void> {
        private final SharedPreferences prefs;

        public ModSyncTask(SharedPreferences prefs){
            this.prefs = prefs;
        }

        @Override
        protected Void doInBackground(Void... params) {

            String[] modules = getResources().getStringArray(R.array.Modules); //get module list
            try {
                for (String module : modules) { //for each module
                    Class cls = Class.forName(module); //get the class from the module list
                    KatrinaModule moduleInstance = (KatrinaModule) cls.newInstance(); //create a new instance (i.e. new Class)
                    //moduleInstance.registerKMListener(moduleAdapter); //register KMListener
                    moduleInstance.setActive(prefs.getBoolean(moduleInstance.getUniqueID(),false));
                    moduleInstance.registerEmergencyListener(MainUI.this); //register EmergencyListener
                    moduleAdapter.addModule(moduleInstance); //add module to home screen
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.i("ClassNotFoundException",e.toString());
            } catch (InstantiationException e) {
                e.printStackTrace();
                Log.i("InstantiationException",e.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.i("IllegalAccessException",e.toString());
            }

            return null;
        }
    }

    private class AppSyncTask extends AsyncTask<Void,Integer,Void> {
        ProgressDialog progressDialog;
        Context mContext;
        ArrayList<String> homescreen;

        public AppSyncTask(Context c, ArrayList<String> homescreen){
            mContext = c;
            this.homescreen = homescreen;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading List of Applications...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            PackageManager manager = getPackageManager();

            List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

            int maxSize = availableActivities.size();
            int currPos = 0;
            publishProgress(currPos,maxSize);

            for(ResolveInfo ri:availableActivities){
                ModuleApp app = new ModuleApp(ri.loadLabel(manager).toString(),ri.activityInfo.packageName,ri.activityInfo.loadIcon(manager));

                boolean addedToHS = false;

                if (homescreen != null) if (!homescreen.isEmpty())
                    for (int iHS=0;iHS<homescreen.size();iHS++) {
                        String appS = homescreen.get(iHS);
                        if (app.getUniqueID().equals(appS)) {
                            appsAdapter.addModule(app, true);
                            addedToHS = true;
                            homescreen.remove(iHS);
                            Log.i("Homescreen Load", "Adding " + appS + "to HS.");
                            break;
                        }
                    }
                if (!addedToHS) appsAdapter.addModule(app,false);

                currPos++;
                publishProgress(currPos,maxSize);

                if (isCancelled()) break;
            }

            publishProgress(maxSize,maxSize);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) progressDialog.dismiss();
            appsAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
            progressDialog.setMax(values[1]);
        }
    }
}