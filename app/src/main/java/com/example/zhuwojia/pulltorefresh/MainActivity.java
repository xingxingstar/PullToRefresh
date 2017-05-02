package com.example.zhuwojia.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    float firstDistance, secondDistance, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float eventY = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstDistance = eventY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        distance = eventY - firstDistance;
                        recyclerView.setY(distance);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("tag", "暂停手势");
                        break;
                    case MotionEvent.ACTION_UP:
                        recyclerView.setY(0);
                        break;
                }

                return true;
            }
        });

    }

}
