package com.closeby.clzby.activity.login;


import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;


public class TabSignupBusiness extends LoginBaseFragment {

	int m_shopCenter = 0;
	int m_mainCategory = 0;


	JSONArray arrayShopCenter, arrayMainCategory;

	CustomFontEdittext etShopCenter, etMainCategory;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		final View inflatedView = inflater.inflate(R.layout.tab_signup_business_, container, false);

//		final ScrollView scrollView = (ScrollView) inflatedView.findViewById(R.id.scrollView);
//		scrollView.setEnabled(false);

		final CustomFontEdittext etBusinessName = (CustomFontEdittext) inflatedView.findViewById(R.id.etBusinessName);
		final CustomFontEdittext etBusinessAddress = (CustomFontEdittext) inflatedView.findViewById(R.id.etBusinessAddress);
		final CustomFontEdittext etFullName = (CustomFontEdittext) inflatedView.findViewById(R.id.etFullName);
		etShopCenter = (CustomFontEdittext) inflatedView.findViewById(R.id.etShopCenter);
		etMainCategory = (CustomFontEdittext) inflatedView.findViewById(R.id.etMainCategory);
		final CustomFontEdittext etEmail = (CustomFontEdittext) inflatedView.findViewById(R.id.etEmail);
		final CustomFontEdittext etPassword = (CustomFontEdittext) inflatedView.findViewById(R.id.etPassword);
		final CustomFontEdittext etContactNumber = (CustomFontEdittext) inflatedView.findViewById(R.id.etContactNumber);

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


		
		CustomFontButton btnSubmit = (CustomFontButton) inflatedView.findViewById(R.id.btnSubmit);
		btnSubmit.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String businessName = etBusinessName.getText().toString();
				String businessAddress = etBusinessAddress.getText().toString();
				String fullName = etFullName.getText().toString();
				String email = etEmail.getText().toString();
				String password = etPassword.getText().toString();
				String contactNumber = etContactNumber.getText().toString();



				if (businessName.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input business name");
					return;
				}
				if (businessAddress.length() < 1) {
                    DialogHelper.showToast(getActivity(), "Please input business address");
					return;
				}
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
				if (arrayMainCategory == null) {
					DialogHelper.showToast(getActivity(), "Please choose main category");
					return;
				}
				if (arrayShopCenter == null) {
					DialogHelper.showToast(getActivity(), "Please choose shop center");
					return;
				}

				String shopCenterID = "";
				String mainCategoryID = "";
				try {
					shopCenterID = "" + arrayShopCenter.getJSONObject(m_shopCenter).getInt("ID");
					mainCategoryID = "" + arrayMainCategory.getJSONObject(m_mainCategory).getInt("ID");
				} catch (Exception e) {e.printStackTrace();}

				final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
				waitDialog.show();

				AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
				httpClient.setMaxRetries(3);
				ParameterMap params = httpClient.newParams()
						.add("guid", Global.kGUID)
						.add("BusinessName", businessName)
						.add("BusinessAddress", businessAddress)
						.add("YourName", fullName)
						.add("SID", shopCenterID)
						.add("MainCategoryID", mainCategoryID)
						.add("Email", email)
						.add("Pass", password)
						.add("ContactNumber", contactNumber);


				httpClient.get("/AddBusiness.aspx", params, new AsyncCallback() {

					@Override
					public void onComplete(HttpResponse httpResponse) {

						waitDialog.dismiss();

						try {
							JSONObject result = new JSONObject(httpResponse.getBodyAsString());

							if (!result.getString("Success").equalsIgnoreCase("FAIL")) {
								AppData.getInstance().saveLoginUserID(result.getJSONObject("Data").getString("UserID"));
								AppData.getInstance().setBusiness(result.getJSONObject("Data").getBoolean("IsBusiness"));
								AppData.getInstance().storeUsername(result.getJSONObject("Data").getString("FullName"));

								DialogHelper.getDialog(getActivity(), "", result.getString("Success"), "OK", null, new DialogCallBack() {
									@Override
									public void onClick(int which) {
										if (which == 0) {
											getActivity().runOnUiThread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													Intent intent = new Intent(getActivity(), HomeActivity.class);
													intent.putExtra("first", true);
													startActivity(intent);
													getActivity().finish();
												}
											});
										}
									}
								}).show();

							} else {

								DialogHelper.showToast(getActivity(), result.getString("message"));
							}
						}catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

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
		
		return inflatedView;
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

							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub


									openShopCenterDlg();


								}
							});
						} else {

							DialogHelper.showToast(getActivity(), result.getString("Message"));
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
				new AlertDialog.Builder(getActivity());

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

							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub


									openMainCategoryDlg();


								}
							});
						} else {

							DialogHelper.showToast(getActivity(), result.getString("Message"));
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
				new AlertDialog.Builder(getActivity());


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
