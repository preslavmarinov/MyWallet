package com.example.mywallet.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WalletDB extends SQLiteOpenHelper {
    private static WalletDB instance;

    private static final String DATABASE_NAME = "mywallet";
    private static final int DATABASE_VERSION = 1;

    public WalletDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized WalletDB getInstance(Context context) {
        if(instance == null) {
            instance = new WalletDB(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createTables(db);

        if(!isUsersTableSeeded(db)) {
            this.seedData(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "firstname TEXT, " +
                "lastname TEXT, " +
                "username TEXT, " +
                "password TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "amount REAL, " +
                "categoryId INTEGER, " +
                "userId INTEGER, " +
                "dateCreated TEXT, " +
                "dateUpdated TEXT, " +
                "FOREIGN KEY (categoryId) REFERENCES categories(id) ON DELETE SET NULL, " +
                "FOREIGN KEY (userId) REFERENCES users(id)" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT);");

//        db.execSQL("CREATE TABLE IF NOT EXISTS transactionCategories (" +
//                "transactionId INTEGER, " +
//                "categoryId INTEGER, " +
//                "FOREIGN KEY (transactionId) REFERENCES transactions(id) ON DELETE CASCADE, " +
//                "FOREIGN KEY (categoryId) REFERENCES category(id) ON DELETE SET NULL" +
//                ");");
    }

    private void seedData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO users (firstname, lastname, username, password) VALUES ('Preslav', 'Marinov', 'pmarinov', 'pm12345');");
    }

    private boolean isUsersTableSeeded(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count > 0;
    }
}
