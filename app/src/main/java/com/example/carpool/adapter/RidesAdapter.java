package com.example.carpool.adapter;

import static com.example.carpool.utils.Utils.formatValue;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.carpool.R;
import com.example.carpool.dialogs.ViewRideCreatedDialog;
import com.example.carpool.models.Ride;
import com.example.carpool.utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.MyViewHolder> {
    private String[] mDataset;
    private Context mContext;
    private ArrayList<Ride> ride;

    public RidesAdapter(Context c, ArrayList<Ride> o){
        this.mContext = c;
        this.ride = o;
    }

    public RidesAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.individual_ride_information, parent, false));
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String userID = ride.get(position).getUserId();

        final String username = ride.get(position).getUsername();
        //final String rides = ride.get(position).getCompleteRides();
        final String seats = ride.get(position).getSeatsAvailable() + " " + mContext.getString(R.string.seats_left);
        String from = mContext.getString(R.string.from) + ride.get(position).getCurrentLocation();
        String to = mContext.getString(R.string.to) + ride.get(position).getDestination();
        final String date = parseDateToddMMyyyy(ride.get(position).getDateOfJourney()) + " - " + ride.get(position).getPickupTime();
        final float rating = ride.get(position).getUserRating();
        final String dateOnly = ride.get(position).getPickupTime();
        final String extraTime = String.valueOf(ride.get(position).getExtraTime());
        final String fromOnly = parseLocation(ride.get(position).getCurrentLocation());
        final String toOnly = parseLocation(ride.get(position).getDestination());
        final String rideID = ride.get(position).getRideID();
        final String duration = ride.get(position).getDuration();
        final String completeRides = String.valueOf(ride.get(position).getCompleteRides());
        final String pickupLocation = ride.get(position).getPickupLocation();


        Log.d("MinhMX", "onBindViewHolder: " +ride.get(position).getPickupLocation() );
        final String cost = formatValue(ride.get(position).getCost());


        //holder.rides.setText(rides);
        holder.seats.setText(seats);
        if (to.length() > 20){
            to = to.substring(0 , 19);
            to = to + "...";
        }
        holder.to.setText(to);

        if (from.length() > 20){
            from = from.substring(0 , 19);
            from = from + "...";
        }
        holder.from.setText(from);

        holder.date.setText(date);
        holder.costs.setText(cost);
        holder.ratingBar.setRating(rating);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);

        UniversalImageLoader.setImage(ride.get(position).getProfile_picture(), holder.profile_photo, null,"");

        Log.d("RidesAdapter", "onBindViewHolder: " + ride.get(position).getProfile_picture() + " " + rating);

        holder.view.setOnClickListener(view -> {
            Log.d("MinhMX", "onBindViewHolder: "+ userID);
            ViewRideCreatedDialog dialog =
                    new ViewRideCreatedDialog(
                            mContext,
                            rideID ,
                            username,
                            "",
                            seats,
                            fromOnly,
                            toOnly,
                            date,
                            ride.get(position).getCost(),
                            rating,
                            dateOnly,
                            String.valueOf(ride.get(position).getExtraTime()), duration, completeRides, pickupLocation ,userID, ride.get(position).getProfile_picture(), ride.get(position).getDriverID());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return ride.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout view;
        TextView rides, from, to, date, seats, costs;
        CircleImageView profile_photo;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view);
            rides = itemView.findViewById(R.id.indivcompletedRidesTxt);
            from = itemView.findViewById(R.id.fromTxt);
            to = itemView.findViewById(R.id.toTxt);
            date = itemView.findViewById(R.id.individualTimeTxt);
            seats = itemView.findViewById(R.id.seatsTxt);
            costs = itemView.findViewById(R.id.priceTxt);

            ratingBar = itemView.findViewById(R.id.individualRatingBar);

            profile_photo = itemView.findViewById(R.id.indiviual_profile_picture);

        }
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = "dd MMMM";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
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