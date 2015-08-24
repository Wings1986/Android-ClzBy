package com.closeby.clzby.fragment.profile;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.EditProfileActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.fragment.BaseFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpPost;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by iGold on 6/3/15.
 */
public class BusinessProfileFragment extends BaseFragment {


    public static BusinessProfileFragment newInstance() {
        BusinessProfileFragment fragment = new BusinessProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_business_profile, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((EditProfileActivity) getActivity()).setTitle("Business Profile");

        final CustomFontEdittext etFullName = (CustomFontEdittext) view.findViewById(R.id.etFullName);
        final CustomFontEdittext etEmail = (CustomFontEdittext) view.findViewById(R.id.etEmail);
        final CustomFontEdittext etPassword = (CustomFontEdittext) view.findViewById(R.id.etPassword);
        final CustomFontEdittext etContactNumber = (CustomFontEdittext) view.findViewById(R.id.etContactNumber);

        final ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

        CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String fullName = etFullName.getText().toString();
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

                Bitmap bitmap = ((BitmapDrawable)ivPhoto.getDrawable()).getBitmap();
                if (bitmap == null) {
                    DialogHelper.showToast(getActivity(), "Please choose image");
                    return;
                }

                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();


                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("FullName", fullName)
                        .add("EmailAddress", email)
                        .add("Password", password)
                        .add("ContactNumber", contactNumber)
                        .add("BusinessLogo", Global.base64StringForImage(bitmap))
                ;



                httpClient.post("/UpdatePersonalProfile.aspx", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        waitDialog.dismiss();


                        try {
                            final JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                            if (result.getString("Success").equals("Success")) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        try {
                                            String imageUrl = result.getString("BusinessLogoPathSmall");

                                            AppData.getInstance().storeUsername(fullName);
                                            AppData.getInstance().storeUserImageUrl(imageUrl);

                                        } catch (Exception e) {e.printStackTrace();}

                                        ((EditProfileActivity) getActivity()).addBusinessAddress();

                                    }
                                });
                            } else {

                                DialogHelper.showToast(getActivity(), result.getString("message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });



//                try {
//
//                    HttpClient httpClient = new DefaultHttpClient();
//
//                    org.apache.http.client.methods.HttpPost postMethod = new HttpPost(Global.kServerURL + "/FileUploadMultipart.aspx");
//
//
//                    String responseBody = "";
//
//
//
//                    MultipartEntityBuilder builder = new MultipartEntityBuilder().create();
//                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
//                    byte[] data = bos.toByteArray();
//
//                    ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
//
//                    builder.addPart("files", bab);
//                    builder.addPart("name", new StringBody("image"));
//                    builder.addPart("filename", new StringBody("testing.gif"));
//
//
//                    postMethod.setEntity(builder.build());
//
//                    HttpResponse httpResponse = httpClient.execute(postMethod);
//                    HttpEntity httpEntity = httpResponse.getEntity();
//                    is = httpEntity.getContent();
//
////                    responseBody = hc.execute(postMethod,res);
//
//                } catch (ClientProtocolException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }


            }
        });

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
//        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);

        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID());


        httpClient.post("/PersonalProfile.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();


                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                    if (result.getString("Success").equals("Success")) {

                        final JSONObject dicInfo = result.getJSONObject("Data");


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                try {

                                    etFullName.setText(dicInfo.getString("FullName"));
                                    etEmail.setText(dicInfo.getString("EmailAddress"));
                                    etPassword.setText(dicInfo.getString("Password"));
                                    etContactNumber.setText(dicInfo.getString("ContactNumber"));

                                    String url = Global.getURLEncoded(dicInfo.getString("BusinessLogoPath"));
                                    ImageLoader.getInstance().displayImage(url, ivPhoto);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } else {

                        DialogHelper.showToast(getActivity(), result.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }




}
