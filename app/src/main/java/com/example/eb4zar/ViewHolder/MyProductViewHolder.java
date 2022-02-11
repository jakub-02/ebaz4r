package com.example.eb4zar.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.Interface.ItemClickListener;
import com.example.eb4zar.MyProductsActivity;
import com.example.eb4zar.ProductDetailActivity;
import com.example.eb4zar.ProfileActivity;
import com.example.eb4zar.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtNazovInzeratu;
    public ItemClickListener listener;
    public ImageButton buttonEdit;
    public ImageButton buttonDelete;

    public MyProductViewHolder(View itemView){
        super(itemView);

        txtNazovInzeratu = itemView.findViewById(R.id.nazovInzeratu);
        buttonEdit = itemView.findViewById(R.id.buttonEdit);
        buttonDelete = itemView.findViewById(R.id.buttonDelete);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
