package com.example.mywallet.database.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mywallet.database.WalletDB;
import com.example.mywallet.database.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private WalletDB walletDB;

    public CategoryRepository(Context context) {
        this.walletDB = WalletDB.getInstance(context.getApplicationContext());
    }

    public long createCategory(Category category) {
        SQLiteDatabase db = walletDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", category.getName());
        values.put("description", category.getDescription());

        long categoryId = db.insert("categories", null, values);
        db.close();

        return categoryId;
    }

    public List<Category> getAllCategories() {
        SQLiteDatabase db = walletDB.getReadableDatabase();
        List<Category> categories = new ArrayList<>();
        Cursor cursor = null;

        try {
            String[] projection = {"id", "name", "description"};
            cursor = db.query("categories", projection, null, null, null, null, null);
            if(cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                    String  name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String  description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

                    Category category = new Category(name, description);
                    category.setId(id);
                    categories.add(category);
                } while (cursor.moveToNext());

                return categories;
            }
            return categories;
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = walletDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", category.getName());
        values.put("description", category.getDescription());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(category.getId())};

        db.update("categories", values, whereClause, whereArgs);
        db.close();
    }

    public void deleteCategory(long id) {
        SQLiteDatabase db = walletDB.getWritableDatabase();

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};

        db.delete("categories", whereClause, whereArgs);
        db.close();
    }
}
