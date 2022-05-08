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
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    String uid;
    ImageView userProfil;
    Button save;

    boolean fotka = false;

    EditText menoProfil, priezviskoProfil, telefonProfil;

    String meno, priezvisko, telefon, downloadImageUrl;

    View headerView;

    DatabaseReference reference;
    StorageReference fotkyReference;

    Uri ImageUri;

    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.toolbar);

        menoProfil = findViewById(R.id.menoProfil);
        priezviskoProfil = findViewById(R.id.priezviskoProfil);
        telefonProfil = findViewById(R.id.telefonProfil);
        userProfil = findViewById(R.id.userProfil);
        save = findViewById(R.id.save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Úprava profilu");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
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
        fotkyReference = FirebaseStorage.getInstance().getReference().child("profiloveObrazky");

        nacitajData();
        nacitajFotku();
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
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(EditProfileActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });

        userProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OtvorGaleriu();
            }
        });
    }

    private void nacitajFotku() {
        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.child("fotka").getValue().toString();
                if (link.equals("default")) {

                }
                else{
                    Picasso.get().load(link).into(userProfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OtvorGaleriu() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            userProfil.setImageURI(ImageUri);
            fotka = true;
        }
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
                Intent intent = new Intent(EditProfileActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(EditProfileActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search):{
                Intent intent = new Intent(EditProfileActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add):{
                Intent intent = new Intent(EditProfileActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(EditProfileActivity.this, PublicProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts):{
                Intent intent = new Intent(EditProfileActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myRatings): {
                Intent intent = new Intent(EditProfileActivity.this, UserRatingsActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_editProfile): {
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
        meno = menoProfil.getText().toString();
        int menoLength = meno.length();
        priezvisko = priezviskoProfil.getText().toString();
        int priezviskoLength = priezvisko.length();
        telefon = telefonProfil.getText().toString();
        int telefonLength = telefon.length();

        if (TextUtils.isEmpty(meno) && TextUtils.isEmpty(priezvisko) && TextUtils.isEmpty(telefon)){
            Toast.makeText(this, "Vyššie uvedené polia nesmú byť prázdne.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(meno)){
            Toast.makeText(this, "Zadajte svoje meno!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(priezvisko)){
            Toast.makeText(this, "Zadajte svoje priezvisko!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(telefon)){
            Toast.makeText(this, "Zadajte svoje telefónne číslo!", Toast.LENGTH_SHORT).show();
        }
        else if (menoLength > 25){
            Toast.makeText(this, "Meno nesmie mať viac ako 25 znakov.", Toast.LENGTH_SHORT).show();
        }
        else if (priezviskoLength > 25){
            Toast.makeText(this, "Priezvisko nesmie mať viac ako 25 znakov.", Toast.LENGTH_SHORT).show();
        }
        else if (telefonLength > 10){
            Toast.makeText(this, "Telefón nesmie mať viac ako 10 znakov.", Toast.LENGTH_SHORT).show();
        }

        else{
            reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);

            reference.child("meno").setValue(menoProfil.getText().toString());
            meno = menoProfil.getText().toString();

            reference.child("priezvisko").setValue(priezviskoProfil.getText().toString());
            priezvisko = priezviskoProfil.getText().toString();

            reference.child("telefon").setValue(telefonProfil.getText().toString());
            telefon = telefonProfil.getText().toString();

            if (fotka) {
                ulozFotkuStorage();
            }

            Toast.makeText(this, "Úpravy boli prijaté", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(EditProfileActivity.this, PublicProfileActivity.class);
            intent.putExtra("uid", uid);
            startActivity(intent);
        }

    }

    private void ulozFotkuStorage()
    {
        final StorageReference filePath = fotkyReference.child(ImageUri.getLastPathSegment() + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(EditProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid);
                            reference.child("fotka").setValue(downloadImageUrl);
                        }
                    }
                });
            }
        });
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

                }
                else{
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