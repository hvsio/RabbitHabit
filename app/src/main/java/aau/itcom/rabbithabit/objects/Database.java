package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Database {

    // TODO : UPDATE LOGS AFTER COPY & PASTE

    private static final String TAG = "DatabaseClass";
    private FirebaseFirestore db;
    private static Database instance = null;
    private StorageReference mStorageRef;

    private Uri photoUri;
    private Uri profilePhotoUri;
    private Story story;
    private ArrayList<HabitPersonal> habitPersonals;
    private ArrayList<HabitPublished> habitsPublished;

    private boolean isStoryDownloadCompleted = false;
    private boolean isPhotoDownloadCompleted = false;
    private boolean isProfilePhotoDownloadCompleted = false;
    private boolean isHabitsPersonalDownloadCompleted = false;
    private boolean isHabitsPublishedDownloadCompleted = false;

    public static final Object LOCK_FOR_HABITS = new Object();
    public static final Object LOCK_FOR_HABITS_PUBLISHED = new Object();
    public static final Object LOCK_FOR_STORY = new Object();
    public static final Object LOCK_FOR_PHOTO = new Object();
    public static final Object LOCK_FOR_PROFILE_PIC = new Object();

    private Database() {
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public synchronized static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void addHabitPersonal(HabitPersonal habit, FirebaseUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("startDate", habit.getStartDate());
        map.put("arrayOfDates", Arrays.asList(habit.getArrayOfDates()));
        map.put("duration", habit.getDuration());
        map.put("details", habit.getDetails());
        map.put("complexity", habit.getComplexionMap());

        db.collection("Users").document(user.getUid()).collection("Habits").document(habit.getName())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Directory for Habits created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating directory for Habits!", e);
                    }
                });
    }


    public String countHabits() {
        String number = String.valueOf(getArrayListOfHabitsPersonal().size());
        return number;
    }

    ;

    public void loadSetOfHabitsOnDate(Date date, FirebaseUser user) {

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        habitPersonals = new ArrayList<>();

        db.collection("Users").document(user.getUid()).collection("Habits")
                .whereArrayContains("arrayOfDates", dateFormat.format(date))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                habitPersonals.add(new HabitPersonal(document.getId(), document.getLong("duration"), document.getString("details"), document.getTimestamp("startDate").toDate()));
                            }
                            Log.d(TAG, "Habits are now loaded!");
                            isHabitsPersonalDownloadCompleted = true;
                            synchronized (LOCK_FOR_HABITS) {
                                Log.d(TAG, "I am about to notify about finishing loading habits");
                                LOCK_FOR_HABITS.notify();
                                Log.d(TAG, "NOTIFIED!");
                            }
                        } else {
                            isHabitsPersonalDownloadCompleted = false;
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public ArrayList<HabitPersonal> getArrayListOfHabitsPersonal() throws NoSuchElementException {
        Log.d(TAG, "HabitsPersonal consists: " + habitPersonals);

        if (isHabitsPersonalDownloadCompleted) {
            return habitPersonals;
        } else {
            throw new NoSuchElementException("Unable to execute getArrayListOfHabitsPersonal(). Files doesn't exist or connection error occurred!");
        }
    }

    public void addHabitPublished(HabitPublished habit) {
        Map<String, Object> map = new HashMap<>();
        map.put("showCreator", habit.getShowCreator());
        map.put("creator", habit.getCreator());
        map.put("suggestedDuration", habit.getDuration());
        map.put("numberOfLikes", 0);

        db.collection("HabitsPublished").document(habit.getName())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Directory for Habits created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating directory for Habits!", e);
                    }
                });
    }

    public void loadSetOfTrendingHabits() {
        habitsPublished = new ArrayList<>();

        db.collection("HabitsPublished")
                .orderBy("numberOfLikes")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                habitsPublished.add(new HabitPublished(document.getId(), document.getLong("duration"), document.getString("details"), document.getString("showCreator"), document.getString("creator"), document.getLong("numberOfLikes")));
                            }
                            Log.d(TAG, "Habits are now loaded!");
                            isHabitsPublishedDownloadCompleted = true;
                            synchronized (LOCK_FOR_HABITS_PUBLISHED) {
                                Log.d(TAG, "I am about to notify about finishing loading habits");
                                LOCK_FOR_HABITS_PUBLISHED.notify();
                                Log.d(TAG, "NOTIFIED!");
                            }
                        } else {
                            isHabitsPublishedDownloadCompleted = false;
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public ArrayList<HabitPublished> getArrayListOfHabitsPublished() throws NoSuchElementException {
        Log.d(TAG, "HabitsPublished consists: " + habitsPublished);

        if (isHabitsPublishedDownloadCompleted) {
            return habitsPublished;
        } else {
            throw new NoSuchElementException("Unable to execute getArrayListOfHabitsPublished(). Files doesn't exist or connection error occurred!");
        }
    }

    public void loadPhotoOnDate(final Date date, FirebaseUser user, Context context) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        photoUri = null;

        File directory = context.getCacheDir();
        File outputFile = null;
        try {
            outputFile = File.createTempFile(dateFormat.format(date), "jpg", directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalOutputFile = outputFile;

        mStorageRef.child(user.getUid() + "/" + dateFormat.format(date) + ".jpg")
                .getFile(outputFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        photoUri = Uri.fromFile(finalOutputFile);

                        isPhotoDownloadCompleted = true;
                        synchronized (LOCK_FOR_PHOTO) {
                            LOCK_FOR_PHOTO.notify();
                            Log.d(TAG, "NOTIFIED!");
                        }
                        //Log.i(TAG, "Image URI: " + photoUri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                isPhotoDownloadCompleted = false;
                Log.d(TAG, "Cannot download the photo. The photo may not exist!");
                synchronized (LOCK_FOR_PHOTO) {
                    LOCK_FOR_PHOTO.notify();
                    Log.d(TAG, "NOTIFIED!");
                }
            }
        });
    }

    public Uri getPhoto() throws NoSuchElementException {
        if (!isPhotoDownloadCompleted) {
            Log.i(TAG, "No picture ON DATE detected");
            return null;
        } else {
            Uri uri = photoUri;
            photoUri = null;
            isPhotoDownloadCompleted = false;
            return uri;
        }
    }

    public void uploadPhotoFile(Date date, FirebaseUser user, File filePassed) {

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
//        Uri file = Uri.fromFile(new File(AddPhotoActivity.getCurrentPhotoPath()));
//        Uri file = Uri.fromFile(filePassed);
        StorageReference riversRef = mStorageRef.child(user.getUid() + "/" + dateFormat.format(date) + ".jpg");

        riversRef.putFile(Uri.fromFile(filePassed))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "Cannot upload the photo. Check your internet connection!");
                        exception.printStackTrace();
                    }
                });
    }

    public void uploadProfilePic(FirebaseUser user, File filePassed) {
        Uri file = Uri.fromFile(filePassed);
        StorageReference profilesRef = mStorageRef.child(user.getUid() + "/profile_picture.jpg");

        profilesRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "Cannot upload the photo. Check your internet connection!");
                        exception.printStackTrace();
                    }
                });
    }

    public void addStory(Story story, FirebaseUser user) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");


        Map<String, Object> map = new HashMap<>();
        map.put("date", dateFormat.format(story.getDate()));
        map.put("storyContent", story.getTextContent());
        map.put("mood", story.getMood());


        db.collection("Users").document(user.getUid()).collection("Story").document(dateFormat.format(story.getDate()))
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Directory for Habits created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating directory for Habits!", e);
                    }
                });
    }

    public void loadStoryOnDate(final Date date, FirebaseUser user) {
        story = null;

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Log.d(TAG, "I am inside loadStoryOnDate() and date is: " + dateFormat.format(date));

        db.collection("Users").document(user.getUid()).collection("Story")
                .whereEqualTo("date", dateFormat.format(date))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                story = new Story(date, document.getString("storyContent"), (long) document.get("mood"));

                            }
                            isStoryDownloadCompleted = true;
                            synchronized (LOCK_FOR_STORY) {
                                LOCK_FOR_STORY.notify();
                            }
                        } else {
                            isStoryDownloadCompleted = false;
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public Story getStory() throws NoSuchElementException {
        if (!isStoryDownloadCompleted)
            throw new NoSuchElementException("Story needs to be loaded before getting or the story doesn't exist!");
        else {
            Story toReturn = story;
            story = null;
            isStoryDownloadCompleted = false;
            return toReturn;
        }
    }

    /**
     * Create field for the user in the database
     *
     * @param firebaseUser
     */
    public void createNewUser(FirebaseUser firebaseUser) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", firebaseUser.getDisplayName());

        db.collection("Users").document(firebaseUser.getUid())
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Directory for new User created!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating directory for new User!", e);
                    }
                });
    }

    public void loadProfilePicture(FirebaseUser user, Context context) {

        File directory = context.getCacheDir();
        File outputFile = null;
        try {
            outputFile = File.createTempFile("profile_picture", "jpg", directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalOutputFile = outputFile;

        mStorageRef.child(user.getUid() + "/profile_picture.jpg")
                .getFile(outputFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        profilePhotoUri = Uri.fromFile(finalOutputFile);
                        isProfilePhotoDownloadCompleted = true;
                        synchronized (LOCK_FOR_PROFILE_PIC) {
                            LOCK_FOR_PROFILE_PIC.notify();
                            Log.d(TAG, "NOTIFIED!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                isProfilePhotoDownloadCompleted = false;
                Log.d(TAG, "Cannot download the photo. The photo may not exist!");
                synchronized (LOCK_FOR_PROFILE_PIC) {
                    LOCK_FOR_PROFILE_PIC.notify();
                    Log.d(TAG, "NOTIFIED!");
                }
            }
        });
    }

    public Uri getProfilePhoto() throws NoSuchElementException {
        if (!isProfilePhotoDownloadCompleted) {
            Log.i("PROFILE_PIC", "No profile picture detected");
            return null;
        } else {
            Uri uri = profilePhotoUri;
            profilePhotoUri = null;
            isProfilePhotoDownloadCompleted = false;
            return uri;
        }
    }
}
