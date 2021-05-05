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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    String uid, email;
    TextView emailProfil;
    Button save;

    EditText menoProfil, priezviskoProfil, telefonProfil;

    String meno, priezvisko, telefon;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout=findViewById(R.id.drawer_layout1);
        navigationView=findViewById(R.id.nav_view1);
        toolbar=findViewById(R.id.toolbar);

        menoProfil = findViewById(R.id.menoProfil);
        priezviskoProfil = findViewById(R.id.priezviskoProfil);
        telefonProfil = findViewById(R.id.telefonProfil);
        emailProfil = findViewById(R.id.emailProfil);

        save = findViewById(R.id.save);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            email = user.getEmail();

            emailProfil.setText(email);
        }

        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);

        nacitajData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });

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
                Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(ProfileActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                break;
            }

            case (R.id.nav_logout): {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(ProfileActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }


    public void nacitajData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meno = snapshot.child("meno").getValue().toString();
                priezvisko = snapshot.child("priezvisko").getValue().toString();
                telefon = snapshot.child("telefon").getValue().toString();

                menoProfil.setText(meno);
                priezviskoProfil.setText(priezvisko);
                telefonProfil.setText(telefon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void update(View view) {
        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);

        reference.child("meno").setValue(menoProfil.getText().toString());
        meno = menoProfil.getText().toString();

        reference.child("priezvisko").setValue(priezviskoProfil.getText().toString());
        priezvisko = priezviskoProfil.getText().toString();

        reference.child("telefon").setValue(telefonProfil.getText().toString());
        telefon = telefonProfil.getText().toString();

        Toast.makeText(this, "Úpravy boli prijaté", Toast.LENGTH_LONG).show();

    }
}