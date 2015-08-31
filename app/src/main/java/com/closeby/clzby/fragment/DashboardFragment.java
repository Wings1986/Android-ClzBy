package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontButton;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by iGold on 6/3/15.
 */
public class DashboardFragment extends BaseFragment {

    static int TODAY = 0;
    static int YESTERDAY = 1;
    static int THISMONTH = 2;
    static int LASTMONTH = 3;
    static int CUSTOMDATE = 4;


    CustomFontTextView pinImpressions, pinLikes, pinMessages;
    LinearLayout customDateView;
    CustomFontTextView lbStartTime, lbEndTime;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pinImpressions = (CustomFontTextView) view.findViewById(R.id.lbImpressions);
        pinLikes = (CustomFontTextView) view.findViewById(R.id.lbLikes);
        pinMessages = (CustomFontTextView) view.findViewById(R.id.lbMessages);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("Today");
        list.add("Yesterday");
        list.add("This Month");
        list.add("Last Month");
        list.add("Specify Date");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == CUSTOMDATE) {
                    customDateView.setVisibility(View.VISIBLE);
                }
                else {
                    customDateView.setVisibility(View.GONE);

                    reqeustApi(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        customDateView = (LinearLayout) view.findViewById(R.id.customDateView);
        lbStartTime = (CustomFontTextView) view.findViewById(R.id.lbStartTime);
        lbStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDlg(lbStartTime);
            }
        });
        lbEndTime = (CustomFontTextView) view.findViewById(R.id.lbEndTime);
        lbEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDlg(lbEndTime);
            }
        });

        CustomFontButton btnGo = (CustomFontButton) view.findViewById(R.id.btnGo);
        btnGo.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String startTime = lbStartTime.getText().toString();
                String endTime = lbEndTime.getText().toString();

                if (startTime.equalsIgnoreCase("Start Time")) {
                    DialogHelper.showToast(getActivity(), "Please set start time");
                    return;
                }
                if (endTime.equalsIgnoreCase("End Time")) {
                    DialogHelper.showToast(getActivity(), "Please set end time");
                    return;
                }

                reqeustApi(CUSTOMDATE);

            }
        });
        customDateView.setVisibility(View.GONE);

        reqeustApi(TODAY);
    }

    private void reqeustApi(int type) {


        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);


        String url = "";
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID());

        if (type == TODAY) {
            url = "/Analytics/BusinessAnalyticsTotals.aspx";
        }
        else if (type == YESTERDAY) {
            url = "/Analytics/BusinessAnalyticsDays.aspx";
            params.add("SelectedDay", "Yesterday");
        }
        else if (type == THISMONTH) {
            url = "/Analytics/BusinessAnalyticsMonths.aspx";
            params.add("SelectedMonth", "ThisMonth");
        }
        else if (type == LASTMONTH) {
            url = "/Analytics/BusinessAnalyticsMonths.aspx";
            params.add("SelectedMonth", "LastMonth");
        }
        else {

            url = "/Analytics/BusinessAnalyticsBetween.aspx";
            params.add("StartDate", lbStartTime.getText().toString());
            params.add("EndDate", lbEndTime.getText().toString());
        }



        httpClient.get(url, params, new AsyncCallback() {

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
                                    int impressions = dicInfo.getInt("TotalDealImpressions");
                                    pinImpressions.setText("" + impressions);
                                } catch (Exception e) {
                                    pinImpressions.setText("0");
                                    e.printStackTrace();
                                }

                                try {
                                    int likes = dicInfo.getInt("TotalProductLikes");
                                    pinLikes.setText("" + likes);
                                } catch (Exception e) {
                                    pinLikes.setText("0");
                                    e.printStackTrace();
                                }

                                try {
                                    int messages = dicInfo.getInt("TotalNumberOfSentOutMessages");
                                    pinMessages.setText("" + messages);
                                } catch (Exception e) {
                                    pinMessages.setText("0");
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


    private void openDateDlg(final CustomFontTextView editText)
    {
        final Dialog dlg = new Dialog(getActivity());

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_date_layout);

        final DatePicker datePicker = (DatePicker) dlg.findViewById(R.id.datePicker);

        int year = 0, month = 0, day = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = sdf.parse(editText.getText().toString());
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            year = calendar.get(Calendar.YEAR); // gets hour in 24h format
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } catch (Exception e) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(year, month, day, null);


        CustomFontButton btnSave = (CustomFontButton) dlg.findViewById(R.id.btnSave);
        btnSave.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlg.dismiss();

                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                editText.setText(String.format(Locale.US, "%02d/%02d/%d", month, day, year));

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
