package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.livingtogether.fragments.MessageBoardFragment;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Like;
import com.livingtogether.models.Message;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class MessageDetailActivity extends AppCompatActivity {
    public static final String TAG = "MessageDetailActivity";
    private ImageView ivProfile;
    private TextView tvTitle;
    private TextView tvBody;
    private ImageView ivMedia;
    private TextView tvTime;
    private TextView tvLikeDescription;
    private ImageView ivLike;
    private RecyclerView rvComments;
    private EditText etComment;
    private ImageButton btSend;
    private Message message;
    private RelativeLayout messageWrapper;
    protected ImageView ivExit;
    private boolean liked;
    private int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        Log.i(TAG, "onCreate");
        ivProfile = findViewById(R.id.ivProfile);
        tvTitle = findViewById(R.id.tvTitle);
        tvBody = findViewById(R.id.tvBody);
        ivMedia = findViewById(R.id.ivMedia);
        tvTime = findViewById(R.id.tvTime);
        tvLikeDescription = findViewById(R.id.tvLike);
        ivLike = findViewById(R.id.ivLike);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btSend = findViewById(R.id.btSend);
        messageWrapper = findViewById(R.id.messageWrapper);
        ivExit = findViewById(R.id.ivExit);
        message = (Message) Parcels.unwrap(getIntent().getParcelableExtra(MessageBoardFragment.MESSAGE));

        if (message.getBody().equals("")) {
            tvBody.setVisibility(View.GONE);
        } else {
            tvBody.setVisibility(View.VISIBLE);
            tvBody.setText(Html.fromHtml(message.getBody()));
        }

        CustomUser customUser = null;
        try {
            customUser = message.getCustomUser().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (customUser.getProfilePhoto() != null) {
            Glide.with(this)
                    .load(message.getCustomUser().getProfilePhoto().getUrl()).into(ivProfile);
        } else if (message.getCustomUser().getIsFacebookUser()) {
            Glide.with(this)
                    .load(message.getCustomUser().getPhotoUrl()).into(ivProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.com_facebook_profile_picture_blank_portrait).into(ivProfile);
        }

        tvTime.setText(message.getRelativeTime());

        if (message.getType().equals(Message.MessageType.ANNOUNCEMENT.toString())) {
            onCreateAnnouncement(message);
        } else if (message.getType().equals(Message.MessageType.SHOPPING_LIST_ITEM.toString())) {
            onCreateShoppingListItem(message);
        } else if (message.getType().equals(Message.MessageType.PURCHASE.toString()))
            onCreatePurchase(message);

        // TODO double tap to like, comments, submit comment
        likes = message.getLikes();
        tvLikeDescription.setText("" + likes);
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
                                tvLikeDescription.setText("" + message.getLikes());
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
                                tvLikeDescription.setText("" + message.getLikes());
                            }
                        });

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        int position = getIntent().getExtras().getInt("position");
        Intent i = new Intent();
        i.putExtra(MessageBoardFragment.MESSAGE, Parcels.wrap(message));
        i.putExtra(MessageBoardFragment.POSITION, position);
        setResult(RESULT_OK, i);
        finish();

    }

    private void onCreateAnnouncement(Message message) {
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
            Glide.with(this)
                    .load(message.getImage().getUrl())
                    .into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
        }
    }

    private void onCreateShoppingListItem(Message message) {
        messageWrapper.setBackgroundColor(getResources().getColor(R.color.shoppingList));
        ivMedia.setVisibility(View.GONE);
        String title = "<b>" + message.getCustomUser().getName()+ "</b> added <b>"+  message.getTitle() +"</b> to the shopping list";
        tvTitle.setText(Html.fromHtml(title));
    }

    private void onCreatePurchase(Message message) {
        messageWrapper.setBackgroundColor(getResources().getColor(R.color.purchase));
        String title = "<b>" + message.getCustomUser().getName() + "</b> purchased <b>" + message.getTitle() + "</b>";
        Glide.with(this)
                .load(message.getImage().getUrl())
                .into(ivMedia);
        ivMedia.setVisibility(View.VISIBLE);
        tvTitle.setText(Html.fromHtml(title));
    }
}

