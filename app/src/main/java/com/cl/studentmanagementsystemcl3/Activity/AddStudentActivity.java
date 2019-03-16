package com.cl.studentmanagementsystemcl3.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Database.DatabaseHelper;
import com.cl.studentmanagementsystemcl3.Database.DatabaseService;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;


public class AddStudentActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etName;
    private Button btnSave;
    private boolean isEditing = false;
    private int editingSystemId = -1;
    private boolean isSuccesfullyAdded = true;
    private DatabaseHelper db;

    private Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        init();

        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        if(mBundle != null)
        {
            Student mStudent = (Student) mIntent.getParcelableExtra(Constants.STUDENT_OBJECT);
            String action = mIntent.getExtras().getString(Constants.ACTION,Constants.VIEW);
            if(action.equals(Constants.VIEW))
            {
                etId.setText(String.valueOf(mStudent.getStudentId()));
                etId.setEnabled(false);
                etName.setText(mStudent.getStudentName());
                etName.setEnabled(false);
                btnSave.setVisibility(View.GONE);
            }
            else
            {
                etId.setText(String.valueOf(mStudent.getStudentId()));
                etName.setText(mStudent.getStudentName());
                editingSystemId = mStudent.getSystemId();
                isEditing = true;
            }


        }

        LocalBroadcastManager.getInstance(AddStudentActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SERVICE_STUDENTS_APPENED));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStudentDetails();
            }
        });

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(AddStudentActivity.this,MainActivity.class));
        }
    };

    private void init()
    {
        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);

        db = new DatabaseHelper(this);
    }

    public void saveStudentDetails()
    {
        if(etName.getText().toString().trim().length() == 0)
        {
            Toast.makeText(AddStudentActivity.this,getString(R.string.name_check),Toast.LENGTH_SHORT).show();
            return;
        }


        if(etId.getText().toString().trim().length() == 0)
        {
            Toast.makeText(AddStudentActivity.this,getString(R.string.id_check),Toast.LENGTH_SHORT).show();
            return;
        }


        SharedPreferences pref = getApplicationContext().getSharedPreferences( Constants.PREFS, 0);
        String dbWriteMode = pref.getString(Constants.DB_MODE,Constants.DB_MODE_ASYNC);

        if(dbWriteMode.equals(Constants.DB_MODE_ASYNC))
        {
            new saveToPaper().execute(etName.getText().toString().trim(),etId.getText().toString().trim());
        }
        if(dbWriteMode.equals(Constants.DB_MODE_SERVICE))
        {
            intentService = new Intent(AddStudentActivity.this, DatabaseService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_WRITE);
            intentService.putExtra(Constants.COLUMN_NAME,etName.getText().toString().trim());
            intentService.putExtra(Constants.COLUMN_ID,etId.getText().toString().trim());
            intentService.putExtra(Constants.IS_EDITING,isEditing);
            intentService.putExtra(Constants.EDITING_SYSTEM_ID,editingSystemId);
            startService(intentService);
        }
        if(dbWriteMode.equals(Constants.DB_MODE_INTENT_SERVICE))
        {
            intentService = new Intent(AddStudentActivity.this, DatabaseService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_WRITE);
            intentService.putExtra(Constants.COLUMN_NAME,etName.getText().toString().trim());
            intentService.putExtra(Constants.COLUMN_ID,etId.getText().toString().trim());
            intentService.putExtra(Constants.IS_EDITING,isEditing);
            intentService.putExtra(Constants.EDITING_SYSTEM_ID,editingSystemId);
            startService(intentService);
        }

    }

    private class saveToPaper extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            ArrayList<Student> existingStudents;
            existingStudents = db.getAllStudents();

            if(!isEditing)
            {
                Student mStudent =
                        new Student(params[0],
                                Integer.valueOf(params[1]),
                                existingStudents.size()+1);

                for(int i = 0; i < existingStudents.size();i++)
                {
                    if(existingStudents.get(i).getStudentId() == Integer.valueOf(params[1]))
                    {
                        isSuccesfullyAdded = false;
                        return null;
                    }
                }
                db.insertStudent(mStudent);
            }
            else
            {

                for(int i = 0; i < existingStudents.size();i++)
                {
                    if(existingStudents.get(i).getStudentId() == Integer.valueOf(params[1]) && existingStudents.get(i).getSystemId() != editingSystemId)
                    {
                        isSuccesfullyAdded = false;
                        return null;
                    }
                }


                    int index = -1;
                    for (int i = 0; i < existingStudents.size(); i++) {
                        Student mStudentTemp = existingStudents.get(i);
                        if (mStudentTemp.getSystemId() == editingSystemId) {
                            index = i;
                            break;
                        }
                    }
                    Student mStudent = existingStudents.get(index);
                    mStudent.setStudentName(params[0]);
                    mStudent.setStudentId(Integer.valueOf(params[1]));
                    db.updateStudent(mStudent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isSuccesfullyAdded) {

                if (!isEditing) {
                    Toast.makeText(AddStudentActivity.this, getString(R.string.student_added), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddStudentActivity.this, getString(R.string.student_updated), Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(AddStudentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else
            {
                isSuccesfullyAdded = true;
                Toast.makeText(AddStudentActivity.this,getString(R.string.unique_id_check), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(intentService!=null)
        {
            stopService(intentService);
        }

        try {
            unregisterReceiver(mMessageReceiver);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }
}
