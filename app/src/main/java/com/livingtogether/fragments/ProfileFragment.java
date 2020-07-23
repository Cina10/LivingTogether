package com.livingtogether.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.client.result.TextParsedResult;
import com.livingtogether.livingtogether.R;
import com.livingtogether.models.CustomUser;

public class ProfileFragment extends Fragment {
    TextView tvName;
    TextView tvGroup;
    ImageView ivProfile;


    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvName);
        CustomUser curUser = CustomUser.queryForCurUser();

        ivProfile = view.findViewById(R.id.ivProfile);
        if (curUser.getProfilePhoto() != null) {
            Glide.with(getContext())
                    .load(curUser.getProfilePhoto().getUrl()).into(ivProfile);
        } else if(curUser.getIsFacebookUser()) {
            Glide.with(getContext())
                    .load(curUser.getPhotoUrl()).into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
        }

        tvName.setText(curUser.getName());
        tvGroup = view.findViewById(R.id.tvGroup);
        // TODO insert group specific information

    }
}