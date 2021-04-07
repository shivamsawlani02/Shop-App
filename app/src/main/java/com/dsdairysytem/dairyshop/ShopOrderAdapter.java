package com.dsdairysytem.dairyshop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ShopOrderAdapter extends RecyclerView.Adapter<ShopOrderAdapter.OrdersViewHolder> {

    ArrayList<OrderModel> arrayList;
    Context context;
    Activity parentActivity;

    public ShopOrderAdapter(ArrayList<OrderModel> arrayList, Context context, Activity parentActivity) {
        this.arrayList = arrayList;
        this.context = context;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrdersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_order, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        holder.populate(arrayList.get(position));
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder
    {
        TextView date,quantity,fatUnits,amount;
        RelativeLayout relativeLayout;
        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            quantity=itemView.findViewById(R.id.quantity);
            relativeLayout=itemView.findViewById(R.id.relative_layout);
            fatUnits = itemView.findViewById(R.id.fat_units);
            amount = itemView.findViewById(R.id.amount);
        }
        void populate(final OrderModel order)
        {
            date.setText("Date : "+order.getDate());
            amount.setText("Rs." + order.getAmount());
            quantity.setText("Quantity - " + order.getQuantity());
            fatUnits.setText("Total Fat Units - " + order.getFatUnits());

//            Map<String,Object> map;
//            map=order.getMap();
//            quantity.setText("");
//            for(Map.Entry<String,Object> entry : map.entrySet())
//            {
//                quantity.append("\n"+entry.getKey()+" : "+entry.getValue());
//            }

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog orderDialog = new Dialog(parentActivity);
                            orderDialog.setContentView(R.layout.detailed_order_dialog);
                            TextView date = orderDialog.findViewById(R.id.dailog_date);
                            TextView time = orderDialog.findViewById(R.id.dailog_time);
                            TextView milkmanName = orderDialog.findViewById(R.id.dialog_milkman);
                            TextView amount = orderDialog.findViewById(R.id.dialog_amount);
                            TextView quantity = orderDialog.findViewById(R.id.dialog_quantity);
                            TextView fatUnits = orderDialog.findViewById(R.id.dialog_fat_units);
                            //Button raiseQuery = orderDialog.findViewById(R.id.dailog_raise_query);

                            date.setText(order.getDate());
                            time.setText(order.getTime());
                            milkmanName.setText(order.getName());
                            Log.d("MILKMAN NAME",order.getName());
                            amount.setText("Rs. "+order.getAmount());
                            quantity.setText("Quantity - "+order.getQuantity());
                            fatUnits.setText(order.getFatUnits() + " @ " + order.getRate()+" Rs./unit");

//                            Map<String,Object> map;
//                            map=order.getMap();
//
//                            type.setText("");
//
//                            for(Map.Entry<String,Object> entry : map.entrySet())
//                            {
//                                type.append("\n"+entry.getKey()+" - "+entry.getValue());
//                            }



//                            raiseQuery.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    orderDialog.dismiss();
//                                    Intent query = new Intent(context,SendQuery.class);
//                                    query.putExtra("milkmanMobile",order.getMilkmanMobile());
//                                    query.putExtra("OrderID",order.getID());
//                                    query.putExtra("Date",order.getDate());
//                                    query.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(query);
//
//                                }
//                            });
                            orderDialog.show();
                        }
                    });

                }
            });

        }
    }
}
