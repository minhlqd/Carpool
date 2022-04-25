package com.example.carpool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.R;
import com.example.carpool.dialogs.BookRideDialog;
import com.example.carpool.models.OfferRide;
import com.example.carpool.models.Ride;
import com.example.carpool.utils.UniversalImageLoader;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<OfferRide> ride;

    public SearchAdapter(Context context, ArrayList<OfferRide> rides){
        this.mContext = context;
        this.ride = rides;
    }

    public SearchAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.individual_ride_information, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String username = ride.get(position).getUsername();
        final String rides = ride.get(position).getCompleteRides() + " Rides";
        String seats = ride.get(position).getSeatsAvailable() + " Seats Left!";
        String from = "From: " + ride.get(position).getCurrentLocation();
        String to = "To: " + ride.get(position).getDestination();
        final String date = parseDateToddMMyyyy(ride.get(position).getDateOfJourney()) + " - " + ride.get(position).getPickupTime() + " PM";
        final String dateOnly = parseDateToddMMyyyy(ride.get(position).getDateOfJourney());
        final String cost =  String.valueOf(ride.get(position).getCost());
        final Float rating = (float) ride.get(position).getUserRating();
        final String pickupTime = ride.get(position).getPickupTime() + " PM";
        final String extraTime = ride.get(position).getExtraTime() + " mins";
        final String fromOnly = parseLocation(ride.get(position).getCurrentLocation());
        final String toOnly = parseLocation(ride.get(position).getDestination());
        final String licencePlate = ride.get(position).getLicencePlate();
        final String rideID = ride.get(position).getRideID();
        final String duration = ride.get(position).getDuration();
        final String driverID = ride.get(position).getDriverID();
        final String profile_photo = ride.get(position).getProfile_picture();
        final String completedRides = String.valueOf(ride.get(position).getCompleteRides());
        final String pickupLocation = ride.get(position).getPickupLocation();

        if (to.length() > 20){
            to = to.substring(0 , 21);
            to = to + "...";
        }
        holder.to.setText(to);

        if (from.length() > 20){
            from = from.substring(0 , 21);
            from = from + "...";
        }
        final String location = from;
        final String destination = to;

        holder.from.setText(from);

        if (ride.get(position).getSeatsAvailable() == 1) {
            seats = "Only " + ride.get(position).getSeatsAvailable() + " Seat remaining!";
            holder.seats.setTextColor(Color.parseColor("#920000"));
            holder.seats.setTypeface(null, Typeface.BOLD);
        }

        holder.rides.setText(rides);
        holder.seats.setText(seats);
        holder.date.setText(date);
        holder.costs.setText(cost);
        holder.ratingBar.setRating(rating);

        UniversalImageLoader.setImage(ride.get(position).getProfile_picture(), holder.profile_photo, null,"");

        final String finalSeats = seats;
        holder.view.setOnClickListener(view -> {
            BookRideDialog dialog =
                    new BookRideDialog(mContext, rideID, username, licencePlate, rides, finalSeats, location, destination, date,
                            dateOnly, cost, rating, pickupTime, extraTime, duration, driverID,
                            profile_photo, completedRides, pickupLocation);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return ride.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView rides, from, to, date, seats, costs;
        CircleImageView profile_photo;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = (LinearLayout) itemView.findViewById(R.id.view);
            rides = (TextView) itemView.findViewById(R.id.indivcompletedRidesTxt);
            from = (TextView) itemView.findViewById(R.id.fromTxt);
            to = (TextView) itemView.findViewById(R.id.toTxt);
            date = (TextView) itemView.findViewById(R.id.individualTimeTxt);
            seats = (TextView) itemView.findViewById(R.id.seatsTxt);
            costs = (TextView) itemView.findViewById(R.id.priceTxt);

            ratingBar = (RatingBar) itemView.findViewById(R.id.individualRatingBar);

            profile_photo = (CircleImageView) itemView.findViewById(R.id.indiviual_profile_picture);

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
        }

        return location;
    }
}