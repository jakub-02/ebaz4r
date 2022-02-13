package com.example.eb4zar.ViewHolder;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eb4zar.R;
import com.example.eb4zar.SelectedCategoryActivity;
import com.example.eb4zar.model.Kategoria;

import java.util.ArrayList;

public class KategoriaAdapter extends RecyclerView.Adapter<KategoriaAdapter.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotkaKategorie;
        public TextView textKategorie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotkaKategorie = itemView.findViewById(R.id.fotkaKategorie);
            textKategorie = itemView.findViewById(R.id.textKategorie);
        }
    }

    ArrayList<Kategoria> list;

    public KategoriaAdapter(ArrayList<Kategoria> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriaAdapter.ViewHolder holder, int position) {
        Kategoria currentItem = list.get(position);

        ImageView imageView = holder.fotkaKategorie;
        imageView.setImageResource(currentItem.getFotka_kategorie());

        TextView textView = holder.textKategorie;
        textView.setText(currentItem.getNazov_kategorie());

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), SelectedCategoryActivity.class);
                        i.putExtra("kategoria", holder.textKategorie.getText().toString());
                        v.getContext().startActivity(i);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
