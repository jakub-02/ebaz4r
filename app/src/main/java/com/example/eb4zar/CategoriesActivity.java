package com.example.eb4zar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class CategoriesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    ImageView auta, elektronika, hudba, knihy, nabytok, oblecenie, ostatne, sport, zvierata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        auta = findViewById(R.id.auta);
        elektronika = findViewById(R.id.elektronika);
        hudba = findViewById(R.id.hudba);
        knihy = findViewById(R.id.knihy);
        nabytok = findViewById(R.id.nabytok);
        oblecenie = findViewById(R.id.oblecenie);
        ostatne = findViewById(R.id.ostatne);
        sport = findViewById(R.id.sport);
        zvierata = findViewById(R.id.zvierata);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vyberte si kategóriu");

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

        auta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Autá");
                startActivity(intent);
            }
        });
        oblecenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Oblečenie");
                startActivity(intent);
            }
        });
        elektronika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Elektronika");
                startActivity(intent);
            }
        });
        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Šport");
                startActivity(intent);
            }
        });
        knihy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Knihy");
                startActivity(intent);
            }
        });
        hudba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Hudba");
                startActivity(intent);
            }
        });
        ostatne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Ostatné");
                startActivity(intent);
            }
        });
        zvierata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Zvieratá");
                startActivity(intent);
            }
        });
        nabytok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(CategoriesActivity.this, SelectedCategoryActivity.class);
                intent.putExtra("kategoria", "Nábytok");
                startActivity(intent);
            }
        });
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

            case (R.id.nav_home): {
                Intent intent = new Intent(CategoriesActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                break;
            }

            case (R.id.nav_search):{
                Intent intent = new Intent(CategoriesActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add):{
                Intent intent = new Intent(CategoriesActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(CategoriesActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts):{
                Intent intent = new Intent(CategoriesActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(CategoriesActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }
}