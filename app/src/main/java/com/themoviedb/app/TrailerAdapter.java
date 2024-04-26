package com.themoviedb.app;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private final List<String> trailerKeys;

    public TrailerAdapter(List<String> trailerKeys) {
        this.trailerKeys = trailerKeys;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {

        Animation animation =
                AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        String trailerKey = trailerKeys.get(position);

        Glide.with(holder.itemView)
                .load("https://img.youtube.com/vi/" + trailerKey + "/hqdefault.jpg")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivTrailer);

        holder.itemView.setOnClickListener(
                v -> {
                    if (trailerKey != null && !trailerKey.isEmpty()) {
                        String youtubeUrl = "https://youtu.be/" + trailerKey;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return trailerKeys.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
       public RoundedImageView ivTrailer;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTrailer = itemView.findViewById(R.id.ivTrailer);
        }
    }
}
