package com.closeby.clzby.activity;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.closeby.clzby.R;


/**
 * Created by Stone on 11/28/2014.
 */
@SuppressLint("InflateParams") 
public class BaseActivity extends AppCompatActivity
{
    public static final int LEFT_BTN_NONE = 0;
    public static final int LEFT_BTN_BACK = 1;
    public static final int LEFT_BTN_MENU = 2;


    ActionBar actionBar;
    TextView actionBarTitleTV = null;

    Button btnBack, btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


//        actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle("adfdfsaf");
//        actionBar.setBackgroundDrawable(new ColorDrawable(0xffff0000));


        LayoutInflater inflater = LayoutInflater.from(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        View v = inflater.inflate(R.layout.actionbar_custom_title, null);

        actionBarTitleTV = (TextView) v.findViewById(R.id.actionbar_title_text_view);
        //actionBarTitleTV.setText(this.getTitle());
        actionBarTitleTV.setText("sfsdfsdf");


        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setLogo(null);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00a3e8")));

        actionBar.setCustomView(v, params); // set custom view here
        
    }
    
    public void setTitle(String title) {
        super.setTitle(title);
        actionBarTitleTV.setText(title);
    }
    


    public final boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final boolean isValidPhonenumber(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }
}
