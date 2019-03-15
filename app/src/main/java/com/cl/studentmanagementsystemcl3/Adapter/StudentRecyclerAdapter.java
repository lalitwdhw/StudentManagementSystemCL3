package com.cl.studentmanagementsystemcl3.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cl.studentmanagementsystemcl3.Activity.AddStudentActivity;
import com.cl.studentmanagementsystemcl3.Activity.MainActivity;
import com.cl.studentmanagementsystemcl3.Constants;
import com.cl.studentmanagementsystemcl3.Models.Student;
import com.cl.studentmanagementsystemcl3.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.MyViewHolder> {

    private List<Student> studentList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvName;
        CardView cvMain;

        public MyViewHolder(View view) {
            super(view);

            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            cvMain = view.findViewById(R.id.cvMain);
        }
    }


    public StudentRecyclerAdapter(List<Student> moviesList, Activity activity) {
        this.studentList = moviesList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Student mStudent = studentList.get(position);

        holder.tvId.setText(String.valueOf(mStudent.getStudentId()));
        holder.tvName.setText(mStudent.getStudentName());

        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position, mStudent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    private void showDialog(final int position, final Student mStudent)
    {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_student_options);
        dialog.setTitle(activity.getString(R.string.choose_option));
        dialog.setCancelable(true);

        Paper.init(activity);

        dialog.findViewById(R.id.tvView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(activity, AddStudentActivity.class);
                mIntent.putExtra(Constants.STUDENT_OBJECT, mStudent);
                mIntent.putExtra(Constants.ACTION,Constants.VIEW);
                activity.startActivity(mIntent);
            }
        });


        dialog.findViewById(R.id.tvEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(activity, AddStudentActivity.class);
                mIntent.putExtra(Constants.STUDENT_OBJECT, mStudent);
                mIntent.putExtra(Constants.ACTION,Constants.EDIT);
                activity.startActivity(mIntent);
            }
        });


        dialog.findViewById(R.id.tvDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,studentList.size());
                new deleteFromPaper().execute(position);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private static class deleteFromPaper extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... position) {
            ArrayList<Student> existingStudents;
            existingStudents = Paper.book().read(Constants.STUDENT_DB, new ArrayList<Student>());
            existingStudents.remove((int)position[0]);
            Paper.book().write(Constants.STUDENT_DB, existingStudents);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}