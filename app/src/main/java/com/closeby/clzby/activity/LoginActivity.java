package com.closeby.clzby.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar.Tab;

import com.closeby.clzby.R;
import com.closeby.clzby.activity.login.TabListener;
import com.closeby.clzby.activity.login.TabLoginBusiness;
import com.closeby.clzby.activity.login.TabLoginUser;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		setTitle("Log In");
		
		Tab tab1 = actionBar.newTab();
		tab1.setText("BUSINESS");
		TabListener<TabLoginBusiness> tl1 = new TabListener<TabLoginBusiness>(this, "BUSINESS", TabLoginBusiness.class);
		tab1.setTabListener(tl1);
		actionBar.addTab(tab1);
		

		Tab tab2 = actionBar.newTab();
		tab2.setText("USER");
		TabListener<TabLoginUser> tl2 = new TabListener<TabLoginUser>(this, "USER", TabLoginUser.class);
		tab2.setTabListener(tl2);
		actionBar.addTab(tab2);
	}

}
