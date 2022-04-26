package com.example.carpool.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.R;
import com.example.carpool.account.ProfileActivity;
import com.example.carpool.models.Participants;
import com.example.carpool.utils.UniversalImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<Participants> participants;
    String userId;

    public ParticipantsAdapter(Context context, ArrayList<Participants> participants){
        this.mContext = context;
        this.participants = participants;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_participants, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String username = participants.get(position).getUsername();

        holder.username.setText(username);
        UniversalImageLoader.setImage(participants.get(position).getUserProfilePhoto(), holder.profile_photo, null,"");

        holder.username.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra("user", participants.get(position).getId());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout view;
        TextView username;
        ImageView profile_photo;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view);
            username = itemView.findViewById(R.id.user_id_2_username);
            profile_photo = itemView.findViewById(R.id.user_id_2);

        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = "dd MMMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private String parseLocation(String location){
        if(location.contains(",")){
            location = location.replaceAll(",", "\n");
            location = location.replaceAll(" ", "");
            return location;
        } else {

        }

        return location;
    }
}