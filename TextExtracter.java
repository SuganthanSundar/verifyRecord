package com.quantrium.verifydoc;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PatternMatcher;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextExtracter extends AppCompatActivity implements View.OnClickListener {
    SurfaceView surfaceView;
    TextView extractText;
    CameraSource mcameraSource;
    ImageView imageView;
    StringBuilder textShow;
    FloatingActionButton floatingActionButton;
    String[] namesData,sendData;
    List<String>showUser;
    PatternMatcher patternMatcher;
    public static final int REQUEST_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_extracter);
        surfaceView = findViewById(R.id.surfaceText_view);
        floatingActionButton = findViewById(R.id.floatingButton);
        imageView=findViewById(R.id.imageCapture);
        showUser=new ArrayList<>();
        floatingActionButton.setOnClickListener(this);
        startCamera();

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
                        if (ActivityCompat.checkSelfPermission(TextExtracter.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                           ActivityCompat.requestPermissions(TextExtracter.this,new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION);
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


            /*textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
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
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if(bitmap==null){
                Toast.makeText(TextExtracter.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            imageView.setImageBitmap(bitmap);

        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.floatingButton){

            mcameraSource.takePicture(shutterCallback,pictureCallback);
           // mcameraSource.stop();
           /* extractText.setText(null);
            namesData=textShow.toString().split("\n");
            for(int i=0;i<namesData.length;i++){
                String nameUser=namesData[i];
                if(nameUser.length()==19){
                    showUser.add(namesData[i].toString());
                }
                if(nameUser.length()==21){
                    showUser.add(namesData[i].toString());
                }
            }
            Intent intent=new Intent(TextExtracter.this,MainActivity.class);
            intent.putStringArrayListExtra("ShowDetails", (ArrayList<String>) showUser);
            startActivity(intent);*/


        }

    }
}
