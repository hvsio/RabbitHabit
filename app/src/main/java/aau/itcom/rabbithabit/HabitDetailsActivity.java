package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import aau.itcom.rabbithabit.objects.HabitPersonal;

public class HabitDetailsActivity extends AppCompatActivity {

    TextView habitsName;
    TextView habitsDuration;
    TextView habitsDetails;
    String[] dates;
    XAxis xAxis;
    YAxis yAxis2;
    YAxis yAxis;
    LineChart chart;
    ArrayList<Entry> dataValues;
    Description description;
    Legend legend;
    HabitPersonal habitPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent receivedIntent = getIntent();
        habitPersonal = (HabitPersonal) receivedIntent.getSerializableExtra("habit");
        dates = habitPersonal.getArrayOfDates();
        String firstDay = dates[0];
        String lastDay = dates[dates.length - 1];
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitdetails);


        habitsName = findViewById(R.id.textViewHabitsName);
        habitsDetails = findViewById(R.id.textViewDetails);
        habitsDuration = findViewById(R.id.textViewHabitsDuration);
        chart = (LineChart) findViewById(R.id.chart);

        habitsName.setText(habitPersonal.getName());
        habitsDetails.setText(habitPersonal.getDetails());
        habitsDuration.setText("Duration: " + firstDay + " - " + lastDay);

        transformDates();
        statsSettings();
        LineDataSet lineDataSet = new LineDataSet(dataValues, "DataSet");
        lineDataSet.setCircleColor(R.color.colorChart);
        lineDataSet.setDrawValues(true);
        lineDataSet.setColor(R.color.colorChart1);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();dataSets.add(lineDataSet);
        LineData data = new LineData(lineDataSet);
        chart.setData(data);
        chart.animateX(2500, Easing.EaseInBounce );




    }

    private int convertBooleanValue(HabitPersonal habit, String date) {
        boolean ifDone = habit.getComplexionMap().get(date);
        System.out.println(date + ifDone);
        if (ifDone) {
            return 1;
        } else {
            return 0;
        }
    }

    private void statsSettings() {
        description = new Description();
        description.setText("Habit fulfillment");
        description.setTextSize(15);
        chart.setDescription(description);
        legend = chart.getLegend();
        legend.setEnabled(true);
        LegendEntry[] legendEntry = new LegendEntry[1];
        LegendEntry entry = new LegendEntry();
        entry.label = habitPersonal.getName();
        legendEntry[0] = entry;
        legend.setCustom(legendEntry);
        chart.setPinchZoom(true);
    }

    private void transformDates() {
        dataValues = new ArrayList<>();

        for (String date : dates) {
            StringBuilder sb = new StringBuilder(date);
            String day = sb.substring(0, 2);
            System.out.println(day);
            dataValues.add(new Entry(Integer.valueOf(day), convertBooleanValue(habitPersonal, date)));
        }

        xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);



        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInt = (int) value;
                return String.valueOf(dataValues.get(valueInt));
            }
        });

        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);
        xAxis.setDrawLabels(true);


        yAxis = chart.getAxisLeft() ;
        yAxis.setDrawGridLines(false);

        yAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInt = (int) value;
                axis.setLabelCount(2);
                return String.valueOf(valueInt);
            }

            });

        chart.getAxisRight().setEnabled(false);

    }

    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitDetailsActivity.class);
    }

//    public class MyValueFormatter extends ValueFormatter implements IValueFormatter {
//        @Override
//        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//            int valueInt = (int) value;
//            return String.valueOf(dataValues.get(valueInt));
//        }
    }




