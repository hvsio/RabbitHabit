package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;

import aau.itcom.rabbithabit.objects.Database;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
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
        changeProfInfo = getView().findViewById(R.id.change);

        changeProfInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePicSelection();
                    }
                });
            }
        });

        try {
            profilePicture = File.createTempFile("currentProfilePicture", ".jpg", storageDir);
            photoPath = profilePicture.getAbsolutePath();
            db.uploadProfilePic(FirebaseAuth.getInstance().getCurrentUser(), new File(photoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void profilePicSelection() {

        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
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

    private void cameraIntent() {

        //CHOOSE CAMERA
        Log.d("CAMERA", "entered camera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        Log.d("GALLERY", "entered gallery");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imageUri = null;
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

        }else if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_OK ){
            imageUri = data.getData();
        }

        profilePic.setImageURI(imageUri);

    }


}
