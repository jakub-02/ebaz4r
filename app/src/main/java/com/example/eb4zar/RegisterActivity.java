package com.example.eb4zar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

    private EditText menoEdit, mailEdit, hesloEdit, heslo2Edit;
    private Button registerTlacdilo;
    private ProgressDialog postupReg;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerTlacdilo = findViewById(R.id.register);
        menoEdit = findViewById(R.id.meno);
        mailEdit = findViewById(R.id.mail);
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
        String mail = mailEdit.getText().toString();
        String heslo = hesloEdit.getText().toString();
        String heslo2 = heslo2Edit.getText().toString();


        if (TextUtils.isEmpty(meno)){
            Toast.makeText(this, "Zadajte svoje meno!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Zadajte svoj email!", Toast.LENGTH_SHORT).show();
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

        else{
            postupReg.setTitle("Vytváranie účtu");
            postupReg.setMessage("Vaše údaje sa overujú...");
            postupReg.setCanceledOnTouchOutside(false);
            postupReg.show();

            registeracia(mail, heslo, meno);
        }

    }

    private void registeracia(String mail, String heslo, String meno){
        mFirebaseAuth.createUserWithEmailAndPassword(mail, heslo)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            postupReg.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registrácia neúspešná, skúste to znovu prosím.", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            UserDetail userDetail = new UserDetail(meno);
                            String uid = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference(uid).setValue(userDetail)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                             Intent intent = new Intent(RegisterActivity.this, CategoriesActivity.class);
                                             startActivity(intent);
                                        }
                                    });
                            Toast.makeText(RegisterActivity.this, "Váš účet bol vytvorený.", Toast.LENGTH_SHORT).show();
                            postupReg.dismiss();
                        }
                    }
                });

    }

    /*
    private void overMail(final String name, final String mail, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(mail).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("mail", mail);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(mail).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        postupReg.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        postupReg.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "This " + mail + " already exists.", Toast.LENGTH_SHORT).show();
                    postupReg.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}