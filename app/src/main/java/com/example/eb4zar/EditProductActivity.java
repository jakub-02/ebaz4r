package com.example.eb4zar;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String zvolenyProdukt;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;

    ImageView obrazok;
    TextInputEditText nazov, popis, cena;
    AutoCompleteTextView kategoria;
    Button uprav;

    String kategoriaText, downloadImageUrl;

    DatabaseReference reference;
    StorageReference fotkyReference;

    boolean fotka = false;

    Uri ImageUri;

    private static final int GalleryPick = 1;

    String uid;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        zvolenyProdukt = getIntent().getExtras().get("zvolenyProdukt").toString();

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(zvolenyProdukt);
        fotkyReference = FirebaseStorage.getInstance().getReference().child("produktoveObrazky");

        drawerLayout = findViewById(R.id.drawer_layout1);
        navigationView = findViewById(R.id.nav_view1);
        toolbar = findViewById(R.id.toolbar);
        obrazok = findViewById(R.id.obrazokProduktu);
        kategoria = findViewById(R.id.kategoria);
        uprav = findViewById(R.id.uprav);
        nazov = findViewById(R.id.nazovText);
        popis = findViewById(R.id.popisText);
        cena = findViewById(R.id.cenaText);

        setSupportActionBar(toolbar);

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

        obrazok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OtvorGaleriu();
            }
        });

        kategoria.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                kategoriaText =  parent.getItemAtPosition(position).toString();
            }
        });

        nacitajFotku();
        nacitajData();

        uprav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skontrolujPrazndePolia();
                Intent intent = new Intent(EditProductActivity.this, MyProductsActivity.class);
                startActivity(intent);
            }
        });

        nacitajDataHeader();
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
            obrazok.setImageURI(ImageUri);
            fotka = true;
        }
    }

    private void nacitajFotku() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.child("fotka").getValue().toString();
                if (link.equals("default")) {

                }
                else{
                    Picasso.get().load(link).into(obrazok);
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
                String cat = snapshot.child("kategoria").getValue().toString();

                nazov.setText(name);
                popis.setText(desc);
                cena.setText(price);
                kategoriaText = cat;
                kategoria.setText(kategoriaText);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        EditProductActivity.this, R.array.kategorie, R.layout.spinner_item);
                kategoria.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void skontrolujPrazndePolia(){
        String nazovText = nazov.getText().toString();
        String popisText = popis.getText().toString();
        String cenaText = cena.getText().toString();

        if(TextUtils.isEmpty(nazovText) && TextUtils.isEmpty(popisText) && TextUtils.isEmpty(cenaText)){
            Toast.makeText(this, "Vyššie uvedené polia nesmú byť prázdne.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nazovText)){
            Toast.makeText(this, "Zadajte názov inzerátu!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(popisText)){
            Toast.makeText(this, "Zadajte popis inzerátu!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cenaText)){
            Toast.makeText(this, "Zadajte cenu inzerátu!", Toast.LENGTH_SHORT).show();
        }

        else{
            update();
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
                Toast.makeText(EditProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                            reference.child("fotka").setValue(downloadImageUrl);
                        }
                    }
                });
            }
        });
    }

    public void update() {
        reference.child("nazov").setValue(nazov.getText().toString());

        reference.child("kategoria").setValue(kategoriaText);

        reference.child("popis").setValue(popis.getText().toString());

        reference.child("cena").setValue(cena.getText().toString());

        if (fotka){
        ulozFotkuStorage();}

        Toast.makeText(this, "Inzerát bol upravený.", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(EditProductActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_categories):{
                Intent intent = new Intent(EditProductActivity.this, CategoriesActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_search):{
                Intent intent = new Intent(EditProductActivity.this, SearchProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_add):{
                Intent intent = new Intent(EditProductActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_profile): {
                Intent intent = new Intent(EditProductActivity.this, PublicProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                break;
            }

            case (R.id.nav_myProducts):{
                Intent intent = new Intent(EditProductActivity.this, MyProductsActivity.class);
                startActivity(intent);
                break;
            }

            case (R.id.nav_logout): {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(EditProductActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(EditProductActivity.this, "Odhlásenie bolo úspešné.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
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