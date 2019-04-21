package aau.itcom.rabbithabit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class AddPhotoActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {

    public static final String TAG = "photo";
    private Camera mCamera;
    private Camera.PictureCallback mPicture;
    private FloatingActionButton takePhoto;
    private SurfaceView surfaceViewCamera;
    private SurfaceHolder surfaceHolder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        takePhoto = findViewById(R.id.take_photo_button);
        surfaceViewCamera = findViewById(R.id.surfaceView3);

        surfaceViewCamera.getHolder().addCallback(this);

        mCamera = Camera.open();

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            FileOutputStream out = openFileOutput("picture.jpg", Activity.MODE_PRIVATE);
            out.write(data);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(surfaceViewCamera.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }


    public void takePhoto(View v) {
        mCamera.takePicture(null, null, this);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, AddPhotoActivity.class);
    }


}

