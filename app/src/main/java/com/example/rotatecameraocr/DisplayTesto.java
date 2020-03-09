package com.example.rotatecameraocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayTesto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_testo);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        Intent intent = getIntent();
        String msg = intent.getExtras().getString("test");//intent.getExtras(MainActivity.KEY);
        String[] lines = msg.split(System.getProperty("line.separator"));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        // Add textview 1
        for( int i=0; i<lines.length; i++) {
            EditText et = new EditText(this);
            et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            et.setText(lines[i]);
            et.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(et);
        }
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();// per customizzare il comportamento del back button del action bar in alto
        return true;
    }
}