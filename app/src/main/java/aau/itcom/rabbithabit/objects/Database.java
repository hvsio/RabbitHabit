package aau.itcom.rabbithabit.objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Database {

    // TODO : MAKE IT SINGLETON !!!

    private Database(){}
    public void getInstance(){
    }

    public void addHabitPersonal(HabitPersonal habit, FirebaseUser user){
    }

    public HabitPersonal getHabitPersonalByName(String name){
        return null;
    }

    public HabitPersonal getHabitPersonalByDate(Date date){
        return null;
    }

    public void addHabitPublished(HabitPublished habit){
    }

    public HabitPublished getHabitPublishedByName(String name){
        return null;
    }

    public void addPhoto(Photo photo){}

    public Photo getPhoto(Date date){return null;}

    public void addStory (Story story){}

    public Story getStory (Date date){return null;}
}
