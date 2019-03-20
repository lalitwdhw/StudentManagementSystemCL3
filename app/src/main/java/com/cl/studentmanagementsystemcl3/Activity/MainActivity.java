package com.cl.studentmanagementsystemcl3.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Adapter.StudentRecyclerAdapter;
import com.cl.studentmanagementsystemcl3.Database.DatabaseIntentService;
import com.cl.studentmanagementsystemcl3.Database.DatabaseService;
import com.cl.studentmanagementsystemcl3.Fragments.AddStudentFragment;
import com.cl.studentmanagementsystemcl3.Fragments.MainFragment;
import com.cl.studentmanagementsystemcl3.RecyclerActivityInterface;
import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Database.DatabaseHelper;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
       // implements RecyclerActivityInterface
{
  /*  private RecyclerView recyclerView;
    private LinearLayout llNodata;
    private ArrayList<Student> mStudentList = new ArrayList<>();
    private StudentRecyclerAdapter mStudentRecycelerAdapter;
    private boolean isGrid = false;
  */  private SharedPreferences pref;
    private String dbWriteMode = "";
/*

    private DatabaseHelper db;
    Intent intentService;
*/

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

       // init();

        pref = getApplicationContext().getSharedPreferences( Constants.PREFS, 0);
      //  isGrid = pref.getBoolean(Constants.IS_GRID,false);
        dbWriteMode = pref.getString(Constants.DB_MODE,Constants.DB_MODE_ASYNC);
      //  Toast.makeText(MainActivity.this,dbWriteMode,Toast.LENGTH_SHORT).show();

     /*   //Paper.init(MainActivity.this);
        if(dbWriteMode.equals(Constants.DB_MODE_ASYNC))
        {
            new readFromPaper().execute();
        }
        if(dbWriteMode.equals(Constants.DB_MODE_SERVICE))
        {
            intentService = new Intent(MainActivity.this, DatabaseService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            startService(intentService);
        }
        if(dbWriteMode.equals(Constants.DB_MODE_INTENT_SERVICE))
        {
            intentService = new Intent(MainActivity.this, DatabaseIntentService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            startService(intentService);
        }*/

//        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SERVICE_STUDENTS_SEND));
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment(), getString(R.string.main_frag));
        adapter.addFragment(new AddStudentFragment(), getString(R.string.add_fragment));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

 /*   private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            mStudentList = b.getParcelableArrayList(Constants.STUDENT_LIST);
            if(!isGrid)
            {
                setRecycler();
            }
            else
            {
                setRecyclerGrid();
            }
        }
    };

    private void init()
    {
        recyclerView = findViewById(R.id.recycler);
        llNodata = findViewById(R.id.llNoData);

        db = new DatabaseHelper(this);

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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this,this, db);
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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, MainActivity.this,this, db);
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
            mStudentList.clear();
            mStudentList.addAll(db.getAllStudents());
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
*/

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
               /* if(isGrid)
                {
                    setRecycler();
                }
                else
                {
                    setRecyclerGrid();
                }
                break;*/
               return false;
            case R.id.action_sort_id:
              /*  sortById();
                break;*/

            return false;
            case R.id.action_sort_name:
           /*     sortByName();
                break;
*/
            return false;
            case R.id.action_async:
                dbWriteMode = Constants.DB_MODE_ASYNC;
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.DB_MODE,dbWriteMode);
                editor.apply();
                Toast.makeText(MainActivity.this,dbWriteMode,Toast.LENGTH_SHORT).show();
                break;

        //    return false;
            case R.id.action_service:
                dbWriteMode = Constants.DB_MODE_SERVICE;
                SharedPreferences.Editor editorr = pref.edit();
                editorr.putString(Constants.DB_MODE,dbWriteMode);
                editorr.apply();
                Toast.makeText(MainActivity.this,dbWriteMode,Toast.LENGTH_SHORT).show();
                break;
         //   return false;
            case R.id.action_intent_service:
                dbWriteMode = Constants.DB_MODE_INTENT_SERVICE;
                SharedPreferences.Editor editorrr = pref.edit();
                editorrr.putString(Constants.DB_MODE,dbWriteMode);
                editorrr.apply();
                Toast.makeText(MainActivity.this,dbWriteMode,Toast.LENGTH_SHORT).show();
                break;

           // return false;
            default:
                break;
        }

        return true;
    }
/*
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

    }*/
}
