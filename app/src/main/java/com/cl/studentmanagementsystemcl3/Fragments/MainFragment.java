package com.cl.studentmanagementsystemcl3.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cl.studentmanagementsystemcl3.Activity.AddStudentActivity;
import com.cl.studentmanagementsystemcl3.Activity.MainActivity;
import com.cl.studentmanagementsystemcl3.Adapter.StudentRecyclerAdapter;
import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Database.DatabaseHelper;
import com.cl.studentmanagementsystemcl3.Database.DatabaseIntentService;
import com.cl.studentmanagementsystemcl3.Database.DatabaseService;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;
import com.cl.studentmanagementsystemcl3.RecyclerActivityInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainFragment extends Fragment implements RecyclerActivityInterface {

    private RecyclerView recyclerView;
    private LinearLayout llNodata;
    private ArrayList<Student> mStudentList = new ArrayList<>();
    private StudentRecyclerAdapter mStudentRecycelerAdapter;
    private boolean isGrid = false;
    private SharedPreferences pref;
    private String dbWriteMode = "";

    private DatabaseHelper db;
    Intent intentService;

    Button addButton;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        init(view);

        pref = getContext().getSharedPreferences( Constants.PREFS, 0);
        isGrid = pref.getBoolean(Constants.IS_GRID,false);
        dbWriteMode = pref.getString(Constants.DB_MODE,Constants.DB_MODE_ASYNC);
        Toast.makeText(getContext(),dbWriteMode,Toast.LENGTH_SHORT).show();

        if(dbWriteMode.equals(Constants.DB_MODE_ASYNC))
        {
            new readFromPaper().execute();
        }
        if(dbWriteMode.equals(Constants.DB_MODE_SERVICE))
        {
            intentService = new Intent(getContext(), DatabaseService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            getContext().startService(intentService);
        }
        if(dbWriteMode.equals(Constants.DB_MODE_INTENT_SERVICE))
        {
            intentService = new Intent(getContext(), DatabaseIntentService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            getContext().startService(intentService);
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SERVICE_STUDENTS_SEND));

        return view;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
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

    private void init(View view)
    {
        recyclerView = view.findViewById(R.id.recycler);
        llNodata = view.findViewById(R.id.llNoData);
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addStudent();
                TabLayout tabs =  getActivity().findViewById(R.id.tabs);
                tabs.getTabAt(1).select();
            }
        });
        db = new DatabaseHelper(getContext());

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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, getActivity(),this, db);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext())
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

            mStudentRecycelerAdapter = new StudentRecyclerAdapter(mStudentList, getActivity(),this, db);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),2)
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

    public void addStudent()
    {
        startActivity(new Intent(getContext(), AddStudentActivity.class));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
            case R.id.action_async:
                dbWriteMode = Constants.DB_MODE_ASYNC;
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.DB_MODE,dbWriteMode);
                editor.apply();
                Toast.makeText(getContext(),dbWriteMode,Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_service:
                dbWriteMode = Constants.DB_MODE_SERVICE;
                SharedPreferences.Editor editorr = pref.edit();
                editorr.putString(Constants.DB_MODE,dbWriteMode);
                editorr.apply();
                Toast.makeText(getContext(),dbWriteMode,Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_intent_service:
                dbWriteMode = Constants.DB_MODE_INTENT_SERVICE;
                SharedPreferences.Editor editorrr = pref.edit();
                editorrr.putString(Constants.DB_MODE,dbWriteMode);
                editorrr.apply();
                Toast.makeText(getContext(),dbWriteMode,Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        if(intentService!=null)
        {
            getContext().stopService(intentService);
        }

        try {
            getContext().unregisterReceiver(mMessageReceiver);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }

    }
/*


    public void refreshFragmentData(Context context)
    {
        db = new DatabaseHelper(context);

        pref = context.getSharedPreferences( Constants.PREFS, 0);
        isGrid = pref.getBoolean(Constants.IS_GRID,false);
        dbWriteMode = pref.getString(Constants.DB_MODE,Constants.DB_MODE_ASYNC);
        Toast.makeText(context,dbWriteMode,Toast.LENGTH_SHORT).show();

        if(dbWriteMode.equals(Constants.DB_MODE_ASYNC))
        {
            new readFromPaper().execute();
        }
        if(dbWriteMode.equals(Constants.DB_MODE_SERVICE))
        {
            intentService = new Intent(context, DatabaseService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            context.startService(intentService);
        }
        if(dbWriteMode.equals(Constants.DB_MODE_INTENT_SERVICE))
        {
            intentService = new Intent(context, DatabaseIntentService.class);
            intentService.putExtra(Constants.ACTION,Constants.SERVICE_READ);
            context.startService(intentService);
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter(Constants.SERVICE_STUDENTS_SEND));


    }

*/

}