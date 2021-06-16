package com.example.eb4zar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView obrazokProduktu;

    EditText nazovText, popisText, cenaText;
    String uid, kategoria, downloadImageUrl;

    String saveCurrentDate, saveCurrentTime, productRandomKey;

    Button add;

    DatabaseReference reference;
    StorageReference fotkyReference;

    Uri ImageUri;

    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        //nacitanie uid uzivatela
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(uid);
        fotkyReference = FirebaseStorage.getInstance().getReference().child("produktoveObrazky");

        Spinner vyberKategorie = findViewById(R.id.vyberKategorie);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.kategorie, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vyberKategorie.setAdapter(adapter);
        vyberKategorie.setOnItemSelectedListener(this);

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
                update(v);
            }
        });

        nazovText = findViewById(R.id.nazov);
        popisText = findViewById(R.id.popisText);
        cenaText = findViewById(R.id.cena);
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

    public void update(View view) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMMddyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentTime + "" +saveCurrentDate;

        reference = FirebaseDatabase.getInstance().getReference().child("produkty").child(uid + productRandomKey);

        reference.child("nazov").setValue(nazovText.getText().toString());

        reference.child("uzivatel").setValue(uid);

        reference.child("kategoria").setValue(kategoria);

        reference.child("popis").setValue(popisText.getText().toString());

        reference.child("cena").setValue(cenaText.getText().toString());

        ulozFotkuStorage();

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        kategoria = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


}