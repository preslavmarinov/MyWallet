package com.example.mywallet.database.repositories;

import android.content.Context;
import android.util.Log;

import com.example.mywallet.database.WalletDB;
import com.example.mywallet.database.models.Category;
import com.example.mywallet.database.models.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsRepository {

    public StatisticsRepository() {}

    public Map<String, Double> getStatsCategory(List<Transaction> transactions, List<Category> categories) {
        Map<String, Double> stats = new HashMap<>();

        for(int i=0; i<transactions.size(); i++) {
            double amount = transactions.get(i).getAmount();
            String key = getCategoryName(categories, transactions.get(i).getCategoryId());

            if(stats.containsKey(key)) {
                double currVal = stats.get(key);
                stats.put(key, currVal + amount);
            } else {
                stats.put(key, amount);
            }
        }

        return stats;
    }

    public Map<String ,Double> getStatsType(List<Transaction> transactions) {
        Map<String, Double> stats = new HashMap<>();
        for(int i=0; i<transactions.size(); i++) {
            double amount = transactions.get(i).getAmount();
            String key = amount > 0 ? "Income" : "Expense";
            if(stats.containsKey(key)) {
                double currVal = stats.get(key);
                stats.put(key, currVal + amount);
            } else {
                stats.put(key, amount);
            }
        }

        return stats;
    }

    public float[] getMonthOverview(List<Transaction> transactions) {
        double incomeSum = 0;
        double expensesSum = 0;

        for(int i=0; i<transactions.size(); i++) {
            double amount = transactions.get(i).getAmount();

            if(amount > 0) incomeSum += amount;
            else expensesSum += amount;
        }
        float a = (float) incomeSum;
        float b = (float) Math.abs(expensesSum);

        return new float[]{a, b};
    }


    private String getCategoryName(List<Category> categories, long id) {
        for(int i=0; i<categories.size(); i++) {
            if(categories.get(i).getId() == id) {
                return categories.get(i).getName();
            }
        }

        return "";
    }

}
