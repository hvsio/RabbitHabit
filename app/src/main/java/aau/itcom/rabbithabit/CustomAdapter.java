package aau.itcom.rabbithabit;


import android.content.Context;
import android.support.design.widget.Snackbar;
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

public class CustomAdapter extends ArrayAdapter<Habit> implements View.OnClickListener{

    private ArrayList<Habit> habitArrayList;
    Context mContext;

    private static class ViewHolder {
        TextView nameOfHabit;
        Button addButton;
        ImageView info;
    }

    public CustomAdapter(ArrayList<Habit> habitArrayList, Context context) {
        super(context, R.layout.custom_list_row, habitArrayList);
        this.habitArrayList = habitArrayList;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Habit habit=(Habit)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Details " +habit.getDetails(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Habit habit = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list_row, parent, false);
            viewHolder.nameOfHabit = (TextView) convertView.findViewById(R.id.name);
            viewHolder.addButton = (Button) convertView.findViewById(R.id.button12);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.nameOfHabit.setText(habit.getName());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
