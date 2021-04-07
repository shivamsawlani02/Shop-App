package com.dsdairysytem.dairyshop.seller_tab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysytem.dairyshop.R;
import com.dsdairysytem.dairyshop.order_placing.OrderSellerAdapter;

import java.util.ArrayList;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    ArrayList<Map> mapArrayList;
    Context context;

    public OrderAdapter(ArrayList<Map> mapArrayList, Context context) {
        this.mapArrayList = mapArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map m = mapArrayList.get(position);
        holder.date.setText(m.get("Date").toString());
        holder.amount.setText(Double.toString((Double) m.get("Amount")));
        holder.rate.setText(Double.toString((Double) m.get("Rate")));
        holder.quantity.setText(Double.toString((Double) m.get("Quantity")));
        holder.total_fat.setText(Double.toString((Double) m.get("Total Fat")));
    }

    @Override
    public int getItemCount() {
        return mapArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rate, total_fat, quantity, amount, date;
        public ViewHolder(View itemView) {
            super(itemView);
            total_fat = itemView.findViewById(R.id.tvOrderTotalFat);
            quantity = itemView.findViewById(R.id.tvOrderQuantity);
            rate = itemView.findViewById(R.id.tvOrderRate);
            amount = itemView.findViewById(R.id.tvOrderAmount);
            date = itemView.findViewById(R.id.tvOrderDate);
        }
    }

}
