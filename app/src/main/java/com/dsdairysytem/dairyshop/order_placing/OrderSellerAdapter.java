package com.dsdairysytem.dairyshop.order_placing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysytem.dairyshop.R;
import com.dsdairysytem.dairyshop.add_client.ConnectClientAdapter;
import com.dsdairysytem.dairyshop.seller_tab.MilkmanDetail;

import java.util.ArrayList;
import java.util.Map;

import static com.dsdairysytem.dairyshop.order_placing.OrderActivity.connectedMobile;
import static com.dsdairysytem.dairyshop.order_placing.OrderActivity.connectedName;
import static com.dsdairysytem.dairyshop.order_placing.OrderActivity.searchResult;
import static com.dsdairysytem.dairyshop.order_placing.OrderActivity.selectSeller;
import static com.dsdairysytem.dairyshop.order_placing.OrderActivity.searchbar;

public class OrderSellerAdapter extends RecyclerView.Adapter<OrderSellerAdapter.ViewHolder> {

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> mobile = new ArrayList<>();
    Map<String,String> map ;
    Context context;
    Boolean search;
    double stock;

    public OrderSellerAdapter(Map<String, String> map,Context context,Boolean search, double stock) {
        this.map = map;
        this.context = context;
        this.search = search;
        this.stock = stock;
        names.addAll(map.keySet());
        mobile.addAll(map.values());
    }

    @NonNull
    @Override
    public OrderSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_clients_layout, parent, false);
        return new OrderSellerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSellerAdapter.ViewHolder holder, final int position) {
        names.addAll(map.keySet());
        mobile.addAll(map.values());

        holder.phone.setText(mobile.get(position));
        holder.name.setText(names.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (search){
                        searchbar.setVisibility(View.GONE);
                        searchResult.setVisibility(View.GONE);
                        selectSeller.setVisibility(View.VISIBLE);
                        connectedMobile.setText(mobile.get(position));
                        connectedName.setText(names.get(position));

                        Intent intent = new Intent(context,OrderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("name",names.get(position));
                        intent.putExtra("mobile",mobile.get(position));
                        intent.putExtra("Stock", stock);
                        context.startActivity(intent);
                    } else {

                        Intent intent = new Intent(context, MilkmanDetail.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("name",names.get(position));
                        intent.putExtra("mobile",mobile.get(position));
                        intent.putExtra("Stock", stock);
                        context.startActivity(intent);
                    }

                }
            });
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.connectName);
            phone = itemView.findViewById(R.id.connectMobile);
        }
    }



}
