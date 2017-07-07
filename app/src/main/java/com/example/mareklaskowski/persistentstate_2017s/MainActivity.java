package com.example.mareklaskowski.persistentstate_2017s;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //recall: to perform database operations we need several "helper classes"
    ArrayList<String> studentList = new ArrayList<String>();
    ArrayList<String> studentListPage = new ArrayList<String>();

    int maxRecords = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        maxRecords = intent.getIntExtra("records", 3);

        //load shared prefereces from the Shared Preferences file (if it exists!)
        loadSharedPreferences();


        System.out.println(maxRecords + ".................");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }

        ListView listView = (ListView) findViewById(R.id.myListView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentList);



        //handler for the Add button that will write records to the database
        Button saveGradeButton = (Button) findViewById(R.id.button);
        saveGradeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //delegate the actual work to a method...
                saveGrade();
            }
        });

        //load data from the database
        loadDatabase();
        listView.setAdapter(adapter);

    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //handler code
        if(requestCode == 1){

            if(resultCode == Activity.RESULT_OK){
                maxRecords = data.getIntExtra("records", 5);
                //message is received as text and as a toast
                Toast.makeText(this,"Max Records shown: " + maxRecords, Toast.LENGTH_LONG).show();

                //ListView listView = (ListView) findViewById(R.id.myListView);
                //ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentList);
                //listView.setAdapter(adapter);

                loadDatabase();

            }
            else {
                Log.e("SetRecordsActivity", "Something went wrong!");
            }
        }

    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_set_max_records:
                setMaxRecords();
                return true;
            case R.id.menu_previous:
                showPrevious();
                return true;
            case R.id.menu_next:
                showNext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setMaxRecords(){
        Intent intent = new Intent(this, SetRecordsActivity.class);
        startActivityForResult(intent, 1);

    }

    public void showNext(){
        ListView listView = (ListView) findViewById(R.id.myListView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentListPage);
        listView.setAdapter(adapter);
    }

    public void showPrevious(){
        ListView listView = (ListView) findViewById(R.id.myListView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentListPage);
        listView.setAdapter(adapter);

    }



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

        //editor.putInt("max_recs", maxRecords);

        //step 2: get the values that we want to store...
        EditText studentID_EditText = (EditText) findViewById(R.id.editText_ID);
        long studentId = Long.parseLong(studentID_EditText.getText().toString());
        //step 3: use the editor to put our data into SharedPrefrences
        String studentIdKey = getString(R.string.student_id_key);
        editor.putLong(studentIdKey, studentId);

        EditText studentGrade_EditText = (EditText) findViewById(R.id.editText_Grade);
        float studentGrade = Float.parseFloat(studentGrade_EditText.getText().toString());
        //step 3: use the editor to put our data into SharedPrefrences
        String studentGradeKey = getString(R.string.student_grade_key);
        editor.putFloat(studentGradeKey, studentGrade);

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

        //maxRecords = sharedPreferences.getInt("max_recs", -1);

        long studentId = sharedPreferences.getLong("studentID", -1);
        if(studentId > 0){
            //if there was a valid studentID saved in the shared preferences file, insert it into the appropriate text box
            EditText studentID_EditText = (EditText) findViewById(R.id.editText_ID);
            studentID_EditText.setText(""+studentId);
        }

        float studentGrade = sharedPreferences.getFloat("studentGrade", -1);
        if(studentGrade > 0){
            EditText studentGrade_EditText = (EditText) findViewById(R.id.editText_Grade);
            studentGrade_EditText.setText(""+studentGrade);
        }


    }

    /**
     * this method demonstrates how to save data in the database
     */
    protected void saveGrade(){
        /*
        here we write the student id and grade to the database
        ideally, any work with the database should be done in an AsyncTask
        because these are potentially long running operations if the database is large
        //TODO: put this in an AsyncTask
         */
        //step 0: get an instance of MyDbHelper - notice it requires a Context object
        MyDbHelper helper = new MyDbHelper(this);
        //get a writeable reference to the database using the helper
        SQLiteDatabase db = helper.getWritableDatabase();
        //step 1: create a new map of values representing the new row in the table
        //where the column or field names are the table keys
        ContentValues newRow = new ContentValues();
        //step 2: add fields to the map
        EditText studentID_EditText = (EditText) findViewById(R.id.editText_ID);
        long studentId = Long.parseLong(studentID_EditText.getText().toString());
        EditText studentGrade_EditText = (EditText) findViewById(R.id.editText_Grade);
        String studentGrade = studentGrade_EditText.getText().toString();
        newRow.put(MyDataEntry.STUDENT_ID_FIELD, studentId);
        newRow.put(MyDataEntry.STUDENT_GRADE_FIELD, studentGrade);
        Log.i("DEBUG", "writing a new row to the database: "+ studentId + " " + studentGrade);
        //insert the row into the table
        //the middle argument (null) is what to insert in case newRow is itself a null object
        //returns the primary key value for the new row if it was successful
        long newRowId = db.insert(MyDataEntry.TABLE_NAME, null, newRow);
        Log.i("DEBUG", "result of database insertion: "+ newRowId);
    }

    /*
    demonstrates how to access the database for reading
     */
    protected void loadDatabase(){
        //get a reference to myDbHelper
        MyDbHelper helper = new MyDbHelper(this);
        //get a readable database instance
        SQLiteDatabase db = helper.getReadableDatabase();
        //define columns that we want to include in our query
        String[] query_columns = {
                MyDataEntry._ID,
                MyDataEntry.STUDENT_ID_FIELD,
                MyDataEntry.STUDENT_GRADE_FIELD
        };
        //construct a select query - and get a cursor object (see documentation and notes)
        String selectQuery = MyDataEntry.STUDENT_ID_FIELD + " = ?";
        //Strings to store arguments for the query
        String[] selectionArgs = {" Filter string "};
        String sortOrder = MyDataEntry.STUDENT_ID_FIELD + " DESC";
        //get the actual cursor object
        Cursor cursor = db.query(
                MyDataEntry.TABLE_NAME,
                query_columns,
                null,
                null,
                null,
                null,
                sortOrder
        );
        //use the API to navigate the record set
        //move the cursor to the first row returned
        int count = 0;
        boolean hasMoreData = cursor.moveToFirst();
        while(hasMoreData){
            //get the value out of each column or field
            long key = cursor.getLong(cursor.getColumnIndexOrThrow(MyDataEntry._ID));
            String studentID = cursor.getString(cursor.getColumnIndexOrThrow(MyDataEntry.STUDENT_ID_FIELD));
            String studentGrade = cursor.getString(cursor.getColumnIndexOrThrow(MyDataEntry.STUDENT_GRADE_FIELD));
            //for now print the row for debugging purposes
            System.out.println("RECORD KEY: " + key + " student id: " + studentID + " student grade: " + studentGrade);

            String s = "Student ID: " + studentID + ", Grade: " + studentGrade;
            //studentList.add(s);

            if(count < maxRecords) {
                studentList.add(s);
                count++;
            }
            //don't forget to get the next row:
            hasMoreData = cursor.moveToNext();

        }

    }
}
