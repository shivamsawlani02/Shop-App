package com.dsdairysytem.dairyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, email, contact, add1, add2, city, firstLetter;
    ProgressDialog pd;
    String shopMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.tvProfileName);
        firstLetter = findViewById(R.id.tvFirstLetter);
        email = findViewById(R.id.tvProfileEmail);
        contact = findViewById(R.id.tvProfileMobile);
        add1 = findViewById(R.id.tvProfileAdd1);
        add2 = findViewById(R.id.tvProfileAdd2);
        city = findViewById(R.id.tvProfileCity);
        backButton = findViewById(R.id.back_button);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        FirebaseFirestore.getInstance().collection("DairyShop").document(shopMobile).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        pd.dismiss();
                        name.setText(documentSnapshot.getString("Name"));
                        char letter = (documentSnapshot.getString("Name")).charAt(0);
                        firstLetter.setText(Character.toString(letter));
                        email.setText(documentSnapshot.getString("Email"));
                        contact.setText(documentSnapshot.getString("Mobile"));
                        city.setText(documentSnapshot.getString("City"));
                        add1.setText(documentSnapshot.getString("Adl1"));
                        add2.setText(documentSnapshot.getString("Adl2"));
                    }
                }
            }
        });
    }
}