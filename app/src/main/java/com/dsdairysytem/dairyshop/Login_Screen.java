package com.dsdairysytem.dairyshop;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Login_Screen extends AppCompatActivity {

    ImageView logo;
    TextView text1;
    TextView text2;
    Button btn_next;
    EditText phonenumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen          super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

        //  logo = findViewById(R.id.logo);
        text1 = findViewById(R.id.welocme_text);
        //text2 = findViewById(R.id.mini_text);
        btn_next = findViewById(R.id.btn_continue);
        phonenumber = findViewById(R.id.phonenumber);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                String number = phonenumber.getText().toString();

                if (number.isEmpty()) {
                    phonenumber.setError("Field is empty");
                } else {
                    if (number.length() != 10) {
                        phonenumber.setError("wrong format");
                    } else {
                        Intent intent = new Intent(Login_Screen.this, OTP_Verify.class);

                        Pair[] pairs = new Pair[3];
                        // pairs[0] = new Pair<View, String>(logo, "trans1");
                        pairs[0] = new Pair<View, String>(text1, "trans2");
                        pairs[1] = new Pair<View, String>(btn_next, "trans4");
                        pairs[2] = new Pair<View, String>(phonenumber, "trans5");

                        intent.putExtra("phonenumber", number);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_Screen.this, pairs);
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                }

            }
        });


    }
}
