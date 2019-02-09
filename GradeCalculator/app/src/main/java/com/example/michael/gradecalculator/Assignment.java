package com.example.michael.gradecalculator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Property;

public class Assignment implements Parcelable{
    private String name;
    private Double grade, weight;

    public Assignment (String name, double grade, double weight){
        this.name = name;
        this.grade = grade;
        this.weight = weight;
    }

    public Assignment (Parcel parcel){
        this.name = parcel.readString();
        this.grade = parcel.readDouble();
        this.weight = parcel.readDouble();
    }

    public String toString(){
        return (name + "    Grade: " + grade + "   Weight: " + weight);
    }

    public String getName() {
        return name;
    }

    public Double getGrade(){
        return grade;
    }

    public Double getWeight(){
        return weight;
    }

    public void setName(String name){this.name = name; }

    public void setGrade(double grade){this.grade = grade; }

    public void setWeight(double weight){this.weight = weight; }

    public void editAssignment(String newNameStr, String newGradeStr, String newWeightStr){

        if (!newNameStr.equals("")){
            this.setName(newNameStr);
        }

        if (!newGradeStr.equals("")) {
            this.setGrade(Double.parseDouble(newGradeStr));
        }
        if (!newWeightStr.equals("")) {
            this.setWeight(Double.parseDouble(newWeightStr));
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.grade);
        dest.writeDouble(this.weight);
    }

    public static final Parcelable.Creator<Assignment> CREATOR = new Parcelable.Creator<Assignment>(){
        @Override
        public Assignment createFromParcel(Parcel parcel) {
            return new Assignment(parcel);
        }

        public Assignment[] newArray(int size){
            return new Assignment[0];
        }
    };

    public int describeContents(){
        return hashCode();
    }
}
