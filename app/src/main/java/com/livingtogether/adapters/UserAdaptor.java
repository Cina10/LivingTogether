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
import com.livingtogether.models.CustomUser;
import com.parse.ParseException;

import java.util.List;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.ViewHolder> {
    Context context;
    List<CustomUser> users;

    public UserAdaptor(Context context, List<CustomUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomUser user = users.get(position);
        try {
            holder.bind(user);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(CustomUser user) throws ParseException {
            if (user.getProfilePhoto() != null) {
                Glide.with(context)
                        .load(user.getProfilePhoto().getUrl())
                        .into(ivProfile);
            } else if (user.getIsFacebookUser()) {
                Glide.with(context)
                        .load(user.getPhotoUrl())
                        .into(ivProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(ivProfile);
            }

            tvName.setText(user.getName());
        }
    }
}
