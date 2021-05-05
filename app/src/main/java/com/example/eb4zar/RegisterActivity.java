package com.example.eb4zar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eb4zar.model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText menoEdit, priezviskoEdit ,mailEdit, telefonEdit ,hesloEdit, heslo2Edit;
    private Button registerTlacdilo;
    private ProgressDialog postupReg;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerTlacdilo = findViewById(R.id.register);
        menoEdit = findViewById(R.id.meno);
        priezviskoEdit = findViewById(R.id.priezvisko);
        mailEdit = findViewById(R.id.mail);
        telefonEdit = findViewById(R.id.telefon);
        hesloEdit = findViewById(R.id.heslo);
        heslo2Edit = findViewById(R.id.heslo2);

        postupReg = new ProgressDialog(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        registerTlacdilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vytvorUcet();
            }
        });
    }

    private void vytvorUcet() {
        String meno = menoEdit.getText().toString();
        String priezvisko = priezviskoEdit.getText().toString();
        String mail = mailEdit.getText().toString();
        String telefon = telefonEdit.getText().toString();
        String heslo = hesloEdit.getText().toString();
        String heslo2 = heslo2Edit.getText().toString();

        if (TextUtils.isEmpty(meno) && TextUtils.isEmpty(priezvisko) && TextUtils.isEmpty(mail) &&
                TextUtils.isEmpty(telefon) && TextUtils.isEmpty(heslo) && TextUtils.isEmpty(heslo2)){
            Toast.makeText(this, "Vyššie uvedené polia nesmú byť prázdne.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(meno)){
            Toast.makeText(this, "Zadajte svoje meno!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(priezvisko)){
            Toast.makeText(this, "Zadajte svoje priezvisko!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Zadajte svoj email!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(telefon)){
            Toast.makeText(this, "Zadajte svoje telefónne číslo!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(heslo)){
            Toast.makeText(this, "Zadajte svoje heslo!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(heslo2)){
            Toast.makeText(this, "Zopakujte svoje heslo!", Toast.LENGTH_SHORT).show();
        }
        else if (!heslo.equals(heslo2)){
            Toast.makeText(this, "Zadané heslá sa nezhodujú!", Toast.LENGTH_SHORT).show();
        }
        else if (heslo.length() < 8){
            Toast.makeText(this, "Musíš zadať minimálne 8 znakov", Toast.LENGTH_SHORT).show();
        }
        else if (heslo2.length() < 8){
            Toast.makeText(this, "Musíš zadať minimálne 8 znakov", Toast.LENGTH_SHORT).show();
        }

        else{
            postupReg.setTitle("Vytváranie účtu");
            postupReg.setMessage("Vaše údaje sa overujú...");
            postupReg.setCanceledOnTouchOutside(false);
            postupReg.show();

            registeracia(mail, heslo, meno, priezvisko, telefon);
        }
    }

    private void registeracia(String mail, String heslo, String meno, String priezvisko, String telefon){
        mFirebaseAuth.createUserWithEmailAndPassword(mail, heslo)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            postupReg.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registrácia neúspešná, skúste to znovu prosím.", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            UserDetail userDetail = new UserDetail(meno, priezvisko, telefon, mail);
                            String uid = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference("uzivatelia").child(uid).setValue(userDetail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                             Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                                             startActivity(intent);
                                        }
                                    });
                            Toast.makeText(RegisterActivity.this, "Váš účet bol vytvorený.", Toast.LENGTH_SHORT).show();
                            postupReg.dismiss();
                        }
                    }
                });
    }
}