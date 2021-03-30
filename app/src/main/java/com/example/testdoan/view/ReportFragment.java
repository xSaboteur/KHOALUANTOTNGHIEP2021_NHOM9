package com.example.testdoan.view;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testdoan.R;
import com.example.testdoan.model.Expense;
import com.example.testdoan.viewmodel.ReportViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReportFragment extends Fragment {

    private ReportViewModel mViewModel;
    private PieChart chart,expenseChart;
    private LineChart lineChart;
    List<Expense> expenses = new ArrayList<>();
    List<Expense> incomes = new ArrayList<>();
    private static final String ARG_mode = "param1";
    private static final String ARG_time = "param2";
    private String mode;
    private String time;

    public static ReportFragment newInstance(String mode, String time) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();

        args.putString(ARG_mode, mode);
        args.putString(ARG_time, time);

        fragment.setArguments(args);
        return fragment;
    }

    public  ReportFragment()
    {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_mode);
            time = getArguments().getString(ARG_time);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.report_fragment, container, false);
        chart =v.findViewById(R.id.chartIncome);
        chart.setTouchEnabled(true);
        lineChart = v.findViewById(R.id.chartLine);
        expenseChart =v.findViewById(R.id.chartExpense);
        expenseChart.setTouchEnabled(true);

        Query query = MainActivity.db
                .collection("users")
                .document("YanMbTpDzBW2VKVBwDoC")
                .collection("expense");

        switch (mode) {
            case "date":
                int day = Integer.valueOf(time.split("-")[0]);
                int month = Integer.valueOf(time.split("-")[1]);
                int year = Integer.valueOf(time.split("-")[2]);
                LocalDate localDate1 = LocalDate.of(year, month, day);
                ZoneId zoneid = ZoneId.systemDefault();
                Instant instant = Instant.now();
                ZoneOffset currentOffsetForMyZone = zoneid.getRules().getOffset(instant);
                Date begin = Date.from(localDate1.atStartOfDay(zoneid).toInstant());
                Date end = Date.from(localDate1.atTime(23, 59, 59).toInstant(currentOffsetForMyZone));
                query = query.whereGreaterThanOrEqualTo("timeCreated", begin).whereLessThanOrEqualTo("timeCreated", end);
                getdataforChart(query);

                createLinechart("date",begin,year, month,day);
                break;
            case "week":
                String time2begin = time.split("-")[0];
                String time2end = time.split("-")[1];
                int day2begin = Integer.valueOf(time2begin.split("/")[0]);
                int month2begin = Integer.valueOf(time2begin.split("/")[1]);
                int year2begin = Integer.valueOf(time2begin.split("/")[2]);
                int day2end = Integer.valueOf(time2end.split("/")[0]);
                int month2end = Integer.valueOf(time2end.split("/")[1]);
                int year2end = Integer.valueOf(time2end.split("/")[2]);
                LocalDate localDate2begin = LocalDate.of(year2begin, month2begin, day2begin);
                LocalDate localDate2end = LocalDate.of(year2end, month2end, day2end);
                ZoneId zoneid2 = ZoneId.systemDefault();
                Instant instant2 = Instant.now();
                ZoneOffset currentOffsetForMyZone2 = zoneid2.getRules().getOffset(instant2);
                Date begin2 = Date.from(localDate2begin.atStartOfDay(zoneid2).toInstant());
                Date end2 = Date.from(localDate2end.atTime(23, 59, 59).toInstant(currentOffsetForMyZone2));
                query = query.whereGreaterThanOrEqualTo("timeCreated", begin2).whereLessThanOrEqualTo("timeCreated", end2);
                getdataforChart(query);

                break;
            case "month":
                month2begin = Integer.valueOf(time.split("/")[0]);
                year2begin = Integer.valueOf(time.split("/")[1]);
                localDate2begin = LocalDate.of(year2begin, month2begin, 1);
                localDate2end = LocalDate.of(year2begin, month2begin, 1).with(TemporalAdjusters.lastDayOfMonth());
                zoneid2 = ZoneId.systemDefault();
                instant2 = Instant.now();
                currentOffsetForMyZone2 = zoneid2.getRules().getOffset(instant2);
                begin2 = Date.from(localDate2begin.atStartOfDay(zoneid2).toInstant());
                end2 = Date.from(localDate2end.atTime(23, 59, 59).toInstant(currentOffsetForMyZone2));
                query = query.whereGreaterThanOrEqualTo("timeCreated", begin2).whereLessThanOrEqualTo("timeCreated", end2);
                getdataforChart(query);
                break;
            case "year":
                year2begin = Integer.valueOf(time);
                localDate2begin = LocalDate.of(year2begin, 1, 1);
                localDate2end = LocalDate.of(year2begin, 12, 31);
                zoneid2 = ZoneId.systemDefault();
                instant2 = Instant.now();
                currentOffsetForMyZone2 = zoneid2.getRules().getOffset(instant2);
                begin2 = Date.from(localDate2begin.atStartOfDay(zoneid2).toInstant());
                end2 = Date.from(localDate2end.atTime(23, 59, 59).toInstant(currentOffsetForMyZone2));
                query = query.whereGreaterThanOrEqualTo("timeCreated", begin2).whereLessThanOrEqualTo("timeCreated", end2);
                getdataforChart(query);
                break;
        }
        return v;
    }

    void handlerIncomeChart(List<Expense> data)
    {
        if(data.size()==0)
            return;
        Map<String, Double> typeAmountMap = new HashMap<>();
        for (Expense e:data) {
            if(typeAmountMap.containsKey(e.getCategory()))
                typeAmountMap.put(e.getCategory(),typeAmountMap.get(e.getCategory()) + e.getAmount());
            else
                typeAmountMap.put(e.getCategory(), e.getAmount());
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Category";

        int[] colors ;
        colors= getContext().getResources().getIntArray(R.array.mdcolor_random);

        //input data and fit data into pie chart entry
        Set<String> local = typeAmountMap.keySet();
        for(String type: local ){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.BLACK);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setValueFormatter(new PercentFormatter());
        pieDataSet.setDrawValues(true);

        chart.setData(pieData);
        chart.animateY(600);
        chart.invalidate();
        chart.setUsePercentValues(true);
        chart.setHighlightPerTapEnabled(true);
        chart.getDescription().setText("");


        Legend l = chart.getLegend(); // get legend of pie
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
     //   l.setDrawInside(false); // set if legend should be drawn inside or not
        chart.setCenterText("Income" );
        chart.setCenterTextSize(23f);
        chart.setCenterTextColor(Color.parseColor("#1F8B24"));
        chart.invalidate();

    }

    void handlerExpenseChart(List<Expense> data)
    {
        if(data.size()==0)
            return;
        Map<String, Double> typeAmountMap = new HashMap<>();
        for (Expense e:data) {
            if(typeAmountMap.containsKey(e.getCategory()))
                typeAmountMap.put(e.getCategory(),typeAmountMap.get(e.getCategory()) + e.getAmount());
            else
                typeAmountMap.put(e.getCategory(), e.getAmount());
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Category";

        int[] colors ;
        colors= getContext().getResources().getIntArray(R.array.mdcolor_random);

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieDataSet.setDrawValues(true);

        expenseChart.setData(pieData);
        expenseChart.animateY(600);
        expenseChart.setUsePercentValues(true);
        expenseChart.setHighlightPerTapEnabled(true);
        expenseChart.getDescription().setText("");
        expenseChart.setCenterTextSize(23f);

        Legend l = expenseChart.getLegend(); // get legend of pie
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        expenseChart.setCenterText("Expense" );
        expenseChart.setCenterTextColor(Color.RED);
        expenseChart.invalidate();

    }

    void getdataforChart(Query query)
    {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Expense iteam = new Expense(doc.getId(), doc.getString("category"), doc.getTimestamp("timeCreated"), doc.getString("note"), doc.getDouble("amount"), doc.getBoolean("expen"));
                        if (iteam.isExpen()) {
                            expenses.add(iteam);
                        } else {
                            incomes.add(iteam);
                        }
                    }
                } else {
                    Log.d("sai r", "Error getting documents: ", task.getException());
                }
                handlerIncomeChart(incomes);
                handlerExpenseChart(expenses);
            }
        });
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createLinechart(String mode, Date time, int year, int month, int day)
    {
        LineData lineData;
        List<Entry> entryList = new ArrayList<>();
        List<Entry> entryList2 = new ArrayList<>();
        switch (mode) {
            case "date":
                lineChart.getDescription().setText("Day in week");
                ZoneId zoneid = ZoneId.systemDefault();
                Instant instant = Instant.now();
                ZoneOffset currentOffsetForMyZone = zoneid.getRules().getOffset(instant);

                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String[] days = new String[7];
                boolean vcl =false;

                for (int i = 0; i < 7 && vcl ; i++)
                {
                   vcl =false;
                    days[i] = format.format(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    LocalDate localDate1 = LocalDate.of(year, month, day);
                    Date begin = Date.from(localDate1.atStartOfDay(zoneid).toInstant());
                    Date end = Date.from(localDate1.atTime(23, 59, 59).toInstant(currentOffsetForMyZone));
                    Query query = MainActivity.db
                            .collection("users")
                            .document("YanMbTpDzBW2VKVBwDoC")
                            .collection("expense");
                    query.whereGreaterThanOrEqualTo("timeCreated", begin)
                            .whereLessThanOrEqualTo("timeCreated", end).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                double expenseSum =0;
                                double incomesum =0 ;
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Expense iteam = new Expense(doc.getId(), doc.getString("category"), doc.getTimestamp("timeCreated"), doc.getString("note"), doc.getDouble("amount"), doc.getBoolean("expen"));
                                    if (iteam.isExpen()) {
                                        expenseSum += iteam.getAmount();
                                    } else {
                                        incomesum += iteam.getAmount();
                                    }
                                }
                                entryList.add(new Entry(i+1,expenseSum));
                                entryList2.add(new Entry(i+1,incomesum));
                            } else {
                                Log.d("sai r", "Error getting documents: ", task.getException());
                            }

                            vcl=true;
                        }
                    });
                    lineChart.getXAxis().setLabelCount(7);
                    lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            switch ((int) value)
                            {
                                case 1:
                                    return getString(R.string.Monday);
                                case 2:
                                    return getString(R.string.Tuesday);
                                case 3:
                                    return getString(R.string.Wednesday);
                                case 4:
                                    return getString(R.string.Thursday);
                                case 5:
                                    return getString(R.string.Friday);
                                case 6:
                                    return getString(R.string.Saturday);
                                case 7:
                                    return getString(R.string.Sunday);


                                default: return "";
                            }
                        }
                    });

                }
                break;
            case "week":
                lineChart.getDescription().setText("Week in month");
                break;
            case "month":

                lineChart.getDescription().setText("Month in year");
                break;
            case "year":

                lineChart.getDescription().setText("Year");
                break;

            default:
                return;


        }


        entryList.add(new Entry(1,20));
        entryList.add(new Entry(2,10));
        entryList.add(new Entry(3,31));
        entryList.add(new Entry(4,14));


        entryList2.add(new Entry(1,3));
        entryList2.add(new Entry(2,5));
        entryList2.add(new Entry(3,8));
        entryList2.add(new Entry(4,9));

        LineDataSet lineDataSet = new LineDataSet(entryList,"Expense");
        LineDataSet lineDataSet2 = new LineDataSet(entryList2,"Income");

       // lineDataSet.setdrawFilled = true;
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.gradient));

        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.gradient));


        lineDataSet.setColor(Color.RED);
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet.setFillAlpha(110);
      //  lineDataSet2.setColor(ColorTemplate.MATERIAL_COLORS);
        lineDataSet2.setFillAlpha(110);

        lineData = new LineData(lineDataSet,lineDataSet2);



        lineChart.setData(lineData);
        lineChart.animateX(500);
        lineChart.setDrawGridBackground(false);
        lineChart.setClickable(true);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);



        lineChart.invalidate();

    }




}