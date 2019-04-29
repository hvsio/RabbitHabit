package aau.itcom.rabbithabit;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import aau.itcom.rabbithabit.objects.Database;

public class AddPhotoActivity extends AppCompatActivity {

    static String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_GET_PIC = 100;
    private ImageView imageView;
    private FloatingActionButton takePhoto;
    private FloatingActionButton pickFromGallery;
    Database db;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        imageView = findViewById(R.id.imageViewDisplay);
        takePhoto = findViewById(R.id.take_photo_button);
        pickFromGallery = findViewById(R.id.pick_from_gallery);
        db = Database.getInstance();

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.CAMERA},
                MY_CAMERA_REQUEST_CODE);
        }
    }
    public static String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File f = new File(currentPhotoPath);
            imageView.setImageURI(Uri.fromFile(f));
            galleryAddPic();
            db.uploadPhotoFile(new Date(), FirebaseAuth.getInstance().getCurrentUser(), new File(getCurrentPhotoPath()));
        }
        if (requestCode == REQUEST_GET_PIC && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final String path = getPathFromURI(selectedImageUri);
            if (path != null) {
                File f = new File(path);
                selectedImageUri = Uri.fromFile(f);
            }
            imageView.setImageURI(selectedImageUri);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void dispatchTakePictureIntent(View view) {
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void pickFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GET_PIC);
    }

    static Intent createNewIntent(Context context) {
        return new Intent(context, AddPhotoActivity.class);
    }
}

