package com.dsdairysytem.dairyshop;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysytem.dairyshop.add_client.AddClient;
import com.dsdairysytem.dairyshop.add_client.ConnectClient;
import com.dsdairysytem.dairyshop.order_placing.OrderActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.dsdairysytem.dairyshop.MainActivity.drawer;

public class HomeFragment extends Fragment {
//    private Button btAddClient, btConnectClient, btOrder;
private Button btAddClient, btOrder;
    public final int REQUEST_CODE_ASK_PERMISSIONS = 1001;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private double Milk_Ordered =0;
    private TextView tvMilk;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btAddClient = view.findViewById(R.id.btAddClient);
        tvMilk =view.findViewById(R.id.tvMilkLeft);
//        btConnectClient = view.findViewById(R.id.btConnectClient);
        btOrder = view.findViewById(R.id.btOrder);
        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        final String date = df.format(cal);
        final int day = Integer.parseInt(date.split("-")[0]);
        final int month = Integer.parseInt(date.split("-")[1]);
        final int year = Integer.parseInt(date.split("-")[2]);
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Stats").document(Integer.toString(year)).collection(Integer.toString((month))).document(Integer.toString(day));
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>(){
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    System.err.println("Listen Failed: "+error);
                    return;
                }
                if(value != null && value.exists()){
                    Milk_Ordered = value.getDouble("Stock");
                    tvMilk.setText(Double.toString(Milk_Ordered));
                    Log.v("Stocks",Milk_Ordered+" is the value fetched from Server");
                    return;
                }
                tvMilk.setText("0.0");
                Log.v("Stocks",Milk_Ordered+" is the value which not found at Server"+(day)+"_"+(month)+ "_"+(year));
            }
        });
//        FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Orders").whereEqualTo("Month",month_name[month])
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    for (DocumentSnapshot documentSnapshot:task.getResult())
//                        Milk_Ordered += (double) documentSnapshot.get("Quantity");
//                }
//                tvMilk.setText(Double.toString(Milk_Ordered));
//            }
//        });


//        btConnectClient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ConnectClient.class);
//                startActivity(intent);
//            }
//        });

        btOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("Stock", Milk_Ordered);
                startActivity(intent);
            }
        });

        btAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hasCamPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                if (hasCamPermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                            showMessageOKCancel("You need to allow access to Camera",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE_ASK_PERMISSIONS);
                                            }
                                        }
                                    });
                            return;
                        }
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }
                    return;
                } else {
                        Intent intent = new Intent(getActivity(), AddClient.class);
                        startActivity(intent);
                }
            }
        });

        Toast.makeText(getActivity(),"home",Toast.LENGTH_SHORT);

        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });


        return view;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}