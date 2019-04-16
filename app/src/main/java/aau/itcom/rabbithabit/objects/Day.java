package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aau.itcom.rabbithabit.HabitDetailsActivity;
import aau.itcom.rabbithabit.R;

public class Day {
    Database db;
    Date date;
    List<HabitPersonal> listOfHabits;
    Story story;
    Photo photo;
    LinearLayout.LayoutParams params;


    public Day(Date date) {
        db = Database.getInstance();
        this.date = date;
        story = db.getStory(date, FirebaseAuth.getInstance().getCurrentUser());
        photo = db.getPhoto(date, FirebaseAuth.getInstance().getCurrentUser());
        listOfHabits = db.getSetHabitPersonalOnDay(date, FirebaseAuth.getInstance().getCurrentUser());

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public ArrayList<TextView> displayHabits(final Context context){
        ArrayList<TextView> arrayOfTextViews = new ArrayList<>();
        for (int i =0; i<listOfHabits.size(); i++){
            TextView textView = new TextView(context);
            textView.setText(listOfHabits.get(i).getName());
            textView.setTextSize(18);
            final HabitPersonal habitPersonal = listOfHabits.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = HabitDetailsActivity.createNewIntent(context);
                    intent.putExtra("habitPersonal",habitPersonal);
                    context.startActivity(intent);
                }
            });
            textView.setLayoutParams(params);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));
            arrayOfTextViews.add(textView);
        }
        return arrayOfTextViews;
    }

    public ImageView displayPhoto(ImageView imageView, Context context){
        //imageView.set
        return imageView;
    }

    public TextView displayStory(TextView textView, Context context){
        textView.setText(story.getTextContent());
        return textView;
    }
}
