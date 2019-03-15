package com.cl.studentmanagementsystemcl3.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;

import io.paperdb.Paper;

public class AddStudentActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etName;
    private Button btnSave;
    private boolean isEditing = false;
    private int editingSystemId = -1;

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


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStudentDetails();
            }
        });

    }

    private void init()
    {
        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);
    }

    public void saveStudentDetails()
    {
        Paper.init(AddStudentActivity.this);
        new saveToPaper().execute(etName.getText().toString(),etId.getText().toString());
    }

    private class saveToPaper extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<Student> existingStudents;
            existingStudents = Paper.book().read(Constants.STUDENT_DB, new ArrayList<Student>());
            if(!isEditing)
            {
                Student mStudent =
                        new Student(params[0],
                                Integer.valueOf(params[1]),
                                existingStudents.size()+1);
                existingStudents.add(mStudent);
                Paper.book().write(Constants.STUDENT_DB,existingStudents);
            }
            else
            {
                int index = -1;
                for(int i = 0; i < existingStudents.size();i++)
                {
                    Student mStudentTemp = existingStudents.get(i);
                    if(mStudentTemp.getSystemId() == editingSystemId)
                    {
                        index = i;
                        break;
                    }
                }
                Student mStudent  = existingStudents.get(index);
                mStudent.setStudentName(params[0]);
                mStudent.setStudentId(Integer.valueOf(params[1]));
                Paper.book().write(Constants.STUDENT_DB,existingStudents);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(!isEditing)
            {
                Toast.makeText(AddStudentActivity.this,getString(R.string.student_added),Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(AddStudentActivity.this,getString(R.string.student_updated),Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(AddStudentActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
