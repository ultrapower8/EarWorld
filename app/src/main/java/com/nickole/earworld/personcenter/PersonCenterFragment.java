package com.nickole.earworld.personcenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nickole.earworld.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonCenterFragment extends Fragment {

    @BindView(R.id.setting_button)
    Button settingButton;
    @BindView(R.id.user_head_image)
    CircleImageView userHeadImage;
    @BindView(R.id.nickname_text_view)
    TextView nicknameTextView;
    @BindView(R.id.sex)
    ImageView sex;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.my_like_button)
    Button myLikeButton;
    @BindView(R.id.my_speech_button)
    Button mySpeechButton;
    @BindView(R.id.my_wallet_button)
    Button myWalletButton;
    @BindView(R.id.my_listen_record_button)
    Button myListenRecordButton;
    @BindView(R.id.server_button)
    Button serverButton;
    @BindView(R.id.open_platform_button)
    Button openPlatformButton;
    @BindView(R.id.contact_us_button)
    Button contactUsButton;
    Unbinder unbinder;
    @BindView(R.id.my_information_layout)
    RelativeLayout myInformationLayout;

    public PersonCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_center, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((MainActivity) getActivity()).updateMainDownBar();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (!hidden)
//            ((MainActivity) getActivity()).updateMainDownBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.my_information_layout})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.my_information_layout) {
            Intent intent = new Intent(getContext(), PersonDetailActivity.class);
            getActivity().startActivityForResult(intent, 1);
        }
    }

    private void initView(){
        initIconButton(myLikeButton, R.drawable.icon_like);
        initIconButton(mySpeechButton, R.drawable.icon_my_speech);
        initIconButton(myWalletButton, R.drawable.icon_wallet);
        initIconButton(myListenRecordButton, R.drawable.icon_voice_record);
        initIconButton(serverButton, R.drawable.icon_voice_server);
        initIconButton(openPlatformButton, R.drawable.icon_open_platform);
        initIconButton(contactUsButton, R.drawable.icon_question);
    }

    private void initIconButton(Button button, int id) {
        Drawable drawable0 = getResources().getDrawable(id);
        Drawable drawable1 = getResources().getDrawable(R.drawable.icon_to_right);
        drawable0.setBounds(0, 0, 64, 64);
        drawable1.setBounds(0, 0, 48, 48);
        button.setCompoundDrawables(drawable0, null, drawable1, null);
    }

}
