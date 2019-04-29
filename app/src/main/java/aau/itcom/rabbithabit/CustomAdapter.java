package aau.itcom.rabbithabit;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import aau.itcom.rabbithabit.objects.Habit;

public class CustomAdapter extends ArrayAdapter<Habit> implements View.OnClickListener {

    private ArrayList<Habit> habitArrayList;
    Context mContext;
    public static final String PASS_HABIT_NAME = "HABIT_NAME";
    public static final String PASS_HABIT_DURATION = "HABIT_DURATION";
    public static final String PASS_HABIT_DETAILS = "HABIT_DETAILS";
    public static final int OPEN_ACTIVITY = 1;

    private static class ViewHolder {
        TextView nameOfHabit;
        Button addButton;
        ImageView info;
    }


    public CustomAdapter(ArrayList<Habit> habitArrayList, Context context) {
        super(context, R.layout.custom_list_row, habitArrayList);
        this.habitArrayList = habitArrayList;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Habit habit = (Habit) object;
        assert habit != null;
        switch (v.getId()) {
            case R.id.item_info:
                Snackbar.make(v, "Details " + habit.getDetails(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
            case R.id.addButton:
                Intent intent = new Intent(mContext, HabitActivity.class);
                Bundle extras = new Bundle();
                extras.putString(PASS_HABIT_NAME, habit.getName());
                extras.putString(PASS_HABIT_DETAILS, habit.getDetails());
                extras.putString(PASS_HABIT_DURATION, String.valueOf(habit.getDuration()));
                intent.putExtras(extras);
                mContext.startActivity(intent);

        }
    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mContext = parent.getContext();

        // Get the data item for this position
        final Habit habit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list_row, parent, false);
            viewHolder.nameOfHabit = convertView.findViewById(R.id.name);
            viewHolder.addButton = convertView.findViewById(R.id.addButton);
            viewHolder.info = convertView.findViewById(R.id.item_info);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert habit != null;
        viewHolder.nameOfHabit.setText(habit.getName());
        viewHolder.info.setTag(position);
        viewHolder.addButton.setTag(position);
        viewHolder.addButton.setOnClickListener(this);
        // Return the completed view to render on screen

        return convertView;
    }

}
