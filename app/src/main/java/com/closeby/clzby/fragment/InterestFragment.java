package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();


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


        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (arrayData == null) {
                    return false;
                }

                arrayDataSource = MyJSON.clearJSONArray(arrayDataSource);


                if (newText.length() < 1) {
                    arrayDataSource = arrayData;
                }
                else {
                    for (int i = 0 ; i < arrayData.length() ; i ++) {

                        try {
                            JSONObject obj = arrayData.getJSONObject(i);


                            String businessName = obj.getString("BusinessName");
                            String categories = obj.getString("CategoryName");

                            if ((businessName != null && businessName.toUpperCase().contains(newText.toUpperCase()))
                                    || (categories != null && categories.toUpperCase().contains(newText.toUpperCase()))) {

                                arrayDataSource = MyJSON.addJSONObject(arrayDataSource, obj);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mAdapter.setItem(arrayDataSource);
                mAdapter.notifyDataSetChanged();

                return false;
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
                        arrayDataSource = arrayData;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                mAdapter.setItem(arrayDataSource);
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

    public void clickDone() {

        String selectedCategory = getChooseCategories();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID())
                .add("lat", "" + MyLocation.getInstance().getLatitude())
                .add("long", "" + MyLocation.getInstance().getLongitude())
                .add("SelectedCategories", selectedCategory)
                ;


        httpClient.post("/SaveInterests.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {


                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

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

                holder.imageView = (ImageView) convertView.findViewById(R.id.ivAvatar);
                holder.lbTitle = (CustomFontTextView) convertView.findViewById(R.id.tvTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {

                JSONObject item = items.getJSONObject(section).getJSONArray("SubCategoryIDS").getJSONObject(row);

                try {

                    if (item.getBoolean("selected")) {
                        holder.imageView.setImageResource(R.drawable.interest_checked);
                    } else {
                        holder.imageView.setImageResource(R.drawable.interest_unchecked);
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
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.list_item_interest_header, null);

                holder.lbTitle = (CustomFontTextView) convertView.findViewById(R.id.tvTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {

                JSONObject item = items.getJSONObject(section);

                try {

                    holder.lbTitle.setText(item.getString("MainCategoryName"));

                } catch (Exception e) {e.printStackTrace();}


            } catch (Exception e) {e.printStackTrace();}

            return convertView;
        }

        @Override
        public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id) {
            super.onRowItemClick(parent, view, section, row, id);

            try {

                JSONObject item = arrayDataSource.getJSONObject(section).getJSONArray("SubCategoryIDS").getJSONObject(row);

                boolean selected = item.getBoolean("selected");

                item.put("selected", !selected);

                mAdapter.setItem(arrayDataSource);
                mAdapter.notifyDataSetChanged();

                try {
                    JSONArray dataOfsection = arrayData.getJSONObject(section).getJSONArray("SubCategoryIDS");

                    int index = MyJSON.indexOfJSONArray(dataOfsection, item);
                    if (index != -1) {
                        JSONObject obj = dataOfsection.getJSONObject(index);
                        obj.put("selected", !selected);
                    }

                } catch (Exception e) {e.printStackTrace();}

            } catch (Exception e) {e.printStackTrace();}

        }

    }

    public static class ViewHolder {

        public ImageView imageView;
        public CustomFontTextView lbTitle;

    }

}
