package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eb4zar.ViewHolder.HodnotenieViewHolder;
import com.example.eb4zar.model.HodnotenieDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRatingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    FloatingActionButton pridaj;

    String uid, uidProfil;

    View headerView;

    DatabaseReference reference, userReference;

    RecyclerView recyclerView;

    String [] mesiace = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Seb", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ratings);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.toolbar);
        pridaj = findViewById(R.id.pridaj);
        recyclerView = findViewById(R.id.recycler);

        uidProfil = getIntent().getExtras().get("uidProfil").toString();

        reference = FirebaseDatabase.getInstance().getReference().child("hodnotenia");

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        if (uidProfil.equals(uid)){
            pridaj.setVisibility(View.INVISIBLE);
        }

        pridaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserRatingsActivity.this, RateUserActivity.class);
                i.putExtra("uidProfil", uidProfil);
                startActivity(i);
            }
        });

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this));

        nacitajDataHeader();
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
                Intent intent = new Intent(UserRatingsActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories): {
                Intent intent = new Intent(UserRatingsActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search): {
                Intent intent = new Intent(UserRatingsActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add): {
                Intent intent = new Intent(UserRatingsActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(UserRatingsActivity.this, PublicProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts): {
                Intent intent = new Intent(UserRatingsActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserRatingsActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(UserRatingsActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Query query = reference.orderByChild("uzivatel").equalTo(uidProfil);

        FirebaseRecyclerOptions<HodnotenieDetail> options =
                new FirebaseRecyclerOptions.Builder<HodnotenieDetail>()
                        .setQuery(query, HodnotenieDetail.class)
                        .build();

        FirebaseRecyclerAdapter<HodnotenieDetail, HodnotenieViewHolder> adapter =
                new FirebaseRecyclerAdapter<HodnotenieDetail, HodnotenieViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HodnotenieViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull HodnotenieDetail model) {
                        holder.datum.setText(upravDatum(model.getDatumPridania()));
                        holder.textRecenzie.setText(model.getText());
                        holder.rating.setRating(model.getPocetHviezd());
                        userReference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(model.getUzivatelPridal());
                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String meno = snapshot.child("meno").getValue().toString();
                                String priezvisko = snapshot.child("priezvisko").getValue().toString();
                                holder.userName.setText(meno + " " + priezvisko);

                                String link = snapshot.child("fotka").getValue().toString();
                                if (link.equals("default")) {

                                }
                                else{
                                    Picasso.get().load(link).into(holder.fotka);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public HodnotenieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review, parent, false);
                        HodnotenieViewHolder holder = new HodnotenieViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public String upravDatum(String datum){
        String mesiac = datum.substring(0,3);
        String den = datum.substring(3,5);
        String rok = datum.substring(5);

        if (mesiac.equals(mesiace[0])){
            mesiac = "01";
        }
        else if (mesiac.equals(mesiace[1])){
            mesiac = "02";
        }
        else if (mesiac.equals(mesiace[2])){
            mesiac = "03";
        }
        else if (mesiac.equals(mesiace[3])){
            mesiac = "04";
        }
        else if (mesiac.equals(mesiace[4])){
            mesiac = "05";
        }
        else if (mesiac.equals(mesiace[5])){
            mesiac = "06";
        }
        else if (mesiac.equals(mesiace[6])){
            mesiac = "07";
        }
        else if (mesiac.equals(mesiace[7])){
            mesiac = "08";
        }
        else if (mesiac.equals(mesiace[8])){
            mesiac = "09";
        }
        else if (mesiac.equals(mesiace[9])){
            mesiac = "10";
        }
        else if (mesiac.equals(mesiace[10])){
            mesiac = "11";
        }
        else if (mesiac.equals(mesiace[11])){
            mesiac = "12";
        }

        return den + "/" + mesiac + "/" + rok;
    }

    public void nacitajDataHeader() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);
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