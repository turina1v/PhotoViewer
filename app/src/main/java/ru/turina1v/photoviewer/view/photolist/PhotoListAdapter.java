package ru.turina1v.photoviewer.view.photolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.ImageLoader;
import ru.turina1v.photoviewer.model.entity.Hit;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ImageViewHolder> {
    private List<Hit> photos;
    private OnPhotoClickListener listener;

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        ImageViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.item_photo);
        }
    }

    public PhotoListAdapter() {
        this.photos = new ArrayList<>();
    }

    public PhotoListAdapter(List<Hit> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_photos, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (photos.get(position).isCommercial()) {
            holder.imageView.setImageResource(R.drawable.pixabay_logo);
            String url = "https://pixabay.com/";
            holder.imageView.setOnClickListener(v -> listener.onCommercialClick(url));
        } else {
            ImageLoader.loadImage(holder.imageView, photos.get(position).getWebFormatUrl());
            holder.imageView.setOnClickListener(v -> listener.onPhotoClick(photos.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setOnPictureClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
    }

    public void setPhotosList(List<Hit> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    void addToPhotosList(List<Hit> photos) {
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    void clearPhotosList() {
        photos.clear();
        notifyDataSetChanged();
    }
}
