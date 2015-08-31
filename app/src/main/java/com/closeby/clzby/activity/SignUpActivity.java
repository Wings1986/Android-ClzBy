package com.closeby.clzby.activity;


import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

public class SignUpActivity extends LoginBaseActivity {


	View viewUser, viewBusiness;

	int m_shopCenter = 0;
	int m_mainCategory = 0;
	JSONArray arrayShopCenter, arrayMainCategory;
	CustomFontEdittext etShopCenter, etMainCategory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		setTitle("Sign Up");
		
		
//		Tab tab1 = actionBar.newTab();
//		tab1.setText("Business");
//		TabListener<TabSignupBusiness> tl1 = new TabListener<TabSignupBusiness>(this, "business", TabSignupBusiness.class);
//		tab1.setTabListener(tl1);
//		actionBar.addTab(tab1);
//
//
//		Tab tab2 = actionBar.newTab();
//		tab2.setText("User");
//		TabListener<TabSignupUser> tl2 = new TabListener<TabSignupUser>(this, "user", TabSignupUser.class);
//		tab2.setTabListener(tl2);
//		actionBar.addTab(tab2);

		Button btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});


		/*
			User View
		 */
		viewUser = findViewById(R.id.viewUser);

		CustomFontButton btnBusiness = (CustomFontButton) viewUser.findViewById(R.id.btnBusiness);
		btnBusiness.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnBusiness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (viewBusiness.getVisibility() != View.VISIBLE) {
					viewBusiness.setVisibility(View.VISIBLE);
					viewUser.setVisibility(View.GONE);
				}

			}
		});

		final CustomFontEdittext etFullName = (CustomFontEdittext) viewUser.findViewById(R.id.etFullName);
		final CustomFontEdittext etEmail = (CustomFontEdittext) viewUser.findViewById(R.id.etEmail);
		final CustomFontEdittext etPassword = (CustomFontEdittext) viewUser.findViewById(R.id.etPassword);
		final CustomFontEdittext etContactNumber = (CustomFontEdittext) viewUser.findViewById(R.id.etContactNumber);

		CustomFontButton btnSubmit = (CustomFontButton) viewUser.findViewById(R.id.btnSubmit);
		btnSubmit.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String fullName = etFullName.getText().toString();
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();
				String contactNumber = etContactNumber.getText().toString();

				if (fullName.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input full name");
					return;
				}
				if (email.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input email");
					return;
				}
				if (password.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input password");
					return;
				}
				if (contactNumber.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input contact number");
					return;
				}

				requestSignupUser(fullName, email, password, contactNumber);

			}
		});

		Button btnFacebook = (Button) viewUser.findViewById(R.id.btnFacebook);
		btnFacebook.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnFacebook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				loginFacebook();

			}
		});


		/*
			Business View
		 */
		viewBusiness = findViewById(R.id.viewBusiness);
		viewBusiness.setVisibility(View.GONE);

		CustomFontButton btnUser = (CustomFontButton) viewBusiness.findViewById(R.id.btnUser);
		btnUser.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (viewUser.getVisibility() != View.VISIBLE) {
					viewUser.setVisibility(View.VISIBLE);
					viewBusiness.setVisibility(View.GONE);
				}

			}
		});

		final CustomFontEdittext etBusinessName = (CustomFontEdittext) viewBusiness.findViewById(R.id.etBusinessName);
		final CustomFontEdittext etBusinessAddress = (CustomFontEdittext) viewBusiness.findViewById(R.id.etBusinessAddress);
		final CustomFontEdittext etBusinessFullName = (CustomFontEdittext) viewBusiness.findViewById(R.id.etFullName);
		etShopCenter = (CustomFontEdittext) viewBusiness.findViewById(R.id.etShopCenter);
		etMainCategory = (CustomFontEdittext) viewBusiness.findViewById(R.id.etMainCategory);
		final CustomFontEdittext etBusinessEmail = (CustomFontEdittext) viewBusiness.findViewById(R.id.etEmail);
		final CustomFontEdittext etBusinessPassword = (CustomFontEdittext) viewBusiness.findViewById(R.id.etPassword);
		final CustomFontEdittext etBusinessContactNumber = (CustomFontEdittext) viewBusiness.findViewById(R.id.etContactNumber);

		etShopCenter.setFocusableInTouchMode(false);
		etMainCategory.setFocusableInTouchMode(false);

		etShopCenter.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

					getShopCenter();

				}

				return false;
			}
		});

		etMainCategory.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					getMainCategory();
				}
				return false;
			}
		});



		CustomFontButton btnSubmitBusiness = (CustomFontButton) viewBusiness.findViewById(R.id.btnSubmit);
		btnSubmitBusiness.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSubmitBusiness.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String businessName = etBusinessName.getText().toString();
				String businessAddress = etBusinessAddress.getText().toString();
				String fullName = etBusinessFullName.getText().toString();
				String email = etBusinessEmail.getText().toString();
				String password = etBusinessPassword.getText().toString();
				String contactNumber = etBusinessContactNumber.getText().toString();

				if (businessName.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input business name");
					return;
				}
				if (businessAddress.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input business address");
					return;
				}
				if (fullName.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input full name");
					return;
				}
				if (email.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input email");
					return;
				}
				if (password.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input password");
					return;
				}
				if (contactNumber.length() < 1) {
					DialogHelper.showToast(SignUpActivity.this, "Please input contact number");
					return;
				}
				if (arrayMainCategory == null) {
					DialogHelper.showToast(SignUpActivity.this, "Please choose main category");
					return;
				}
				if (arrayShopCenter == null) {
					DialogHelper.showToast(SignUpActivity.this, "Please choose shop center");
					return;
				}

				String shopCenterID = "";
				String mainCategoryID = "";
				try {
					shopCenterID = "" + arrayShopCenter.getJSONObject(m_shopCenter).getInt("ID");
					mainCategoryID = "" + arrayMainCategory.getJSONObject(m_mainCategory).getInt("ID");
				} catch (Exception e) {e.printStackTrace();}

				requestSignupBusiness(businessName, businessAddress, fullName, email, password, contactNumber, shopCenterID, mainCategoryID);

			}
		});


		ImageView btnSupport = (ImageView) viewBusiness.findViewById(R.id.btnSupport);
		btnSupport.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSupport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				supportEmail();

			}
		});
	}

	void getShopCenter() {

		if (arrayShopCenter == null) {
			AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
			httpClient.setMaxRetries(3);
			ParameterMap params = httpClient.newParams()
					.add("guid", Global.kGUID);



			httpClient.get("/getshoppingmalls.aspx", params, new AsyncCallback() {

				@Override
				public void onComplete(HttpResponse httpResponse) {

					try {
						JSONObject result = new JSONObject(httpResponse.getBodyAsString());

						if (result.getString("Success").equals("Success")) {

							arrayShopCenter = result.getJSONArray("Data");

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub

									openShopCenterDlg();


								}
							});
						} else {

							DialogHelper.showToast(SignUpActivity.this, result.getString("Message"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		}
		else {
			openShopCenterDlg();
		}

	}

	private void openShopCenterDlg() {

		if (arrayShopCenter == null)
			return;


		AlertDialog.Builder builder =
				new AlertDialog.Builder(this);

		final String[] choiceList = new String[arrayShopCenter.length()];

		for (int i = 0 ; i < arrayShopCenter.length() ; i ++) {
			try {
				JSONObject obj = arrayShopCenter.getJSONObject(i);

				choiceList[i] = obj.getString("Name");

			} catch (Exception e) {e.printStackTrace();}
		}



		builder.setSingleChoiceItems(
				choiceList,
				m_shopCenter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						//set to buffKey instead of selected
						//(when cancel not save to selected)
						m_shopCenter = which;
					}
				})
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
												int which) {

//								m_shopCenter = which;

								etShopCenter.setText(choiceList[m_shopCenter]);

							}
						}
				)
				.setNegativeButton("Cancel", null);

		AlertDialog alert = builder.create();
		alert.show();

	}

	void getMainCategory() {

		if (arrayMainCategory == null) {
			AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
			httpClient.setMaxRetries(3);
			ParameterMap params = httpClient.newParams()
					.add("guid", Global.kGUID);



			httpClient.get("/GetMainCategories.aspx", params, new AsyncCallback() {

				@Override
				public void onComplete(HttpResponse httpResponse) {

					try {
						JSONObject result = new JSONObject(httpResponse.getBodyAsString());

						if (result.getString("Success").equals("Success")) {

							arrayMainCategory = result.getJSONArray("Data");

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub

									openMainCategoryDlg();


								}
							});
						} else {

							DialogHelper.showToast(SignUpActivity.this, result.getString("Message"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		}
		else {
			openMainCategoryDlg();
		}

	}

	private void openMainCategoryDlg() {

		if (arrayMainCategory == null)
			return;


		AlertDialog.Builder builder =
				new AlertDialog.Builder(this);


		final String[] choiceList = new String[arrayMainCategory.length()];

		for (int i = 0 ; i < arrayMainCategory.length() ; i ++) {
			try {
				JSONObject obj = arrayMainCategory.getJSONObject(i);

				choiceList[i] = obj.getString("CategoryName");

			} catch (Exception e) {e.printStackTrace();}
		}



		builder.setSingleChoiceItems(
				choiceList,
				m_mainCategory,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						//set to buffKey instead of selected
						//(when cancel not save to selected)
						m_mainCategory = which;
					}
				})
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,
												int which) {

//								m_mainCategory = which;

								etMainCategory.setText(choiceList[m_mainCategory]);

							}
						}
				)
				.setNegativeButton("Cancel", null);

		AlertDialog alert = builder.create();
		alert.show();

	}
}
