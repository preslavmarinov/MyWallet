package com.example.mywallet.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mywallet.R;
import com.example.mywallet.common.Utils;
import com.example.mywallet.database.models.Transaction;
import com.example.mywallet.database.repositories.StatisticsRepository;
import com.example.mywallet.database.repositories.TransactionRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YearStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearStatisticsFragment extends Fragment implements OnChartValueSelectedListener {

    TransactionRepository transactionRepository;

    List<Transaction> transactions;

    StatisticsRepository statisticsRepository;

    List<MonthData> monthData = new ArrayList<>();

    long userId;

    public YearStatisticsFragment() {
        // Required empty public constructor
    }

    public static YearStatisticsFragment newInstance() {
        YearStatisticsFragment fragment = new YearStatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transactionRepository = new TransactionRepository(requireContext());
        statisticsRepository = new StatisticsRepository();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.userId = preferences.getLong(Utils.USER_ID_KEY, -1);

        getMonthsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View yearStatsFragment = inflater.inflate(R.layout.fragment_year_statistics, container, false);

        BarChart barChart = yearStatsFragment.findViewById(R.id.barChart);
        setUpBarChart(barChart);
        barChart.setOnChartValueSelectedListener(this);

        return yearStatsFragment;
    }

    private void setUpBarChart(BarChart barChart) {
        List<BarEntry> entries = new ArrayList<>();

        for(int i=0; i<monthData.size(); i++) {
            MonthData monthData = this.monthData.get(i);
            entries.add(new BarEntry(i, new float[]{monthData.getIncome(), monthData.getExpenses()}));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Year Overview");
        dataSet.setColors(getResources().getColor(R.color.light_green), getResources().getColor(R.color.light_red));

        BarData data = new BarData(dataSet);

        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MonthAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(this.monthData.size(), false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        barChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(e instanceof BarEntry) {
            BarEntry barEntry = (BarEntry) e;
            float[] sums = barEntry.getYVals();
            int monthIndex = (int) barEntry.getX();

            TextView monthName = getActivity().findViewById(R.id.monthName);
            TextView incomeView = getActivity().findViewById(R.id.monthIncome);
            TextView expensesView = getActivity().findViewById(R.id.monthExpenses);
            TextView balanceView = getActivity().findViewById(R.id.monthBalance);

            String[] monthsArray = getResources().getStringArray(R.array.months_array);
            monthName.setText(monthsArray[monthIndex]);

            float balance = sums[0] - sums[1];
            incomeView.setText(String.valueOf(sums[0]));
            expensesView.setText(String.valueOf(sums[1]));
            balanceView.setText(String.valueOf(balance));
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private void getMonthsData() {

        for(int i=0; i<12; i++) {
            transactions = transactionRepository.getFilteredTransactions(
                   this.userId,
                   -1,
                   Utils.getFirstDateMonth(i),
                   Utils.getLastDateMonth(i),
                   ""
            );

            float[] monthStats = statisticsRepository.getMonthOverview(transactions);
            monthData.add(new MonthData(monthStats[0], monthStats[1]));
        }

    }


    private static class MonthData {
        private final float income;
        private final float expenses;

        public MonthData(float income, float expenses) {
            this.income = income;
            this.expenses = expenses;
        }

        public float getIncome() {
            return this.income;
        }

        public float getExpenses() {
            return this.expenses;
        }
    }

    private static class MonthAxisValueFormatter extends ValueFormatter {
        private final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if(index >= 0 && index < months.length) {
                return months[index];
            }
            return "";
        }
    }
}
