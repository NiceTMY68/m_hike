package com.example.m_hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "m_hike.db";
    private static final int DATABASE_VERSION = 1;

    // Hikes table
    private static final String TABLE_HIKES = "hikes";
    private static final String COLUMN_HIKE_ID = "id";
    private static final String COLUMN_HIKE_NAME = "name";
    private static final String COLUMN_HIKE_LOCATION = "location";
    private static final String COLUMN_HIKE_DATE = "date";
    private static final String COLUMN_HIKE_PARKING = "parking_available";
    private static final String COLUMN_HIKE_LENGTH = "length";
    private static final String COLUMN_HIKE_DIFFICULTY = "difficulty";
    private static final String COLUMN_HIKE_DESCRIPTION = "description";
    private static final String COLUMN_HIKE_WEATHER = "weather";
    private static final String COLUMN_HIKE_DURATION = "estimated_duration";

    // Observations table
    private static final String TABLE_OBSERVATIONS = "observations";
    private static final String COLUMN_OBS_ID = "id";
    private static final String COLUMN_OBS_HIKE_ID = "hike_id";
    private static final String COLUMN_OBS_OBSERVATION = "observation";
    private static final String COLUMN_OBS_TIME = "time";
    private static final String COLUMN_OBS_COMMENTS = "additional_comments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createHikesTable = "CREATE TABLE " + TABLE_HIKES + " (" +
                COLUMN_HIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_HIKE_NAME + " TEXT NOT NULL, " +
                COLUMN_HIKE_LOCATION + " TEXT NOT NULL, " +
                COLUMN_HIKE_DATE + " TEXT NOT NULL, " +
                COLUMN_HIKE_PARKING + " TEXT NOT NULL, " +
                COLUMN_HIKE_LENGTH + " TEXT NOT NULL, " +
                COLUMN_HIKE_DIFFICULTY + " TEXT NOT NULL, " +
                COLUMN_HIKE_DESCRIPTION + " TEXT, " +
                COLUMN_HIKE_WEATHER + " TEXT, " +
                COLUMN_HIKE_DURATION + " TEXT" +
                ")";

        String createObservationsTable = "CREATE TABLE " + TABLE_OBSERVATIONS + " (" +
                COLUMN_OBS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_OBS_HIKE_ID + " INTEGER NOT NULL, " +
                COLUMN_OBS_OBSERVATION + " TEXT NOT NULL, " +
                COLUMN_OBS_TIME + " TEXT NOT NULL, " +
                COLUMN_OBS_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_OBS_HIKE_ID + ") REFERENCES " + TABLE_HIKES + "(" + COLUMN_HIKE_ID + ")" +
                ")";

        db.execSQL(createHikesTable);
        db.execSQL(createObservationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIKES);
        onCreate(db);
    }

    // Hike CRUD operations
    public long addHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_NAME, hike.getName());
        values.put(COLUMN_HIKE_LOCATION, hike.getLocation());
        values.put(COLUMN_HIKE_DATE, hike.getDate());
        values.put(COLUMN_HIKE_PARKING, hike.getParkingAvailable());
        values.put(COLUMN_HIKE_LENGTH, hike.getLength());
        values.put(COLUMN_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(COLUMN_HIKE_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_HIKE_WEATHER, hike.getWeather());
        values.put(COLUMN_HIKE_DURATION, hike.getEstimatedDuration());

        long id = db.insert(TABLE_HIKES, null, values);
        db.close();
        return id;
    }

    public List<Hike> getAllHikes() {
        List<Hike> hikes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HIKES + " ORDER BY " + COLUMN_HIKE_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hikes;
    }

    public Hike getHike(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIKES, null, COLUMN_HIKE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Hike hike = null;
        if (cursor != null && cursor.moveToFirst()) {
            hike = cursorToHike(cursor);
            cursor.close();
        }
        db.close();
        return hike;
    }

    public int updateHike(Hike hike) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HIKE_NAME, hike.getName());
        values.put(COLUMN_HIKE_LOCATION, hike.getLocation());
        values.put(COLUMN_HIKE_DATE, hike.getDate());
        values.put(COLUMN_HIKE_PARKING, hike.getParkingAvailable());
        values.put(COLUMN_HIKE_LENGTH, hike.getLength());
        values.put(COLUMN_HIKE_DIFFICULTY, hike.getDifficulty());
        values.put(COLUMN_HIKE_DESCRIPTION, hike.getDescription());
        values.put(COLUMN_HIKE_WEATHER, hike.getWeather());
        values.put(COLUMN_HIKE_DURATION, hike.getEstimatedDuration());

        int result = db.update(TABLE_HIKES, values, COLUMN_HIKE_ID + "=?",
                new String[]{String.valueOf(hike.getId())});
        db.close();
        return result;
    }

    public void deleteHike(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Also delete all observations for this hike
        db.delete(TABLE_OBSERVATIONS, COLUMN_OBS_HIKE_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_HIKES, COLUMN_HIKE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllHikes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OBSERVATIONS, null, null);
        db.delete(TABLE_HIKES, null, null);
        db.close();
    }

    // Observation CRUD operations
    public long addObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OBS_HIKE_ID, observation.getHikeId());
        values.put(COLUMN_OBS_OBSERVATION, observation.getObservation());
        values.put(COLUMN_OBS_TIME, observation.getTime());
        values.put(COLUMN_OBS_COMMENTS, observation.getAdditionalComments());

        long id = db.insert(TABLE_OBSERVATIONS, null, values);
        db.close();
        return id;
    }

    public List<Observation> getObservationsByHikeId(long hikeId) {
        List<Observation> observations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OBSERVATIONS, null, COLUMN_OBS_HIKE_ID + "=?",
                new String[]{String.valueOf(hikeId)}, null, null, COLUMN_OBS_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                observations.add(cursorToObservation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return observations;
    }

    public Observation getObservation(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_OBSERVATIONS, null, COLUMN_OBS_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Observation observation = null;
        if (cursor != null && cursor.moveToFirst()) {
            observation = cursorToObservation(cursor);
            cursor.close();
        }
        db.close();
        return observation;
    }

    public int updateObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_OBS_OBSERVATION, observation.getObservation());
        values.put(COLUMN_OBS_TIME, observation.getTime());
        values.put(COLUMN_OBS_COMMENTS, observation.getAdditionalComments());

        int result = db.update(TABLE_OBSERVATIONS, values, COLUMN_OBS_ID + "=?",
                new String[]{String.valueOf(observation.getId())});
        db.close();
        return result;
    }

    public void deleteObservation(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OBSERVATIONS, COLUMN_OBS_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Search operations
    public List<Hike> searchHikesByName(String name) {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HIKES, null, COLUMN_HIKE_NAME + " LIKE ?",
                new String[]{"%" + name + "%"}, null, null, COLUMN_HIKE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hikes;
    }

    public List<Hike> advancedSearch(String name, String location, String length, String date) {
        List<Hike> hikes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_HIKES + " WHERE 1=1");
        List<String> args = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            query.append(" AND ").append(COLUMN_HIKE_NAME).append(" LIKE ?");
            args.add("%" + name + "%");
        }
        if (location != null && !location.isEmpty()) {
            query.append(" AND ").append(COLUMN_HIKE_LOCATION).append(" LIKE ?");
            args.add("%" + location + "%");
        }
        if (length != null && !length.isEmpty()) {
            query.append(" AND ").append(COLUMN_HIKE_LENGTH).append(" LIKE ?");
            args.add("%" + length + "%");
        }
        if (date != null && !date.isEmpty()) {
            query.append(" AND ").append(COLUMN_HIKE_DATE).append(" = ?");
            args.add(date);
        }

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                hikes.add(cursorToHike(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hikes;
    }

    // Helper methods to reduce code duplication
    private Hike cursorToHike(Cursor cursor) {
        Hike hike = new Hike();
        hike.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_HIKE_ID)));
        hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_NAME)));
        hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LOCATION)));
        hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DATE)));
        hike.setParkingAvailable(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_PARKING)));
        hike.setLength(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_LENGTH)));
        hike.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DIFFICULTY)));
        hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DESCRIPTION)));
        hike.setWeather(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_WEATHER)));
        hike.setEstimatedDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HIKE_DURATION)));
        return hike;
    }

    private Observation cursorToObservation(Cursor cursor) {
        Observation observation = new Observation();
        observation.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_OBS_ID)));
        observation.setHikeId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_OBS_HIKE_ID)));
        observation.setObservation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBS_OBSERVATION)));
        observation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBS_TIME)));
        observation.setAdditionalComments(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OBS_COMMENTS)));
        return observation;
    }
}

