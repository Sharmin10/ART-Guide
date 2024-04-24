package com.example.art_guide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
    ImageView imageView;
    TextView galleryDetails;
    Button ArButton;
    ImageView mapImage;
    TextView addressInfo;
    TextView openInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        imageView = findViewById(R.id.actionImage);
        galleryDetails = findViewById(R.id.galleryDetails);
        ArButton = findViewById(R.id.ArButton);
        mapImage = findViewById(R.id.map);
        addressInfo = findViewById(R.id.addressInfo);
        openInfo = findViewById(R.id.openInfo);

        Intent intent = getIntent();
        GalleryInfo galleryInfo = (GalleryInfo) intent.getSerializableExtra("GalleryDetails");

        String imageNameWithoutExtension = galleryInfo.getImage().replace(".jpg", "");
        int imageResId = getResources().getIdentifier(imageNameWithoutExtension, "drawable", getPackageName());
        imageView.setImageResource(imageResId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(galleryInfo.getName());

        galleryDetails.setText(galleryInfo.getDescription());

        addressInfo.setText(galleryInfo.getAddress());

        openInfo.setText(galleryInfo.getOpen());

        String mapImageNameWithoutExtension = galleryInfo.getMap().replace(".png", "");
        int mapImageResId = getResources().getIdentifier(mapImageNameWithoutExtension, "drawable", getPackageName());
        mapImage.setImageResource(mapImageResId);

        ArButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(GalleryActivity.this, ArStart.class);
            startActivity(intent1);
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
