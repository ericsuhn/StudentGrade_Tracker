package com.example.mareklaskowski.persistentstate_2017s;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SetRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_records);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }


        Button done_button = (Button) findViewById(R.id.done_button);
        Button cancel_button = (Button) findViewById(R.id.cancel_button);
        done_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                done_button_click_handler();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cancel_button_click_handler();
            }
        });

    }


    public void done_button_click_handler(){

        Intent intent = new Intent(this, MainActivity.class);
        EditText maxRecords_EditText = (EditText) findViewById(R.id.maxRecordsEditText);

        int maxRecords = Integer.parseInt(maxRecords_EditText.getText().toString());
        intent.putExtra("records", maxRecords);
        startActivity(intent);

    }

    public void cancel_button_click_handler(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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
                //setMaxRecords();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
