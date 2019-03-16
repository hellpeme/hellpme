package com.example.vincius.myapplication;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivitySubjectSelect extends AppCompatActivity {

    private Button op1, op2,op3,op4, next;
    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_select);
        startComponents();
        if(op1.isSelected()){

        }

    }

    private void startComponents() {
        next = findViewById(R.id.next);
        op1 = findViewById(R.id.button3);
        op2 = findViewById(R.id.button4);
        op3 = findViewById(R.id.button5);
        op4 = findViewById(R.id.button6);
    }
}
