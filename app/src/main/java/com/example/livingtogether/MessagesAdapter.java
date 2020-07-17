package com.example.livingtogether;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.livingtogether.models.Message;


import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    List<Message> messages;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private TextView tvHeader;
        private TextView tvTitle;
        private TextView tvBody;
        private ImageView ivMedia;
        private TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvTime = itemView.findViewById(R.id.tvTime);

        }

        public void bind(Message message) {
            // If/else block so that if there is no body text, it doesn't show a blank line.
            if (message.getBody().equals("")) {
                tvBody.setVisibility(View.GONE);
            } else {
                tvBody.setVisibility(View.VISIBLE);
                tvBody.setText(message.getBody());
            }

            Glide.with(context)
                    .load(message.getCustomUser().getProfilePhoto().getUrl())
                    .into(ivProfile);
            tvTime.setText(message.getRelativeTime());

            if (message.getType() == Message.ANNOUNCEMENT_TYPE) {
                bindAnnouncement(message);
            }

        }

        private void bindAnnouncement(Message message) {
            String title = message.getCustomUser().getName() + ": " + message.getTitle();
            tvTitle.setText(title);

            // Shows or hides image depending on if message has image.
            if (message.getImage() == null) {
                ivMedia.setVisibility(View.GONE);
            } else {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImage().getUrl())
                        .into(ivMedia);
            }
        }
    }

}

