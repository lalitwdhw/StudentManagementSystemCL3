package com.cl.studentmanagementsystemcl3.Activity;

import android.app.Activity;
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


public class AddStudentActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etName;
    private Button btnSave;
    private boolean isEditing = false;
    private int editingSystemId = -1;
    private boolean isSuccesfullyAdded = true;

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

        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.STUDENT_NAME,etName.getText().toString().trim());
        returnIntent.putExtra(Constants.STUDENT_ID,Integer.valueOf(etId.getText().toString().trim()));
        returnIntent.putExtra(Constants.STUDENT_ID_SYSTEM,editingSystemId);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


}
