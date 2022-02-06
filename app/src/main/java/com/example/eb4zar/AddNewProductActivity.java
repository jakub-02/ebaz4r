package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    ImageView obrazokProduktu;

    TextInputEditText nazovText, popisText, cenaText;
    AutoCompleteTextView kategoria;
    String uid, downloadImageUrl, kategoriaText;

    String saveCurrentDate, saveCurrentTime, productRandomKey;

    Button add;

    DatabaseReference reference;
    StorageReference fotkyReference;

    Uri ImageUri;

    int inzeraty;

    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        nazovText = findViewById(R.id.nazovText);
        popisText = findViewById(R.id.popisText);
        cenaText = findViewById(R.id.cenaText);
        kategoria = findViewById(R.id.kategoria);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pridajte nový inzerát");

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

        //nacitanie uid uzivatela
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(uid);
        fotkyReference = FirebaseStorage.getInstance().getReference().child("produktoveObrazky");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.kategorie, R.layout.spinner_item);
        kategoria.setAdapter(adapter);

        kategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kategoriaText =  parent.getItemAtPosition(position).toString();
            }
        });

        //nastavenie obrazka
        obrazokProduktu = findViewById(R.id.obrazokProduktu);
        obrazokProduktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OtvorGaleriu();
            }
        });

        //onclick tlacitko
        add = findViewById(R.id.vytvorit);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skontrolujPrazndePolia();
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
            obrazokProduktu.setImageURI(ImageUri);
        }
    }

    private void skontrolujPrazndePolia(){
        String nazov = nazovText.getText().toString();
        String popis = popisText.getText().toString();
        String cena = cenaText.getText().toString();

        if(TextUtils.isEmpty(nazov) && TextUtils.isEmpty(popis) && TextUtils.isEmpty(cena)){
            Toast.makeText(this, "Vyššie uvedené polia nesmú byť prázdne.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nazov)){
            Toast.makeText(this, "Zadajte názov inzerátu!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(popis)){
            Toast.makeText(this, "Zadajte popis inzerátu!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cena)){
            Toast.makeText(this, "Zadajte cenu inzerátu!", Toast.LENGTH_SHORT).show();
        }

        else{
            update();
        }
    }

    public void update() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMMddyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentTime + "" +saveCurrentDate;

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(uid + productRandomKey);

        reference.child("nazov").setValue(nazovText.getText().toString());

        reference.child("uzivatel").setValue(uid);

        reference.child("kategoria").setValue(kategoriaText);

        reference.child("popis").setValue(popisText.getText().toString());

        reference.child("cena").setValue(cenaText.getText().toString());

        reference.child("datumPridania").setValue(saveCurrentDate);

        reference.child("casPridania").setValue(saveCurrentTime);

        reference.child("fotka").setValue("default");

        ulozFotkuStorage();

        updateInzeraty();

        Toast.makeText(this, "Inzerát bol vytvorený.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(AddNewProductActivity.this, MenuActivity.class);
        startActivity(intent);
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
                Toast.makeText(AddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                            reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(uid + productRandomKey);
                            reference.child("fotka").setValue(downloadImageUrl);
                        }
                    }
                });
            }
        });
    }

    private void updateInzeraty(){
        reference = FirebaseDatabase.getInstance().getReference().child("uzivatelia").child(uid).child("inzeraty");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inzeraty = Integer.parseInt(dataSnapshot.getValue().toString());
                reference.setValue(inzeraty + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                Intent intent = new Intent(AddNewProductActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(AddNewProductActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search):{
                Intent intent = new Intent(AddNewProductActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add):{
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(AddNewProductActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts):{
                Intent intent = new Intent(AddNewProductActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AddNewProductActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(AddNewProductActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }


}