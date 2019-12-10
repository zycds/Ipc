package com.zhangyc.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, BundleActivity.class));
    }

    public void startBundle(View view) {
        Intent intent = new Intent(this, BundleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("key", "hello");
        intent.putExtra("bundleKey", bundle);
        startActivity(intent);
    }

}
