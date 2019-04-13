package aau.itcom.rabbithabit.objects;

import java.util.Date;

public class Photo {
    private Date date;
    private String photoURLinDB;

    public Photo(Date date, String photoURLinDB) {
        this.date = date;
        this.photoURLinDB = photoURLinDB;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhotoURLinDB() {
        return photoURLinDB;
    }

    public void setPhotoURLinDB(String photoURLinDB) {
        this.photoURLinDB = photoURLinDB;
    }
}
