package com.example.ismail.hiddenfounderstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomListView list = new CustomListView(this);//Initialising the CustomListView
        ((LinearLayout) findViewById(R.id.LayPrinc)).addView(list); //Adding the CustomListView to the principal layout
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}