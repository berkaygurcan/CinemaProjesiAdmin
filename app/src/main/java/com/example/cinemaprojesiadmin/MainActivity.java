package com.example.cinemaprojesiadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
 ImageButton filmEkleMainButon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tanimla();
    }
    public void tanimla()
    {
        filmEkleMainButon = findViewById(R.id.filmEkleMainButon);
        filmEkleMainButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FilmEkleActivity.class);
                startActivity(intent);
            }
        });
    }
}
