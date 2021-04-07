package com.dsdairysytem.dairyshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {


    String phnumber;
    int check=0;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        loadLocale();



        videoView = findViewById(R.id.video_view);
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.splash_screen_client_2);
        videoView.start();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        videoView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(isFinishing())
                    return;
                if(check==0) {
                    startActivity(new Intent(SplashScreen.this, ChooseLang.class));
                    finish();
                }
                else if (check==1){
                    Intent work = new Intent(SplashScreen.this, MainActivity.class);
                    work.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(work);

                }
                else {
                    Intent intent1 = new Intent(getApplicationContext(), Register.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("phonenumber", phnumber);
                    finish();
                    startActivity(intent1);
                }
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            phnumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference documentReference = db.collection("DairyShop").document(phnumber);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot != null && snapshot.exists()) {

                            check=1;
                            Log.d("ac", "exists");

                        } else {
                            check=2;
                        }
                    }

                }
            });
       }
        else {
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    startActivity(new Intent(SplashScreen.this, ChooseLang.class));
                    finish();
                }
            });
        }

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

