package com.nickole.earworld.personcenter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickole.earworld.R;
import com.nickole.earworld.main.ActivityCollector;
import com.nickole.earworld.main.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author shuzijie
 */
public class PersonDetailActivity extends BaseActivity {

    @BindView(R.id.my_head_image)
    CircleImageView myHeadImage;
    @BindView(R.id.my_nickname)
    TextView myNickname;
    @BindView(R.id.my_sex)
    ImageView mySex;
    @BindView(R.id.my_personalized_signature)
    TextView myPersonalizedSignature;
    @BindView(R.id.write_personalized_signature)
    ImageView writePersonalizedSignature;
    @BindView(R.id.my_information)
    Button myInformation;
    @BindView(R.id.my_job)
    Button myJob;
    @BindView(R.id.my_company)
    Button myCompany;
    @BindView(R.id.my_school)
    Button mySchool;
    @BindView(R.id.my_phone)
    Button myPhone;
    @BindView(R.id.my_wechat)
    Button myWechat;
    @BindView(R.id.my_email)
    Button myEmail;
    @BindView(R.id.my_information_go_back)
    Button myInformationGoBack;
    @BindView(R.id.modify_my_information_btn)
    Button modifyMyInformationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        ButterKnife.bind(this);

        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews() {
        initIconButton(myInformation, R.drawable.icon_my_information);
        initIconButton(myJob, R.drawable.icon_job);
        initIconButton(myCompany, R.drawable.icon_company);
        initIconButton(mySchool, R.drawable.icon_school);
        initIconButton(myPhone, R.drawable.icon_phone);
        initIconButton(myWechat, R.drawable.icon_wechat);
        initIconButton(myEmail, R.drawable.icon_email);
    }

    private void initIconButton(Button button, int id) {
        Drawable drawable0 = getResources().getDrawable(id);
        drawable0.setBounds(0, 0, 64, 64);
        button.setCompoundDrawables(drawable0, null, null, null);
    }

    @OnClick({R.id.my_head_image, R.id.write_personalized_signature, R.id.my_information, R.id.my_job, R.id.my_company, R.id.my_school, R.id.my_phone, R.id.my_wechat, R.id.my_email,R.id.my_information_go_back,R.id.modify_my_information_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_head_image:
                break;
            case R.id.write_personalized_signature:
                break;
            case R.id.my_information:
                break;
            case R.id.my_job:
                break;
            case R.id.my_company:
                break;
            case R.id.my_school:
                break;
            case R.id.my_phone:
                break;
            case R.id.my_wechat:
                break;
            case R.id.my_email:
                break;
            case R.id.my_information_go_back:
                finish();
                break;
            case R.id.modify_my_information_btn:
                break;
            default:
        }
    }
}
