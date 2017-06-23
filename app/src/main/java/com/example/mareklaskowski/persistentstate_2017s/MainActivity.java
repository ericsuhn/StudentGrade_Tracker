package com.example.mareklaskowski.persistentstate_2017s;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //recall: to perform database operations we need several "helper classes"

    /*
    HELPER CLASS #1 - for defining SQL table layouts in Java
    we also need this because Android database API expects a "magic" _ID field
    (remember the fish)
     */
    public static class MyDataEntry implements BaseColumns{
        //here we'll define the table and column names as static String constants
        //notice the _ID field is inherited from BaseColumns

        //SQL table name
        public static final String TABLE_NAME = "student_grades";
        //SQL column/field names
        public static final String STUDENT_ID_FIELD = "student_id";
        public static final String STUDENT_GRADE_FIELD = "student_grade";

    }

    /*
    HELPER CLASS #2 for database creation and version management
    stores the database name, and version, and the queries that we will use to interact with the database
     */
    public class MyDbHelper extends SQLiteOpenHelper{

        //database name
        public static final String DB_NAME = "MyCoolDatabase.db";
        //every time the databse schema chanes we need to update the table format
        //the framework takes care of this for us.. but we need to increment the DB_VERSION manually
        public static final int DB_VERSION = 1;

        //store SQL queries as strings
        //query to create the table
        private static final String SQL_CREATE_TABLE_QUERY = "CREATE TABLE " + MyDataEntry.TABLE_NAME + " (" +
                MyDataEntry._ID + " INTEGER PRIMARY KEY," + MyDataEntry.STUDENT_ID_FIELD + " TEXT," +
                MyDataEntry.STUDENT_GRADE_FIELD + " TEXT )";
        //query to delete the table (needed in case of update or upgrade)
        private static final String SQL_DELETE_QUERY = "DROP TABLE IF EXISTS " + MyDataEntry.TABLE_NAME;
        //constructor
        public MyDbHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);//call the base class's constructor!
        }


        /**
         * this framework method is called whenver the database is opened but doesn't yet exist
         * db is the database that we are working with
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("DEBUG", "Executing Query: SQL_CREATE_TABLE " + SQL_CREATE_TABLE_QUERY);
            //execute the query on the database...
            db.execSQL(SQL_CREATE_TABLE_QUERY);
        }

        /**
         * this framework method is called whenver DB_VERSION is incremented
         * which will happen when the database schema changes
         * normally, we would write code to migrate the old database format to the new database format
         * however, here we'll be lazy and just delete and recreate the database
         * @param db - the database we are working with
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("DEBUG", "Executing Query: SQL_DELETE_QUERY " + SQL_DELETE_QUERY);
            //shortcut is to delete and create a new table
            db.execSQL(SQL_DELETE_QUERY);
            onCreate(db);

        }
    }



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
