package com.example.eb4zar.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.Interface.ItemClickListener;
import com.example.eb4zar.ProfileActivity;
import com.example.eb4zar.R;

public class MyProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtNazovInzeratu;
    public ItemClickListener listener;
    public Button buttonEdit;
    public Button buttonDelete;

    public MyProductViewHolder(View itemView){
        super(itemView);

        txtNazovInzeratu.findViewById(R.id.nazovInzeratu);
        buttonDelete.findViewById(R.id.buttonDelete);
        buttonEdit.findViewById(R.id.buttonEdit);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
