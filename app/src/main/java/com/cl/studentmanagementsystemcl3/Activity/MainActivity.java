package com.cl.studentmanagementsystemcl3.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Adapter.StudentRecyclerAdapter;
import com.cl.studentmanagementsystemcl3.CheckSizeInterface;
import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements CheckSizeInterface {

    private RecyclerView recyclerView;
    private LinearLayout llNodata;
    private static ArrayList<Student> mStudentList = new ArrayList<>();
    private StudentRecyclerAdapter mStudentRecycelerAdapter;
    private boolean isGrid = false;
    private SharedPreferences pref;
    boolean isSuccesfullyAdded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        pref = getApplicationContext().getSharedPreferences( Constants.PREFS, 0);
        isGrid = pref.getBoolean(Constants.IS_GRID,false);

        if(!isGrid)
        {
            setRecycler();
        }
        else
        {
            setRecyclerGrid();
        }
    }

    private void init()
    {
        recyclerView = findViewById(R.id.recycler);
        llNodata = findViewById(R.id.llNoData);
    }

    private void setRecycler()
    {
        if(mStudentList.size() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            llNodata.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            llNodata.setVisibility(View.GONE);


            isGrid = false;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(Constants.IS_GRID,isGrid);
            editor.apply();

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this,this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this)
            {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mStudentRecycelerAdapter);
        }
    }

    private void setRecyclerGrid()
    {
        if(mStudentList.size() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            llNodata.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            llNodata.setVisibility(View.GONE);

            isGrid = true;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(Constants.IS_GRID,isGrid);
            editor.apply();

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this,this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this,2)
            {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mStudentRecycelerAdapter);
        }
    }

    public void addStudent(View view)
    {
        startActivityForResult(new Intent(MainActivity.this,AddStudentActivity.class),Constants.RESULT_ADD_STUDENT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_grid_list:
                if(isGrid)
                {
                    setRecycler();
                }
                else
                {
                    setRecyclerGrid();
                }
                break;
            case R.id.action_sort_id:
                sortById();
                break;
            case R.id.action_sort_name:
                sortByName();
                break;
            default:
                break;
        }

        return true;
    }

    private void sortByName() {
        Collections.sort(mStudentList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getStudentName().compareToIgnoreCase(s2.getStudentName());
            }
        });
        mStudentRecycelerAdapter.notifyDataSetChanged();

    }

    private void sortById() {
        Collections.sort(mStudentList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getStudentId() - s2.getStudentId();
            }
        });
        mStudentRecycelerAdapter.notifyDataSetChanged();
    }

    @Override
    public void sizeZero() {
        recyclerView.setVisibility(View.GONE);
        llNodata.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.RESULT_ADD_STUDENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result_name = data.getStringExtra(Constants.STUDENT_NAME);
                int result_id = data.getIntExtra(Constants.STUDENT_ID,-1);
                Student mStudent  = new Student(result_name,result_id,mStudentList.size()+1);


                for(int i = 0; i < mStudentList.size();i++)
                {
                    if(mStudentList.get(i).getStudentId() == result_id)
                    {
                        isSuccesfullyAdded = false;
                    }
                }

                if(!isSuccesfullyAdded)
                {
                    isSuccesfullyAdded = true;
                    Toast.makeText(MainActivity.this,getString(R.string.unique_id_check), Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mStudentList.size() == 0) {
                        mStudentList.add(mStudent);
                        if (!isGrid) {
                            setRecycler();
                        } else {
                            setRecyclerGrid();
                        }
                    } else {
                        mStudentList.add(mStudent);
                        mStudentRecycelerAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(MainActivity.this, getString(R.string.student_updated), Toast.LENGTH_SHORT).show();

                }

            }
        }

        if(requestCode == Constants.RESULT_EDIT_STUDENT)
        {
            if (resultCode == Activity.RESULT_OK) {
                String result_name = data.getStringExtra(Constants.STUDENT_NAME);
                int result_id = data.getIntExtra(Constants.STUDENT_ID, -1);
                int result_id_system = data.getIntExtra(Constants.STUDENT_ID_SYSTEM, -1);


                for(int i = 0; i < mStudentList.size();i++)
                {
                    if(mStudentList.get(i).getStudentId() == result_id)
                    {
                        isSuccesfullyAdded = false;
                    }
                }

                if(!isSuccesfullyAdded)
                {
                    isSuccesfullyAdded = true;
                    Toast.makeText(MainActivity.this,getString(R.string.unique_id_check), Toast.LENGTH_SHORT).show();
                }
                else {

                    int index = -1;
                    for (int i = 0; i < mStudentList.size(); i++) {
                        Student mStudentTemp = mStudentList.get(i);
                        if (mStudentTemp.getSystemId() == result_id_system) {
                            index = i;
                            break;
                        }
                    }
                    Student mStudent = mStudentList.get(index);
                    mStudent.setStudentName(result_name);
                    mStudent.setStudentId(result_id);

                    mStudentRecycelerAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, getString(R.string.student_added), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


}
