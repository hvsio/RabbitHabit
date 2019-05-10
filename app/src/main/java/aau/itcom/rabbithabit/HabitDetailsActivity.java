package aau.itcom.rabbithabit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

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
        String lastDay = dates[dates.length-1];
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());




        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitdetails);


        habitsName = findViewById(R.id.textViewHabitsName);
        habitsDetails = findViewById(R.id.textViewDetails);
        habitsDuration = findViewById(R.id.textViewHabitsDuration);
        chart = (LineChart) findViewById(R.id.chart);

        habitsName.setText(habitPersonal.getName());
        habitsDetails.setText(habitPersonal.getDetails());
        habitsDuration.setText("Duration: "+ firstDay + " - " + lastDay);

        transformDates();
        statsSettings();
        LineDataSet lineDataSet = new LineDataSet(dataValues, "DataSet");
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineDataSet);
        LineData data= new LineData(lineDataSet);
        data.setValueFormatter(new MyValueFormatter());
        chart.setData(data);
        chart.invalidate();





    }

    private void statsSettings() {
        chart.setDrawBorders(true);
        description = new Description();
        description.setText("Habit fulfillment");
        description.setTextSize(20);
        chart.setDescription(description);
        legend = chart.getLegend();
        legend.setEnabled(true);
        LegendEntry [] legendEntry = new LegendEntry[1];
        LegendEntry entry = new LegendEntry();
        entry.label = habitPersonal.getName();
        legendEntry[0] = entry;
        legend.setCustom(legendEntry);
        chart.setDrawGridBackground(false);
    }

    private void transformDates() {
        dataValues = new ArrayList<>();

        for (String date: dates) {
            StringBuilder sb = new StringBuilder(date);
            String day = sb.substring(0,2);
            System.out.println(day);
            dataValues.add(new Entry(Integer.valueOf(day), 1));
        }

        xAxis = chart.getXAxis();
       // xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInt = (int) value;
                axis.setLabelCount(dataValues.size());
                return String.valueOf(dataValues.get(valueInt));
            }
        });
        yAxis = chart.getAxisLeft() ;
        yAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInt = (int) value;
                axis.setLabelCount(2);
                return String.valueOf(value);
            }

            });

        yAxis2 = chart.getAxisRight() ;
        yAxis2.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valueInt = (int) value;
                axis.setLabelCount(2);
                return String.valueOf(value);
            }

        });



    }

    private class MyValueFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf(value);
        }
    }



    public static Intent createNewIntent(Context context) {
        return new Intent(context, HabitDetailsActivity.class);
    }

}

