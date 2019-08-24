package javed.com.learningdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static String SERVER_IP="http://192.168.43.145/";;
    public static final String UPLOAD_URL=SERVER_IP+"demoandroidapp/upload.php";
    public static final String UPLOAD_KEY="image";
    int PICK_IMAGE_REQ=1;
    Button btnChoose, btnUpload, btnView;
    ImageView imgView;
    Bitmap bitmap;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btnChoose=findViewById(R.id.buttonChoose);
        btnUpload=findViewById(R.id.buttonUpload);
        btnView=findViewById(R.id.buttonViewImage);

        imgView=findViewById(R.id.imageView);

        btnChoose.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnView.setOnClickListener(this);

    }

    private void showFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath=data.getData();
            try{
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imgView.setImageBitmap(bitmap);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes=baos.toByteArray();
        String encodeImage= Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodeImage;
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap, Void, String>
        {
            ProgressDialog pBar;
            RequestHandler rh=new RequestHandler();
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                pBar=ProgressDialog.show(UploadActivity.this, "Uploading..", null, true, true);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pBar.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Bitmap... bitmaps)
            {
                Bitmap bitmap=bitmaps[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data=new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                String result=rh.sendPostRequest(UPLOAD_URL, data);

                return  result;
            }
        }

        UploadImage ui=new UploadImage();
        ui.execute(bitmap);
    }

    @Override
    public void onClick(View v)
    {
        if(v==btnChoose)
        {
            Log.e("Javed", "url: "+UPLOAD_URL);
            showFileChooser();
        }
        if(v==btnUpload){
            uploadImage();
        }

        if(v==btnView){
            viewImage();
        }
    }

    private void viewImage()
    {
        Intent i=new Intent(this, SMSInbox.class);
        startActivity(i);
    }
}
