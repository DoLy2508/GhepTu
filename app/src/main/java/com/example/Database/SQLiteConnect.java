package com.example.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteConnect extends SQLiteOpenHelper {

    public SQLiteConnect(@Nullable Context context,
                         @Nullable String name,
                         @Nullable SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS tuvungGT (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "maTu TEXT NOT NULL," +
                        "tiengAnh TEXT NOT NULL," +
                        "tiengViet TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tuvungGT");
        onCreate(db);
    }

    // truy vấn không trả kết quả: CREATE, INSERT, UPDATE,...
    public void queryData(String query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    // truy vấn trả về kết quả: SELECT,...
    public Cursor getData(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }
}
