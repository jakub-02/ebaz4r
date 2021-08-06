package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eb4zar.model.ProductDetail;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String zvolenyProdukt;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    ImageView imageView;
    TextView productName, productDesc, productPrice, sellerMail, sellerPhone;

    DatabaseReference reference, sellerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail2);

        zvolenyProdukt = getIntent().getExtras().get("zvolenyProdukt").toString();

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(zvolenyProdukt);
        sellerReference = FirebaseDatabase.getInstance().getReference().child("uzivatelia");

        imageView = findViewById(R.id.imageView);
        productName = findViewById(R.id.productName);
        productDesc = findViewById(R.id.productDesc);
        productPrice = findViewById(R.id.productPrice);
        sellerMail = findViewById(R.id.sellerMail);
        sellerPhone = findViewById(R.id.sellerPhone);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        nacitajFotku();
        nacitajData();

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
        switch (item.getItemId()){

            case (R.id.nav_home): {
                Intent intent = new Intent(ProductDetailActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(ProductDetailActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(ProductDetailActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(ProductDetailActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }

    private void nacitajFotku() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.child("fotka").getValue().toString();
                if (link.equals("default")) {

                }
                else{
                    Picasso.get().load(link).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void nacitajData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("nazov").getValue().toString();
                String desc = snapshot.child("popis").getValue().toString();
                String price = snapshot.child("cena").getValue().toString();
                String seller = snapshot.child("uzivatel").getValue().toString();

                productName.setText(name);
                productDesc.setText(desc);
                productPrice.setText(price + " €");

                sellerReference.child(seller).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String mail = snapshot.child("mail").getValue().toString();
                        String phone = snapshot.child("telefon").getValue().toString();

                        sellerMail.setText(mail);
                        sellerPhone.setText(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}