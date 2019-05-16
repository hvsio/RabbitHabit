package aau.itcom.rabbithabit.objects;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aau.itcom.rabbithabit.activities.HabitDetailsActivity;
import aau.itcom.rabbithabit.R;

public class HabitPersonal extends Habit{
    private Date startDate;
    private String[] arrayOfDates;
    private Map<String, Boolean> complexion;
    private HabitPersonal reference = this;



    public HabitPersonal(String name, long duration, String details, Date startDate, @Nullable Map<String, Boolean> complexion) {
        super(name, duration, details);
        this.startDate = startDate;

        if (complexion != null) {
            this.complexion = new HashMap<>(complexion);
            initializeCArrayOfDates(startDate, duration);
        } else {
            initializeComplexionAndArrayOfDates(startDate, duration);
        }
    }

    private void initializeComplexionAndArrayOfDates(Date startDate, long duration) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        arrayOfDates = new String[(int) duration];
        complexion = new HashMap<>();

        for (int i = 0; i < duration; i++){
            arrayOfDates[i] = dateFormat.format(calendar.getTime());
            complexion.put(arrayOfDates[i], false);
            calendar.add(Calendar.DATE, 1);
        }
    }

    private void initializeCArrayOfDates(Date startDate, long duration) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        arrayOfDates = new String[(int) duration];

        for (int i = 0; i < duration; i++){
            arrayOfDates[i] = dateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
    }

    @Override
    public TextView display(final Context context, int textSize, ViewGroup.LayoutParams params, final Habit listener, Date date) {
        Log.d("DISPLAY IN HABIT PRS!", " am inside");
        final TextView textView = new TextView(context);
        textView.setText(listener.getName());
        textView.setTextSize(textSize);
        textView.setLayoutParams(params);
        textView.setVisibility(View.VISIBLE);
        //textView.setPadding(10,10,10,10);
        //textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));

        if (listener instanceof HabitPersonal){
            HabitPersonal habit = (HabitPersonal) listener;
            if (habit.isCompletedOn(date)) {
                textView.setBackgroundResource(R.drawable.my_button);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_done_green, 0);
            } else {
                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.my_button_white));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_not_done, 0);
            }
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HabitDetailsActivity.createNewIntent(context);
                intent.putExtra("habit", listener);
                context.startActivity(intent);
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isCompletedOn(Calendar.getInstance().getTime())) {
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    complexion.put(dateFormat.format(Calendar.getInstance().getTime()), true);
                    //db.addHabitPersonal(reference, FirebaseAuth.getInstance().getCurrentUser());
                    Database.updateComplexion(reference);
                    // Get instance of Vibrator from current Context
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 300 milliseconds
                    vibrator.vibrate(300);

                    textView.setBackgroundResource(R.drawable.my_button);
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_done_green, 0);
                }
                return true;
            }
        });

        return textView;
    }

    public Date getStartDate() {
        return startDate;
    }

    public boolean isCompletedOn(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        if (complexion.get(dateFormat.format(date)))
            return true;
        else
            return false;
    }

    public String[] getArrayOfDates(){
        return arrayOfDates;
    }

    public Map<String,Boolean> getComplexionMap(){
        return complexion;
    }

    public long numberOfDaysLeft (Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateInString = dateFormat.format(date);

        for (int i=0; i < arrayOfDates.length; i++){
            if (arrayOfDates[i].equals(dateInString))
                return arrayOfDates.length - i;
        }
        return 0;
    }
}
