package com.closeby.clzby.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogHelper;

public class LoginActivity extends LoginBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		setTitle("Log In");
		
//		Tab tab1 = actionBar.newTab();
//		tab1.setText("BUSINESS");
//		TabListener<TabLoginBusiness> tl1 = new TabListener<TabLoginBusiness>(this, "BUSINESS", TabLoginBusiness.class);
//		tab1.setTabListener(tl1);
//		actionBar.addTab(tab1);
//
//
//		Tab tab2 = actionBar.newTab();
//		tab2.setText("USER");
//		TabListener<TabLoginUser> tl2 = new TabListener<TabLoginUser>(this, "USER", TabLoginUser.class);
//		tab2.setTabListener(tl2);
//		actionBar.addTab(tab2);

		final CustomFontEdittext etEmail = (CustomFontEdittext) findViewById(R.id.etEmail);
		final CustomFontEdittext etPassword = (CustomFontEdittext) findViewById(R.id.etPassword);

		etEmail.setText("marmalade@gmail.com");
		etPassword.setText("george69");

		CustomFontButton btnLogin = (CustomFontButton) findViewById(R.id.btnLogin);
		btnLogin.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();

				if (email.length() < 1) {
					DialogHelper.showToast(LoginActivity.this, "Please input email");
					return;
				}
				if (password.length() < 1) {
					DialogHelper.showToast(LoginActivity.this, "Please input password");
					return;
				}

				requestLogin(email, password);

			}
		});

		CustomFontButton btnRegister = (CustomFontButton) findViewById(R.id.btnRegister);
		btnRegister.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});

		Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
		btnFacebook.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnFacebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				loginFacebook();

			}
		});

		CustomFontButton btnSkip = (CustomFontButton) findViewById(R.id.btnSkip);
		btnSkip.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSkip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				requestSkip();
			}
		});

	}

}
