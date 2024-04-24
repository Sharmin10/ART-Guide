package com.example.art_guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleriesAdapter extends RecyclerView.Adapter<GalleriesAdapter.GalleriesViewHolder>{

    private static List<GalleryInfo> galleries;
    static Context context;
    OnClickInterface onClickInterface;

    public GalleriesAdapter(Context context, List<GalleryInfo> galleries, OnClickInterface onClickInterface) {
        this.context = context;
        this.galleries = galleries;
        this.onClickInterface = onClickInterface;
    }

    @NonNull
    @Override
    public GalleriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_galleries, parent, false);
                return new GalleriesAdapter.GalleriesViewHolder(view, onClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleriesViewHolder holder, int position) {
        holder.setGalleryData(galleries.get(position));
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }

    public static class GalleriesViewHolder extends RecyclerView.ViewHolder {

        private KenBurnsView kbvGallery;
        private TextView textTitle;
        public GalleriesViewHolder(@NonNull View itemView, OnClickInterface onClickInterface) {
            super(itemView);
            this.kbvGallery = itemView.findViewById(R.id.kbvGallery);
            this.textTitle = itemView.findViewById(R.id.textTitle);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    GalleryInfo galleryInfo = galleries.get(position);
                    onClickInterface.onItemClick(galleryInfo);
                }
            });
        }

        public void setGalleryData(GalleryInfo gallery) {
            String imageNameWithoutExtension = gallery.getImage().replace(".jpg", "");
            int imageResId = context.getResources().getIdentifier(
                    imageNameWithoutExtension,
                    "drawable",
                    context.getPackageName()
            );
            Picasso.get().load(imageResId).into(kbvGallery);
            textTitle.setText(gallery.name);
        }

    }
}
