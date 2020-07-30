package com.livingtogether.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.TouchTypeDetector;
import com.livingtogether.OnDoubleTapListener;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;
import com.parse.ParseException;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        ivProfile = findViewById(R.id.ivProfile);
        tvTitle = findViewById(R.id.tvTitle);
        tvBody = findViewById(R.id.tvBody);
        ivMedia = findViewById(R.id.ivMedia);
        tvTime = findViewById(R.id.tvTime);
        tvLikeDescription = findViewById(R.id.tvLikeDescription);
        ivLike = findViewById(R.id.ivLike);
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btSend = findViewById(R.id.btSend);
        messageWrapper = findViewById(R.id.messageWrapper);
        message = (Message) Parcels.unwrap(getIntent().getParcelableExtra(Message.class.getSimpleName()));

        if (message.getBody().equals("")) {
            tvBody.setVisibility(View.GONE);
        } else {
            tvBody.setVisibility(View.VISIBLE);
            tvBody.setText(message.getBody());
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
        // TODO likes, double tap to like, comments, submit comment


        messageWrapper.setOnTouchListener(new OnDoubleTapListener(this) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                Toast.makeText(MessageDetailActivity.this, "Double Tap", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i(TAG, "onTouch");

//        messageWrapper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MessageDetailActivity.this, "send OnClick", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void onCreateAnnouncement(Message message) {
        String title = message.getCustomUser().getName() + ": ";
        if (message.getTitle() != null) {
            title = title + message.getTitle();
        }
        tvTitle.setText(title);

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
        ivMedia.setVisibility(View.GONE);
        String title = message.getTitle() + " added to the shopping list";
        tvTitle.setText(title);
    }

    private void onCreatePurchase(Message message) {
        String title = message.getCustomUser().getName() + " purchased " + message.getTitle();
        Glide.with(this)
                .load(message.getImage().getUrl())
                .into(ivMedia);
        ivMedia.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }
}

