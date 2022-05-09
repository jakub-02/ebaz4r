package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eb4zar.ViewHolder.ProductViewHolder;
import com.example.eb4zar.model.ProductDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    private DatabaseReference ProductsRef, reference;
    private RecyclerView recyclerView;

    TextInputEditText searchView;
    Button button;

    String uid;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("produkty");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        button = findViewById(R.id.button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vyhľadávanie inzerátov");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);

        headerView = navigationView.getHeaderView(0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);

        recyclerView = findViewById(R.id.recycler_menu);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        nacitajDataHeader();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vyhladaj();
            }
        });

        ImageButton logoutButton = headerView.findViewById(R.id.logoutButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Naozaj sa chcete odhlásiť?")
                        .setCancelable(false)
                        .setPositiveButton("Áno", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("remember", "false");
                                editor.apply();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(SearchProductActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(SearchProductActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Odhlásenie");
                alert.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        vyhladaj();
    }

    public void vyhladaj() {
        String searchText = searchView.getText().toString();

        Query query = ProductsRef.orderByChild("nazov").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<ProductDetail> options =
                new FirebaseRecyclerOptions.Builder<ProductDetail>()
                        .setQuery(query, ProductDetail.class)
                        .build();

        FirebaseRecyclerAdapter<ProductDetail, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductDetail, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ProductDetail model) {
                        holder.txtNazovInzeratu.setText(model.getNazov());
                        holder.txtCenaInzeratu.setText(model.getCena() + "€");
                        Picasso.get().load(model.getFotka()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String zvolenyProdukt = getRef(position).getKey();
                                Intent i = new Intent(SearchProductActivity.this, ProductDetailActivity.class);
                                i.putExtra("zvolenyProdukt", zvolenyProdukt);
                                startActivity(i);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case (R.id.nav_home): {
                Intent intent = new Intent(SearchProductActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories): {
                Intent intent = new Intent(SearchProductActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search): {
                break;
            }

            case (R.id.nav_add): {
                Intent intent = new Intent(SearchProductActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(SearchProductActivity.this, PublicProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts): {
                Intent intent = new Intent(SearchProductActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myRatings): {
                Intent intent = new Intent(SearchProductActivity.this, UserRatingsActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_editProfile): {
                Intent intent = new Intent(SearchProductActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void nacitajDataHeader() {
        CircleImageView userPicture = headerView.findViewById(R.id.userPicture);
        TextView userName = headerView.findViewById(R.id.userName);
        TextView userMail = headerView.findViewById(R.id.userMail);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mail = snapshot.child("mail").getValue().toString();
                String name = snapshot.child("meno").getValue().toString();
                String surname = snapshot.child("priezvisko").getValue().toString();

                String link = snapshot.child("fotka").getValue().toString();
                if (link.equals("default")) {

                } else {
                    Picasso.get().load(link).into(userPicture);
                }

                userMail.setText(mail);
                userName.setText(name + " " + surname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}