package com.themoviedb.app.trending;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.themoviedb.app.R;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    private final List<Trending> trendingList;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public TrendingAdapter(Context context, List<Trending> trendingList) {
        this.context = context;
        this.trendingList = trendingList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_trending, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation =
                AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        Trending trending = trendingList.get(position);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w780" + trending.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPoster);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w780" + trending.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivBackdrop);

        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView ivPoster;
        public KenBurnsView ivBackdrop;
        //  public TextView tvTitle;
      //  public TextView tvRating;
      //  public CircularProgressIndicator cpiRating;
        public OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            ivBackdrop = itemView.findViewById(R.id.ivBackdrop);
            // tvTitle = itemView.findViewById(R.id.tvTitle);
          //  tvRating = itemView.findViewById(R.id.tvRating);
           // cpiRating = itemView.findViewById(R.id.cpiRating);
            this.onItemClickListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }
}
