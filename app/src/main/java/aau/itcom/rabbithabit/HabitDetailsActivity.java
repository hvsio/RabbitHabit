package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import aau.itcom.rabbithabit.objects.HabitPersonal;
import aau.itcom.rabbithabit.objects.HabitPublished;

public class HabitDetailsActivity extends AppCompatActivity {

    HabitPersonal habit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitdetails);

        if (getIntent().getSerializableExtra("habitPersonal") != null){
            habit = (HabitPersonal) getIntent().getSerializableExtra("habitPersonal");
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
        series.setShape(PointsGraphSeries.Shape.POINT);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling


    }

    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitDetailsActivity.class);
    }
}
