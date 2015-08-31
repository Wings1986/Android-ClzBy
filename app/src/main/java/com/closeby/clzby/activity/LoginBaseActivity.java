package com.closeby.clzby.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.closeby.clzby.AppData;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by iGold on 6/2/15.
 */
public class LoginBaseActivity extends Activity {


    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");

                        final AccessToken accessToken = loginResult.getAccessToken();
                        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                reqeustFacebookLogin(user.optString("email"), accessToken.getToken());

                            }
                        }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginBaseActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginBaseActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loginFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));

    }

    private void reqeustFacebookLogin(String email, String accessToken) {

        final Dialog waitDialog = DialogHelper.getWaitDialog(LoginBaseActivity.this);
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("email", email)
                .add("password", "123456")
                .add("facebookid", accessToken)
                ;


        httpClient.get("/AddUser.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {
                        AppData.getInstance().saveLoginUserID(result.getJSONObject("Data").getString("UserID"));
                        AppData.getInstance().setBusiness(result.getJSONObject("Data").getBoolean("IsBusiness"));
                        AppData.getInstance().storeUsername(result.getJSONObject("Data").getString("FullName"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(LoginBaseActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {

                        DialogHelper.showToast(LoginBaseActivity.this, result.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void requestLogin(String email, String password) {

        final Dialog waitDialog = DialogHelper.getWaitDialog(LoginBaseActivity.this);
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("email", email)
                .add("password", password)
                .add("DeviceID", getMacAddress());


        httpClient.get("/ManualLogin.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {
                        AppData.getInstance().saveLoginUserID(result.getJSONObject("Data").getString("UserID"));
                        AppData.getInstance().setBusiness(result.getJSONObject("Data").getBoolean("IsBusiness"));
                        AppData.getInstance().storeUsername(result.getJSONObject("Data").getString("FullName"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(LoginBaseActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {

                        DialogHelper.showToast(LoginBaseActivity.this, result.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void requestSkip() {

        final Dialog waitDialog = DialogHelper.getWaitDialog(LoginBaseActivity.this);
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("Deviceid", getMacAddress());



        httpClient.get("/SkipUser.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {
                        AppData.getInstance().saveLoginUserID(result.getJSONObject("Data").getString("UserID"));
                        AppData.getInstance().setBusiness(result.getJSONObject("Data").getBoolean("IsBusiness"));
                        AppData.getInstance().storeUsername(result.getJSONObject("Data").getString("FullName"));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(LoginBaseActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {

                        DialogHelper.showToast(LoginBaseActivity.this, result.getString("message"));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void requestSignupUser(String fullName, String email, String password, String contactNumber) {

        final Dialog waitDialog = DialogHelper.getWaitDialog(this);
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("FullName", fullName)
                .add("EmailAddress", email)
                .add("password", password)
                .add("ContactNumber", contactNumber)
                .add("DeviceID", getMacAddress());


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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(LoginBaseActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {

                        DialogHelper.showToast(LoginBaseActivity.this, result.getString("Message"));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void requestSignupBusiness(String businessName, String businessAddress, String fullName, String email, String password, String contactNumber,
                                       String shopCenterID, String mainCategoryID) {

        final Dialog waitDialog = DialogHelper.getWaitDialog(this);
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
                .add("ContactNumber", contactNumber)
                .add("DeviceID", getMacAddress());


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

                        DialogHelper.getDialog(LoginBaseActivity.this, "", result.getString("Success"), "OK", null, new DialogCallBack() {
                            @Override
                            public void onClick(int which) {
                                if (which == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            Intent intent = new Intent(LoginBaseActivity.this, HomeActivity.class);
                                            intent.putExtra("first", true);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }
                        }).show();

                    } else {

                        DialogHelper.showToast(LoginBaseActivity.this, result.getString("message"));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String getMacAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String mac = wInfo.getMacAddress();
        return mac;
    }

    public void supportEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "support@clzby.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
