package com.themoviedb.app;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.themoviedb.app.people.PeopleActivity;
import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Cast> castList;

    public CastAdapter(Context context, ArrayList<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Animation animation =
                AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);

        Cast cast = castList.get(position);

        holder.nameTextView.setText(cast.getName());
        holder.charTextView.setText(cast.getCharacter());

        holder.nameTextView.setSelected(true);
        holder.nameTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.nameTextView.setSingleLine(true);
        holder.nameTextView.setMarqueeRepeatLimit(-1);
        holder.nameTextView.setFocusable(true);
        holder.nameTextView.setFocusableInTouchMode(true);

        holder.charTextView.setSelected(true);
        holder.charTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.charTextView.setSingleLine(true);
        holder.charTextView.setMarqueeRepeatLimit(-1);
        holder.charTextView.setFocusable(true);
        holder.charTextView.setFocusableInTouchMode(true);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w780" + cast.getProfilePath())
                // .placeholder(R.drawable.noimage)
                // .error(R.drawable.noimage)
                .into(holder.profileImageView);

        holder.itemView.setOnClickListener(
                v -> {
                    Intent intent = new Intent(context, PeopleActivity.class);
                    intent.putExtra("id", cast.getId());
                    intent.putExtra("name", cast.getName());
                    context.startActivity(intent);
                });

        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameTextView;
        private final TextView charTextView;
        private final RoundedImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            charTextView = itemView.findViewById(R.id.charTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
        }
    }
}
