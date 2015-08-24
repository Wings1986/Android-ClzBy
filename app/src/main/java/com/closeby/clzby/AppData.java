package com.closeby.clzby;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by iGold on 22.02.15.
 */
public class AppData {
    public static final String PREFERENCES = "preferences";
    
    public static final String KEY_LOGIN_USERID = "login_userid";
    public static final String KEY_IS_BUSINESS = "is_business";
    public static final String KEY_LOGIN_USERNAME = "login_user_name";
    public static final String KEY_LOGIN_USER_IMAGE = "login_user_image";

    public Context context;

    private static AppData instance;

    public  static int RANGE = 500;



    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    
    public void init(Context context) {
        this.context = context;
    }


    public String loadLoginUserID() {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (sp.contains(KEY_LOGIN_USERID)) {
            return sp.getString(KEY_LOGIN_USERID, "");
        }
        return "";
    }
    public void saveLoginUserID(String userID) {
    	SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_LOGIN_USERID, userID);
        edit.commit();
    }
    
    public boolean isBusiness() {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (sp.contains(KEY_LOGIN_USERID)) {
        	return sp.getBoolean(KEY_IS_BUSINESS, false);
        }
        return false;
    }
    public void setBusiness(boolean isBusiness) {
    	SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(KEY_IS_BUSINESS, isBusiness);
        edit.commit();
    }

    public String loadUsername() {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (sp.contains(KEY_LOGIN_USERNAME)) {
            return sp.getString(KEY_LOGIN_USERNAME, "");
        }
        return "";
    }
    public void storeUsername(String userName) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_LOGIN_USERNAME, userName);
        edit.commit();
    }

    public String loadUserImageUrl() {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (sp.contains(KEY_LOGIN_USER_IMAGE)) {
            return sp.getString(KEY_LOGIN_USER_IMAGE, "");
        }
        return "";
    }
    public void storeUserImageUrl(String userName) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_LOGIN_USER_IMAGE, userName);
        edit.commit();
    }
}
