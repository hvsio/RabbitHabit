package aau.itcom.rabbithabit.objects;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import aau.itcom.rabbithabit.CalendarFragment;

public class Database{

    // TODO : UPDATE LOGS AFTER COPY & PASTE

    private static final String TAG = "DatabaseClass";
    private FirebaseFirestore db;
    private static Database instance = null;

    private HabitPersonal habitPersonal;
    private HabitPublished habitPublished;
    private Photo photo;
    private Story story;
    private ArrayList<HabitPersonal> habitPersonals;

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
        map.put("arrayOfDates", habit.getArrayOfDates());
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

    public HabitPersonal getHabitPersonalByName(String name, FirebaseUser user){

        DocumentReference docRef = db.collection("Users").document(user.getUid()).collection("Habits").document(name);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setHabitToReturn(documentSnapshot.toObject(HabitPersonal.class));
            }
        });

        return habitPersonal;
    }

    private void setHabitToReturn(HabitPersonal object) {
        habitPersonal = object;
    }

    public void loadSetOfHabitsOnDate(Date date, FirebaseUser user){

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
                            /*synchronized (CalendarActivity.LOCK_FOR_HABITS){
                                CalendarActivity.LOCK_FOR_HABITS.notify();
                            }*/
                            synchronized (CalendarFragment.LOCK_FOR_HABITS){
                                CalendarFragment.LOCK_FOR_HABITS.notify();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public ArrayList<HabitPersonal> getArrayListOfHabits(){
        Log.d(TAG, "final result() and habitPersonal is: " + habitPersonals);
        return habitPersonals;
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

        DocumentReference docRef = db.collection("Users").document(name);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                setHabitPublishedToReturn(documentSnapshot.toObject(HabitPublished.class));
            }
        });

        return habitPublished;
    }

    private void setHabitPublishedToReturn(HabitPublished object) {
        habitPublished = object;
    }


    public void addPhoto(Photo photo, FirebaseUser user){
        Map<String, Object> map = new HashMap<>();
        map.put("URLinDATABASE", photo.getPhotoURLinDB());

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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

    public void loadPhotoOnDate(final Date date, FirebaseUser user){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        DocumentReference docRef = db.collection("Users").document(user.getUid()).collection("Photos").document(dateFormat.format(date));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                photo = new Photo(date, documentSnapshot.getString("URLinDATABASE"));
                synchronized (CalendarFragment.LOCK_FOR_PHOTO){
                    CalendarFragment.LOCK_FOR_PHOTO.notify();
                }
            }
        });
    }

    public Photo getPhoto() throws NoSuchElementException {
        if (photo == null){
            throw new NoSuchElementException("Photo needs to be loaded before getting");
        }
        return photo;
    }

    public void addStory (Story story, FirebaseUser user){
        Map<String, Object> map = new HashMap<>();
        map.put("storyContent", story.getTextContent());
        map.put("mood", story.getMood());

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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

    public void loadStoryOnDate(final Date date, FirebaseUser user){
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
                            /*synchronized (CalendarActivity.LOCK_FOR_STORY){
                                CalendarActivity.LOCK_FOR_STORY.notify();
                            }*/
                            synchronized (CalendarFragment.LOCK_FOR_STORY){
                                CalendarFragment.LOCK_FOR_STORY.notify();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public Story getStory() throws NoSuchElementException{
        if (story == null){
            throw new NoSuchElementException("Story needs to be loaded before getting");
        }
        return story;
    }

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
