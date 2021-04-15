package com.example.editpart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class HomePage extends AppCompatActivity {

    LinearLayout delete;
    LinearLayout remove;
    LinearLayout modify;
    LinearLayout manual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        //隐藏状态栏
        //this.getSupportActionBar().hide();

        initialView();


        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //加内容（intent或者干嘛干嘛）
                Intent intent = new Intent(HomePage.this, RemovedManually.class);
                startActivity(intent);
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //加内容（intent或者干嘛干嘛）
                Intent intent = new Intent(HomePage.this, ModifyContent.class);
                startActivity(intent);
            }
        });
    }

    public void initialView(){
        delete = findViewById(R.id.delete);
        remove = findViewById(R.id.remove);
        modify = findViewById(R.id.modify);
        manual = findViewById(R.id.manual);
    }
}
