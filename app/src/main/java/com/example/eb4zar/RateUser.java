package com.example.eb4zar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class RateUser extends AppCompatActivity {

    TextView rateCount, ukazRate;
    EditText nazor;
    Button tlacidlo;
    RatingBar ratingBar;
    float rateValue;
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_rate_user);

        rateCount = findViewById(R.id.rateCount);
        ukazRate = findViewById(R.id.ukazRate);
        nazor = findViewById(R.id.nazor);
        tlacidlo = findViewById(R.id.tlacidlo);
        ratingBar = findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                rateValue = ratingBar.getRating();

                if (rateValue <=1 && rateValue>0)
                    rateCount.setText("Bad " +rateValue+ "/5");
                else if (rateValue <=2 && rateValue>1)
                    rateCount.setText("Good " +rateValue+ "/5");
                else if (rateValue <=3 && rateValue>2)
                    rateCount.setText("OK " +rateValue+ "/5");
                else if (rateValue <=4 && rateValue>3)
                    rateCount.setText("Nice " +rateValue+ "/5");
                else if (rateValue <=5 && rateValue>4)
                    rateCount.setText("Very Nice " +rateValue+ "/5");
            }
        });

        tlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = rateCount.getText().toString();
                ukazRate.setText("MenoUzovatela \n" +temp+ "\n" +nazor.getText());
                nazor.setText("");
                ratingBar.setRating(0);
                rateCount.setText("");
            }
        });

    }
}