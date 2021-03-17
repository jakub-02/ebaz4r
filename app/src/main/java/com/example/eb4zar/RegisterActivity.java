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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText menoEdit, mailEdit, hesloEdit, heslo2Edit;
    private Button registerTlacdilo;
    private ProgressDialog postupReg;

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

        /*
        menoEdit.addTextChangedListener(textWatcher);
        mailEdit.addTextChangedListener(textWatcher);
        heslo2Edit.addTextChangedListener(textWatcher);
        hesloEdit.addTextChangedListener(textWatcher);
         */

        registerTlacdilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vytvorUcet();
                /*
                Intent intent = new Intent(RegisterActivity.this, CategoriesActivity.class);
                startActivity(intent);
                 */
            }
        });
    }

    private void vytvorUcet() {
        String meno = menoEdit.getText().toString();
        String mail = mailEdit.getText().toString();
        String heslo = hesloEdit.getText().toString();
        String heslo2 = heslo2Edit.getText().toString();

        if (TextUtils.isEmpty(meno)){
            Toast.makeText(this, "Zadajte svoje meno!", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(mail)){
            Toast.makeText(this, "Zadajte svoj email!", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(heslo)){
            Toast.makeText(this, "Zadajte svoje heslo!", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(heslo2)){
            Toast.makeText(this, "Zopakujte svoje heslo!", Toast.LENGTH_SHORT);
        }
        else if (!heslo.equals(heslo2)){
            Toast.makeText(this, "Zadané heslá sa nezhodujú!", Toast.LENGTH_SHORT);
        }

        else{
            postupReg.setTitle("Vytváranie účtu");
            postupReg.setMessage("Vaše údaje sa overujú...");
            postupReg.setCanceledOnTouchOutside(false);
            postupReg.show();

            overEmail(meno, mail, heslo);
        }

    }

    private void overEmail(String meno, String mail, String heslo) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("Uzivatelia").child(mail).exists()){
                    HashMap<String, Object> uzivatelData = new HashMap<>();
                    uzivatelData.put("meno", meno);
                    uzivatelData.put("email", mail);
                    uzivatelData.put("heslo", heslo);

                    RootRef.child("Uzivatelia").child(mail).updateChildren(uzivatelData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Účet bol vytvorený.", Toast.LENGTH_SHORT);
                                        postupReg.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        postupReg.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Chyba siete.", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Tento email je už zaregistrovaný.", Toast.LENGTH_SHORT);
                    postupReg.dismiss();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String meno = menoEdit.getText().toString().trim();
            String mail = mailEdit.getText().toString().trim();
            String h1 = hesloEdit.getText().toString().trim();
            String h2 = heslo2Edit.getText().toString().trim();

            registerTlacdilo.setEnabled(!meno.isEmpty() || !mail.isEmpty() || h1.isEmpty() || h2.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

     */
}