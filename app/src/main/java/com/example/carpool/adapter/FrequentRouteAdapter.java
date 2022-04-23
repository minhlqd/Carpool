package com.example.carpool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.carpool.frequentroute.MapFrequentRouteActivity;
import com.example.carpool.R;
import com.example.carpool.models.FrequentRouteResults;

import java.util.ArrayList;

public class FrequentRouteAdapter extends RecyclerView.Adapter<FrequentRouteAdapter.MyViewHolder> {

    private final Context mContext;
    private final ArrayList<FrequentRouteResults> routes;

    public FrequentRouteAdapter(Context mContext, ArrayList<FrequentRouteResults> routes) {
        this.mContext = mContext;
        this.routes = routes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_frequent_route, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FrequentRouteResults route = routes.get(position);

        String from = "From: " + /*route.getAddress_start().replaceAll("\n", ", ")*/  "Dại học Quốc gia Hà Nội, Xuân Thủy, dịch vọng hậu, cầu giấy, hà nội, việt nam";
        String to = "To: " + /*route.getAddress_destination().replaceAll("\n", ", ")*/ "155 Đường Cầu Giấy, Quan Hoa, Cầu Giấy, Hà Nội";

        if (route.getIs_shared() != 0) {
            holder.cardView.setCardBackgroundColor(Color.rgb(234, 255, 236));
            holder.frequentRouteStatusTextview.setText("You have shared!");
            holder.frequentRouteStatusTextview.setTextColor(Color.rgb(0, 160, 66));
            if(route.getType_shared() != null && !route.getType_shared().equals("none")) {
                holder.tv_type_share.setText("Yout are: "+route.getType_shared());
            }
        }
        else {
            holder.tv_type_share.setText("");
        }

        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MapFrequentRouteActivity.class);
            intent.putExtra("data", route);
            mContext.startActivity(intent);
        });

        if (to.length() > 30) {
            to = to.substring(0, Math.min(to.length(), 31));
            to = to + "...";
            holder.to.setText(to);
        } else {
            holder.to.setText(to);
        }

        if (from.length() > 30) {
            from = from.substring(0, Math.min(from.length(), 31));
            from = from + "...";
            holder.from.setText(from);
        } else {
            holder.from.setText(from);
        }
        holder.tv_id.setText("ID: #"+route.getId());

        holder.length.setText(/*route.getLength_route()*/ 2 + " km");
        holder.date.setText("Time: " + route.getTime_start() + " - " + route.getTime_destination());
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout view;
        TextView from, to, date, length, frequentRouteStatusTextview, tv_id,tv_type_share;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.bookingCardView);
            view = itemView.findViewById(R.id.view);
            from = itemView.findViewById(R.id.fromTxt);
            to = itemView.findViewById(R.id.toTxt);
            date = itemView.findViewById(R.id.individualDateTxt);
            length = itemView.findViewById(R.id.lengthTxt);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_type_share = itemView.findViewById(R.id.tv_type_share);
            frequentRouteStatusTextview = itemView.findViewById(R.id.frequentRouteStatusTextview);

        }
    }
}
