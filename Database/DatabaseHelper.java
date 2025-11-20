package com.mainPackage.randevuapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mainPackage.randevuapp.Model.Doctor;
import com.mainPackage.randevuapp.Model.Hospital;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.Model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "randevu.db";
    private static final int DATABASE_VERSION = 4;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_ID_NUMBER = "id_number";
    private static final String COLUMN_PASSWORD = "password";

    // Hospitals table
    private static final String TABLE_HOSPITALS = "hospitals";
    private static final String COLUMN_HOSPITAL_ID = "hospital_id";
    private static final String COLUMN_COUNTY = "county";
    private static final String COLUMN_DISTRICT = "district";
    private static final String COLUMN_HOSPITAL_NAME = "hospital_name";

    // Doctors table
    private static final String TABLE_DOCTORS = "doctors";
    private static final String COLUMN_DOCTOR_ID = "doctor_id";
    private static final String COLUMN_DOCTOR_HOSPITAL = "hospital";
    private static final String COLUMN_DOCTOR_NAME = "doctor_name";
    private static final String COLUMN_DEPARTMENT = "department";

    // Time Intervals table
    private static final String TABLE_TIME_INTERVALS = "time_intervals";
    private static final String COLUMN_TIME_INTERVAL_ID = "time_interval_id";
    private static final String COLUMN_TI_DOCTOR_ID = "doctor_id";
    private static final String COLUMN_TI_DATE = "date";
    private static final String COLUMN_TI_START_TIME = "start_time";
    private static final String COLUMN_TI_USER_ID = "user_id";
    private static final String COLUMN_TI_AVAILABLE = "available";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ID_NUMBER + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_HOSPITALS_TABLE = "CREATE TABLE " + TABLE_HOSPITALS + "("
                + COLUMN_HOSPITAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_COUNTY + " TEXT,"
                + COLUMN_DISTRICT + " TEXT,"
                + COLUMN_HOSPITAL_NAME + " TEXT UNIQUE" + ")";
        db.execSQL(CREATE_HOSPITALS_TABLE);

        String CREATE_DOCTORS_TABLE = "CREATE TABLE " + TABLE_DOCTORS + "("
                + COLUMN_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DOCTOR_HOSPITAL + " TEXT,"
                + COLUMN_DOCTOR_NAME + " TEXT,"
                + COLUMN_DEPARTMENT + " TEXT" + ")";
        db.execSQL(CREATE_DOCTORS_TABLE);

        String CREATE_TIME_INTERVALS_TABLE = "CREATE TABLE " + TABLE_TIME_INTERVALS + "("
                + COLUMN_TIME_INTERVAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TI_DOCTOR_ID + " INTEGER,"
                + COLUMN_TI_DATE + " TEXT,"
                + COLUMN_TI_START_TIME + " TEXT,"
                + COLUMN_TI_USER_ID + " INTEGER,"
                + COLUMN_TI_AVAILABLE + " INTEGER" + ")";
        db.execSQL(CREATE_TIME_INTERVALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOSPITALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_INTERVALS);
        onCreate(db);
    }

    // User methods
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_NUMBER, user.getIdNumber());
        values.put(COLUMN_PASSWORD, user.getPassword());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public User getUser(String idNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_ID_NUMBER, COLUMN_PASSWORD},
                COLUMN_ID_NUMBER + "=?",
                new String[]{idNumber},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(cursor.getString(1), cursor.getString(2));
            cursor.close();
            db.close();
            return user;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return null;
    }

    // Hospital methods
    public void addHospital(Hospital hospital) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNTY, hospital.getCounty());
        values.put(COLUMN_DISTRICT, hospital.getDistrict());
        values.put(COLUMN_HOSPITAL_NAME, hospital.getHospitalName());
        db.insert(TABLE_HOSPITALS, null, values);
        db.close();
    }

    public void removeHospital(String hospitalName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HOSPITALS, COLUMN_HOSPITAL_NAME + " = ?", new String[]{hospitalName});
        db.close();
    }

    public List<String> getAllCounties() {
        List<String> counties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_HOSPITALS, new String[]{COLUMN_COUNTY}, null, null, null, null, COLUMN_COUNTY + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                counties.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTY)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return counties;
    }

    public List<String> getDistrictsByCounty(String county) {
        List<String> districts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_HOSPITALS, new String[]{COLUMN_DISTRICT}, COLUMN_COUNTY + "=?", new String[]{county}, null, null, COLUMN_DISTRICT + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                districts.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISTRICT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return districts;
    }

    public List<String> getHospitalsByDistrict(String district) {
        List<String> hospitals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOSPITALS, new String[]{COLUMN_HOSPITAL_NAME}, COLUMN_DISTRICT + "=?", new String[]{district}, null, null, COLUMN_HOSPITAL_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                hospitals.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOSPITAL_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return hospitals;
    }

    // Doctor methods
    public void addDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCTOR_HOSPITAL, doctor.getHospitalName());
        values.put(COLUMN_DOCTOR_NAME, doctor.getDoctorName());
        values.put(COLUMN_DEPARTMENT, doctor.getDepartment());
        db.insert(TABLE_DOCTORS, null, values);
        db.close();
    }

    public void removeDoctor(String doctorName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCTORS, COLUMN_DOCTOR_NAME + " = ?", new String[]{doctorName});
        db.close();
    }

    public int getDoctorId(String doctorName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOCTORS,
                new String[]{COLUMN_DOCTOR_ID},
                COLUMN_DOCTOR_NAME + "=?",
                new String[]{doctorName},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int doctorId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID));
            cursor.close();
            db.close();
            return doctorId;
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return -1; // Return -1 if doctor not found
    }
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOCTORS, null);

        if (cursor.moveToFirst()) {
            do {
                Doctor doctor = new Doctor(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_HOSPITAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT))
                );
                doctor.setDoctorId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID)));
                doctors.add(doctor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return doctors;
    }


    public List<String> getDepartmentsByHospital(String hospital) {
        List<String> departments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_DOCTORS, new String[]{COLUMN_DEPARTMENT}, COLUMN_DOCTOR_HOSPITAL + "=?", new String[]{hospital}, null, null, COLUMN_DEPARTMENT + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                departments.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return departments;
    }

    public List<String> getDoctorsByDepartmentAndHospital(String department, String hospital) {
        List<String> doctors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOCTORS, new String[]{COLUMN_DOCTOR_NAME}, COLUMN_DEPARTMENT + "=? AND " + COLUMN_DOCTOR_HOSPITAL + "=?", new String[]{department, hospital}, null, null, COLUMN_DOCTOR_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                doctors.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return doctors;
    }

    // TimeInterval methods
    public void addTimeInterval(TimeInterval timeInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TI_DOCTOR_ID, timeInterval.getDoctorId());
        values.put(COLUMN_TI_DATE, timeInterval.getDate());
        values.put(COLUMN_TI_START_TIME, timeInterval.getStartTime());
        values.put(COLUMN_TI_AVAILABLE, timeInterval.isAvailable() ? 1 : 0);
        db.insert(TABLE_TIME_INTERVALS, null, values);
        db.close();
    }

    public List<TimeInterval> getAllTimeIntervals() {
        List<TimeInterval> timeIntervals = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TIME_INTERVALS, null);

        if (cursor.moveToFirst()) {
            do {
                TimeInterval timeInterval = new TimeInterval(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TI_DOCTOR_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TI_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TI_START_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TI_AVAILABLE)) == 1
                );
                timeInterval.setTimeIntervalId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME_INTERVAL_ID)));
                timeInterval.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TI_USER_ID)));
                timeIntervals.add(timeInterval);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return timeIntervals;
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_HOSPITALS, null, null);
        db.delete(TABLE_DOCTORS, null, null);
        db.delete(TABLE_TIME_INTERVALS, null, null);
        db.close();
    }
}
