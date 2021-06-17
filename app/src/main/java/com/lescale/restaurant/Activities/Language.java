package com.lescale.restaurant.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.Classes.Utils;
import com.lescale.restaurant.R;
import com.squareup.picasso.Picasso;

public class Language extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        ImageView arabic = findViewById(R.id.img_arabic);
        ImageView english = findViewById(R.id.img_english);
        ImageView french = findViewById(R.id.img_french);

        Picasso.get().load(R.drawable.flag_of_mauritania).noPlaceholder().fit().centerCrop().into(arabic);
        Picasso.get().load(R.drawable.englishflag).noPlaceholder().fit().centerCrop().into(english);
        Picasso.get().load(R.drawable.frenchflag).noPlaceholder().fit().centerCrop().into(french);

        arabic.setOnClickListener(v -> {
            AppHelper.setLanguage(Language.this, Utils.ARABIC);
            AppHelper.setLocal(Language.this,"ar");
            startActivity(new Intent(Language.this,HomeActivity.class));
            finish();
        });
        english.setOnClickListener(v -> {
            AppHelper.setLanguage(Language.this, Utils.ENGLISH);
            AppHelper.setLocal(Language.this,"en");
            startActivity(new Intent(Language.this,HomeActivity.class));
            finish();
        });
        french.setOnClickListener(v -> {
            AppHelper.setLanguage(Language.this, Utils.FRENCH);
            AppHelper.setLocal(Language.this,"fr");
            startActivity(new Intent(Language.this,HomeActivity.class));
            finish();
        });
    }
}
