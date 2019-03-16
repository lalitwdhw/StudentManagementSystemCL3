package com.cl.studentmanagementsystemcl3.Models;

import android.animation.ObjectAnimator;
import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable, Comparable {

    private String studentName;
    private int studentId;
    private int systemId;


    public Student() {
    }

    public Student(String studentName, int studentId, int systemId) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.systemId = systemId;
    }

    //parceable thing
    public Student(Parcel source) {
        studentName = source.readString();
        studentId = source.readInt();
        systemId = source.readInt();
    }

    //parceable thing
    @Override
    public int describeContents() {
        return 0;
    }

    //parceable thing
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentName);
        dest.writeInt(studentId);
        dest.writeInt(systemId);
    }

    //parceable thing
    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }

        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }
    };

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public static Creator<Student> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int compareTo(Object comparestu) {
        int compareage=((Student)comparestu).getStudentId();
        return this.studentId-compareage;
    }

}
