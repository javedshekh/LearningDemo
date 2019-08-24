package javed.com.learningdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

public class SMSInbox extends AppCompatActivity {

    ListView lstSMS;
    Vector[] v=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsinbox);

        lstSMS=findViewById(R.id.lstsms);

        fetchInbox();
        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                fetchInbox();
            }
        };

    }

    public void fetchInbox()
    {
        Uri uriSms=Uri.parse("content://sms/inbox");
        Cursor cursor=getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);

        if(cursor.getCount() !=0)
        {
        v=new Vector[cursor.getCount()];
        int i=0;
        while (cursor.moveToNext())
        {

            v[i]=new Vector();
            String id=cursor.getString(0);
            String address=cursor.getString(1);
            String date=cursor.getString(2);
            String body=cursor.getString(3);

            v[i].addElement(address.trim());
            v[i].addElement(body.trim());
            v[i].addElement(id.trim());
            v[i].addElement(date.trim());


            i++;
        }
            callAdapter();
        }
        else{
            Toast.makeText(this, "No SMS Found", Toast.LENGTH_SHORT).show();
            Intent  intent=new Intent(this, UploadActivity.class);
            startActivity(intent);
        }


    }

    private void callAdapter()
    {
        if (v.length!=0)
        {
            CustomAdapter adapter=new CustomAdapter();
            lstSMS.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "No SMS Found", Toast.LENGTH_SHORT).show();
            Intent  intent=new Intent(this, UploadActivity.class);
            startActivity(intent);
        }

    }

    private class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return v.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int p, View cView, ViewGroup parent)
        {
            try
            {
                cView=getLayoutInflater().inflate(R.layout.custom_inbox, null);

                TextView txtSr=cView.findViewById(R.id.txtsr);
                TextView txtAdr=cView.findViewById(R.id.txtadr);;
                TextView txtBody=cView.findViewById(R.id.txtbody);;

                txtSr.setText(""+(p+1)+" "+v[p].elementAt(2));
                txtAdr.setText(""+v[p].elementAt(0)+" "+v[p].elementAt(3));
                txtBody.setText(""+v[p].elementAt(1));

            }
            catch (Exception ex){ex.printStackTrace();}

            return cView;
        }
    }
}
