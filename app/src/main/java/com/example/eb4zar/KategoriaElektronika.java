package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eb4zar.ViewHolder.ProductViewHolder;
import com.example.eb4zar.model.ProductDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class KategoriaElektronika extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategoria_oblecenie);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("produkty");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Elektronika");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Query query = ProductsRef.orderByChild("kategoria").equalTo("Elektronika");

        FirebaseRecyclerOptions<ProductDetail> options =
                new FirebaseRecyclerOptions.Builder<ProductDetail>()
                        .setQuery(query, ProductDetail.class)
                        .build();

        FirebaseRecyclerAdapter<ProductDetail, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductDetail, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductDetail model)
                    {
                        if(model.getKategoria().equals("Elektronika")){
                            holder.txtNazovInzeratu.setText(model.getNazov());
                            holder.txtCenaInzeratu.setText(model.getCena() + "€");
                            Picasso.get().load(model.getFotka()).into(holder.imageView);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String zvolenyProdukt = getRef(position).getKey();
                                    Intent i = new Intent(KategoriaElektronika.this, ProductDetailActivity.class);
                                    i.putExtra("zvolenyProdukt", zvolenyProdukt);
                                    startActivity(i);
                                }
                            });
                        }
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.nav_home):{
                Intent intent = new Intent(KategoriaElektronika.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(KategoriaElektronika.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(KategoriaElektronika.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                Intent intent = new Intent(KategoriaElektronika.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(KategoriaElektronika.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }
}