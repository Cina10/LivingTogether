package com.livingtogether.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;
import com.livingtogether.models.Message;
import com.livingtogether.models.PinnedMessages;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.List;

public class ReceiptComposeActivity extends ComposeActivity implements View.OnClickListener {
    private TextView tvPage;
    private Message itemMessage;
    private EditText etCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_compose);
        tvPage = findViewById(R.id.tvPage);
        ivPreview = findViewById(R.id.ivPreview);
        etCost = findViewById(R.id.etCost);
        btTakePicture = findViewById(R.id.btTakePicture);
        btTakePicture.setOnClickListener(this);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(this);
        itemMessage = (Message) Parcels.unwrap(getIntent().getParcelableExtra(Message.class.getSimpleName()));
        tvPage.setText(itemMessage.getTitle());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSubmit:
                // TODO show preview first?
                submit();
                break;
            case R.id.btTakePicture:
                launchCamera();
                break;
            default:
                break;
        }
    }

    private void submit() {
        String title = itemMessage.getTitle();
        double cost = (double) Math.round(Double.parseDouble(etCost.getText().toString()) * 100) / 100;
        NumberFormat defaultFormat = NumberFormat.getCurrencyInstance();
        String strCost = defaultFormat.format(cost);
        String body = "It cost " + strCost + ". ";
        // TODO change who it charges to who likes/"dittos" the post
        Drawable preview = ivPreview.getDrawable();
        if (preview == null) {
            Toast.makeText(this, "Please submit a receipt!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            CustomUser curUser = CustomUser.queryForCurUser();
            if (itemMessage.getCustomUser() != curUser) {
                itemMessage.getCustomUser().addOwed(cost);
                itemMessage.getCustomUser().saveInBackground();

                curUser.addLent(cost);
                curUser.saveInBackground();
                body += "<b>" + itemMessage.getCustomUser().getName() + "</b> was charged.";

                Message message = new Message();
                message.setTitle(title);
                message.setBody(body);
                message.setCustomUser(curUser);
                message.setType(Message.MessageType.PURCHASE.toString());
                message.setImage(new ParseFile(photoFile));
                message.setLikes(0);
                message.setGroup(curUser.getCurGroup());
                deletePinned(itemMessage);
                itemMessage.deleteInBackground();
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(ReceiptComposeActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
    }

    private void deletePinned(Message message) {
        ParseQuery query = ParseQuery.getQuery(PinnedMessages.class);
        query.whereEqualTo(PinnedMessages.KEY_MESSAGE, message);
        query.findInBackground(new FindCallback<PinnedMessages>() {
            @Override
            public void done(List<PinnedMessages> pinned, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with querying for related pins", e);
                    return;
                }
                for(PinnedMessages pin: pinned) {
                    pin.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.e(TAG, "Error deleting pins", e);
                            }
                        }
                    });
                }
            }
        });
    }
}