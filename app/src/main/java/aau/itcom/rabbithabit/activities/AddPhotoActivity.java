package aau.itcom.rabbithabit.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.fragments.SettingsFragment;
import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.system.PhoneState;

public class AddPhotoActivity extends AppCompatActivity {

    private static final String KEY_URI = "key_uri";
    static String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_GET_PIC = 100;
    private static final int SELECT_FILE = 101;
    private ImageView imageView;
    private FloatingActionButton takePhoto;
    private FloatingActionButton pickFromGallery;
    Database db;
    String path;
    File f;
    private Uri uriOfPicture;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        imageView = findViewById(R.id.imageViewDisplay);
        takePhoto = findViewById(R.id.take_photo_button);
        pickFromGallery = findViewById(R.id.pick_from_gallery);
        db = Database.getInstance();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        SELECT_FILE);
            }
        }

        if (savedInstanceState != null){
            uriOfPicture = savedInstanceState.getParcelable(KEY_URI);
            imageView.setImageURI((Uri) savedInstanceState.getParcelable(KEY_URI));
            imageView.setRotation(90);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_URI, uriOfPicture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            f = new File(currentPhotoPath);
            uriOfPicture = Uri.fromFile(f);
            imageView.setImageURI(Uri.fromFile(f));
            imageView.setRotation(90);
        }
        if (requestCode == REQUEST_GET_PIC && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            path = getPathFromURI(selectedImageUri);
            if (path != null) {
                f = new File(path);
                uriOfPicture = Uri.fromFile(f);
                imageView.setImageURI(Uri.fromFile(f));
                imageView.setRotation(90);
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void dispatchTakePictureIntent(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SettingsFragment.SETTINGS, Context.MODE_PRIVATE);

        if (PhoneState.getBatteryLevelInPrc(getApplicationContext()) >= PhoneState.BATTERY_LIMIT || pref.getBoolean(SettingsFragment.TAKE_PHOTO, false)) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "The battery level is too low", Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void pickFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GET_PIC);
    }

    public void addAsDailyPic(View view) {
        db.uploadPhotoFile(new Date(), FirebaseAuth.getInstance().getCurrentUser(), f);
        Toast.makeText(getApplicationContext(),"It may take a while until we upload the photo to our servers", Toast.LENGTH_LONG).show();
        startActivity(MainPageActivity.createNewIntent(getApplicationContext()));
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, AddPhotoActivity.class);
    }
}