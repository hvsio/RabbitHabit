package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import aau.itcom.rabbithabit.activities.HabitActivity;
import aau.itcom.rabbithabit.R;

public class HabitPublished extends Habit {
    private String showCreator;
    private String creator;
    private long numberOfLikes;

    public HabitPublished(String name, long duration, String details, String showCreator, String creator, long numberOfLikes) {
        super(name, duration, details);
        this.showCreator = showCreator;
        this.creator = creator;
        this.numberOfLikes = numberOfLikes;
    }

    @Override
    public TextView display(final Context context, int textSize, ViewGroup.LayoutParams params, final Habit listener, Date date) {
        Log.d("display() in hPublished", " am inside");
        TextView textView = new TextView(context);
        textView.setText(listener.getName());
        textView.setTextSize(textSize);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HabitActivity.createNewIntent(context);
                intent.putExtra("habit", listener);
                context.startActivity(intent);
            }
        });
        textView.setLayoutParams(params);
        textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));
        return textView;
    }

    public String getShowCreator() {
        return showCreator;
    }

    public String getCreator() {
        return creator;
    }

    public void incrementNumberOfLikes(){
        numberOfLikes++;
    }

    public long getNumberOfLikes() {
        return numberOfLikes;
    }

}
