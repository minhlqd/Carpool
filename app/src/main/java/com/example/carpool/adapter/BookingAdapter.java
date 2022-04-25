package com.example.carpool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.interfaces.ResponseBooked;
import com.example.carpool.pickup.PickupActivity;
import com.example.carpool.R;
import com.example.carpool.models.BookingResults;
import com.example.carpool.utils.UniversalImageLoader;
import com.example.carpool.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<BookingResults> mBookingResults;
    private Boolean isDriver;
    private ResponseBooked responseBooked;
    private DatabaseReference mRef;

    public BookingAdapter(Context context, ArrayList<BookingResults> mBookingResults, Boolean isDriver, ResponseBooked responseBooked, DatabaseReference databaseReference){
        this.mContext = context;
        this.mBookingResults = mBookingResults;
        this.isDriver = isDriver;
        this.responseBooked = responseBooked;
        this.mRef = databaseReference;
    }

    public BookingAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_booking_information, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final boolean accepted = mBookingResults.get(position).getAccepted();
        final String username = mBookingResults.get(position).getUsername();
        final String passengerID = mBookingResults.get(position).getPassengerID();
        final String rideID = mBookingResults.get(position).getRide_id();
        final String licencePlate = mBookingResults.get(position).getLicencePlate();
        final String pickupTime = mBookingResults.get(position).getPickupTime();
        String from = mBookingResults.get(position).getLocation().replaceAll("\n", ", ");
        String to = mBookingResults.get(position).getDestination().replaceAll("\n", ", ");
        final String pickupLocation = mBookingResults.get(position).getPickupLocation();
        final String date = mBookingResults.get(position).getDateOfJourney() + " - " + mBookingResults.get(position).getPickupTime();

        Log.d("MinhMX", "onBindViewHolder: " + mBookingResults.get(position).getPassengerID() + " " +  username);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(0);
        format.setCurrency(Currency.getInstance("VND"));
        final String cost = format.format(mBookingResults.get(position).getCost());

        if (isDriver) {
            holder.bookingStatusTextview.setVisibility(View.GONE);
            holder.accept.setOnClickListener(v -> {
                mRef.child(Utils.AVAILABLE_RIDE).child(rideID).child("seatsAvailable").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int seat = snapshot.getValue(Integer.class);
                        responseBooked.responseBooked(true, rideID, position, passengerID, seat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            });
            holder.inject.setOnClickListener(v -> {
                responseBooked.responseBooked(false, rideID, position, passengerID, 0);
            });
        } else {
            holder.inject.setVisibility(View.GONE);
            holder.accept.setVisibility(View.GONE);
            if (accepted) {
                holder.bookingStatusTextview.setText("Booking accepted!");
                holder.bookingStatusTextview.setTextColor(Color.rgb(0, 160, 66));

                holder.view.setOnClickListener(view -> {
                    Intent intent = new Intent(mContext, PickupActivity.class);
                    intent.putExtra("pickupLocation", pickupLocation);
                    intent.putExtra("rideID", rideID);
                    intent.putExtra("userID", passengerID);
                    intent.putExtra("licencePlate", licencePlate);
                    intent.putExtra("pickupTime", pickupTime);
                    mContext.startActivity(intent);
                });
            }
        }

        if (to.length() > 20){
            to = to.substring(0 , 21);
            to = to + "...";
        }
        holder.to.setText(to);

        if (from.length() > 20){
            from = from.substring(0 , 21);
            from = from + "...";
        }
        holder.from.setText(from);

        holder.costs.setText(cost);

        UniversalImageLoader.setImage(mBookingResults.get(position).getProfile_photo(), holder.profile_photo, null,"");

        holder.date.setText(date);

    }

    @Override
    public int getItemCount() {
        return mBookingResults.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView rides;
        TextView from;
        TextView to;
        TextView date;
        TextView seats;
        TextView costs;
        TextView bookingStatusTextview;
        CircleImageView profile_photo;
        RatingBar ratingBar;
        CardView cardView;
        Button accept;
        Button inject;

        public MyViewHolder(View itemView) {
            super(itemView);

            accept = itemView.findViewById(R.id.accept);
            inject = itemView.findViewById(R.id.inject);
            cardView = itemView.findViewById(R.id.bookingCardView);
            view = itemView.findViewById(R.id.view);
            from = itemView.findViewById(R.id.fromTxt);
            to = itemView.findViewById(R.id.toTxt);
            date = itemView.findViewById(R.id.individualDateTxt);
            costs = itemView.findViewById(R.id.priceTxt);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            profile_photo = itemView.findViewById(R.id.indiviual_profile_picture);
            bookingStatusTextview = itemView.findViewById(R.id.bookingStatusTextview);


        }
    }

}