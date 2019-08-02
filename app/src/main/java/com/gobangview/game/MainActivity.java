package com.gobangview.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GoBangPanel bangPanel = findViewById(R.id.gobang_view);
        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bangPanel.restart();
            }
        });
    }
}
