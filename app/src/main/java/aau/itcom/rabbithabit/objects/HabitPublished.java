package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

public class HabitPublished extends Habit {
    private String showCreator;
    private String creator;
    private int numberOfLikes;

    public HabitPublished(String name, int duration, String details, String showCreator, String creator, int numberOfLikes) {
        super(name, duration, details);
        this.showCreator = showCreator;
        this.creator = creator;
        this.numberOfLikes = numberOfLikes;
    }

    public String getShowCreator() {
        return showCreator;
    }

    public void setShowCreator(String showCreator) {
        this.showCreator = showCreator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
