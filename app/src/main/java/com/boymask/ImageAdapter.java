package com.boymask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boymask.myapplication.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<File> images;
    private OnItemClickListener listener;

    // Interfaccia per click sugli elementi
    public interface OnItemClickListener {
        void onItemClick(File file, int position);
        void onItemLongClick(File file, int position);
    }

    public ImageAdapter(List<File> images) {
        this.images = images;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setImages(List<File> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void addImage(File file) {
        images.add(file);
        notifyItemInserted(images.size() - 1);
    }

    public void removeImage(int position) {
        if (position >= 0 && position < images.size()) {
            images.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, images.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File file = images.get(position);

        Glide.with(holder.imageView.getContext())
                .load(file)
                .centerCrop()
                .into(holder.imageView);

        // Click normale
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(file, position);
            }
        });

        // Click lungo (es: elimina)
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(file, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}