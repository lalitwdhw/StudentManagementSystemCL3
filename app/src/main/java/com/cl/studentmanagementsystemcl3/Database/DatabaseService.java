package com.cl.studentmanagementsystemcl3.Database;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Database.DatabaseHelper;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;

public class DatabaseService extends Service {


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if(intent.getExtras().get(Constants.ACTION).equals(Constants.SERVICE_READ))
        {
            returnDbToActivity();
        }

        if(intent.getExtras().get(Constants.ACTION).equals(Constants.SERVICE_WRITE))
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    writeToDb(intent.getExtras().getString(Constants.COLUMN_NAME),
                            intent.getExtras().getString(Constants.COLUMN_ID),
                            intent.getExtras().getBoolean(Constants.IS_EDITING),
                            intent.getExtras().getInt(Constants.EDITING_SYSTEM_ID));
                }
            },1);
        }

        if(intent.getExtras().get(Constants.ACTION).equals(Constants.SERVICE_DELETE))
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    deleteFromDB((Student)intent.getExtras().getParcelable(Constants.STUDENT_OBJECT));
                }
            },1);
        }



        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void returnDbToActivity()
    {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                ArrayList<Student> existingStudents = db.getAllStudents();
                Intent intentReturn = new Intent(Constants.SERVICE_STUDENTS_SEND);
                Bundle b = new Bundle();
                b.putParcelableArrayList(Constants.STUDENT_LIST, existingStudents);
                intentReturn.putExtras(b);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentReturn);
            }
        },1);


    }

    private void writeToDb(String name,String id, boolean isEditing, int editingSystemId)
    {

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        ArrayList<Student> existingStudents;
        existingStudents = db.getAllStudents();

        boolean isSuccesfullyAdded = true;

        if(!isEditing)
        {
            Student mStudent =
                    new Student(name,
                            Integer.valueOf(id),
                            existingStudents.size()+1);

            for(int i = 0; i < existingStudents.size();i++)
            {
                if(existingStudents.get(i).getStudentId() == Integer.valueOf(id))
                {
                    isSuccesfullyAdded = false;
                }
            }
            if(isSuccesfullyAdded)
            {
                db.insertStudent(mStudent);
                Toast.makeText(getApplicationContext(), getString(R.string.student_added), Toast.LENGTH_SHORT).show();

                Intent intentReturn = new Intent(Constants.SERVICE_STUDENTS_APPENED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentReturn);
            }
            else
            {
                isSuccesfullyAdded = true;
                Toast.makeText(getApplicationContext(),getString(R.string.unique_id_check), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {

            for(int i = 0; i < existingStudents.size();i++)
            {
                if(existingStudents.get(i).getStudentId() == Integer.valueOf(id) && existingStudents.get(i).getSystemId() != editingSystemId)
                {
                    isSuccesfullyAdded = false;
                }
            }

            if(isSuccesfullyAdded)
            {
                int index = -1;
                for (int i = 0; i < existingStudents.size(); i++) {
                    Student mStudentTemp = existingStudents.get(i);
                    if (mStudentTemp.getSystemId() == editingSystemId) {
                        index = i;
                        break;
                    }
                }
                Student mStudent = existingStudents.get(index);
                mStudent.setStudentName(name);
                mStudent.setStudentId(Integer.valueOf(id));
                db.updateStudent(mStudent);
                Toast.makeText(getApplicationContext(), getString(R.string.student_updated), Toast.LENGTH_SHORT).show();

                Intent intentReturn = new Intent(Constants.SERVICE_STUDENTS_APPENED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentReturn);
            }

            else {
                isSuccesfullyAdded = true;
                Toast.makeText(getApplicationContext(),getString(R.string.unique_id_check), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteFromDB(Student student)
    {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.deleteStudent(student);
    }
}
