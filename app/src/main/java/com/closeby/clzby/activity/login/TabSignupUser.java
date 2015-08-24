package com.closeby.clzby.activity.login;


import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;


public class TabSignupUser extends LoginBaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		final View inflatedView = inflater.inflate(R.layout.tab_signup_user_, container, false);
		
		final CustomFontEdittext etFullName = (CustomFontEdittext) inflatedView.findViewById(R.id.etFullName);
		final CustomFontEdittext etEmail = (CustomFontEdittext) inflatedView.findViewById(R.id.etEmail);
		final CustomFontEdittext etPassword = (CustomFontEdittext) inflatedView.findViewById(R.id.etPassword);
		final CustomFontEdittext etContactNumber = (CustomFontEdittext) inflatedView.findViewById(R.id.etContactNumber);
		
		final LinearLayout layoutFirst = (LinearLayout) inflatedView.findViewById(R.id.layoutFirst);
		final LinearLayout layoutSecond = (LinearLayout) inflatedView.findViewById(R.id.layoutSecond);
		layoutFirst.setVisibility(View.VISIBLE);
		layoutSecond.setVisibility(View.GONE);
		
		CustomFontButton btnSubmit = (CustomFontButton) inflatedView.findViewById(R.id.btnSubmit);
		btnSubmit.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String fullName = etFullName.getText().toString();
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();
				String contactNumber = etContactNumber.getText().toString();
				
				if (fullName.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input full name");
					return;
				}
				if (email.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input email");
					return;
				}
				if (password.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input password");
					return;
				}
				if (contactNumber.length() < 1) {
					DialogHelper.showToast(getActivity(), "Please input contact number");
					return;
				}


				final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
				waitDialog.show();

				AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
				httpClient.setMaxRetries(3);
				ParameterMap params = httpClient.newParams()
						.add("guid", Global.kGUID)
						.add("FullName", fullName)
						.add("EmailAddress", email)
						.add("password", password)
						.add("ContactNumber", contactNumber);


				httpClient.get("/AddManualUser.aspx", params, new AsyncCallback() {

					@Override
					public void onComplete(HttpResponse httpResponse) {

						waitDialog.dismiss();

						try {
							JSONObject result = new JSONObject(httpResponse.getBodyAsString());

							if (result.getString("Success").equals("Success")) {
								AppData.getInstance().saveLoginUserID(result.getJSONObject("Data").getString("UserID"));
								AppData.getInstance().setBusiness(result.getJSONObject("Data").getBoolean("IsBusiness"));
								AppData.getInstance().storeUsername(result.getJSONObject("Data").getString("FullName"));

								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Intent intent = new Intent(getActivity(), HomeActivity.class);
										startActivity(intent);
										getActivity().finish();
									}
								});
							} else {

								DialogHelper.showToast(getActivity(), result.getString("Message"));
							}
						}catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			}
		});
		
		Button btnGoto = (Button) inflatedView.findViewById(R.id.btnGoto);
		btnGoto.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnGoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutFirst.setVisibility(View.GONE);
				layoutSecond.setVisibility(View.VISIBLE);
			}
		});
		
		Button btnFacebook = (Button) inflatedView.findViewById(R.id.btnFacebook);
		btnFacebook.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				loginFacebook();

			}
		});
		
		
		return inflatedView;
	}



}
