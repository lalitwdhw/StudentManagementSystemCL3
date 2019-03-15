package com.cl.studentmanagementsystemcl3.Activity;

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

import com.cl.studentmanagementsystemcl3.Adapter.StudentRecyclerAdapter;
import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout llNodata;
    private ArrayList<Student> mStudentList = new ArrayList<>();
    private StudentRecyclerAdapter mStudentRecycelerAdapter;
    private boolean isGrid = false;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        pref = getApplicationContext().getSharedPreferences( Constants.PREFS, 0);
        isGrid = pref.getBoolean(Constants.IS_GRID,false);

        Paper.init(MainActivity.this);
        new readFromPaper().execute();
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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this);
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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this);
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
        startActivity(new Intent(MainActivity.this,AddStudentActivity.class));
    }

    private class readFromPaper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mStudentList = Paper.book().read(Constants.STUDENT_DB, new ArrayList<Student>());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!isGrid)
            {
                setRecycler();
            }
            else
            {
                setRecyclerGrid();
            }
        }
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

    /*
     * method sortByName
     * To sort rvStudentList items by Student Name
     */
    private void sortByName() {
        Collections.sort(mStudentList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getStudentName().compareToIgnoreCase(s2.getStudentName());
            }
        });
        mStudentRecycelerAdapter.notifyDataSetChanged();

    }
    /*
     * method sortById
     * To sort rvStudentList items by Student Roll No
     */
    private void sortById() {
        Collections.sort(mStudentList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getStudentId() - s2.getStudentId();
            }
        });
        mStudentRecycelerAdapter.notifyDataSetChanged();
    }
}
