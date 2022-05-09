package com.example.carpool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.R;
import com.example.carpool.models.Leaderboards;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * create by minhmx on 22/04/2022
 */

public class LeaderboardsAdapter extends RecyclerView.Adapter<LeaderboardsAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<Leaderboards> leaderboards;

    public LeaderboardsAdapter(Context c, ArrayList<Leaderboards> o){
        this.mContext = c;
        this.leaderboards = o;
    }

    public LeaderboardsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_leaderboards, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String username = leaderboards.get(position).getUsername();
        final String points = leaderboards.get(position).getPoints() + " points";
        final String profile_photo = leaderboards.get(position).getProfile_photo();

        if ((position+1) == 1) {
            holder.leaderboardPlace.setBackgroundResource(R.drawable.ic_trophy_first_place);
            holder.leaderboardPlace.setText(null);
            holder.userID.setText(username);
            holder.totalPoints.setText(points);
            Picasso.get().load(profile_photo).into(holder.profile_photo);
        } else if ((position+1) == 2) {
            holder.leaderboardPlace.setBackgroundResource(R.drawable.ic_trophy_second_place);
            holder.leaderboardPlace.setText(null);
            holder.userID.setText(username);
            holder.totalPoints.setText(points);
            Picasso.get().load(profile_photo).into(holder.profile_photo);
        } else if ((position+1) == 3) {
            holder.leaderboardPlace.setBackgroundResource(R.drawable.ic_trophy_third_place);
            holder.leaderboardPlace.setText(null);
            holder.userID.setText(username);
            holder.totalPoints.setText(points);
            Picasso.get().load(profile_photo).into(holder.profile_photo);
        }
        else {
            holder.userID.setText(username);
            holder.totalPoints.setText(points);
            holder.leaderboardPlace.setText(Integer.toString(position+1));
            Picasso.get().load(profile_photo).into(holder.profile_photo);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return leaderboards.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView userID, totalPoints, leaderboardPlace;
        CircleImageView profile_photo;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = (LinearLayout) itemView.findViewById(R.id.view);
            userID = (TextView) itemView.findViewById(R.id.userID);
            totalPoints = (TextView) itemView.findViewById(R.id.totalPoints);
            leaderboardPlace = (TextView) itemView.findViewById(R.id.leaderboardPlace);

            profile_photo = (CircleImageView) itemView.findViewById(R.id.leaderboards_profile_picture);

        }
    }
}