package com.dsdairysytem.dairyshop.add_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.dsdairysytem.dairyshop.MainActivity;
import com.dsdairysytem.dairyshop.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectClient extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private String user_id= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private ArrayList<String> list;
    private FirestoreRecyclerAdapter<ClientModel, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_client);

        toolbar=findViewById(R.id.main_all_users);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All Connected Milkmans");


        recyclerView = findViewById(R.id.connect_client_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("Delivery")
                .orderBy("Name", Query.Direction.ASCENDING)
                .orderBy("Mobile Number");

        FirestoreRecyclerOptions<ClientModel> options = new FirestoreRecyclerOptions.Builder<ClientModel>()
                .setQuery(query, ClientModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<ClientModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final ClientModel model) {
                holder.mobile.setText(model.getMobileNumber());
                holder.name.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        String name = model.getName();
                                        final String number = model.getMobileNumber();

                                        Map<String,Object> docData = new HashMap<>();
                                        docData.put("Name",name);
                                        docData.put("Mobile",number);

                                        FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Seller").document(number).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(ConnectClient.this, MainActivity.class);
                                                    Log.i("MOBILE INTENT",number);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }


                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConnectClient.this);
                        builder.setMessage("Do you want to connect with this Milkman?").setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();

                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_clients_layout, parent, false);
                return new ViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, mobile;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.connectName);
            mobile = itemView.findViewById(R.id.connectMobile);
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.user_menu,menu);

        MenuItem searchViewItem
                = menu.findItem(R.id.app_bar_search);

        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                char[] chars = query.toLowerCase().toCharArray();
                boolean found = false;
                for (int i = 0; i < chars.length; i++) {
                    if (!found && Character.isLetter(chars[i])) {
                        chars[i] = Character.toUpperCase(chars[i]);
                        found = true;
                    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                        found = false;
                    }
                }

                String output = String.valueOf(chars);
                Query firebaseSearchQuery = FirebaseFirestore.getInstance().collection("Delivery").orderBy("Name").startAt(output).endAt(output + "\uf8ff");

                FirestoreRecyclerOptions<ClientModel> options = new FirestoreRecyclerOptions.Builder<ClientModel>()
                        .setQuery(firebaseSearchQuery, ClientModel.class)
                        .build();
                adapter = new FirestoreRecyclerAdapter<ClientModel, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final ClientModel model) {
                        holder.mobile.setText(model.getMobileNumber());
                        holder.name.setText(model.getName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                //Yes button clicked
                                                String name = model.getName();
                                                final String number = model.getMobileNumber();

                                                Map<String,Object> docData = new HashMap<>();
                                                docData.put("Name",name);
                                                docData.put("Mobile",number);

                                                FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Seller").document(number).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(ConnectClient.this, MainActivity.class);
                                                            Log.i("MOBILE INTENT",number);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }


                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectClient.this);
                                builder.setMessage("Do you want to connect with this Milkman?").setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                                        .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_clients_layout, parent, false);
                        return new ViewHolder(view);
                    }
                };
                recyclerView.setAdapter(adapter);
                adapter.startListening();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                char[] chars =newText.toLowerCase().toCharArray();
                boolean found = false;
                for (int i = 0; i < chars.length; i++) {
                    if (!found && Character.isLetter(chars[i])) {
                        chars[i] = Character.toUpperCase(chars[i]);
                        found = true;
                    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                        found = false;
                    }
                }

                String output= String.valueOf(chars);

                Query firebaseSearchQuery = FirebaseFirestore.getInstance().collection("Delivery").orderBy("Name").startAt(output).endAt(output + "\uf8ff");

                FirestoreRecyclerOptions<ClientModel> options = new FirestoreRecyclerOptions.Builder<ClientModel>()
                        .setQuery(firebaseSearchQuery, ClientModel.class)
                        .build();
                adapter = new FirestoreRecyclerAdapter<ClientModel, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final ClientModel model) {
                        holder.mobile.setText(model.getMobileNumber());
                        holder.name.setText(model.getName());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                //Yes button clicked
                                                String name = model.getName();
                                                final String number = model.getMobileNumber();

                                                Map<String,Object> docData = new HashMap<>();
                                                docData.put("Name",name);
                                                docData.put("Mobile",number);

                                                FirebaseFirestore.getInstance().collection("DairyShop").document(user_id).collection("Seller").document(number).set(docData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(ConnectClient.this, MainActivity.class);
                                                            Log.i("MOBILE INTENT",number);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }


                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectClient.this);
                                builder.setMessage("Do you want to connect with this Milkman?").setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                                        .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_clients_layout, parent, false);
                        return new ViewHolder(view);
                    }
                };
                recyclerView.setAdapter(adapter);
                adapter.startListening();

                return false;

            }

        });



        if(menu instanceof MenuBuilder) {  //To display icon on overflow menu

            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem){
        super.onOptionsItemSelected(menuitem);


        switch (menuitem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(menuitem);


    }

}