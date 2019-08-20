package com.quantrium.verifydoc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NormalPic extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;
    private Button usePic,reTake;
    private Apidata apidata;
    private Bitmap aadharpic,bmp;
    private byte[]byteArray_send;
    private Docresult docresult;
    private File file;
    private String imgString;

    private Base64 base64;
    public NormalPic() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_pic);
        usePic=findViewById(R.id.but_use_pic);
        reTake=findViewById(R.id.but_retake);
        imageView=findViewById(R.id.image_show);
        //Bundle extras=getIntent().getExtras();
        //byteArray_send=extras.getByteArray("Picture");
        //bmp=BitmapFactory.decodeByteArray(byteArray_send,0,byteArray_send.length);
        aadharpic = BitmapFactory.decodeResource(getResources(), R.drawable.pancard);
        imageView.setImageBitmap(aadharpic);
        usePic.setOnClickListener(this);
        reTake.setOnClickListener(this);
    }
    public void compressImage() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                aadharpic.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byteArray_send = stream.toByteArray();
                imgString = Base64.encodeToString(byteArray_send,Base64.DEFAULT);
            }
        };
        thread.run();
    }
    private void callApi() {
        compressImage();
        apidata = SetURL.getInstance().getRetrofit().create(Apidata.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imgString);
        //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");
        MultipartBody.Part body = MultipartBody.Part.createFormData("data", "imgString", requestFile);
        Call<ResponseData> call = apidata.callApi(body);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if(response.isSuccessful()) {
                    ResponseData responseData = response.body();
                    docresult = new Docresult(responseData);
                    Intent intent = new Intent(NormalPic.this, Docresult.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Toast.makeText(NormalPic.this, "Response Error", Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.but_use_pic){
            callApi();
            Intent intent=new Intent(NormalPic.this,Docresult.class);
            startActivity(intent);
        }else if(view.getId()==R.id.but_retake){
            Intent intent=new Intent(NormalPic.this,MainActivity.class);
            startActivity(intent);
        }
        }

    }

    /*f(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);

        } else{
            Toast.makeText(this,"File is empty",Toast.LENGTH_LONG).show();
        }*/
//imageView.setImageDrawable(Drawable.createFromPath(file.toString()));
//Glide.with(this).load(file.getAbsoluteFile()).into(imageView);
  /* public NormalPic(File file) {
        this.file = file;
    }*/