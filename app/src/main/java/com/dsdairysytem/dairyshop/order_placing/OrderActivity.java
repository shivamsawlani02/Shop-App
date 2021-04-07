package com.dsdairysytem.dairyshop.order_placing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsdairysytem.dairyshop.DatePickerFragment;
import com.dsdairysytem.dairyshop.MainActivity;
import com.dsdairysytem.dairyshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private RecyclerView recyclerView;
    private EditText etQuantity;
    private EditText etTotalFat;
    private EditText etFatRate;
    private TextView tvPrice;
    private EditText etSearchSeller;
    private String name, mobile;
    private String user_id= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
    private Map<String,String> mapList = new HashMap<>();
    private OrderSellerAdapter orderSellerAdapter;
    public static LinearLayout selectSeller, searchResult, searchbar;
    public static TextView connectedName, connectedMobile;
    public Button btCancel, btDelivered;
    private ProgressDialog progressDialog;
    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private double Stock;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button date;
    String Date="";
    TextView noMilkman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Stock = getIntent().getDoubleExtra("Stock",0.0);
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        selectSeller = findViewById(R.id.selectedSeller);
        TextView tvTitle = findViewById(R.id.title);
        etTotalFat = findViewById(R.id.etTotalFat);
        etFatRate = findViewById(R.id.etFatRate);
        etQuantity = findViewById(R.id.etQuantity);
        date = findViewById(R.id.date);
        ImageButton btBack = findViewById(R.id.btBack);
        btCancel = findViewById(R.id.btCancel);
        btDelivered = findViewById(R.id.btDelivered);
        noMilkman = findViewById(R.id.no_milkman);
        searchResult = findViewById(R.id.search_result_layout);
        searchbar = findViewById(R.id.searchbar_layout);
        connectedName = findViewById(R.id.connectedName);
        connectedMobile = findViewById(R.id.connectedMobile);
        tvPrice = findViewById(R.id.tvPrice);
        etSearchSeller = findViewById(R.id.etSearchSeller);
        recyclerView = findViewById(R.id.milkman_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderSellerAdapter);
        Log.v("Extra",Double.toString(Stock)+" is something passed from last intent");
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (name!= null && mobile!=null) {
            searchbar.setVisibility(View.GONE);
            etSearchSeller.setVisibility(View.GONE);
            selectSeller.setVisibility(View.VISIBLE);
            connectedName.setText(name);
            connectedMobile.setText(mobile);
        }

        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date = df.format(cal);
        date.setText(getString(R.string.date)+" - " + Date);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker=new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        etSearchSeller.setVisibility(View.VISIBLE);
        //Fetching MapList of the connected Sellers
//        db.collection("DairyShop").document(user_id).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            assert documentSnapshot != null;
//                            if(documentSnapshot.get("Seller") != null){
//                                mapList = ((Map<String, String>) documentSnapshot.get("Seller"));
//                            }
//                            Log.d("SIZE OF MAPLIST",mapList.size()+"");
////                            for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
////                                mapList.put(Objects.requireNonNull(documentSnapshot.get("Name")).toString(), Objects.requireNonNull(documentSnapshot.get("Mobile")).toString());
////                            }
//                        }
//                    }
//                });
        db.collection("DairyShop").document(user_id).collection("Seller").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if (task.isSuccessful()) {
                       QuerySnapshot queryDocumentSnapshots = task.getResult();
                       assert queryDocumentSnapshots != null;
                       for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots) {
                           mapList.put(Objects.requireNonNull(documentSnapshot.get("Name")).toString(), Objects.requireNonNull(documentSnapshot.get("Mobile")).toString());
                       }
                       Log.d("SIZE OF MAPLIST",mapList.size()+"");
                   }
                    }
                });
        Log.v("Map",mapList.values().toString());
        etSearchSeller.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("SEARCH BAR TEXT",s+"");
                searchResult.setVisibility(View.VISIBLE);
                filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFatRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePrice();
            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePrice();
            }
        });

        etTotalFat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePrice();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this,OrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });

        btDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name == null || mobile == null){
                    Toast.makeText(OrderActivity.this,getResources().getString(R.string.please_select_seller),Toast.LENGTH_SHORT).show();
                }
                else if (etQuantity.getText().toString().isEmpty() || etFatRate.getText().toString().isEmpty() ||etTotalFat.getText().toString().isEmpty() || name == null || mobile == null) {
                    Toast.makeText(OrderActivity.this,getResources().getString(R.string.please_fill_all_the_details),Toast.LENGTH_SHORT).show();
                } else {

                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle(v.getContext().getResources().getString(R.string.order_delivered));
                    progressDialog.setMessage(v.getContext().getResources().getString(R.string.updating_please_wait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    Date cal = null;
                    try {
                        cal = df.parse(Date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //final String date = df.format(cal);

                    final int day = Integer.parseInt(Date.split("-")[0]);
                    final int month = Integer.parseInt(Date.split("-")[1]);
                    final int year = Integer.parseInt(Date.split("-")[2]);


                    double fat = Double.parseDouble(etTotalFat.getText().toString());
                    final double quantity = Double.parseDouble(etQuantity.getText().toString());
                    double rate = Double.parseDouble(etFatRate.getText().toString());
                    double price = fat*rate*quantity;

                    final Map<String,Object> docData = new HashMap<>();
                    docData.put("SellerName",name);
                    docData.put("Date",Date);
                    docData.put("RateperFat",rate);
                    docData.put("Quantity",quantity);
                    docData.put("FatUnits",fat);
                    docData.put("Month",month_name[month-1]);
                    docData.put("Amount",price);
                    docData.put("timestamp",cal);

                    db.collection("DairyShop").document(user_id).collection("Seller").document(mobile).collection("Orders")
                            .add(docData)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    if (task.isSuccessful()) {
                                        Map<String,Object> stats = new HashMap<>();
                                        stats.put("Stock",(quantity+Stock));
                                        if(Stock>0.0){
                                            db.collection("DairyShop").document(user_id).collection("Stats").document(Integer.toString(year)).collection(Integer.toString(month)).document(Integer.toString(day)).update(stats);
                                        }else{
                                            db.collection("DairyShop").document(user_id).collection("Stats").document(Integer.toString(year)).collection(Integer.toString(month)).document(Integer.toString(day)).set(stats);
                                        }
                                        db.collection("DairyShop").document(user_id).collection("Orders").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                           if (task.isSuccessful()) {
                                               Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                               progressDialog.dismiss();
                                               startActivity(intent);
                                               finish();
                                           }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(OrderActivity.this,getResources().getString(R.string.error_occured),Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }
            }
        });

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        //String currentDateString= DateFormat.getDateInstance().format(c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date = df.format(c.getTime());
        date.setText(getString(R.string.date)+" - "+Date);
    }

    private void updatePrice() {
        if (etQuantity.getText().toString().isEmpty() || etFatRate.getText().toString().isEmpty() ||etTotalFat.getText().toString().isEmpty()) {
        } else {
            double fat = Double.parseDouble(etTotalFat.getText().toString());
            double quantity = Double.parseDouble(etQuantity.getText().toString());
            double rate = Double.parseDouble(etFatRate.getText().toString());
            tvPrice.setText(String.valueOf(fat*rate*quantity));
        }
    }


    private void filter(CharSequence s) {
    Map<String,String> map = new HashMap<>();
    noMilkman.setVisibility(View.GONE);

    //Log.d("MAPLIST SIZE",mapList.size()+"");

    for (String e: mapList.keySet()) {
        if (e.toLowerCase().contains(s.toString().toLowerCase()) && (!s.equals(""))) {
            map.put(e,mapList.get(e));
        }
    }
    if(map.size()==0)
    {
        noMilkman.setVisibility(View.VISIBLE);
    }
    orderSellerAdapter = new OrderSellerAdapter(map,getApplicationContext(),true, Stock);
    orderSellerAdapter.notifyDataSetChanged();
    recyclerView.setAdapter(orderSellerAdapter);
    //searchbar.setVisibility(View.GONE);

    }
}