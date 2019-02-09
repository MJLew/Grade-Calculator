package com.example.michael.gradecalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText nameInText, gradeInText, weightInText;
    Button addButton, calcButton;
    ListView assignmentListView;

    ArrayList<Assignment> assignmentArrayList;
    ArrayAdapter<Assignment> assignmentArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameInText = (EditText) findViewById(R.id.nameInText);
        gradeInText = (EditText) findViewById(R.id.gradeInText);
        weightInText = (EditText) findViewById(R.id.weightInText);
        addButton = (Button) findViewById(R.id.addButton);
        calcButton = (Button) findViewById(R.id.calculateButton);
        assignmentListView = (ListView) findViewById(R.id.assignmentListView);

        assignmentArrayList = new ArrayList<Assignment>();
        assignmentArrayAdapter = new ArrayAdapter<Assignment>(this, R.layout.list_layout,assignmentArrayList);

        assignmentListView.setAdapter(assignmentArrayAdapter);
        registerForContextMenu(assignmentListView);

    }

    // Adds a new assignment to the Arraylist
    public void onAddClick (View view) {
       // if (!nameInText.getText().toString().equals("") &&
       //         (!gradeInText.getText().toString().equals("") && !weightInText.getText().toString().equals(""))) {
       try {
            String assignmentName = nameInText.getText().toString();
            Double assignmentGrade = Double.parseDouble(gradeInText.getText().toString());
            Double assignmentWeight = Double.parseDouble(weightInText.getText().toString());

            if (assignmentWeight > 100 || (getWeightTotal() + assignmentWeight > 100)){
                Toast.makeText(this, "total weight cannot be over 100%", Toast.LENGTH_SHORT).show();}
            else {
                Assignment newAssignment = new Assignment(assignmentName, assignmentGrade, assignmentWeight);
                assignmentArrayList.add(newAssignment);

//        for (Assignment ass : assignmentArrayList){
//            Log.d("DEBUGGO", ass.getName() + "," + ass.getGrade() + "," + ass.getWeight());
//        }

                nameInText.setText("");
                gradeInText.setText("");
                weightInText.setText("");
            }

        }
        catch (NumberFormatException e){
           e.printStackTrace();
        }
    }

    public void onClearAllClick (View view){
        assignmentArrayList.clear();
        assignmentArrayAdapter.notifyDataSetChanged();
    }

    public void onCalcClick (View view) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("resultArrayList", assignmentArrayList);
        startActivity(intent);
    }

    public void deleteAssignment (MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        assignmentArrayList.remove(info.position);
        assignmentArrayAdapter.notifyDataSetChanged();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu_item_longpress, menu);
    }

    public boolean onContextItemSelected(final MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_delete:
                deleteAssignment(item);
                return true;

            case R.id.menu_edit:
                final LinearLayout horLayout = new LinearLayout(this);
                final EditText newNameView = new EditText(this);
                final EditText newGradeView = new EditText(this);
                final EditText newWeightView = new EditText(this);
                newNameView.setHint("Name");
                newNameView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                newGradeView.setHint("Grade");
                newGradeView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                newWeightView.setHint("Weight");
                newWeightView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                horLayout.addView(newNameView);
                horLayout.addView(newGradeView);
                horLayout.addView(newWeightView);
                final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Assignment assignmentToEdit = assignmentArrayList.get(info.position);



                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Edit");
                builder.setView(horLayout);
                builder.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String newNameStr = newNameView.getText().toString();
                        Log.d("DEBUGGO", "HERE"+newNameStr);

                        final String newGradeStr = newGradeView.getText().toString();
                        final String newWeightStr = newWeightView.getText().toString();
                        assignmentToEdit.editAssignment(newNameStr, newGradeStr, newWeightStr);
                        assignmentArrayAdapter.notifyDataSetChanged();
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
    public double getWeightTotal(){
        double weightTotal = 0;
        for (Assignment a : assignmentArrayList){
            weightTotal += a.getWeight();
        }
        return weightTotal;
    }


}
