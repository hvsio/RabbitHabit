package aau.itcom.rabbithabit.objects;

import java.util.Date;


public class Story {

    private Date date;
    private String textContent;
    private long mood;

    public Story(Date date, String textContent, long mood) {
        this.date = date;
        this.textContent = textContent;
        this.mood = mood;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public long getMood() {
        return mood;
    }

    public void setMood(long mood) {
        this.mood = mood;
    }

}
