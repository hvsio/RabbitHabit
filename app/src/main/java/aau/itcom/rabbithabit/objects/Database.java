package aau.itcom.rabbithabit.objects;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Database {

    // TODO : MAKE IT SINGLETON !!!
    // TODO : UPDATE LOGS AFTER COPY & PASTE

    private static final String TAG = "DatabaseClass";
    FirebaseFirestore db;
    static Database instance = null;

    private Database(){
        db = FirebaseFirestore.getInstance();
    }

    public synchronized static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public void addHabitPersonal(HabitPersonal habit, FirebaseUser user){
        Map<String, Object> map = new HashMap<>();
        map.put("startDate", habit.getStartDate());
        map.put("endDate", habit.getEndDate());
        map.put("duration", habit.getDuration());
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

    public HabitPersonal getHabitPersonalByName(String name){
        return null;
    }

    public HabitPersonal getHabitPersonalByDate(Date date){
        return null;
    }

    public void addHabitPublished(HabitPublished habit){
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

    public HabitPublished getHabitPublishedByName(String name){
        return null;
    }

    public void addPhoto(Photo photo, FirebaseUser user){
        Map<String, Object> map = new HashMap<>();
        map.put("URLinDATABASE", photo.getPhotoURLinDB());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

        db.collection("Users").document(user.getUid()).collection("Photos").document(dateFormat.format(photo.getDate()))
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

    public Photo getPhoto(Date date){return null;}

    public void addStory (Story story, FirebaseUser user){
        Map<String, Object> map = new HashMap<>();
        map.put("storyContent", story.getTextContent());
        map.put("mood", story.getMood());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

        db.collection("Users").document(user.getUid()).collection("Stories").document(dateFormat.format(story.getDate()))
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

    public Story getStory (Date date){return null; }

    /**
     * Create field for the user in the database
     * @param firebaseUser
     */
    public void createNewUser (FirebaseUser firebaseUser){
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
}
