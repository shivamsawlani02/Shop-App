package com.dsdairysytem.dairyshop.seller_tab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysytem.dairyshop.MonthYearPickerDialog;
import com.dsdairysytem.dairyshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MilkmanDetail extends AppCompatActivity {
    TextView name,firstLetter,email,phone,address,dueAmount;
    Button billInvoice;
    ImageButton share;
    FirebaseFirestore db;
    String sellerMobile; //To be received through intent from seller fragment.
    String shopMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ArrayList<Map> mapArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    Dialog customDialog;
    private Button btExPdf, btExcel;
    private ImageView btCancel;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    ImageView back;
    MaterialButton disconnect;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milkman_detail);

        name = findViewById(R.id.textView);
        disconnect = findViewById(R.id.remove_client);
        firstLetter = findViewById(R.id.tvFirstLetter);
        email = findViewById(R.id.textView2);
        phone = findViewById(R.id.textView3);
        address = findViewById(R.id.textView4);
        recyclerView = findViewById(R.id.rv_customerdetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);
        dueAmount = findViewById(R.id.textView6);
        billInvoice = findViewById(R.id.btOpenPdf2);
        share = findViewById(R.id.btSharePdf);
        back = findViewById(R.id.btBack2);
        db = FirebaseFirestore.getInstance();
        sellerMobile = getIntent().getStringExtra("mobile");

        pd = new ProgressDialog(this);
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        billInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfWrapper(false);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MilkmanDetail.this);
                alert.setTitle(R.string.disconnect)
                        .setMessage(R.string.sure_to_disconnect)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.collection("DairyShop").document(shopMobile).collection("Seller").document(sellerMobile).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MilkmanDetail.this, "Seller deleted successfully", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MilkmanDetail.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alert.show();

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfWrapper(true);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

     //   if (db.collection("DairyShop").document(shopMobile).collection("Seller").document(sellerMobile).collection("Orders").get() != null) {}

            db.collection("DairyShop").document(shopMobile).collection("Seller").document(sellerMobile).collection("Orders").orderBy("timestamp").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    Map<String,Object> docData = new HashMap<>();
                                    docData.put("Rate",doc.get("RateperFat"));
                                    docData.put("Total Fat",doc.get("FatUnits"));
                                    docData.put("Quantity",doc.get("Quantity"));
                                    docData.put("Amount",doc.get("Amount"));
                                    docData.put("Date",doc.get("Date"));

                                    mapArrayList.add(docData);
                                }
                                orderAdapter = new OrderAdapter(mapArrayList,getApplicationContext());
                                recyclerView.setAdapter(orderAdapter);
                                orderAdapter.notifyDataSetChanged();
                            }
                            pd.dismiss();
                        }
                    });


        db.collection("DairyShop").document(shopMobile).collection("Seller").document(sellerMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String Name = documentSnapshot.getString("Name");
                name.setText(Name);
                firstLetter.setText(Name.substring(0,1));
                if(documentSnapshot.get("Email") != null) {
                    email.setText(documentSnapshot.getString("Email"));
                } else email.setText("No email registered");
                phone.setText(documentSnapshot.getString("Mobile"));



            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MilkmanDetail.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private void createPdfWrapper(Boolean share) throws FileNotFoundException, DocumentException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel(getString(R.string.allow_storage_access),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            if (share) {
                showExportDialog(true);
            } else {
                ExportOrders pd = new ExportOrders();
                pd.setList(this,this,share,"pdf",phone.getText().toString());
                pd.show(this.getSupportFragmentManager(), "MonthYearPickerDialog");}
        }
    }

    private void showExportDialog(final Boolean share) {
        customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.export_dialog);
        btExcel = customDialog.findViewById(R.id.btExportExcel);
        btExPdf = customDialog.findViewById(R.id.btExportPdf);
        btCancel = customDialog.findViewById(R.id.cancel);

        btExPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                ExportOrders pd = new ExportOrders();
                pd.setList(MilkmanDetail.this, MilkmanDetail.this,share,"pdf",phone.getText().toString());
                pd.show(MilkmanDetail.this.getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                ExportOrders pd = new ExportOrders();
                pd.setList(MilkmanDetail.this, MilkmanDetail.this, share,"excel",phone.getText().toString());
                pd.show(MilkmanDetail.this.getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }
}