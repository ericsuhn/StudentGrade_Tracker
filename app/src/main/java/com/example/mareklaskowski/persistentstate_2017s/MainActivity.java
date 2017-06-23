package com.example.mareklaskowski.persistentstate_2017s;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    //declare a constant for our SharedPreferences file name
    public static final String PREF_FILE_NAME = "mySPfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load shared prefereces from the Shared Preferences file (if it exists!)
        loadSharedPreferences();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //save preferences using a function
        saveSharedPreferences();

    }

    /*
    save data in the Shared Preferences file...
    specifically we're going to save the dtata entered into the
    EditText boxes that has not yet been saved to the database...
     */
    protected void saveSharedPreferences(){
        Log.i("DEBUG", "saveSharedPreferences was called");
        //recall the key classes we need to work with for SharedPreferences
        //step 0: get a SharedPreferences instance from the framework
        //specify a filename and mode arguments
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, 0); //mode 0 means private
        //step 1: next in order to write to SharedPreferences we need to use the SharedPreferences.Editor object
        //calling .edit() on our SharedPreferences instance will get us an Editor object
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //step 2: get the values that we want to store...
        EditText studentID_EditText = (EditText) findViewById(R.id.editText_ID);
        long studentId = Long.parseLong(studentID_EditText.getText().toString());
        //step 3: use the editor to put our data into SharedPrefrences
        editor.putLong("studentID", studentId); //TODO: use a static constant for the key instead!
        //TODO: you will also save the grade for next time!!
        //step 4: call commit to save the changes!
        editor.commit();//alternatively you may use apply
    }

    /*
    this function reads data that we have previously saved in SharedPReferences and use the data
     */
    protected void loadSharedPreferences(){
        Log.i("DEBUG", "loadSharedPreferences was called");
        //step 0: open the shared preferences file (if exists) - otherwise it will be created when you call getSharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_FILE_NAME, 0); //mode 0 means private
        //get data out of the shared preferences file
        long studentId = sharedPreferences.getLong("studentID", -1);
        if(studentId > 0){
            //if there was a valid studentID saved in the shared preferences file, insert it into the appropriate text box
            EditText studentID_EditText = (EditText) findViewById(R.id.editText_ID);
            studentID_EditText.setText(""+studentId);
        }
        //TODO: get the grade as well!

    }
}
