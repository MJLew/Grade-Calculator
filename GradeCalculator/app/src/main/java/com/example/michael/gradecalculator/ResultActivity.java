package com.example.michael.gradecalculator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.awt.font.NumericShaper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ResultActivity extends AppCompatActivity {

    TextView resultStatementView;

    TextView gradeNeededText;
    EditText gradeNeededInView;
    TextView gradeNeededStatementView;

    TextView avgGettingText;
    EditText avgGettingInView;
    TextView avgGettingStatementView;

    Button optionalButton;
    Button saveButton;
    Button deleteButton;
    Button loadButton;

    ListView resultListView;
    ArrayAdapter<Assignment> resultArrayAdapter;
    ArrayList<Assignment> resultArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            resultArrayList = extras.getParcelableArrayList("resultArrayList");
//            for (Assignment a : resultArrayList){
//                Log.d("DEBUGGO", a.getName());
//            }
            resultArrayAdapter = new ArrayAdapter<Assignment>(this,R.layout.list_layout, resultArrayList);
            resultListView = (ListView) findViewById(R.id.resultListView);
            registerForContextMenu(resultListView);
            resultListView.setAdapter(resultArrayAdapter);
        }

        gradeNeededText = (TextView) findViewById(R.id.gradeNeededText);
        gradeNeededInView = (EditText) findViewById(R.id.gradeNeededInView);
        gradeNeededStatementView = (TextView) findViewById(R.id.gradeNeededStatementView);

        avgGettingText = (TextView) findViewById(R.id.avgGettingText);
        avgGettingInView = (EditText) findViewById(R.id.avgGettingInView);
        avgGettingStatementView = (TextView) findViewById(R.id.avgGettingStatementView);

        optionalButton = (Button) findViewById(R.id.optionalButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        loadButton = (Button) findViewById(R.id.loadButton);

        resultStatementView = (TextView) findViewById(R.id.resultStatementView);
        resultStatementView.setText("From the values you entered, you currently have a course grade of " + getCourseAverage() + ".");

    }

    public void onOptionalClick(View view){
        if (!gradeNeededInView.getText().toString().equals("")){
            double targetGrade = Double.parseDouble(gradeNeededInView.getText().toString());
            double neededGrade = Math.ceil(getNeededGrade(targetGrade) * 100) / 100;

            gradeNeededStatementView.setText("\nYou need an average grade of " + neededGrade + " on your remaining assignments to get a grade of " + targetGrade + ".");
        }
        if (!avgGettingInView.getText().toString().equals("")){
            double gradeToGet = Double.parseDouble(avgGettingInView.getText().toString());
            double predictedGrade = Math.ceil(getPredictedGrade(gradeToGet) * 100) / 100;

            avgGettingStatementView.setText("\nIf you get " + gradeToGet + " on your remaining assignments, you will get an average of " + predictedGrade + ".");
        }
    }

    public void onSaveClick(View view){
        final EditText fileNameInputView = new EditText(this);
        fileNameInputView.setHint("Course Name");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(fileNameInputView);

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                String courseName = fileNameInputView.getText().toString();
                if (!courseName.equals("")){
                    String filePath = "Courses" + File.separator + courseName + ".txt";
                    File newFile = new File(getExternalFilesDir(null), filePath);
                    try {
                        newFile.getParentFile().mkdirs();
                        newFile.createNewFile();
                        FileWriter fw = new FileWriter(newFile);
                        BufferedWriter bfw = new BufferedWriter(fw);
                        for (Assignment a : resultArrayList){
                            bfw.write(a.getName() + " " + a.getGrade() + " " + a.getWeight());
                            bfw.newLine();
                        }
                        bfw.close();
                        Toast.makeText(getBaseContext(),"Successfully saved grades for " + courseName + ".", Toast.LENGTH_SHORT ).show();
                    }
                    catch (IOException e){
                        Toast.makeText(getBaseContext(),"Save failed.", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void onDeleteClick(View view){
        final ListView fileListView = new ListView(this);
        String filePath = getExternalFilesDir(null) + File.separator + "Courses";
        final ArrayList<String> fileList = getFileList(filePath);

        final ArrayAdapter<String> courseFileListAdapter = new ArrayAdapter<String>(this, R.layout.multi_checked_list_layout, fileList);
        fileListView.setAdapter(courseFileListAdapter);
        fileListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose files to delete");
        builder.setView(fileListView);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                int i = 0;
                while (i <= fileListView.getCount()){
                    //Log.d("DEBUGGO", i + " " + fileListView.getItemAtPosition(i).toString() + " " + fileListView.isItemChecked(i));
                    if (fileListView.isItemChecked(i)){
                        try {
                            File delFile = new File(getExternalFilesDir(null) +File.separator+ "Courses" + File.separator + fileListView.getItemAtPosition(i).toString());
                            Log.d("DEBUGGO",""+delFile.toString());
                            if(delFile.delete()){
                                Toast.makeText(getBaseContext(), "File successfully deleted.", Toast.LENGTH_SHORT).show();
                                fileList.remove(i);
                                i-=1;
                            }
                            else{
                                Toast.makeText(getBaseContext(), "Delete failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            courseFileListAdapter.notifyDataSetChanged();
                        }
                    }
                    i++;
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
    public void onLoadClick(View view){
        final ListView fileListView = new ListView(this);
        String filePath = getExternalFilesDir(null) + File.separator + "Courses";
        final ArrayList<String> fileList = getFileList(filePath);

        final ArrayAdapter<String> courseFileListAdapter = new ArrayAdapter<String>(this, R.layout.single_checked_list_layout, fileList);
        fileListView.setAdapter(courseFileListAdapter);
        fileListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose File to Load");
        builder.setView(fileListView);

        builder.setPositiveButton("LOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String line;
                ArrayList<Assignment> loadedList = new ArrayList<Assignment>();
                int pos = fileListView.getCheckedItemPosition();
                String filePath = getExternalFilesDir(null) + File.separator + "Courses" + File.separator + fileListView.getItemAtPosition(pos).toString();
                try{
                    FileReader fr = new FileReader(filePath);
                    BufferedReader bfr = new BufferedReader(fr);
                    while ((line = bfr.readLine()) != null){
                        Scanner s = new Scanner(line);
                        String name = s.next();
                        double grade = s.nextDouble();
                        double weight = s.nextDouble();
                        loadedList.add(new Assignment(name, grade, weight));
                    }
                    Log.d("DEBUGGO", resultArrayList.toString());
                    resultArrayAdapter.clear();
                    resultArrayAdapter.addAll(loadedList);
                    resultArrayAdapter.notifyDataSetChanged();
                    Log.d("DEBUGGO", resultListView.toString());
                }
                catch (IOException e){
                    Toast.makeText(getBaseContext(), "Load failed.", Toast.LENGTH_SHORT);
                }
                catch (InputMismatchException e){
                    Toast.makeText(getBaseContext(), "Incorrect item type. Check file for proper format.", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(getBaseContext(), "Unexpected error.", Toast.LENGTH_SHORT);
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu_item_longpress_w_add, menu);
    }

    public boolean onContextItemSelected(final MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_add:
                final LinearLayout horLayout = new LinearLayout(this);
                final EditText addNameView = new EditText(this);
                final EditText addGradeView = new EditText(this);
                final EditText addWeightView = new EditText(this);
                addNameView.setHint("Name");
                addNameView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                addGradeView.setHint("Grade");
                addGradeView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                addWeightView.setHint("Weight");
                addWeightView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                horLayout.addView(addNameView);
                horLayout.addView(addGradeView);
                horLayout.addView(addWeightView);

                AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);
                addBuilder.setTitle("Add");
                addBuilder.setView(horLayout);
                addBuilder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String newName = addNameView.getText().toString();
                            Double newGrade = Double.parseDouble(addGradeView.getText().toString());
                            Double newWeight = Double.parseDouble(addWeightView.getText().toString());

                            if (newWeight > 100 || (getWeightTotal() + newWeight > 100)) {
                                Toast.makeText(getBaseContext(), "total weight cannot be over 100%", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Assignment newAssignment = new Assignment(newName, newGrade, newWeight);
                                resultArrayList.add(newAssignment);
                                resultArrayAdapter.notifyDataSetChanged();
                            }
                        }
                        catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                    }
                });
                addBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                addBuilder.create();
                addBuilder.show();
                return true;
            case R.id.menu_delete:
                deleteAssignment(item);
                return true;

            case R.id.menu_edit:
                final LinearLayout horEditLayout = new LinearLayout(this);
                final EditText editNameView = new EditText(this);
                final EditText editGradeView = new EditText(this);
                final EditText editWeightView = new EditText(this);
                editNameView.setHint("Name");
                editNameView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                editGradeView.setHint("Grade");
                editGradeView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editWeightView.setHint("Weight");
                editWeightView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                horEditLayout.addView(editNameView);
                horEditLayout.addView(editGradeView);
                horEditLayout.addView(editWeightView);
                final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Assignment assignmentToEdit = resultArrayList.get(info.position);



                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit");
                builder.setView(horEditLayout);
                builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String editNameStr = editNameView.getText().toString();
                        final String editGradeStr = editGradeView.getText().toString();
                        final String editWeightStr = editWeightView.getText().toString();
                        assignmentToEdit.editAssignment(editNameStr, editGradeStr, editWeightStr);
                        resultArrayAdapter.notifyDataSetChanged();
                        //Log.d("DEBUGGO", assignmentArrayList.get(info.position).toString());

                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public double getCourseAverage(){
        double average = 0;

        for (Assignment a : resultArrayList){
            average = average + (a.getGrade() * (a.getWeight() / 100));
        }
        return average;
    }

    public double getNeededGrade(double targetGrade) {
        double totalWeight = 0;
        double leftoverWeight;

        for (Assignment a : resultArrayList){
            totalWeight = totalWeight + a.getWeight();
        }
        leftoverWeight = 100 - totalWeight;
        return ((targetGrade - getCourseAverage()) / leftoverWeight) * 100;
    }

    public double getPredictedGrade(double gradeToGet){
        double totalWeight = 0;
        double leftoverWeight;
        for (Assignment a : resultArrayList){
            totalWeight = totalWeight + a.getWeight();
        }
        leftoverWeight = 100 - totalWeight;
        return (getCourseAverage() + (gradeToGet * (leftoverWeight / 100)));
    }

    public ArrayList<String> getFileList(String filePath){
        ArrayList<String> fileList = new ArrayList<String>();
        File[] files = new File(filePath).listFiles();
        for (File f : files){
            if (f.isFile()){
                fileList.add(f.getName());
            }
        }
        return fileList;
    }

    public void deleteAssignment (MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        resultArrayList.remove(info.position);
        resultArrayAdapter.notifyDataSetChanged();
    }
    public double getWeightTotal(){
        double weightTotal = 0;
        for (Assignment a : resultArrayList){
            weightTotal += a.getWeight();
        }
        return weightTotal;
    }

    public void foo(){
        Integer myInt;
        Array myarray;
        ArrayList myList;

        int result = man.hashCode();
        result += engine.hashCode();
        result += color.hashCode();

        int result = 1;

        result = 31 * result + manufacturer.hashCode();

        result = 31 * result + engine.hashCode();

        return 31 * result + color.hashCode();
    }
}
