package com.closeby.clzby.fragment.profile;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.EditProfileActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontEdittext;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.fragment.BaseFragment;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by iGold on 6/3/15.
 */
public class BusinessOperateFragment extends BaseFragment {

    public static BusinessOperateFragment newInstance() {
        BusinessOperateFragment fragment = new BusinessOperateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_business_operate_hour, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((EditProfileActivity) getActivity()).setTitle("Operate Hour");

        final CustomFontTextView lbMonStartTime = (CustomFontTextView) view.findViewById(R.id.lbMonStartTime);
        lbMonStartTime.setOnTouchListener(new CustomEditTouchListener(lbMonStartTime));

        final CustomFontTextView lbMonEndTime = (CustomFontTextView) view.findViewById(R.id.lbMonEndTime);
        lbMonEndTime.setOnTouchListener(new CustomEditTouchListener(lbMonEndTime));

        final CustomFontTextView lbTueStartTime = (CustomFontTextView) view.findViewById(R.id.lbTueStartTime);
        lbTueStartTime.setOnTouchListener(new CustomEditTouchListener(lbTueStartTime));

        final CustomFontTextView lbTueEndTime = (CustomFontTextView) view.findViewById(R.id.lbTueEndTime);
        lbTueEndTime.setOnTouchListener(new CustomEditTouchListener(lbTueEndTime));

        final CustomFontTextView lbWedStartTime = (CustomFontTextView) view.findViewById(R.id.lbWedStartTime);
        lbWedStartTime.setOnTouchListener(new CustomEditTouchListener(lbWedStartTime));

        final CustomFontTextView lbWedEndTime = (CustomFontTextView) view.findViewById(R.id.lbWedEndTime);
        lbWedEndTime.setOnTouchListener(new CustomEditTouchListener(lbWedEndTime));

        final CustomFontTextView lbThuStartTime = (CustomFontTextView) view.findViewById(R.id.lbThuStartTime);
        lbThuStartTime.setOnTouchListener(new CustomEditTouchListener(lbThuStartTime));

        final CustomFontTextView lbThuEndTime = (CustomFontTextView) view.findViewById(R.id.lbThuEndTime);
        lbThuEndTime.setOnTouchListener(new CustomEditTouchListener(lbThuEndTime));

        final CustomFontTextView lbFriStartTime = (CustomFontTextView) view.findViewById(R.id.lbFriStartTime);
        lbFriStartTime.setOnTouchListener(new CustomEditTouchListener(lbFriStartTime));

        final CustomFontTextView lbFriEndTime = (CustomFontTextView) view.findViewById(R.id.lbFriEndTime);
        lbFriEndTime.setOnTouchListener(new CustomEditTouchListener(lbFriEndTime));

        final CustomFontTextView lbSatStartTime = (CustomFontTextView) view.findViewById(R.id.lbSatStartTime);
        lbSatStartTime.setOnTouchListener(new CustomEditTouchListener(lbSatStartTime));

        final CustomFontTextView lbSatEndTime = (CustomFontTextView) view.findViewById(R.id.lbSatEndTime);
        lbSatEndTime.setOnTouchListener(new CustomEditTouchListener(lbSatEndTime));

        final CustomFontTextView lbSunStartTime = (CustomFontTextView) view.findViewById(R.id.lbSunStartTime);
        lbSunStartTime.setOnTouchListener(new CustomEditTouchListener(lbSunStartTime));

        final CustomFontTextView lbSunEndTime = (CustomFontTextView) view.findViewById(R.id.lbSunEndTime);
        lbSunEndTime.setOnTouchListener(new CustomEditTouchListener(lbSunEndTime));


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
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("StartTime1", lbMonStartTime.getText().toString())
                        .add("EndTime1", lbMonEndTime.getText().toString())
                        .add("StartTime2", lbTueStartTime.getText().toString())
                        .add("EndTime2", lbTueEndTime.getText().toString())
                        .add("StartTime3", lbWedStartTime.getText().toString())
                        .add("EndTime3", lbWedEndTime.getText().toString())
                        .add("StartTime4", lbTueStartTime.getText().toString())
                        .add("EndTime4", lbTueEndTime.getText().toString())
                        .add("StartTime5", lbFriStartTime.getText().toString())
                        .add("EndTime5", lbFriEndTime.getText().toString())
                        .add("StartTime6", lbSatStartTime.getText().toString())
                        .add("EndTime6", lbSatEndTime.getText().toString())
                        .add("StartTime7", lbSunStartTime.getText().toString())
                        .add("EndTime7", lbSunEndTime.getText().toString())
                ;


                httpClient.get("/UpdateBusinessOperatingHours.aspx", params, new AsyncCallback() {

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

                                        ((EditProfileActivity) getActivity()).addBusinessProfile();

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
                .add("UserID", AppData.getInstance().loadLoginUserID());


        httpClient.get("/BusinessOperatingHours.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();


                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());


                    if (result.getString("Success").equals("Success")) {

                        final JSONArray datas= result.getJSONArray("Data");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                try {

                                    for (int i = 0 ; i < datas.length() ; i ++) {
                                        JSONObject obj = datas.getJSONObject(i);

                                        String startTime = "", endTime = "";
                                        try {
                                            startTime = obj.getString("StartTime");
                                            endTime = obj.getString("EndTime");
                                        } catch (Exception e) {e.printStackTrace();}


                                        switch (i) {
                                            case 0:
                                                lbMonStartTime.setText(startTime);
                                                lbMonEndTime.setText(endTime);
                                                break;
                                            case 1:
                                                lbTueStartTime.setText(startTime);
                                                lbTueEndTime.setText(endTime);
                                                break;
                                            case 2:
                                                lbWedStartTime.setText(startTime);
                                                lbWedEndTime.setText(endTime);
                                                break;
                                            case 3:
                                                lbThuStartTime.setText(startTime);
                                                lbThuEndTime.setText(endTime);
                                                break;
                                            case 4:
                                                lbFriStartTime.setText(startTime);
                                                lbFriEndTime.setText(endTime);
                                                break;
                                            case 5:
                                                lbSatStartTime.setText(startTime);
                                                lbSatEndTime.setText(endTime);
                                                break;
                                            case 6:
                                                lbSunStartTime.setText(startTime);
                                                lbSunEndTime.setText(endTime);
                                                break;
                                        }
                                    }



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


    class CustomEditTouchListener implements View.OnTouchListener {

        CustomFontTextView editText;

        CustomEditTouchListener(CustomFontTextView editText) {
            this.editText = editText;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (editText != null) {
                openTimeDlg(editText);
            }

            return false;
        }
    }


    private void openTimeDlg(final CustomFontTextView editText)
    {
        final Dialog dlg = new Dialog(getActivity());

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_time_layout);

        final TimePicker timePicker = (TimePicker) dlg.findViewById(R.id.timePicker);

        int hour = 0, minute = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            Date date = sdf.parse(editText.getText().toString());
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
            minute = calendar.get(Calendar.MINUTE);
        } catch (Exception e) {}

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setIs24HourView(true);


        CustomFontButton btnSave = (CustomFontButton) dlg.findViewById(R.id.btnSave);
        btnSave.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlg.dismiss();

                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                editText.setText(String.format(Locale.US, "%02d:%02d", hour, minute));

            }
        });

        CustomFontButton btnCancel = (CustomFontButton) dlg.findViewById(R.id.btnCancel);
        btnCancel.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });

        dlg.show();

    }

}
