package aau.itcom.rabbithabit.objects;

import android.widget.TextView;

public class HabitPublished extends Habit {
    private String showCreator;
    private String creator;
    private int suggestedDuration;
    private int numberOfLikes;

    public HabitPublished(String name, int duration, String showCreator, String creator, int suggestedDuration, int numberOfLikes) {
        super(name, duration);
        this.showCreator = showCreator;
        this.creator = creator;
        this.suggestedDuration = suggestedDuration;
        this.numberOfLikes = numberOfLikes;
    }

    @Override
    void display(TextView textView) {
        // TODO : CHANGE IT ACCORDINGLY
        super.display(textView);
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

    public int getSuggestedDuration() {
        return suggestedDuration;
    }

    public void setSuggestedDuration(int suggestedDuration) {
        this.suggestedDuration = suggestedDuration;
    }
}
