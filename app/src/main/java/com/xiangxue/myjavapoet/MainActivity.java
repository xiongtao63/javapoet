package com.xiangxue.myjavapoet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.xiangxue.annotation.ARouter;

@ARouter(path = "/app/MainActivity") // + 1
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
