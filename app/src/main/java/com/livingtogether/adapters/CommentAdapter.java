package com.livingtogether.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.Comment;
import com.livingtogether.models.CustomUser;
import com.parse.ParseException;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        try {
            holder.bind(comment);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private TextView tvName;
        private TextView tvText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvText = itemView.findViewById(R.id.tvText);
        }

        public void bind(Comment comment) throws ParseException {
            CustomUser commentUser = comment.getCustomUser().fetchIfNeeded();
            if (commentUser.getProfilePhoto() != null) {
                Glide.with(context)
                        .load(commentUser.getProfilePhoto().getUrl())
                        .into(ivProfile);
            } else if (commentUser.getIsFacebookUser()) {
                Glide.with(context)
                        .load(commentUser.getPhotoUrl())
                        .into(ivProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(ivProfile);
            }

            tvName.setText(commentUser.getName());
            tvText.setText(comment.getText());
        }
    }
}
