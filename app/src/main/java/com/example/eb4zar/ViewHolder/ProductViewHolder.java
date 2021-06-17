package com.example.eb4zar.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.Interface.ItemClickListener;
import com.example.eb4zar.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtNazovInzeratu, txtCenaInzeratu;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(View itemView){
        super(itemView);

        txtNazovInzeratu = itemView.findViewById(R.id.nazovInzeratu);
        txtCenaInzeratu = itemView.findViewById(R.id.cenaInzeratu);
        imageView = itemView.findViewById(R.id.fotkaProduktu);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
