package com.example.kapricho.administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.kapricho.R;
import com.example.kapricho.administrador.fragmentsEstadisticas.SectionPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class estadisticas_admin extends AppCompatActivity {
    Toolbar toolbar;
    Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_admin);
        SectionPageAdapter sectionsPagerAdapter = new SectionPageAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_estadisticas_admin);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabs.setupWithViewPager(viewPager);
    }

    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity_admin.class);
        startActivity(i);
        finish();
    }
}