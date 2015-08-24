package com.closeby.clzby.fragment.profile;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.EditProfileActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.fragment.BaseFragment;
import com.closeby.clzby.fragment.DashboardFragment;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iGold on 6/3/15.
 */
public class BusinessTypeFragment extends BaseFragment {

    int m_nCurrentType = 0;

    public static BusinessTypeFragment newInstance() {
        BusinessTypeFragment fragment = new BusinessTypeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_business_type, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((EditProfileActivity) getActivity()).setTitle("Business Type");

        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioServices:
                        m_nCurrentType = 0;
                        break;
                    case R.id.radioProducts:
                        m_nCurrentType = 1;
                        break;
                    case R.id.radioBoth:
                        m_nCurrentType = 2;
                        break;
                    case R.id.radioNone:
                        m_nCurrentType = 3;
                        break;
                }
            }
        });

        CustomFontButton btnUpdate = (CustomFontButton) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                waitDialog.show();

                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);

                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("userid", AppData.getInstance().loadLoginUserID())
                        .add("NewBusinessType", "" + m_nCurrentType)
                ;


                httpClient.get("/UpdateBusinessType.aspx", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        waitDialog.dismiss();


                        try {
                            JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                            if (result.getString("Success").equals("Success")) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        ((EditProfileActivity) getActivity()).addBusinessDescription();

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
        });

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
//        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);

        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("userid", AppData.getInstance().loadLoginUserID());


        httpClient.get("/BusinessType.aspx", params, new AsyncCallback() {

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
                                    m_nCurrentType = dicInfo.getInt("BusinessTypeID");

                                    int resID = 0;
                                    switch (m_nCurrentType) {
                                        case 0:
                                            resID = R.id.radioServices; break;
                                        case 1:
                                            resID = R.id.radioProducts; break;
                                        case 2:
                                            resID = R.id.radioBoth; break;
                                        default:
                                            resID = R.id.radioNone; break;
                                    }

                                    RadioButton radioButton = (RadioButton) view.findViewById(resID);
                                    radioButton.setChecked(true);

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
