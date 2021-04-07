package com.dsdairysytem.dairyshop.add_client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsdairysytem.dairyshop.R;

public class ConnectClientAdapter extends RecyclerView.Adapter<ConnectClientAdapter.ViewHolder> {
    @NonNull
    @Override
    public ConnectClientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_clients_layout, parent, false);
        return new ConnectClientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectClientAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);

        }
    }


}
