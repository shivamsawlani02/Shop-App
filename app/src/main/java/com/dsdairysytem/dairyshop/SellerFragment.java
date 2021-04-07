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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsdairysytem.dairyshop.order_placing.OrderSellerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
//import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.dsdairysytem.dairyshop.MainActivity.drawer;

public class SellerFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderSellerAdapter orderSellerAdapter;
    private Map<String,String> mapList = new HashMap<>();
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    ProgressDialog pd;
    TextView noSellerAdded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller, container, false);;

        noSellerAdded = view.findViewById(R.id.tv_no_added_group);
        recyclerView = view.findViewById(R.id.connected_milkman_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(orderSellerAdapter);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading ...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        final CustomDrawerButton customDrawerButton = view.findViewById(R.id.custom_drawer2);
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

    @Override
    public void onStart() {
        super.onStart();

        FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Seller").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty())
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                noSellerAdded.setVisibility(View.INVISIBLE);
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                mapList.put(documentSnapshot.get("Name").toString(), documentSnapshot.get("Mobile").toString());
                            }
                            orderSellerAdapter = new OrderSellerAdapter(mapList, getActivity(), false, 0.0);
                            orderSellerAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(orderSellerAdapter);
                        }
                            else {
                                recyclerView.setVisibility(View.INVISIBLE);
                                noSellerAdded.setVisibility(View.VISIBLE);
                            }
                            pd.dismiss();
                        }
                    }
                });
    }


}