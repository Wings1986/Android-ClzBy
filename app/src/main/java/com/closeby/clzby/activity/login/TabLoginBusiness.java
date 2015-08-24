package com.closeby.clzby.activity.login;


import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.closeby.clzby.R;
import com.closeby.clzby.activity.SignUpActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogHelper;


public class TabLoginBusiness extends LoginBaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		final View inflatedView = inflater.inflate(R.layout.tab_login_business, container, false);
		
		final CustomFontEdittext etEmail = (CustomFontEdittext) inflatedView.findViewById(R.id.etEmail);
		final CustomFontEdittext etPassword = (CustomFontEdittext) inflatedView.findViewById(R.id.etPassword);
		
		CustomFontButton btnLogin = (CustomFontButton) inflatedView.findViewById(R.id.btnLogin);
		btnLogin.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();

				if (email.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input email");
					return;
				}
				if (password.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input password");
					return;
				}
				
				requestLogin(email, password);

			}
		});

		CustomFontButton btnRegister = (CustomFontButton) inflatedView.findViewById(R.id.btnRegister);
		btnRegister.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), SignUpActivity.class);
				startActivity(intent);
			}
		});

		Button btnSupport = (Button) inflatedView.findViewById(R.id.btnSupport);
		btnSupport.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSupport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				supportEmail();

			}
		});


		etEmail.setText("marmalade@gmail.com");
		etPassword.setText("george69");

		return inflatedView;
	}



}
