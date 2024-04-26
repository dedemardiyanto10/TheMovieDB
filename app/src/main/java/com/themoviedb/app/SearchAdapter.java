package com.themoviedb.app;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.themoviedb.app.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<Search> searchList;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(Context context, List<Search> searchList) {
        this.context = context;
        this.searchList = searchList;
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
                        .inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation =
                AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        Search search = searchList.get(position);

        holder.tvTitle.setText(search.getTitle());

        holder.tvTitle.setSelected(true);
        holder.tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvTitle.setSingleLine(true);
        holder.tvTitle.setMarqueeRepeatLimit(-1);
        holder.tvTitle.setFocusable(true);
        holder.tvTitle.setFocusableInTouchMode(true);

        double rating = search.getVoteAverage();
        if (rating == 0.0) {
            holder.tvRating.setText("0%");
            holder.cpiRating.setProgress(0);
            holder.cpiRating.setIndicatorColor(Color.parseColor("#45474B"));
            holder.cpiRating.setTrackColor(Color.parseColor("#45474B"));
        } else {
            String formattedRating = String.format("%.1f", rating).replace(",", "");
            holder.tvRating.setText(formattedRating + "%");

            int percentage = (int) (((rating / 10) * 100));
            holder.cpiRating.setProgress(percentage);

            if (percentage < 70) {
                holder.cpiRating.setIndicatorColor(Color.parseColor("#D2D431"));
                holder.cpiRating.setTrackColor(Color.parseColor("#413D0E"));
            } else {
                holder.cpiRating.setIndicatorColor(Color.parseColor("#20D07B"));
                holder.cpiRating.setTrackColor(Color.parseColor("#214529"));
            }
        }

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w780" + search.getPosterPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivPoster);

        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView ivPoster;
        public TextView tvTitle;
        public TextView tvRating;
        public LinearProgressIndicator cpiRating;
        public OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
            cpiRating = itemView.findViewById(R.id.cpiRating);
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
