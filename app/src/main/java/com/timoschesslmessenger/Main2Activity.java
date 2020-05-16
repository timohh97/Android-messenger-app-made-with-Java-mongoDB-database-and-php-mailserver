package com.timoschesslmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }


    public void buttonClicked(View view)
    {
       openMainActivity();
    }

    public void openMainActivity()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
