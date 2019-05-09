package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import aau.itcom.rabbithabit.objects.HabitPersonal;

public class HabitDetailsActivity extends AppCompatActivity {

    TextView habitsName;
    TextView habitsDuration;
    TextView habitsDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent receivedIntent = getIntent();
        HabitPersonal habitPersonal = (HabitPersonal) receivedIntent.getSerializableExtra("habit");
        String[] dates = habitPersonal.getArrayOfDates();
        String firstDay = dates[0];
        String lastDay = dates[dates.length-1];
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());




        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitdetails);
        GraphView graph = (GraphView) findViewById(R.id.graph);


        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1),
                new DataPoint(2, 0),
                new DataPoint(3, 1),
                new DataPoint(4, 0),
                new DataPoint(5, 1),
                new DataPoint(6, 0),
                new DataPoint(7, 1)
        });
        graph.addSeries(series);
        graph.getViewport().setMaxY(1);
        series.setShape(PointsGraphSeries.Shape.POINT);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling

        habitsName = findViewById(R.id.textViewHabitsName);
        habitsDetails = findViewById(R.id.textViewDetails);
        habitsDuration = findViewById(R.id.textViewHabitsDuration);

        habitsName.setText(habitPersonal.getName());
        habitsDetails.setText(habitPersonal.getDetails());
        habitsDuration.setText("Duration: "+ firstDay + " - " + lastDay);



    }



    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitDetailsActivity.class);
    }

}

