package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class last extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
    }

    public void toList(View v){
        Intent intent = new Intent(this, listContact.class);
        startActivity(intent);
    }

    public void toSend(View v){
        Intent intent = new Intent(this, Alert.class);
        startActivity(intent);
    }
}
