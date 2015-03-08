package com.katrina.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alatnet on 3/7/2015.
 */
public class ContactsAdapter extends ArrayAdapter<ContactInfo> implements AdapterView.OnItemClickListener {
    private final Context mContext;
    private final ContactInfo[] contacts = new ContactInfo[5];
    private final LayoutInflater inflater;

    public ContactsAdapter(Context context/*, ContactInfo[] objects*/) {
        super(context, R.layout.view_contacts_ui/*, objects*/);
        this.mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            /*for (int i=0;i<5;i++){
                if (i>objects.length) break;
                contacts[i] = objects[i];
            }*/
        for (int i=0;i<5;i++) contacts[i] = new ContactInfo();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) convertView = inflater.inflate(R.layout.view_contacts_ui,null);

        TextView nameTV = (TextView)convertView.findViewById(R.id.cName);
        TextView phoneTV = (TextView)convertView.findViewById(R.id.cPhone);
        ImageView photoIV = (ImageView)convertView.findViewById(R.id.cImg);

        nameTV.setText(contacts[position].name);
        phoneTV.setText(contacts[position].phone);
        photoIV.setImageDrawable(contacts[position].photo);

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
