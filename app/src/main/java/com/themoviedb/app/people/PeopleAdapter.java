package com.themoviedb.app.people;

import android.content.Context;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;
import com.themoviedb.app.R;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private final List<String> peoplePhotoList;

    public PeopleAdapter(List<String> peoplePhotoList) {
        this.peoplePhotoList = peoplePhotoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation =
                AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        String peoplePhoto = peoplePhotoList.get(position);

        Glide.with(holder.itemView)
                .load("https://image.tmdb.org/t/p/w780" + peoplePhoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profileImageView);

        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return peoplePhotoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final RoundedImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}