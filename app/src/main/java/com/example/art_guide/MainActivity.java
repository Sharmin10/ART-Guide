package com.example.art_guide;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickInterface{
    private ImageView imageView;
    GalleriesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ViewPager2 galleryViewPager = findViewById(R.id.galleriesViewPager);
        imageView = findViewById(R.id.ArtGuideImage);

        List<GalleryInfo> galleries = GalleryParser.parseGallery(this);
        adapter = new GalleriesAdapter(this, galleries, this);
        galleryViewPager.setAdapter(adapter);

        galleryViewPager.setClipToPadding(false);
        galleryViewPager.setClipChildren(false);
        galleryViewPager.setOffscreenPageLimit(3);
        galleryViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.95f + r * 0.05f);
            }
        });
        galleryViewPager.setPageTransformer(compositePageTransformer);
    }

    @Override
    public void onItemClick(GalleryInfo galleryInfo) {
        Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
        intent.putExtra("GalleryDetails", galleryInfo);
        startActivity(intent);
    }
}


