package com.dsdairysytem.dairyshop.add_client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.dsdairysytem.dairyshop.MainActivity;
import com.dsdairysytem.dairyshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class AddClient extends AppCompatActivity {
    public static EditText phone,name;
    ImageView proceed,contact;
    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int CONTACTS_PERMISSION_REQUEST_CODE = 2;
    private Map<String,String> mapList = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String p,n;
    String milkmanMobile;
    int REQUEST_CODE_CONTACTS = 3;
    ProgressBar pgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        attachID();
        db.collection("DairyShop").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            if(documentSnapshot.get("Seller") != null){
                                mapList = ((Map<String, String>) documentSnapshot.get("Seller"));
                            }
                            Log.v("Map",mapList.toString());
//                            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
//                                mapList.put(Objects.requireNonNull(documentSnapshot.get("Name")).toString(), Objects.requireNonNull(documentSnapshot.get("Mobile")).toString());
//                            }
                        }
                    }
                });
        mCodeScanner = new CodeScanner(this, scannerView);
        if (!(EasyPermissions.hasPermissions(AddClient.this,
                Manifest.permission.CAMERA))) {
            EasyPermissions.requestPermissions(AddClient.this,
                    "ALLOW CAMERA ACCESS",
                    CAMERA_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA);
        }
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                AddClient.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        phone.setText(result.getText());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(EasyPermissions.hasPermissions(AddClient.this,
                        Manifest.permission.READ_CONTACTS))) {
                    EasyPermissions.requestPermissions(AddClient.this,
                            "ALLOW CONTACTS ACCESS",
                            CONTACTS_PERMISSION_REQUEST_CODE,
                            Manifest.permission.READ_CONTACTS);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent,REQUEST_CODE_CONTACTS);
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p = phone.getText().toString();
                n = name.getText().toString();
                if (!(p.startsWith("+91"))) {
                    p = "+91" + p;
                }
                Log.i("MOBILE NUMBER", p);
                // int f = checkClientAlreadyAdded(p);
                if (p != null && n != null) {
                    if (p.length() < 13) {
                        Toast.makeText(AddClient.this, getResources().getString(R.string.invaild_mobile_no), Toast.LENGTH_SHORT).show();
                    } else {
                        pgBar.setVisibility(View.VISIBLE);
                        proceed.setVisibility(View.INVISIBLE);
                        checkSellerExists(p);
                    }
                } else {
                    Toast.makeText(AddClient.this, getResources().getString(R.string.please_fill_all_the_details), Toast.LENGTH_SHORT).show();
                }

                phone.setText("");
            }

        });


    }



    private void checkSellerExists(final String m)
    {
        db.collection("DairyShop").document(user_id).collection("Seller").document(m).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            Toast.makeText(AddClient.this, R.string.seller_already_connected, Toast.LENGTH_SHORT).show();
                            pgBar.setVisibility(View.INVISIBLE);
                            proceed.setVisibility(View.VISIBLE);
                        }
                            n = name.getText().toString();
                            p = phone.getText().toString();
                            mapList.put(n,m);

                            Map<String,Object> docMap = new HashMap<>();
                            docMap.put("Seller",mapList);
                            if(mapList.isEmpty()){
                                db.collection("DairyShop").document(user_id).set(docMap, SetOptions.merge());
                            }else{
                                db.collection("DairyShop").document(user_id).update(docMap);
                            }
                            Log.v("Seller",docMap.toString());
                            Map<String,Object> docData = new HashMap<>();
                            docData.put("Name",n);
                            docData.put("Mobile",m);

                            db.collection("DairyShop").document(user_id).collection("Seller").document(m).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(AddClient.this, MainActivity.class);
                                        Log.i("MOBILE INTENT",m);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });



                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String num = "";
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (numbers.moveToNext()) {
                            num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone.setText(num);
                            //Toast.makeText(MainActivity.this, "Number="+num, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void attachID() {
        phone = findViewById(R.id.mobile);
        proceed = findViewById(R.id.proceed);
        scannerView = findViewById(R.id.scanner_view);
        db = FirebaseFirestore.getInstance();
        //milkmanMobile = "+919988776655";
        milkmanMobile= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        name = findViewById(R.id.etAddClientName);
        contact=findViewById(R.id.contact);
        pgBar=findViewById(R.id.progress_bar);
    }


}