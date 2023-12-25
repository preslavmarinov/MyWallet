package com.example.mywallet.database.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mywallet.database.WalletDB;
import com.example.mywallet.database.models.User;

public class UserRepository {
    private final WalletDB walletDB;

    public UserRepository(Context context) {
        this.walletDB = WalletDB.getInstance(context);
    }

    public long createUser(User user) {
        SQLiteDatabase db = walletDB.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("firstname", user.getFirstName());
        values.put("lastname", user.getLastName());
        values.put("username", user.getUserName());
        values.put("password", user.getPassword());

        long userId = db.insert("users", null, values);
        db.close();

        return userId;
    }

    public long loginUser(String username, String password) {
        SQLiteDatabase db = walletDB.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {"id"};
            String selection = "username = ? AND password = ?";
            String[] selectionArgs = {username, password};

            cursor = db.query("users", projection, selection, selectionArgs, null, null, null);

            if(cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            } else {
                return 0;
            }
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public User getUserById(long id) {
        SQLiteDatabase db = walletDB.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = { "firstname", "lastname", "username", "password"};
            String selection = "id = ?";
            String[] selectionArgs = { String.valueOf(id) };

            cursor = db.query("users", projection, selection, selectionArgs, null, null, null);
            if(cursor.moveToFirst()) {
                User user = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow("firstname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("lastname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password"))
                );

                return user;
            }
            else {
                return null;
            }
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = walletDB.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {"id"};
            String selection = "username = ?";
            String[] selectionArgs = {username};

            cursor = db.query("users", projection, selection, selectionArgs, null, null, null);

            return cursor.moveToFirst();
        } finally {
            // Close the cursor and database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
}
