package com.example.echolauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.InvalidObjectException;

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
        values.put("Screen", item.getPageNumber());
        db.insert("HomeScreenLocations", null, values);
        db.close();
    }

    public void readFromDatabase() throws InvalidObjectException {
        SQLiteDatabase db = getReadableDatabase();

        // Select all stored items
        Cursor cursor = db.query("HomeScreenLocations",
                new String[] {"Identifier", "Position", "Screen"},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String identifier = cursor.getString(cursor.getColumnIndex("Identifier"));
            Item item = Search.get(identifier).clone();
            item.setGridIndex(cursor.getInt(cursor.getColumnIndex("Position")));
            item.setPageNumber(cursor.getInt(cursor.getColumnIndex("Screen")));

            Pages.addItem(item);
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
