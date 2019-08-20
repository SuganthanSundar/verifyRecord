package com.quantrium.verifydoc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import okhttp3.Address;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SurfaceView surfaceView;
    CameraSource mcameraSource;
    FloatingActionButton floatingActionButton;
    Bitmap bitmap;
    EditText imageView;
    byte[] byteArray;
    NormalPic normalPic;
    File file;
    Button button;
    private static int INTENT_REQUEST_CODE=1001;
    View parent;
    JSONObject jsonObject,jsonObjectcl,jsonObjectcl2;


    public static final int REQUEST_MULTIPLE_PERMISSION=1001;
    public static final int REQUEST_PERMISSION=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // imageView=findViewById(R.id.imageShow);
        surfaceView = findViewById(R.id.surfaceText_view);
        floatingActionButton = findViewById(R.id.floatingButton);
       // button=findViewById(R.id.buttonOk);
        parent=findViewById(android.R.id.content);

        startCamera();
        floatingActionButton.setOnClickListener(this);
        //button.setOnClickListener(this);


        if(checkPermission()){
            Snackbar.make(parent,"All Permision are Granted",Snackbar.LENGTH_LONG).show();
        }else {
            Snackbar.make(parent,"All Permision are Denied",Snackbar.LENGTH_LONG).show();
        }

        }
    private void startCamera() {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "TextRecognizer is not loaded", Toast.LENGTH_LONG).show();
        } else {
            mcameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setAutoFocusEnabled(true)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f).build();
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {


                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION);
                        }
                        mcameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    mcameraSource.stop();

                }
            });


           /* textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items=detections.getDetectedItems();
                    if(items.size()>0){
                        extractText.post(new Runnable() {
                            @Override
                            public void run() {
                                textShow= new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock textBlock=items.valueAt(i);
                                    textShow.append(textBlock.getValue());
                                    textShow.append("\n");
                                }
                                extractText.setText(textShow.toString());
                            }
                        });

                    }

                }
            });*/

        }
    }
    CameraSource.ShutterCallback shutterCallback=new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    CameraSource.PictureCallback pictureCallback= new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            byte[] byteArrayNew= outputStream.toByteArray();


            if (bitmap != null) {
                File filesDir = getApplicationContext().getFilesDir();
                file = new File(filesDir, "image" + ".png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(byteArrayNew);
                    fos.flush();
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bitmap==null){
                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            options.inPurgeable = true;
            options.inScaled = true;
            Bitmap bm = null;
            while (bm==null)
                bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byteArray= stream.toByteArray();
                //imageView.setImageBitmap(bm);
        }
    };
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.floatingButton) {
            mcameraSource.takePicture(shutterCallback, pictureCallback);
            Intent intent = new Intent(MainActivity.this, NormalPic.class);
           // intent.putExtra("Picture",byteArray);
            startActivity(intent);
        }
    }



    private boolean checkPermission() {
        int cameraPermission= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        int writePermission=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String>permisionNeeded=new ArrayList<>();
            if(cameraPermission!=PackageManager.PERMISSION_GRANTED){
                permisionNeeded.add(Manifest.permission.CAMERA);
            }
            if(writePermission!=PackageManager.PERMISSION_GRANTED){
                permisionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(readPermission!=PackageManager.PERMISSION_GRANTED){
                permisionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(!permisionNeeded.isEmpty()){
                ActivityCompat.requestPermissions(this,permisionNeeded.toArray(new String[permisionNeeded.size()]),REQUEST_MULTIPLE_PERMISSION);
                return false;

            }

          return true;
        }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_MULTIPLE_PERMISSION){
            Map<String,Integer>permisionUser=new ArrayMap<>();
            permisionUser.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            permisionUser.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            permisionUser.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            if(grantResults.length>0) {
                for (int i = 0; i < permissions.length; i++) {
                    permisionUser.put(permissions[i], grantResults[i]);
                }
                if (permisionUser.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && permisionUser.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && permisionUser.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(parent, "All Permision are Granted", Snackbar.LENGTH_LONG).show();

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showDialogOK("Service Permissions are required for this app",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkPermission();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                /*openSettingsDialog();*/
                                                finish();
                                                break;
                                        }
                                    }
                                });

                    } else{
                        openSettingsDialog();
                    }
                }
            }

        }
    }

    private void openSettingsDialog() {
        String alertText="Camera Permisssion,Write and Read External Storage Permission are must to use this app";
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(alertText);
        builder.setPositiveButton("Go To Settings",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri=Uri.fromParts("package",getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();

            }
        });
        builder.show();
    }

    private void showDialogOK(String s, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this).setMessage(s).setPositiveButton("Yes",onClickListener)
                .setNegativeButton("Cancel",onClickListener).create().show();
    }



}

        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        startActivityForResult(intent, INTENT_REQUEST_CODE);*/

        /*Intent actioncamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (actioncamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(actioncamera, 1001);*/



/*

        InputStream is = null;
        try {
        is = getContentResolver().openInputStream(data.getData());
        uploadImage(getBytes(is));
        } catch (FileNotFoundException e) {
        e.printStackTrace();
        }
*/

/*if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
        Manifest.permission.CAMERA,
        }, REQUEST_CAMERA_PERMISSION);
        }
        }*/


/* @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==1001&&resultCode==RESULT_OK){
                BitmapFactory.Options options = new BitmapFactory.Options();
               *//* options.inJustDecodeBounds = false;
                options.inSampleSize = 4;
                options.inScaled = true;
                options.inPurgeable = true;
                options.inInputShareable = true;*//*
                Bundle bundle=data.getExtras();
                Bitmap bitmap= (Bitmap) bundle.get("data");
               // Bitmap bitmapRS=  BitmapFactory.decodeFile(String.valueOf(bitmap),options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                Intent intent =new Intent(MainActivity.this,NormalPic.class);
                intent.putExtra("picture",byteArray);
                startActivity(intent);
            }
        }*/
/*
 for(int i=1;i<=10;i++){
         try {
         String keyAdd="AddressId";
         String keyCli="ClientId";
         jsonObjectcl.put(keyAdd,keyAdd+i);
         jsonObjectcl.put(keyCli,keyCli+i);
         jsonArrayAdd.add(String.valueOf(jsonObjectcl));
         } catch (JSONException e) {
         e.printStackTrace();
         }
         }
         try {
         String keyObj= "AddressIds";
         jsonObject.put(keyObj,jsonArrayAdd);
         } catch (JSONException e) {
         e.printStackTrace();
         }
// showText.setText(jsonObject.toString());*/
 /*jsonObject=new JSONObject();
         JSONArray jsonArrayAdd=new JSONArray();
         jsonObjectcl=new JSONObject();
         jsonObjectcl2=new JSONObject();
         ArrayList<String>arrayAddress=new ArrayList<>();
        ArrayList<String>arrayClient=new ArrayList<>();
        arrayAddress.add("8375D1F0-EC07-4D6A-81B3-EADD200D2D70");
        arrayAddress.add("34CEF829-9BB2-4034-9621-3549D2E02B0E");
        arrayClient.add("fd1b5bf4-ff1e-4132-9464-7322b8c129dd");
        arrayClient.add("39e59212-8f14-4ae7-a53a-9038b196b554");
         for(int i=0;i<arrayAddress.size();i++){
                try {
                        jsonObjectcl.put("AddressIDs", arrayAddress.get(i));
                        jsonObjectcl.put("ClientID", arrayClient.get(i));
                       jsonArrayAdd.put(jsonObjectcl);

                }catch (JSONException e) {
                e.printStackTrace();
            }
        }
       try {
            String keyObj= "IDs";
            jsonObject.put(keyObj,jsonArrayAdd);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String textName=jsonObject.toString();
        imageView.setText(textName);
        */