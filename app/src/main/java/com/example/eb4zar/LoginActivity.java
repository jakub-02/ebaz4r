package com.example.eb4zar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button loginTlacidlo;
    private EditText menoEdit, hesloEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTlacidlo = findViewById(R.id.login);
        menoEdit = findViewById(R.id.mail);
        hesloEdit = findViewById(R.id.heslo);

        menoEdit.addTextChangedListener(textWatcher);
        hesloEdit.addTextChangedListener(textWatcher);
        loginTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });
    }
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String m = menoEdit.getText().toString().trim();
            String h = hesloEdit.getText().toString().trim();

            loginTlacidlo.setEnabled(!m.isEmpty() && !h.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}