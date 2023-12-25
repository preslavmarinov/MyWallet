package com.example.mywallet.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mywallet.R;
import com.example.mywallet.common.Utils;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.database.models.Transaction;
import com.example.mywallet.database.repositories.CategoryRepository;
import com.example.mywallet.database.repositories.StatisticsRepository;
import com.example.mywallet.database.repositories.TransactionRepository;
import com.example.mywallet.main.Adapter.TransactionAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private CategoryRepository categoryRepository;
    private List<Category> categories;

    private TransactionRepository transactionRepository;

    private List<Transaction> transactions;

    private String startDate;

    private String endDate;

    private long userId;

    private String selectedType;

    private String selectedStat;

    private PieChart pieChart;

    private StatisticsRepository statisticsRepository;

    private Map<String, Double> stats = new HashMap<>();


    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startDate = Utils.getFirstDateMonth(Calendar.getInstance().get(Calendar.MONTH));
        endDate = Utils.getLastDateMonth(Calendar.getInstance().get(Calendar.MONTH));
        selectedType = "";
        selectedStat = "";

        categoryRepository = new CategoryRepository(requireContext());
        categories = categoryRepository.getAllCategories();

        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        this.userId = preferences.getLong(Utils.USER_ID_KEY, -1);

        transactionRepository = new TransactionRepository(requireContext());
        transactions = transactionRepository.getFilteredTransactions(
                this.userId,
                -1,
                startDate,
                endDate,
                selectedType);

        statisticsRepository = new StatisticsRepository();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View statisticFragment =  inflater.inflate(R.layout.fragment_statistics, container, false);

        this.pieChart = statisticFragment.findViewById(R.id.pieChart);

        setUpPieChart();

        Spinner monthSpinner = statisticFragment.findViewById(R.id.monthSpinnerStats);
        Spinner statsSpinner = statisticFragment.findViewById(R.id.statsSpinner);

        Button filterBtn = statisticFragment.findViewById(R.id.filterBtnStats);
        Button yearlyStatsBtn = statisticFragment.findViewById(R.id.yearlyStatsBtn);

        setUpMonthSpinner(monthSpinner);
        setUpStatsSpinner(statsSpinner);

        setUpFilterListener(filterBtn);
        setUpYearlyStatsListener(yearlyStatsBtn);

        getStatistics();
        
        return statisticFragment;
    }

    private void setUpPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        for(Map.Entry<String, Double> entry : stats.entrySet()) {
            double dval = entry.getValue();
            float val = (float) dval;
            entries.add(new PieEntry(Math.abs(val), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(20f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
    }

    private void getStatistics() {
        if(selectedStat.equals("Income and Expenses")) {
            stats = statisticsRepository.getStatsType(transactions);
        }
        else stats = statisticsRepository.getStatsCategory(transactions, categories);
        setUpPieChart();
    }


    private void setUpMonthSpinner(Spinner spinner) {
        String[] months = getResources().getStringArray(R.array.months_array);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(monthAdapter);
        spinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String firstDateStr = Utils.getFirstDateMonth(i);
                String lastDateStr = Utils.getLastDateMonth(i);
                startDate = firstDateStr;
                endDate = lastDateStr;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpStatsSpinner(Spinner spinner) {
        String[] statOptions = getResources().getStringArray(R.array.stats_array);

        ArrayAdapter<String> statAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statOptions);

        statAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(statAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStat = statOptions[i];
                if(i == 2) selectedType = "Income";
                else if(i == 3) selectedType = "Expense";
                else selectedType = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpFilterListener(Button btn) {
        btn.setOnClickListener(view -> {
            transactions = transactionRepository.getFilteredTransactions(
                    this.userId,
                    -1,
                    this.startDate,
                    this.endDate,
                    this.selectedType
            );
            getStatistics();
        });
    }

    private void setUpYearlyStatsListener(Button btn) {
        btn.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
            );
            fragmentTransaction.replace(R.id.fragmentContainerMain, new YearStatisticsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }


}