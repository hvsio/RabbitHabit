package aau.itcom.rabbithabit;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aau.itcom.rabbithabit.objects.Database;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;
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
    TextView noOfPhotos;

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
        noOfPhotos = getView().findViewById(R.id.textViewNoPhotos);

        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        noOfHabits.setText(db.countHabits());
        profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePicSelection();
                    }
                });
        loadProfilePic();
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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
            saveImage(imageBitmap);
        }else if ( requestCode == SELECT_FILE && resultCode == RESULT_OK ){
            imageUri = data.getData();
            path = getPathFromURI(imageUri);
            if (path != null) {
                f = new File(path);
                profilePic.setImageURI(Uri.fromFile(f));
                db.uploadPhotoFile(new Date(), FirebaseAuth.getInstance().getCurrentUser(), f);
            }

        }

    }

    private void loadProfilePic() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new LoadProfilePicture());
        executorService.shutdown();
        db.loadProfilePicture(FirebaseAuth.getInstance().getCurrentUser(), getContext());
    }

    private void displayProfilePicture() {
        profilePic.setImageURI(db.getPhoto());
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


    private void saveImage(Bitmap finalBitmap) {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(new File(getCacheDir(), "profile_pic.jpeg"));
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = userRefProfilePic.putBytes(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
