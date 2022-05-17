package com.example.eb4zar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button loginTlacidlo;
    private TextInputEditText mailEdit, hesloEdit;

    private CheckBox rememberBox;
    private TextView forgotPassword;

    private String mail;
    private String heslo;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTlacidlo = findViewById(R.id.login);
        mailEdit = findViewById(R.id.mail);
        hesloEdit = findViewById(R.id.heslo);

        rememberBox = findViewById(R.id.rememberBox);
        forgotPassword = findViewById(R.id.forgotPassword);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        loginTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail =  mailEdit.getText().toString();
                heslo = hesloEdit.getText().toString();

                if (TextUtils.isEmpty(mail) && TextUtils.isEmpty(heslo)) {
                    Toast.makeText(LoginActivity.this, "Zadajte svoj email a heslo!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(mail)){
                    Toast.makeText(LoginActivity.this, "Zadajte svoj email!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(heslo)){
                    Toast.makeText(LoginActivity.this, "Zadajte svoje heslo!", Toast.LENGTH_SHORT).show();
                }

                else {
                    mFirebaseAuth.signInWithEmailAndPassword(mail, heslo)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Zlé heslo, alebo email, skúste to znova.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                prihlasenie(task.getResult().getUser());
                                            }
                                        }});
                }
            }
        });

        rememberBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                }
                else if (!buttonView.isChecked()){
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        forgotPassword.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                final EditText emailText = new EditText(LoginActivity.this);
                alert.setMessage("Zadajte váš email");

                alert.setTitle("Zabudli ste heslo?");

                alert.setView(emailText);

                alert.setPositiveButton("Resetovať", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String email = emailText.getText().toString();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplication(), "Zadajte svoju emailovú adresu!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mFirebaseAuth.sendPasswordResetEmail(email)

                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Inštrukcie na resetovanie hesla Vám boli zaslané na email.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Reset hesla zlyhal!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                alert.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });
    }

    private void prihlasenie(FirebaseUser mFirebaseUser) {
        firebaseDatabase.getReference().child("uzivatelia").child(mFirebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent i = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}