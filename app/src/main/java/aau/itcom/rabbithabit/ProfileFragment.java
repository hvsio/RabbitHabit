package aau.itcom.rabbithabit;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import aau.itcom.rabbithabit.objects.PhoneState;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.getCacheDir;


public class ProfileFragment extends Fragment {

    CircleImageView profilePic;
    Button changeProfInfo;
    File storageDir = getCacheDir();
    File profilePicture;
    static String photoPath;
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    Uri imageHoldUri = null;
    Database db;
    TextView name;
    TextView email;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    StorageReference userRefPictures = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    StorageReference userRefProfilePic = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile_picture.jpg");
    TextView noOfHabits;
    TextView noOfStories;
    private static int numberOfStories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = Database.getInstance();

        profilePic = getView().findViewById(R.id.profile_image);
        name = getView().findViewById(R.id.textView12);
        email = getView().findViewById(R.id.textView15);
        noOfHabits = getView().findViewById(R.id.textViewNoHabits);
        noOfStories = getView().findViewById(R.id.textViewNoStories);

        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        noOfHabits.setText(db.countHabits());
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicSelection();
            }
        });
        load();
    }

    private void profilePicSelection() {

        if (checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }

        if (checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_FILE);
        }

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add profile picture");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void galleryIntent() {
        Log.d("GALLERY", "entered gallery");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    private void cameraIntent() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(SettingsFragment.SETTINGS, Context.MODE_PRIVATE);

        if (PhoneState.getBatteryLevelInPrc(getApplicationContext()) >= PhoneState.BATTERY_LIMIT || pref.getBoolean(SettingsFragment.TAKE_PHOTO, false)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getApplicationContext(),"The battery level is too low", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri;
        String path;
        File f ;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA  && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(imageBitmap);
            saveImageAndUpload(imageBitmap);
        }else if ( requestCode == SELECT_FILE && resultCode == RESULT_OK ){
            imageUri = data.getData();
            path = getPathFromURI(imageUri);
            if (path != null) {
                f = new File(path);
                profilePic.setImageURI(Uri.fromFile(f));
                db.uploadProfilePic(FirebaseAuth.getInstance().getCurrentUser(), f);
            }

        }

    }

    private void load() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new LoadProfilePicture());
        executorService.submit(new LoadStories());
        executorService.shutdown();
        db.loadProfilePicture(FirebaseAuth.getInstance().getCurrentUser(), getContext());
        db.addStoriesToList();

    }

    private void displayProfilePicture() {
            profilePic.setImageURI(db.getProfilePhoto());
    }

    private class LoadProfilePicture implements Runnable{

        private static final String THREAD_PHOTO_TAG = "LoadProfilePicture";

        @Override
        public void run() {
            try {
                synchronized (Database.LOCK_FOR_PROFILE_PIC) {
                    Database.LOCK_FOR_PROFILE_PIC.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayProfilePicture();
                    }
                });
            } else {
                Log.w(THREAD_PHOTO_TAG, "Fragment is no longer attached to its activity");
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    private void saveImageAndUpload(Bitmap finalBitmap) {
        try (FileOutputStream outputStream = new FileOutputStream(new File(getCacheDir(), "profile_pic.jpeg"))){
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            db.uploadProfilePic(FirebaseAuth.getInstance().getCurrentUser(), new File(getCacheDir(), "profile_pic.jpeg"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class LoadStories implements Runnable{

        @Override
        public void run() {
            try {
                synchronized (Database.LOCK_FOR_LIST_OF_STORIES) {
                    Database.LOCK_FOR_LIST_OF_STORIES.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        noOfStories.setText(String.valueOf(db.stories.size()));
                    }
                });
            }
        }


    }



}
