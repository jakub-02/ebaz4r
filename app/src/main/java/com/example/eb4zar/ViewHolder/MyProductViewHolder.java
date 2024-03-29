package com.example.eb4zar.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.Interface.ItemClickListener;
import com.example.eb4zar.R;
import com.example.eb4zar.model.ProductDetail;

import java.util.ArrayList;

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
