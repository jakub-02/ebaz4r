package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class RateUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    TextInputEditText hodnotenie;
    Button tlacidlo;
    RatingBar ratingBar;

    DatabaseReference reference;

    float rateValue;
    String uid, uidProfil, saveCurrentDate, saveCurrentTime, hodnotenieRandomKey;
    int hodnotenia;

    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_user);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.toolbar);
        tlacidlo = findViewById(R.id.tlacidlo);
        ratingBar = findViewById(R.id.ratingBar);
        hodnotenie = findViewById(R.id.hodnotenie);

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
        getSupportActionBar().setTitle("Hodnotenie užívateľa");

        headerView = navigationView.getHeaderView(0);

        //nacitanie uid uzivatela
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        uidProfil = getIntent().getExtras().get("uid").toString();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingBar.getRating();
            }
        });

        tlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ulozRating();
            }
        });

        nacitajDataHeader();

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
                                Intent intent = new Intent(RateUserActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(RateUserActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(RateUserActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories): {
                Intent intent = new Intent(RateUserActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search): {
                Intent intent = new Intent(RateUserActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add): {
                Intent intent = new Intent(RateUserActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(RateUserActivity.this, PublicProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts): {
                Intent intent = new Intent(RateUserActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myRatings): {
                Intent intent = new Intent(RateUserActivity.this, UserRatingsActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_editProfile): {
                Intent intent = new Intent(RateUserActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ulozRating() {
        String hod = hodnotenie.getText().toString();
        if (hod.length() > 250) {
            Toast.makeText(this, "Popis nesmie mať viac ako 250 znakov.", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMMddyyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calendar.getTime());
            hodnotenieRandomKey = saveCurrentTime + "" + saveCurrentDate;

            reference = FirebaseDatabase.getInstance().getReference().child("hodnotenia").child(uidProfil + hodnotenieRandomKey);

            reference.child("uzivatel").setValue(uidProfil);
            reference.child("uzivatelPridal").setValue(uid);
            reference.child("text").setValue(hodnotenie.getText().toString());
            reference.child("pocetHviezd").setValue(rateValue);
            reference.child("datumPridania").setValue(saveCurrentDate);
            reference.child("casPridania").setValue(saveCurrentTime);

            updateHodnotenia();

            Toast.makeText(this, "Hodnotenie bolo pridané.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RateUserActivity.this, UserRatingsActivity.class);
            intent.putExtra("uid", uidProfil);
            startActivity(intent);
        }
    }

    private void updateHodnotenia() {
        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uidProfil).child("hodnotenia");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hodnotenia = Integer.parseInt(dataSnapshot.getValue().toString());
                reference.setValue(hodnotenia + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void nacitajDataHeader() {
        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);
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