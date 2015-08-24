package com.closeby.clzby.activity;


import com.closeby.clzby.R;
import com.closeby.clzby.activity.login.TabListener;
import com.closeby.clzby.activity.login.TabSignupBusiness;
import com.closeby.clzby.activity.login.TabSignupUser;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar.Tab;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		setTitle("Sign Up");
		
		
		Tab tab1 = actionBar.newTab();
		tab1.setText("Business");
		
		TabListener<TabSignupBusiness> tl1 = new TabListener<TabSignupBusiness>(this, "business", TabSignupBusiness.class);
		tab1.setTabListener(tl1);
		actionBar.addTab(tab1);
		

		Tab tab2 = actionBar.newTab();
		tab2.setText("User");
		TabListener<TabSignupUser> tl2 = new TabListener<TabSignupUser>(this, "user", TabSignupUser.class);
		tab2.setTabListener(tl2);
		actionBar.addTab(tab2);

	}


}
