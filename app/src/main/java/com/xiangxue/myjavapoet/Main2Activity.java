package com.xiangxue.myjavapoet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xiangxue.annotation.ARouter;

@ARouter(path = "/app/Main2Activity") // + 2
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}
