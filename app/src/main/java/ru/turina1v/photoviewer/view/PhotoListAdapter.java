package ru.turina1v.photoviewer.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.turina1v.photoviewer.R;
import ru.turina1v.photoviewer.model.PicassoLoader;
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

    PhotoListAdapter(List<Hit> photos) {
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
        PicassoLoader.loadImage(holder.imageView, photos.get(position).getWebFormatUrl());
        holder.imageView.setOnClickListener(v -> listener.onPhotoClick(position, photos.get(position).getLargeImageUrl()));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    void setOnPictureClickListener(OnPhotoClickListener listener) {
        this.listener = listener;
    }

    void setPhotosList(List<Hit> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }
}