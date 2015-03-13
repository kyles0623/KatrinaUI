package com.katrina.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alatnet on 3/7/2015.
 */
public class ContactsAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
    private class ContactChild {
        boolean selected = false;
        String number;
        int type;
        View childView;
        CheckBox checkBox;
        int pos;
    }
    private class ContactGroup {
        String name;
        ArrayList<ContactChild> numbers = new ArrayList<>();
        View groupView;
        int pos;
    }

    private final Context mContext;
    private final LayoutInflater inflater;
    private final AlertDialog.Builder aBuild;
    private ArrayList<ContactGroup> contactList;
    private int maxSelection = -1;
    private int numSelected = 0;

    public ContactsAdapter(Context context) {
        this.mContext = context;
        aBuild = new AlertDialog.Builder(mContext);
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contactList = new ArrayList<>();
        new ContactAsyncTask(mContext).execute();
    }

    public ContactsAdapter(Context context, int maxSelection) {
        this(context);
        this.maxSelection = maxSelection;
    }

    @Override
    public int getGroupCount() { return contactList.size(); }

    @Override
    public int getChildrenCount(int groupPosition) { return contactList.get(groupPosition).numbers.size(); }

    @Override
    public Object getGroup(int groupPosition) { return contactList.get(groupPosition).name; }

    @Override
    public Object getChild(int groupPosition, int childPosition) { return contactList.get(groupPosition).numbers.get(childPosition).number; }

    @Override
    public long getGroupId(int groupPosition) { return groupPosition; }

    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) { return contactList.get(groupPosition).groupView; }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ContactChild child = contactList.get(groupPosition).numbers.get(childPosition);
        child.checkBox.setSelected(child.selected);
        return child.childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }

    @Override
    public boolean areAllItemsEnabled() { return true; }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        ContactChild child = contactList.get(groupPosition).numbers.get(childPosition);
        child.selected = !child.selected;

        if (child.selected) numSelected++;
        else numSelected--;
        if (numSelected <= 0) numSelected = 0;

        if (maxSelection != -1) {
            if (numSelected >= maxSelection+1) {
                aBuild.setTitle("Maximum Selection Exceeded.")
                        .setMessage("The maximum number of contacts you can select is "+maxSelection)
                        .setPositiveButton("OK",null);
                aBuild.create().show();

                child.selected = !child.selected;
                if (child.selected) numSelected++;
                else numSelected--;
                if (numSelected <= 0) numSelected = 0;
                return false;
            }
        }

        child.checkBox.setChecked(child.selected);
        Log.i("onChildClick", "CLICKED g:" + groupPosition + "  c:" + childPosition + "  s:"+child.selected + "  cb:"+child.checkBox.isChecked());
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        return false;
    }

    private View createGroupView(Uri photo, String name){
        View view = inflater.inflate(R.layout.contact_ui_group_element_layout,null);
        TextView textView = (TextView)view.findViewById(R.id.cName);
        ImageView imageView = (ImageView)view.findViewById(R.id.cImg);
        textView.setText(name);
        /*if (photo != null) imageView.setImageURI(photo);
        else */imageView.setImageResource(R.mipmap.unknown);
        return view;
    }

    private View createChildView(int type, String number){
        View view = inflater.inflate(R.layout.contact_ui_child_element_layout,null);
        TextView typeView = (TextView)view.findViewById(R.id.cType);
        TextView numberView = (TextView)view.findViewById(R.id.cNumber);
        typeView.setText(getPhoneType(type));
        numberView.setText(number);
        return view;
    }

    private String getPhoneType(int type){
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";
            default:
                return "Unknown";
        }
    }

    public Bundle getSelectedContacts(){
        Bundle ret = new Bundle();

        for (ContactGroup cGroup : this.contactList){
            for (ContactChild cChild : cGroup.numbers){
                if (cChild.selected){
                    Bundle child = new Bundle();
                    child.putString("Number",cChild.number);
                    child.putString("Type",getPhoneType(cChild.type));
                    ret.putBundle(cGroup.name,child);
                }
            }
        }

        return ret;
    }

    private class ContactAsyncTask extends AsyncTask<Void,Integer,Void> {
        private Context cContext;
        private ProgressDialog progressDialog;

        public ContactAsyncTask(Context c){ cContext = c; }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading Contacts...");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        private void getContact(Cursor cur, ContentResolver cr){
            publishProgress(cur.getPosition(),cur.getCount());
            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                ContactGroup cGroup = new ContactGroup();
                cGroup.pos = contactList.size();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                cGroup.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                cGroup.groupView = createGroupView(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id)),cGroup.name); //TODO Fix this! doesnt get contact photo!!!

                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{id}, null);
                if (pCur.getCount() > 0) {
                    pCur.moveToFirst();
                    getContactNumber(pCur, cGroup);
                    while (pCur.moveToNext()) getContactNumber(pCur, cGroup);
                }
                pCur.close();

                contactList.add(cGroup);
            }
        }

        private void getContactNumber(Cursor pCur, final ContactGroup cGroup){
            final ContactChild contactChild = new ContactChild();
            contactChild.number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));;
            contactChild.type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));;
            contactChild.childView = createChildView(contactChild.type,contactChild.number);
            contactChild.checkBox = (CheckBox)contactChild.childView.findViewById(R.id.cSelect);
            contactChild.pos = cGroup.numbers.size();

            contactChild.childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { onChildClick(null,v,cGroup.pos,contactChild.pos,0); }
            });
            cGroup.numbers.add(contactChild);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ContentResolver cr = cContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0){
                cur.moveToFirst();
                getContact(cur,cr);
                while (cur.moveToNext()) getContact(cur,cr);
            }
            cur.close();

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
            notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
            progressDialog.setMax(values[1]);
        }
    }
    /*private static boolean validatePhoneNumber(String phoneNo) {
        if (phoneNo.matches("\\d{10}")) return true; //validate phone numbers of format "1234567890"
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true; //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true; //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true; //validating phone number where area code is in braces ()
        else if(phoneNo.matches("^\\\\+(?:[0-9] ?){6,14}[0-9]$}")) return true; //validating international phone number where area code is in braces ()
        else if(phoneNo.matches("^\\\\+[0-9]{1,3}\\\\.[0-9]{4,14}(?:x.+)?$")) return true; //validating international phone number in EPP format where area code is in braces ()
        else return false; //return false if nothing matches the input
    }*/
}
