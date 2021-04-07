package com.dsdairysytem.dairyshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ChooseLang extends AppCompatActivity {
    TextView english,hindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lang);

        english = findViewById(R.id.english);
        hindi = findViewById(R.id.hindi);

        loadLocale();

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                Intent login = new Intent(ChooseLang.this,Login_Screen.class);
                startActivity(login);
                finish();
            }
        });

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("hi");
                Intent login = new Intent(ChooseLang.this,Login_Screen.class);
                startActivity(login);
                finish();
            }
        });



    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration,getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
        editor.putString("MyLang",lang);
        editor.apply();

    }
    public void loadLocale() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        String language = prefs.getString("MyLang","");
        Log.d("CURRENT LANGUAGE",language);
        setLocale(language);
    }
}