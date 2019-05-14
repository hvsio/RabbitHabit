package aau.itcom.rabbithabit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import aau.itcom.rabbithabit.R;
import aau.itcom.rabbithabit.objects.HabitPersonal;

public class CustomAdapterDayHabit extends ArrayAdapter<HabitPersonal> {

    public CustomAdapterDayHabit(@NonNull Context context, ArrayList<HabitPersonal> habitPersonals) {
        super(context, 0, habitPersonals);
    }

    private static class ViewHolder {
        TextView habitName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HabitPersonal habit = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.habit_day_adapter, parent, false);
            viewHolder.habitName = convertView.findViewById(R.id.habits_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.habitName.setText(habit.getName());
        return convertView;
    }

}
