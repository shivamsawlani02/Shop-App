package com.dsdairysytem.dairyshop;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextView text1;
    EditText name,email,houseNo,colony,city;
    Button btn_nxt;
    String newToken;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
    //    getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.registerscreen);

        text1 = findViewById(R.id.register_text);
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        houseNo = findViewById(R.id.house_number);
        colony = findViewById(R.id.colony);
        city = findViewById(R.id.city);

        btn_nxt = findViewById(R.id.nxt_btn);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Register.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ph = getIntent();
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String phnumber = ph.getStringExtra("phonenumber");
                String adl1 = houseNo.getText().toString().trim();
                String adl2 = colony.getText().toString().trim();
                String City = city.getText().toString().trim();

                final String token = newToken;

                if (Name.isEmpty()) {
                    name.setError("Enter name");
                }
                else if(adl1.isEmpty()) {
                    houseNo.setError("Enter House Number");
                }
                else if(adl2.isEmpty()) {
                    colony.setError("Enter Colony");
                }
                else if(City.isEmpty()) {
                    city.setError("Enter City");
                }

                else {
//                        if (Email.isEmpty()) {
//
//                            FirebaseFirestore db = FirebaseFirestore.getInstance();
//                            DocumentReference documentReference = db.collection("Client").document(phnumber);
//
//
//                            Profile_Deatils profile_deatils = new Profile_Deatils(usermane, Email, phnumber, token);
//                            documentReference.set(profile_deatils);
//                            Intent intent = new Intent(Register.this, HomeFragment.class);
//
//                            Pair[] pairs = new Pair[2];
//                            pairs[0] = new Pair<View, String>(text1, "trans2");
//                            pairs[1] = new Pair<View, String>(btn_nxt, "trans4");
//
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this, pairs);
//                                startActivity(intent, options.toBundle());
//
//
//                            } else {
//                                startActivity(intent);
//                                finish();
//                            }
//                        } else {


                            // LINKING EMAIL WITH PHONE

                    if(!Email.isEmpty()) {
                        firebaseAuth.getCurrentUser().updateEmail(Email);
                    }

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            DocumentReference documentReference = db.collection("DairyShop").document(phnumber);

                            //Profile_Deatils profile_deatils = new Profile_Deatils(Name, Email, phnumber, token,adl1,adl2,City,PostalCode);

                    Map<String, String> profileDoc = new HashMap<>();
                    profileDoc.put("Name",Name);
                    if(!Email.isEmpty()) {
                        profileDoc.put("Email", Email);
                    }
                    profileDoc.put("Mobile",phnumber);
                    profileDoc.put("Token id",token);
                    profileDoc.put("Adl1",adl1);
                    profileDoc.put("Adl2",adl2);
                    profileDoc.put("City",City);
                            documentReference.set(profileDoc);
                            Intent intent = new Intent(Register.this,MainActivity.class);

                            Pair[] pairs = new Pair[2];
                            pairs[0] = new Pair<View, String>(text1, "trans2");
                            pairs[1] = new Pair<View, String>(btn_nxt, "trans4");

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this, pairs);
                                startActivity(intent, options.toBundle());

                            } else {
                                startActivity(intent);
                                finish();
                            }
                    }
                }

        });
    }
}
