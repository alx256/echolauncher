package com.example.echolauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InvalidObjectException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HomeScreenLocations extends SQLiteOpenHelper {
    public HomeScreenLocations(@Nullable Context context) {
        super(context, "HomeScreenLocationsDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table if necessary
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                "HomeScreenLocations(Identifier VARCHAR, Position INT, Screen INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS HomeScreenLocations");
        onCreate(db);
    }

    public void writeItemToDatabase(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Identifier", item.getIdentifier());
        values.put("Position", item.getGridIndex());
        values.put("Screen", 0);
        db.insert("HomeScreenLocations", null, values);
    }

    public void readFromDatabase() throws SQLException, InvalidObjectException {
        SQLiteDatabase db = getReadableDatabase();

        // Select all stored items
        Cursor cursor = db.query("HomeScreenLocations",
                new String[] {"Identifier", "Position", "Screen"},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            HomeScreenGrid.updateGrid(cursor.getInt(cursor.getColumnIndex("Position")),
                    HomeScreenGridAdapter.Instruction.ADD,
                    Search.get(cursor.getString(cursor.getColumnIndex("Identifier"))));
            cursor.moveToNext();
        }

        cursor.close();
    }

    public void removeItemFromDatabase(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete("HomeScreenLocations",
                "Identifier = ? AND Position = ?",
                new String[]{item.getIdentifier(), Integer.toString(item.getGridIndex())});
        db.close();
    }
}
