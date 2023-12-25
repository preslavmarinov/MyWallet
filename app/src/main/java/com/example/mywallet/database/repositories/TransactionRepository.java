package com.example.mywallet.database.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mywallet.database.WalletDB;
import com.example.mywallet.database.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    private WalletDB walletDB;

    public TransactionRepository(Context context) {
        this.walletDB = WalletDB.getInstance(context);
    }

    public List<Transaction> getFilteredTransactions(
            long userId,
            long categoryId,
            String startDate,
            String endDate,
            String type
    ) {
        SQLiteDatabase db = walletDB.getReadableDatabase();
        Cursor cursor = null;
        List<Transaction> transactions = new ArrayList<>();

        try {
            String[] projection = { "id", "amount", "categoryId", "userId", "dateCreated", "dateUpdated"};
            String selection = "userId = ? AND dateCreated BETWEEN ? AND ?";
            List<String> selectionArgs = new ArrayList<>();
            selectionArgs.add(String.valueOf(userId));
            selectionArgs.add(startDate);
            selectionArgs.add(endDate);

            if(categoryId != -1) {
                selection += " AND categoryId = ?";
                selectionArgs.add(String.valueOf(categoryId));
            }

            if(type.equals("Income")) {
                selection += " AND amount > 0";
            } else if(type.equals("Expense")) {
                selection += " AND amount < 0";
            }
           String[] selectionArgsArray = selectionArgs.toArray(new String[0]);
            String sortOrder = "dateCreated DESC";

            cursor = db.query("transactions", projection, selection, selectionArgsArray, null, null, sortOrder);

            if(cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                    String dateCreated = cursor.getString(cursor.getColumnIndexOrThrow("dateCreated"));
                    String dateUpdated = cursor.getString(cursor.getColumnIndexOrThrow("dateUpdated"));
                    long categoryIdFromDB = cursor.getLong(cursor.getColumnIndexOrThrow("categoryId"));
                    long userIdFromDB = cursor.getLong(cursor.getColumnIndexOrThrow("userId"));

                    Transaction transaction = new Transaction(amount, dateCreated, dateUpdated, categoryIdFromDB, userIdFromDB);
                    transaction.setId(id);
                    transactions.add(transaction);
                }
                while (cursor.moveToNext());

                return transactions;
            }
            return transactions;
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public long createTransaction(Transaction transaction) {
        SQLiteDatabase db = walletDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("amount", transaction.getAmount());
        values.put("categoryId", transaction.getCategoryId());
        values.put("userId", transaction.getUserId());
        values.put("dateCreated", transaction.getDateCreated());
        values.put("dateUpdated", transaction.getDateUpdated());

        long transactionId = db.insert("transactions", null, values);
        db.close();

        return transactionId;
    }

    public void updateTransaction(Transaction transaction) {
        SQLiteDatabase db = walletDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("amount", transaction.getAmount());
        values.put("categoryId", transaction.getCategoryId());
        values.put("userId", transaction.getUserId());
        values.put("dateCreated", transaction.getDateCreated());
        values.put("dateUpdated", transaction.getDateUpdated());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(transaction.getId())};

        db.update("transactions", values, whereClause, whereArgs);
        db.close();
    }

    public void deleteTransaction(long id) {
        SQLiteDatabase db = walletDB.getWritableDatabase();

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.delete("transactions", whereClause, whereArgs);
        db.close();
    }
}
