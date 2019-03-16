package com.cl.studentmanagementsystemcl3.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Models.Student;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, Constants.STUDENT_DB, null, Constants.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create table SQL query
        String CREATE_TABLE =
                "CREATE TABLE " + Constants.STUDENT_DETAILS_TABLE + "("
                        + Constants.COLUMN_SYSTEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Constants.COLUMN_NAME + " TEXT,"
                        + Constants.COLUMN_ID + " INTEGER"
                        + ")";

        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.STUDENT_DETAILS_TABLE);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_ID, student.getStudentId());
        values.put(Constants.COLUMN_NAME, student.getStudentName());

        long id = db.insert(Constants.STUDENT_DETAILS_TABLE, null, values);

        db.close();
        return id;
    }

    public Student getStudent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.STUDENT_DETAILS_TABLE,
                new String[]{Constants.COLUMN_SYSTEM_ID, Constants.COLUMN_NAME, Constants.COLUMN_ID},
                Constants.COLUMN_SYSTEM_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Student student = new Student(
                cursor.getString(cursor.getColumnIndex(Constants.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_SYSTEM_ID)));

        cursor.close();

        return student;
    }

    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> students = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constants.STUDENT_DETAILS_TABLE + " ORDER BY " +
                Constants.COLUMN_SYSTEM_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID)));
                student.setStudentName(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_NAME)));
                student.setSystemId(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_SYSTEM_ID)));

                students.add(student);
            } while (cursor.moveToNext());
        }

        db.close();

        return students;
    }

    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + Constants.STUDENT_DETAILS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.STUDENT_DETAILS_TABLE+ " WHERE "+Constants.COLUMN_SYSTEM_ID+"='"+student.getSystemId()+"'");
        db.close();
    }


    public void updateStudent(Student student) {
        SQLiteDatabase db =  this.getWritableDatabase();

        db.execSQL("UPDATE " + Constants.STUDENT_DETAILS_TABLE + " SET " + Constants.COLUMN_NAME+"='" + student.getStudentName() + "'" +
                " WHERE "+Constants.COLUMN_SYSTEM_ID+"='"+student.getSystemId()+"'");

        db.execSQL("UPDATE " + Constants.STUDENT_DETAILS_TABLE + " SET " + Constants.COLUMN_ID+"='" + student.getStudentId() + "'" +
                " WHERE "+Constants.COLUMN_SYSTEM_ID+"='"+student.getSystemId()+"'");

        db.close();
    }
}