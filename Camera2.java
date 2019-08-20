package com.quantrium.verifydoc.cameraApi;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.quantrium.verifydoc.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Camera2 extends AppCompatActivity implements View.OnClickListener {
    TextureView textureView;
    Button captureBut;
    private static SparseIntArray Orientation = new SparseIntArray();

    static {
        Orientation.append(0, 90);
        Orientation.append(90, 0);
        Orientation.append(180, 270);
        Orientation.append(270, 180);
    }

    private String cameraID;
    private CameraDevice cameraDevice;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraCaptureSession;
    private Size imageDimesion;
    private File file;
    Handler mBackground;
    HandlerThread mbackgroundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        textureView = findViewById(R.id.textureView);
        captureBut = findViewById(R.id.cap_but);
        captureBut.setOnClickListener(this);
        textureView.setSurfaceTextureListener(textureSurfaceView);

    }

    TextureView.SurfaceTextureListener textureSurfaceView = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private final CameraDevice.StateCallback statecallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDev) {
            cameraDevice=cameraDev;
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDev) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDev, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    private void createCameraPreview() throws CameraAccessException {
        SurfaceTexture texture=textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimesion.getWidth(),imageDimesion.getHeight());
        Surface surface=new Surface(texture);
        captureRequestBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);
        cameraDevice.createCaptureSession(Arrays.asList(surface),new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptSession) {
                if(cameraDevice==null){
                    return;

                }
                cameraCaptureSession=cameraCaptSession;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onConfigureFailed( CameraCaptureSession cameraCaptureSession) {
                Toast.makeText(Camera2.this,"Configuration Changed",Toast.LENGTH_LONG).show();

            }
        },null);
    }

    private void updatePreview() throws CameraAccessException {
        if(cameraDevice==null){
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,mBackground);
    }

    @Override
    protected void onResume() {
        startBackgroundThread();
        super.onResume();
        if(textureView==null){
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }else{
            textureView.setSurfaceTextureListener(textureSurfaceView);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==101){
            if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                Toast.makeText(Camera2.this,"Camera Permission is needed to use this App",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startBackgroundThread() {
        mbackgroundThread=new HandlerThread("Camera");
        mbackgroundThread.start();
        mBackground=new Handler(mbackgroundThread.getLooper());
    }

    @Override
    protected void onPause() {
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();

    }

    private void stopBackgroundThread() throws InterruptedException {
        mbackgroundThread.quitSafely();
        mbackgroundThread.join();
        mbackgroundThread=null;
        mBackground=null;

    }

    private void openCamera() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraID = cameraManager.getCameraIdList()[0];
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraID);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimesion = map.getOutputSizes(SurfaceTexture.class)[0];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        cameraManager.openCamera(cameraID, statecallBack, null);

    }

    @Override
    public void onClick(View view) {
        int butId=view.getId();
        if(butId==R.id.cap_but){
            try {
                takePicture();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private void takePicture() throws CameraAccessException {
        if(cameraDevice==null){
            return;
        }
        CameraManager manager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpeg=null;
        jpeg=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
        int width=640;
        int height=480;
        if(jpeg!=null&&jpeg.length>0){
            width=jpeg[0].getWidth();
            height=jpeg[0].getHeight();
            ImageReader reader= ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface=new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureRequestBulider=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBulider.addTarget(reader.getSurface());
            captureRequestBulider.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
            int rotation =getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBulider.set(CaptureRequest.JPEG_ORIENTATION,Orientation.get(rotation));
            Long tsLong=System.currentTimeMillis()/1000;
            String ts=tsLong.toString();
            file=new File(Environment.getExternalStorageDirectory()+"/"+ts+".jpg");
            ImageReader.OnImageAvailableListener imageAvailableListener=new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image= null;
                    image=reader.acquireLatestImage();
                    ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                    byte[] bytes=new byte[buffer.capacity()];
                    buffer.get(bytes);
                    try {
                        save(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if(image!=null){
                            image.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(imageAvailableListener,mBackground);
            final CameraCaptureSession.CaptureCallback captureCallback=new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,  CaptureRequest request,  TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(Camera2.this,"Saved",Toast.LENGTH_LONG).show();
                    try {
                        createCameraPreview();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            };
            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureRequestBulider.build(),captureCallback,mBackground);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed( CameraCaptureSession cameraCaptureSession) {

                }
            },mBackground);
        }



    }

    private void save(byte[] bytes) throws IOException {
        OutputStream outputStream=null;
        outputStream=new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
    }
}
