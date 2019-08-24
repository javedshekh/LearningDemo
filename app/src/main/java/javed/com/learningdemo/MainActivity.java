package javed.com.learningdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView fieldLatitude, fieldLongitude;
    Button btnclickme;
    WebView myWebView;
    private Context myContext=MainActivity.this;
    public static final int PERMS_REQ_CODE=123;
    private String mGeolocationOrigin;
    private GeolocationPermissions.Callback mGeolocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fieldLatitude=findViewById(R.id.fieldLatitude);
        fieldLongitude=findViewById(R.id.fieldLongitude);
        btnclickme=findViewById(R.id.btnclickme);
        myWebView=findViewById(R.id.mywebview);

        btnclickme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
            }
        });

    }

    public void getPermission()
    {
        if(hasPermission())
        {
            callgps();
        }
        else
        {
            requestPerms();
        }
    }
    @SuppressLint("WrongConstant")
    private boolean hasPermission()
    {
        try {
            int  res = 0;

            String[] permision = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            for (String param : permision)
            {
                res = checkCallingOrSelfPermission(param);

                if(res<=0){
                    return false;
                }

            }
            return true;
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "Exception from hasPermission", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void requestPerms()
    {
        try{
            String[] permision = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(permision, PERMS_REQ_CODE);
            }

            else if(
                    (ContextCompat.checkSelfPermission(myContext,
                            Manifest.permission.READ_PHONE_STATE)
                            == PackageManager.PERMISSION_GRANTED) &&
                            ( (ContextCompat.checkSelfPermission(myContext,
                                    Manifest.permission.READ_SMS)
                                    == PackageManager.PERMISSION_GRANTED) ) )
            {
                callgps();
            }

        }
        catch(Exception ex)
        {
            Toast.makeText(myContext, "Exception from requestPerms", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean allowed=true;

        switch (requestCode)
        {
            case PERMS_REQ_CODE:

                for(int res: grantResults)
                {
                    allowed=true;
                }
                break;

            default:
                allowed=false;
                break;
        }

        if (allowed)
        {
            callgps();
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                {

                    Toast.makeText(myContext, "Permission not granted...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void callgps()
    {
        int checkP1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int checkP2=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(checkP1==0 && checkP2==0)
        {
            GPSTracker gps = new GPSTracker(myContext);
            final double latitude = gps.getLatitude();
            final double longitude = gps.getLongitude();

            try{
                String lat=String.valueOf(latitude);
                String lon=String.valueOf(longitude);
                fieldLatitude.setText(lat);
                fieldLongitude.setText(lon);


                loadWebView(lat, lon);

            }
            catch (Exception ex){
                Log.e("Exception", ex.getMessage());
            }


            return;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "App needs Permission", Toast.LENGTH_SHORT).show();

        }


    }

    private void loadWebView(String lat, String lon)
    {
        //String url="http://maps.google.com/maps?daddr="+lat+", "+lon+" ";
        String url="https://www.google.com/maps/@"+lat+", "+lon+"";

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setGeolocationEnabled(true);
        myWebView.loadUrl(url);
        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String request) {
                view.loadUrl(request);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }
        });





        /*

     /*   myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                final String[] perm=new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

                if( (ContextCompat.checkSelfPermission (MainActivity.this,perm[0])==PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(MainActivity.this, perm[1]) ==PackageManager.PERMISSION_GRANTED)){
                    callback.invoke(origin, true, false);
                }
                else{
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, perm[0])){
                        Toast.makeText(myContext, "Grant Location Permission", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mGeolocationOrigin=origin;
                        mGeolocationCallback=callback;

                        ActivityCompat.requestPermissions(MainActivity.this, perm, PERMS_REQ_CODE);
                    }
                }


            }
        });*/

    }
}
