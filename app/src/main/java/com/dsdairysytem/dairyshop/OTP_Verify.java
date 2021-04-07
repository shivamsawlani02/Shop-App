package com.dsdairysytem.dairyshop;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;



public class OTP_Verify extends AppCompatActivity {
    ImageView logo;
    TextView text1;
    TextView text2;
    ProgressBar btn_next;
    OtpTextView otpView;
    String phone="";
    String verificationsent;
    TextView warning;
    TextView timer;
    Button resend;

//    TimerButton timerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
   //     getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen          super.onCreate(savedInstanceState);
        setContentView(R.layout.optscreen);

        //timerButton=findViewById(R.id.timer_button);
        warning=findViewById(R.id.warning);
     //   logo = findViewById(R.id.logo);
        text1 = findViewById(R.id.your_number);
        text2 = findViewById(R.id.getting_number);
        btn_next = findViewById(R.id.btn_next);
        otpView = findViewById(R.id.otp_view);
        timer = findViewById(R.id.timer);
        resend = findViewById(R.id.resend);

        warning.setVisibility(View.GONE);
        btn_next.setVisibility(View.GONE);

        Intent ph = getIntent();
        String shownumber=ph.getStringExtra("phonenumber");
        phone = "+91"+ph.getStringExtra("phonenumber");

        verifyphone(phone);

        text2.setText(shownumber);

//        timerButton.setDuration(20000);
//
//        timerButton.startAnimation();
//
//        timerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                verifyphone(phone);
//
//            }
//        });

        final CountDownTimer countDownTimer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long l) {
                timer.setText(l/1000+" seconds");
            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.INVISIBLE);
                resend.setVisibility(View.VISIBLE);
            }
        };

        countDownTimer.start();

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyphone(phone);
                countDownTimer.start();
                timer.setVisibility(View.VISIBLE);
                resend.setVisibility(View.GONE);

            }
        });


        otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                verifyotp(otp);
                btn_next.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });


    }
    protected void verifyphone(String phoneno){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                phoneno,        // Phone number to verify
                20,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.d("OTP SEND","SUCCESSFULLY");
            verificationsent=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


            String opt=phoneAuthCredential.getSmsCode();
            if(opt!=null){
                btn_next.setVisibility(View.VISIBLE);
                verifyotp(opt);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTP_Verify.this,e.getMessage()+"onverfication failed"+phone,Toast.LENGTH_LONG).show();
        }
    };

    protected  void verifyotp(String verifactionopt){
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationsent, verifactionopt);
            signupuser(credential);
        }catch (Exception e){
            btn_next.setVisibility(View.INVISIBLE);
            warning.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Verification Code is wrong", Toast.LENGTH_SHORT).show();
        }
    }

    protected void signupuser(PhoneAuthCredential credentail){

        final FirebaseAuth auth= FirebaseAuth.getInstance();

        auth.signInWithCredential(credentail).addOnCompleteListener(OTP_Verify.this, new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = db.collection("DairyShop").document(phone);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                warning.setVisibility(View.GONE);
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot != null && snapshot.exists()) {
                                    Intent intent = new Intent(OTP_Verify.this, MainActivity.class);

                                    Pair[] pairs=new Pair[3];
                                    //  pairs[0]=new Pair<View ,String>(logo,"trans1");
                                    pairs[0]=new Pair<View ,String>(text1,"trans2");
                                    // pairs[2]=new Pair<View ,String>(text2,"trans3");
                                    pairs[1]=new Pair<View ,String>(btn_next,"trans4");
                                    pairs[2]=new Pair<View ,String>(otpView,"trans5");

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    ActivityOptions options1 = ActivityOptions.makeSceneTransitionAnimation(OTP_Verify.this, pairs);

                                    startActivity(intent, options1.toBundle());
                                    Log.d("ac", "exists");

                                } else {
                                    Intent intent = new Intent(OTP_Verify.this,Register.class);


                                    Pair[] pairs=new Pair[3];
                                    //  pairs[0]=new Pair<View ,String>(logo,"trans1");
                                    pairs[0]=new Pair<View ,String>(text1,"trans2");
                                    // pairs[2]=new Pair<View ,String>(text2,"trans3");
                                    pairs[1]=new Pair<View ,String>(btn_next,"trans4");
                                    pairs[2]=new Pair<View ,String>(otpView,"trans5");

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(OTP_Verify.this, pairs);

                                    intent.putExtra("phonenumber",phone);

                                    startActivity(intent, options.toBundle());
                                }
                            }
                        }
                    });
                    warning.setVisibility(View.GONE);
                }
            }
        });





    }

}
