package com.dsdairysytem.dairyshop;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import static com.dsdairysytem.dairyshop.MainActivity.drawer;


public class PurchaseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    ShopOrderAdapter adapter;
    ArrayList<OrderModel> arrayList;
    FirebaseFirestore db;
    //Spinner spinner;
    ProgressBar pgBar;
    String shopMobile;
    ArrayAdapter<String> arrayAdapter, mobileArrayAdapter;
    int c = 0;
    CalendarView calendarView;
    NestedScrollView nestedScrollView;
    ProgressDialog pd;
    TextView notConnected;
    Button btPdf;
    ImageView btSharePdf;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    Dialog customDialog;
    private Button btExPdf, btExcel;
    private ImageView btCancel;
    private boolean isLastItemReached = false;
    boolean isScrolling = false;
    int currentItems, totalItems, scrolledOutItems;
    private DocumentSnapshot lastVisible;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PurchaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PurchaseFragment newInstance(String param1, String param2) {
        PurchaseFragment fragment = new PurchaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);

        attachID(view);

        shopMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        //clientMobile = "+919879874562";

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        fetchOrdersAndPagination("");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                pd.show();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String date;

                if (("" + month).startsWith("1"))
                    date = dayOfMonth + "/" + (month + 1) + "/" + year;
                else
                    date = dayOfMonth + "/0" + (month + 1) + "/" + year;
                if (("" + dayOfMonth).length() == 1)
                    date = "0" + date;
                Log.d("DATE FROM CALENDER VIEW", date);
                fetchOrders(date.trim());
            }
        });

        btPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfWrapper(false);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
//                } catch (DocumentException e) {
//                    e.printStackTrace();
                }
            }
        });

        btSharePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createPdfWrapper(true);
                } catch (FileNotFoundException e){
                    e.printStackTrace();
//                } catch (DocumentException e) {
//                    e.printStackTrace();
                }
            }
        });

        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer3);
        customDrawerButton.setDrawerLayout( drawer );
        customDrawerButton.getDrawerLayout().addDrawerListener( customDrawerButton );
        customDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDrawerButton.changeState();
            }
        });


//        db.collection("DairyShop").document(shopMobile).collection("Orders").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            pd.dismiss();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                //mobileArrayAdapter.add(document.getId());
//                                //arrayAdapter.add(document.getString("Name"));
//                            }
//                            if (arrayAdapter.getCount() == 0) {
//                                //spinner.setVisibility(View.INVISIBLE);
//                                recyclerView.setVisibility(View.INVISIBLE);
//                                btPdf.setVisibility(View.INVISIBLE);
//                                btSharePdf.setVisibility(View.INVISIBLE);
//                                notConnected.setVisibility(View.VISIBLE);
//                            } else
//                            {
//                                spinner.setAdapter(arrayAdapter);
//
//                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
//                                        pd.show();
//                                        //pagination(mobileArrayAdapter.getItem(i), arrayAdapter.getItem(i),"");
//                                        fetchOrders(mobileArrayAdapter.getItem(i), arrayAdapter.getItem(i), "");
//
//
//
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });


        return view;
    }

    void fetchOrders(final String calenderDate) {
        arrayList.clear();
        recyclerView.setVisibility(View.VISIBLE);
        btPdf.setVisibility(View.VISIBLE);
        btSharePdf.setVisibility(View.VISIBLE);
        notConnected.setVisibility(View.GONE);
        c=0;
        db.collection("DairyShop").document(shopMobile).collection("Orders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String date = sdf.format(document.getTimestamp("timestamp").toDate());
                                SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
                                String Time = localDateFormat.format(document.getTimestamp("timestamp").toDate());
                                Double Quantity = document.getDouble("Quantity");
                                Double rate = document.getDouble("RateperFat");
                                Double amount = document.getDouble("Amount");
                                Double fatUnits = document.getDouble("FatUnits");
                                String name = document.getString("SellerName");

                                OrderModel orderModel = new OrderModel(date,Time,Quantity+"",amount+"",fatUnits+"",name,rate+"");
                                //ClientOrder order = new ClientOrder(documentID, document.getId(), date, map, milkmanName, document.getLong("Amount")+"",Time);

                                if(calenderDate.equalsIgnoreCase("") || calenderDate.equalsIgnoreCase(date.trim())) {

                                    arrayList.add(c, orderModel);
                                    ++c;
                                }
                            }
                            if(arrayList.size() == 0)
                            {
                                recyclerView.setVisibility(View.INVISIBLE);
                                btPdf.setVisibility(View.INVISIBLE);
                                btSharePdf.setVisibility(View.INVISIBLE);
                                notConnected.setVisibility(View.VISIBLE);
                                notConnected.setText(R.string.no_order_available);
                            }
                            else {
                                adapter = new ShopOrderAdapter(arrayList, getActivity(), getActivity());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void fetchOrdersAndPagination(final String calenderDate) {
        Log.d("CALENDER DATE",calenderDate);
        c = 0;
        arrayList.clear();
        isLastItemReached = false;
        isScrolling = false;
        db.collection("DairyShop").document(shopMobile).collection("Orders")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            c = 0;
                            if (task.getResult() == null || task.getResult().isEmpty()){
                                recyclerView.setVisibility(View.GONE);
                                notConnected.setVisibility(View.VISIBLE);
                                btPdf.setVisibility(View.INVISIBLE);
                                btSharePdf.setVisibility(View.INVISIBLE);
                            }else {
                                recyclerView.setVisibility(View.VISIBLE);
                                notConnected.setVisibility(View.GONE);
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.i("DOCUMENT ID", document.getId());
//                                Map<String, Object> map;
//                                map = (Map<String, Object>) document.get("Milk");
//                                String date = DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
//                                Log.i("DATE OF TIMESTAMP", date);
//                                DeliveryOrder order = new DeliveryOrder(document.getId(), date, map);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String date = sdf.format(document.getTimestamp("timestamp").toDate());
                                SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
                                String Time = localDateFormat.format(document.getTimestamp("timestamp").toDate());
                                Double Quantity = document.getDouble("Quantity");
                                Double rate = document.getDouble("RateperFat");
                                Double amount = document.getDouble("Amount");
                                Double fatUnits = document.getDouble("FatUnits");
                                String name = document.getString("SellerName");

                                OrderModel orderModel = new OrderModel(date,Time,Quantity+"",amount+"",fatUnits+"",name,rate+"");

                                if(calenderDate.equalsIgnoreCase("") || calenderDate.equalsIgnoreCase(date.trim())) {

                                    arrayList.add(c, orderModel);
                                    ++c;
                                }

                            }

                            if(arrayList.size() == 0)
                            {
                                recyclerView.setVisibility(View.INVISIBLE);
                                btPdf.setVisibility(View.INVISIBLE);
                                btSharePdf.setVisibility(View.INVISIBLE);
                                notConnected.setVisibility(View.VISIBLE);
                            }

                            Log.i("ARRAY SIZE", String.valueOf(arrayList.size()));
                            adapter = new ShopOrderAdapter(arrayList, getActivity(),getActivity());
                            if (arrayList.size() >= 5) {
                                Log.d("ARRAY SIZE","GREATER THAN 5");
                                lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                                nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                    @Override
                                    public void onScrollChanged()
                                    {
                                        View view = (View)nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);

                                        int diff = (view.getBottom() - (nestedScrollView.getHeight() + nestedScrollView
                                                .getScrollY()));

                                        if (diff == 0 && (!isLastItemReached)) {
                                            // your pagination code
                                            Log.i("ISSCROLLED", "TRUE");
                                            pgBar.setVisibility(View.VISIBLE);

                                            db.collection("DairyShop").document(shopMobile).collection("Orders")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(5).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                    Log.i("DOCUMENT ID", document.getId());
//                                                                    Map<String, Object> map;
//                                                                    map = (Map<String, Object>) document.get("Milk");
//                                                                    String date = DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
//                                                                    DeliveryOrder order = new DeliveryOrder(document.getId(), date, map);

                                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                                                    String date = sdf.format(document.getTimestamp("timestamp").toDate());
                                                                    SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
                                                                    String Time = localDateFormat.format(document.getTimestamp("timestamp").toDate());
                                                                    Double Quantity = document.getDouble("Quantity");
                                                                    Double rate = document.getDouble("RateperFat");
                                                                    Double amount = document.getDouble("Amount");
                                                                    Double fatUnits = document.getDouble("FatUnits");
                                                                    String name = document.getString("SellerName");

                                                                    OrderModel orderModel = new OrderModel(date,Time,Quantity+"",amount+"",fatUnits+"",name,rate+"");
                                                                    Log.d("CALENDER DATE",calenderDate);
                                                                    if(calenderDate.equalsIgnoreCase("") || calenderDate.equalsIgnoreCase(date.trim())) {

                                                                        arrayList.add(c, orderModel);
                                                                        ++c;
                                                                    }
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                                if (task.getResult().size() > 0) {
                                                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                                                }
                                                                pgBar.setVisibility(View.INVISIBLE);
                                                                if (task.getResult().size() < 5) {
                                                                    isLastItemReached = true;
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });



//                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                                    @Override
//                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                                        super.onScrollStateChanged(recyclerView, newState);
//                                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                                            isScrolling = true;
//                                            Log.d("SCROLL STATE CHANGED","TRUE");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                                        super.onScrolled(recyclerView, dx, dy);
//                                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                                        currentItems = linearLayoutManager.getChildCount();
//                                        totalItems = linearLayoutManager.getItemCount();
//                                        scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                                        Log.d("ON SCROLLED","TRUE");
//
//                                        Log.d("CURRENT ITEMS",currentItems+"");
//                                        Log.d("TOTAL ITEMS",totalItems+"");
//                                        Log.d("Scrolled out ITEMS",scrolledOutItems+"");
//
//                                        if (isScrolling && (scrolledOutItems + currentItems == totalItems) && (!isLastItemReached)) {
//                                            Log.i("ISSCROLLED", "TRUE");
//                                            pgBar.setVisibility(View.VISIBLE);
//                                            isScrolling = false;
//                                            db.collection("DairyShop").document(shopMobile).collection("Orders")
//                                                    .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(5).get()
//                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                            if (task.isSuccessful()) {
//                                                                for (QueryDocumentSnapshot document : task.getResult()) {
////                                                                    Log.i("DOCUMENT ID", document.getId());
////                                                                    Map<String, Object> map;
////                                                                    map = (Map<String, Object>) document.get("Milk");
////                                                                    String date = DateFormat.getDateInstance().format(document.getTimestamp("timestamp").toDate());
////                                                                    DeliveryOrder order = new DeliveryOrder(document.getId(), date, map);
//
//                                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                                                    String date = sdf.format(document.getTimestamp("timestamp").toDate());
//                                                                    SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
//                                                                    String Time = localDateFormat.format(document.getTimestamp("timestamp").toDate());
//                                                                    Double Quantity = document.getDouble("Quantity");
//                                                                    Double rate = document.getDouble("RateperFat");
//                                                                    Double amount = document.getDouble("Amount");
//                                                                    Double fatUnits = document.getDouble("FatUnits");
//                                                                    String name = document.getString("SellerName");
//
//                                                                    OrderModel orderModel = new OrderModel(date,Time,Quantity+"",amount+"",fatUnits+"",name,rate+"");
////                                                                    arrayList.add(c, order);
////                                                                    c++;
//                                                                    if(calenderDate.equalsIgnoreCase("") || calenderDate.equalsIgnoreCase(date.trim())) {
//
//                                                                        arrayList.add(c, orderModel);
//                                                                        ++c;
//                                                                    }
//                                                                }
//                                                                adapter.notifyDataSetChanged();
//                                                                if (task.getResult().size() > 0) {
//                                                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
//                                                                }
//                                                                pgBar.setVisibility(View.INVISIBLE);
//                                                                if (task.getResult().size() < 5) {
//                                                                    isLastItemReached = true;
//                                                                }
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                    }
//                                });
                            }
                            recyclerView.setAdapter(adapter);
                        } else {
                            notConnected.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void attachID(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        pgBar = view.findViewById(R.id.progress);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        mobileArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item);
        calendarView = view.findViewById(R.id.calender_view);
        nestedScrollView=view.findViewById(R.id.scroll_view);
        notConnected = view.findViewById(R.id.not_connected);
        btPdf = view.findViewById(R.id.view_invoice);
        btSharePdf = view.findViewById(R.id.share_invoice);

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private void createPdfWrapper(Boolean share) throws FileNotFoundException {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(),share,"pdf");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");}
        }
    }

    private void showExportDialog(final Boolean share) {
        customDialog = new Dialog(getActivity());
        customDialog.setContentView(R.layout.export_dialog);
        btExcel = customDialog.findViewById(R.id.btExportExcel);
        btExPdf = customDialog.findViewById(R.id.btExportPdf);
        btCancel = customDialog.findViewById(R.id.cancel);

        btExPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(),share,"pdf");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

        btExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                MonthYearPickerDialog pd = new MonthYearPickerDialog();
                pd.setList(getActivity(), getActivity(), share,"excel");
                pd.show(getActivity().getSupportFragmentManager(), "MonthYearPickerDialog");
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