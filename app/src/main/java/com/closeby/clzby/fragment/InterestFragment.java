package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.headerlistview.HeaderListView;
import com.applidium.headerlistview.SectionAdapter;
import com.closeby.clzby.AppData;
import com.closeby.clzby.MyLocation;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.ChooseInterestActivity;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by iGold on 6/3/15.
 */


public class InterestFragment extends BaseFragment {

    HeaderListView mListView;
    private MyCustomAdapter mAdapter;

    JSONArray arrayData = new JSONArray();


    public static InterestFragment newInstance() {
        InterestFragment fragment = new InterestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_interest, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        mListView = (HeaderListView) view.findViewById(R.id.listview);
        mAdapter = new MyCustomAdapter();
        mListView.setAdapter(mAdapter);

        Button btnSelAll = (Button) view.findViewById(R.id.btnSelAll);
        btnSelAll.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnSelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    for (int i = 0; i < arrayData.length(); i++) {
                        JSONArray arryCategory = arrayData.getJSONObject(i).getJSONArray("SubCategoryIDS");
                        for (int j = 0; j < arryCategory.length(); j++) {
                            JSONObject subCategory = arryCategory.getJSONObject(j);
                            subCategory.put("selected", true);
                        }
                    }


                } catch (Exception e) {e.printStackTrace();}

                mAdapter.setItem(arrayData);
                mAdapter.notifyDataSetChanged();
            }
        });

        Button btnDeselAll = (Button) view.findViewById(R.id.btnDeselAll);
        btnDeselAll.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnDeselAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    for (int i = 0; i < arrayData.length(); i++) {

                        JSONArray arryCategory = arrayData.getJSONObject(i).getJSONArray("SubCategoryIDS");
                        for (int j = 0 ; j < arryCategory.length() ; j ++) {
                            JSONObject subCategory = arryCategory.getJSONObject(j);
                            subCategory.put("selected", false);
                        }
                    }

                } catch (Exception e) {e.printStackTrace();}

                mAdapter.setItem(arrayData);
                mAdapter.notifyDataSetChanged();

            }
        });

        Button btnGo = (Button) view.findViewById(R.id.btnGo);
        btnGo.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedCategory = "";
                try {
                    for (int i = 0; i < arrayData.length(); i++) {

                        JSONArray arryCategory = arrayData.getJSONObject(i).getJSONArray("SubCategoryIDS");
                        for (int j = 0 ; j < arryCategory.length() ; j ++) {
                            JSONObject subCategory = arryCategory.getJSONObject(j);
                            boolean bSelected = subCategory.getBoolean("selected");
                            if (bSelected) {
                                if (selectedCategory.length() < 1) {
                                    selectedCategory = "" + subCategory.getInt("ID");
                                } else {
                                    selectedCategory += "," + subCategory.getInt("ID");
                                }
                            }
                        }
                    }


                } catch (Exception e) {e.printStackTrace();}


                AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                httpClient.setMaxRetries(3);
                ParameterMap params = httpClient.newParams()
                        .add("guid", Global.kGUID)
                        .add("UserID", AppData.getInstance().loadLoginUserID())
                        .add("lat", "" + MyLocation.getInstance().getLatitude())
                        .add("long", "" + MyLocation.getInstance().getLongitude())
                        .add("SelectedCategories", selectedCategory)
                        ;


                httpClient.get("/SaveInterests.aspx", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        try {
                            JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                            if (result.getString("Success").equals("Success")) {

//                                arrayData = result.getJSONArray("Data");

                                ((ChooseInterestActivity) getActivity()).onDoneCompleted();

                            } else {

                                DialogHelper.showToast(getActivity(), result.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        });

        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("userid", AppData.getInstance().loadLoginUserID())
                .add("lat", "" + MyLocation.getInstance().getLatitude())
                .add("lng", "" + MyLocation.getInstance().getLongitude())
                ;


        httpClient.get("/GetAllCategoriesFixed.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        arrayData = result.getJSONArray("Data");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayData);
                                mAdapter.notifyDataSetChanged();
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



    public String getChooseCategories() {

        if (arrayData == null)
            return "";

        String selectedCategory = "";

        for (int i = 0; i < arrayData.length() ; i ++) {
            try {
                JSONArray section = arrayData.getJSONObject(i).getJSONArray("SubCategoryIDS");

                for (int j = 0 ; j < section.length() ; j ++) {
                    JSONObject obj = section.getJSONObject(j);

                    if (obj.getBoolean("selected")) {
                        if (selectedCategory.length() < 1) {
                            selectedCategory = "" + obj.getInt("ID");
                        } else {
                            selectedCategory += "," + obj.getInt("ID");
                        }
                    }
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        return selectedCategory;
    }

    public class MyCustomAdapter extends SectionAdapter {


        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;


        public MyCustomAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItem(JSONArray array) {
            items = array;
        }

        @Override
        public int numberOfSections() {
            return items.length();
        }

        @Override
        public int numberOfRows(int section) {

            try {

                return items.getJSONObject(section).getJSONArray("SubCategoryIDS").length();

            } catch (Exception e) {e.printStackTrace();}

            return 0;
        }

        @Override
        public Object getRowItem(int section, int row) {
            return null;
        }

        @Override
        public boolean hasSectionHeaderView(int section) {
            return true;
        }

        @Override
        public View getRowView(int section, int row, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_item_interest_cell, null);

                holder.imageView = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.lbTitle = (CustomFontTextView) convertView.findViewById(R.id.tvTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {

                JSONObject item = items.getJSONObject(section).getJSONArray("SubCategoryIDS").getJSONObject(row);

                try {

                    if (item.getBoolean("selected")) {
                        holder.imageView.setImageResource(R.drawable.icon_selected_sub_category);
                    } else {
                        holder.imageView.setImageResource(R.drawable.icon_unselected_sub_category);
                    }

                    holder.lbTitle.setText(item.getString("CategoryName"));

                } catch (Exception e) {e.printStackTrace();}


            } catch (Exception e) {e.printStackTrace();}

            return convertView;
        }

        @Override
        public int getSectionHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public int getSectionHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public View getSectionHeaderView(final int section, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_item_interest_header, null);

                holder.imageView = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.lbTitle = (CustomFontTextView) convertView.findViewById(R.id.tvTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {

                final JSONObject item = items.getJSONObject(section);


                boolean bSelected = false;

                for (int i = 0 ; i < item.getJSONArray("SubCategoryIDS").length() ; i ++) {
                    JSONObject subCategory = item.getJSONArray("SubCategoryIDS").getJSONObject(i);
                    if (subCategory.getBoolean("selected")) {
                        bSelected = true;
                        break;
                    }
                }

                if (bSelected) {
                    holder.imageView.setImageResource(R.drawable.icon_selected_category);
                } else {
                    holder.imageView.setImageResource(R.drawable.icon_unselected_category);
                }


                try {

                    holder.lbTitle.setText(item.getString("MainCategoryName"));

                } catch (Exception e) {e.printStackTrace();}


                // click event
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean bSelected = false;

                        try {

                            for (int i = 0; i < item.getJSONArray("SubCategoryIDS").length(); i++) {
                                JSONObject subCategory = item.getJSONArray("SubCategoryIDS").getJSONObject(i);
                                if (subCategory.getBoolean("selected")) {
                                    bSelected = true;
                                    break;
                                }
                            }

                            for (int i = 0 ; i < arrayData.getJSONObject(section).getJSONArray("SubCategoryIDS").length() ; i ++) {
                                JSONObject subCategory = arrayData.getJSONObject(section).getJSONArray("SubCategoryIDS").getJSONObject(i);
                                subCategory.put("selected", !bSelected);
                            }

                        } catch (Exception e) {e.printStackTrace();}

                        mAdapter.setItem(arrayData);
                        mAdapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {e.printStackTrace();}

            return convertView;
        }

        @Override
        public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
            super.onRowItemClick(parent, view, section, row, id);

            try {

                JSONObject item = arrayData.getJSONObject(section).getJSONArray("SubCategoryIDS").getJSONObject(row);

                boolean selected = item.getBoolean("selected");

                item.put("selected", !selected);

                mAdapter.setItem(arrayData);
                mAdapter.notifyDataSetChanged();


            } catch (Exception e) {e.printStackTrace();}

        }

    }

    public static class ViewHolder {

        public ImageView imageView;
        public CustomFontTextView lbTitle;

    }

}
