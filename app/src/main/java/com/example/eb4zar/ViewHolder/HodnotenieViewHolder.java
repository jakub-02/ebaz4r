package com.example.eb4zar.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.Interface.ItemClickListener;
import com.example.eb4zar.R;

public class HodnotenieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView userName;
    public RatingBar rating;
    public TextView datum;
    public TextView textRecenzie;

    public ItemClickListener listener;

    public HodnotenieViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.userName);
        rating = itemView.findViewById(R.id.rating);
        datum = itemView.findViewById(R.id.datum);
        textRecenzie = itemView.findViewById(R.id.textRecenzie);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
