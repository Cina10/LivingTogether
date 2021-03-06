package com.livingtogether.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.material.card.MaterialCardView;
import com.livingtogether.activities.MainActivity;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Like;
import com.livingtogether.models.Message;
import com.parse.ParseException;
import com.parse.SaveCallback;


import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    List<Message> messages;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view, listener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        try {
            holder.bind(message);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Message> list) {
        messages.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Message message) {
        messages.add(message);
        notifyItemChanged(messages.size());
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private TextView tvTitle;
        private TextView tvBody;
        private ImageView ivMedia;
        private TextView tvTime;
        private MaterialCardView card;
        private TextView tvLike;
        private ImageView ivLike;
        private boolean liked;

        public ViewHolder(@NonNull final View itemView, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvTime = itemView.findViewById(R.id.tvTime);
            card = itemView.findViewById(R.id.card);
            tvLike = itemView.findViewById(R.id.tvLike);
            ivLike = itemView.findViewById(R.id.ivLike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onItemLongClick(itemView, getAdapterPosition());
                    return true;
                }
            });

        }

        public void bind(final Message message) throws ParseException {
            // If/else block so that if there is no body text, it doesn't show a blank line.
            if (message.getBody().equals("")) {
                tvBody.setVisibility(View.GONE);
            } else {
                tvBody.setVisibility(View.VISIBLE);
                tvBody.setText(Html.fromHtml(message.getBody()));
            }

            CustomUser customUser = message.getCustomUser().fetchIfNeeded();
            if (customUser.getProfilePhoto() != null) {
                Glide.with(context)
                        .load(message.getCustomUser().getProfilePhoto().getUrl())
                        .into(ivProfile);
            } else if (customUser.getIsFacebookUser()) {
                Glide.with(context)
                        .load(message.getCustomUser().getPhotoUrl())
                        .into(ivProfile);
            } else {
                Glide.with(context)
                        .load(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .into(ivProfile);
            }

            tvTime.setText(message.getRelativeTime());

            // TODO break up into generic method that gets passed a message type
            if (message.getType().equals(Message.MessageType.ANNOUNCEMENT.toString())) {
                bindAnnouncement(message);
            } else if (message.getType().equals(Message.MessageType.SHOPPING_LIST_ITEM.toString())) {
                bindShoppingListItem(message);
            } else if (message.getType().equals(Message.MessageType.PURCHASE.toString()))
                bindPurchase(message);

            int likes = message.getLikes();
            tvLike.setText("" + likes);
            final CustomUser curUser = MainActivity.getCurUser();
            final Like like = Like.queryIfLiked(message, curUser);
            if (like == null) {
                liked = false;
                ivLike.setImageResource(R.drawable.ic_baseline_star_border_24);
            } else {
                liked = true;
                ivLike.setImageResource(R.drawable.ic_baseline_star_24);
            }

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (liked) {
                        try {
                            ivLike.setImageResource(R.drawable.ic_baseline_star_border_24);
                            like.delete();
                            liked = false;
                            message.decrementLikes();
                            message.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    tvLike.setText("" + message.getLikes());
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ivLike.setImageResource(R.drawable.ic_baseline_star_24);
                        Like newLike = new Like();
                        newLike.setCustomUser(curUser);
                        newLike.setMessage(message);
                        try {
                            newLike.save();
                            liked = true;
                            message.incrementLikes();
                            message.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    tvLike.setText("" + message.getLikes());
                                }
                            });

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }

        private void bindAnnouncement(Message message) {
            card.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            String title = "<b>" + message.getCustomUser().getName() + ": ";
            if (message.getTitle() != null) {
                title = title + message.getTitle() + "</b>";
            } else {
                title += "</b>";
            }
            tvTitle.setText(Html.fromHtml(title));

            // Shows or hides image depending on if message has image.
            if (message.getImage() == null) {
                ivMedia.setVisibility(View.GONE);
            } else {
                Glide.with(context)
                        .load(message.getImage().getUrl())
                        .into(ivMedia);
                ivMedia.setVisibility(View.VISIBLE);
            }
        }

        private void bindShoppingListItem(Message message) {
            card.setBackgroundColor(ContextCompat.getColor(context, R.color.shoppingList));
            ivMedia.setVisibility(View.GONE);
            String title = "<b>" + message.getCustomUser().getName()+ "</b> added <b>"+  message.getTitle() +"</b> to the shopping list";
            tvTitle.setText(Html.fromHtml(title));
        }

        private void bindPurchase(Message message) {
            String title = "<b>" + message.getCustomUser().getName() + "</b> purchased <b>" + message.getTitle() + "</b>";
            card.setBackgroundColor(ContextCompat.getColor(context, R.color.purchase));
            Glide.with(context)
                    .load(message.getImage().getUrl())
                    .into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
            tvTitle.setText(Html.fromHtml(title));
        }
    }
}

