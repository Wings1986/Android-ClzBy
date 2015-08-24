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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontCheckBox;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;
import com.closeby.clzby.listener.ImageViewLayoutSize;
import com.closeby.clzby.listener.OnLayoutSizeListener;
import com.huewu.pla.lib.MultiColumnListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by iGold on 6/3/15.
 */
public class InventoryFragment extends BaseFragment {

    MultiColumnListView mListView;
    MyCustomAdapter mAdapter;

    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();

    int imageViewWidth = 300;

    public static InventoryFragment newInstance() {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_inventory, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(View view) {

        mListView = (MultiColumnListView) view.findViewById(R.id.gridView);
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


                            String productName = obj.getString("ProductName");
                            String businessName = obj.getString("ProductDescription");
                            String categories = obj.getString("CategoryNames");

                            if ((productName != null && productName.toUpperCase().contains(newText.toUpperCase()))
                                    || (businessName != null && businessName.toUpperCase().contains(newText.toUpperCase()))
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
                .add("UserID", AppData.getInstance().loadLoginUserID());

        httpClient.get("/GetAllBusinessProductsAdmin.aspx", params, new AsyncCallback() {

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

                        DialogHelper.showToast(getActivity(), result.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public class MyCustomAdapter extends BaseAdapter {

        private JSONArray items = new JSONArray();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void setItem(JSONArray array) {
            items = array;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return items.length();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.grid_item_inventory, null);


                holder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
//                holder.ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.lbOriginPrice = (TextView) convertView.findViewById(R.id.tvOriginPrice);
                holder.subDecayView = (LinearLayout) convertView.findViewById(R.id.specialOfferView);
                holder.lbDecayOriginPrice = (TextView) convertView.findViewById(R.id.tvDelayOrginPrice);
                holder.lbDecaySpecialPrice = (TextView) convertView.findViewById(R.id.tvDelaySpecialPrice);

                holder.lbTagLine= (CustomFontTextView) convertView.findViewById(R.id.tvTagLine);
                holder.lbDealName= (CustomFontTextView) convertView.findViewById(R.id.tvDealName);
                holder.lbDealDescription= (CustomFontTextView) convertView.findViewById(R.id.tvDealDescription);
                holder.lbDealSubCategory = (CustomFontTextView) convertView.findViewById(R.id.tvSubCategory);

                holder.subDecayTimeView = (LinearLayout) convertView.findViewById(R.id.decayDurationView);
                holder.lbDecayDuration = (TextView) convertView.findViewById(R.id.tvDecayDuration);

                holder.swEnable = (CheckBox) convertView.findViewById(R.id.ckbEnable);


                holder.btnMap = (Button) convertView.findViewById(R.id.btnMap);
                holder.btnMap.setOnTouchListener(CustomButtonTouchListener.getInstance());
                holder.btnMap.setTag(position);


                holder.btnEdit = (Button) convertView.findViewById(R.id.btnEdit);
                holder.btnEdit.setOnTouchListener(CustomButtonTouchListener.getInstance());
                holder.btnEdit.setTag(position);


                holder.btnSetting = (Button) convertView.findViewById(R.id.btnSetting);
                holder.btnSetting.setOnTouchListener(CustomButtonTouchListener.getInstance());
                holder.btnSetting.setTag(position);


                holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
                holder.btnDelete.setOnTouchListener(CustomButtonTouchListener.getInstance());
                holder.btnDelete.setTag(position);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            try {
                final JSONObject item = items.getJSONObject(position);

                try {

                    holder.ivPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ImageViewLayoutSize(holder.ivPhoto, new OnLayoutSizeListener() {
                        @Override
                        public void onGetSize(int width, int height) {
                            imageViewWidth = width;
                        }
                    }));


                    String url = Global.getURLEncoded(item.getString("LargeImage"));
                    Log.d("InventoryFragment", "url = " + url);
//                    Picasso.with(getActivity())
//                            .load(url)
//                            .into(new CustomTarget(holder.ivPhoto));

                    ImageLoader.getInstance().displayImage(url, holder.ivPhoto, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            ImageView imageView = (ImageView) view;

                            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                            int ivWidth = bitmap.getWidth();
                            int ivHeight = bitmap.getHeight();
                            int virturlHeight = 320;
                            if (ivWidth != 0 && ivHeight != 0)
                                virturlHeight= imageViewWidth * ivHeight / ivWidth;
                            param.height = virturlHeight;
                            imageView.setLayoutParams(param);

                            Log.d("InventoryFragment", "width = " + param.width + " height = " + param.height);

                            imageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


                holder.lbOriginPrice.setVisibility(View.GONE);
                try {
                    holder.lbOriginPrice.setText("$" + item.getString("OrigionalPrice"));
                } catch (Exception e) {}

                boolean bDecaySpecial = false;
                try {
                    bDecaySpecial = item.getBoolean("DecayingSpecial");
                } catch (Exception e) {}

                if (bDecaySpecial) {
                    try {
                        holder.lbOriginPrice.setVisibility(View.GONE);
                        holder.subDecayView.setVisibility(View.VISIBLE);
                        holder.subDecayTimeView.setVisibility(View.VISIBLE);

                        holder.lbDecayOriginPrice.setText("$" + item.getInt("OrigionalPrice"));
                        holder.lbDecaySpecialPrice.setText("$" + item.getInt("SpecialPrice"));

                        holder.lbDecayDuration.setText(Global.getLeftTime(item.getString("DecayEndTime")));
                    } catch (Exception e) {}
                }
                else {
                    holder.lbOriginPrice.setVisibility(View.VISIBLE);
                    holder.subDecayView.setVisibility(View.GONE);
                    holder.subDecayTimeView.setVisibility(View.GONE);
                }

                try {

                    holder.lbTagLine.setText(item.getString("DiscountedTagLing"));
                    holder.lbDealName.setText(item.getString("ProductName"));
                    holder.lbDealDescription.setText(item.getString("ProductDescription"));
                    holder.lbDealSubCategory.setText(item.getString("CategoryNames"));

                } catch (Exception e) {}

                try {
                    holder.swEnable.setChecked(item.getInt("CurrentState") == 1 ? true : false);
                } catch (Exception e) {
                    holder.swEnable.setChecked(false);
                }


                // event
//                holder.swEnable.setOnClickListener(new CustomCheckButtonListener(holder.swEnable.isChecked(), position, items));
                holder.swEnable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            String productID = item.getString("ID");
                            final boolean isChecked = item.getInt("CurrentState") == 1 ? true : false;


                            AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                            httpClient.setMaxRetries(3);
                            ParameterMap params = httpClient.newParams()
                                    .add("guid", Global.kGUID)
                                    .add("UserID", AppData.getInstance().loadLoginUserID())
                                    .add("ProductID", productID)
                                    .add("State", isChecked ? "0" : "1");

                            httpClient.post("/EnableDisableDeal.aspx", params, new AsyncCallback() {

                                @Override
                                public void onComplete(HttpResponse httpResponse) {


                                    try {
                                        JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                                        if (result.getString("Success").equals("Success")) {


                                            int indexOfAll = MyJSON.indexOfJSONArray(arrayData, items.getJSONObject(position));

                                            item.put("CurrentState", !isChecked == true ? 1 : 0);

                                            if (indexOfAll != -1) {
//                                MyJSON.replaceJSONObject(arrayData, indexOfAll, item);
                                                arrayData.put(indexOfAll, item);
                                            }
//                            MyJSON.replaceJSONObject(arrayDataSource, position, item);
                                            arrayDataSource.put(position, item);

                                            DialogHelper.showToast(getActivity(), result.getString("Message"));

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // TODO Auto-generated method stub

                                                    notifyDataSetChanged();
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
                        } catch (Exception e) {e.printStackTrace();}

                    }
                });

                holder.btnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((HomeActivity) getActivity()).gotoMap();

                    }
                });

                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            ((HomeActivity) getActivity()).gotoAddProduct(item);

                        } catch (Exception e) {e.printStackTrace();}
                    }
                });

                holder.btnSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = DialogHelper.getSpecialDialog(getActivity(), new DialogCallBack() {
                            @Override
                            public void onClick(int which) {

                                if (which == 0) {
                                    // standard

                                    try {

                                        ((HomeActivity) getActivity()).gotoStandardDealSetting(item);

                                    } catch (Exception e) {e.printStackTrace();}

                                }
                                else if (which == 1) {
                                    // special
                                    try {

                                        ((HomeActivity) getActivity()).gotoSpecialDealSetting(item);

                                    } catch (Exception e) {e.printStackTrace();}

                                }
                                else {
                                    // cancel
                                }
                            }
                        });
                        if (dialog != null)
                            dialog.show();

                    }
                });

                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DialogHelper.getDialog(getActivity(), "NOTE", "Are you sure you want to delete this product?", "YES", "NO", new DialogCallBack() {
                            @Override
                            public void onClick(int which) {

                                if (which == 0) {

                                    String productID = "";
                                    try {
//                                        JSONObject item = items.getJSONObject(position);
                                        productID = item.getString("ID");
                                    } catch (Exception e) {e.printStackTrace();}

                                    final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
                                    waitDialog.show();

                                    AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
                                    httpClient.setMaxRetries(3);
                                    ParameterMap params = httpClient.newParams()
                                            .add("guid", Global.kGUID)
                                            .add("UserID", AppData.getInstance().loadLoginUserID())
                                            .add("ProductID", productID)
                                            ;

                                    httpClient.post("/DeleteProduct.aspx", params, new AsyncCallback() {

                                        @Override
                                        public void onComplete(HttpResponse httpResponse) {

                                            waitDialog.dismiss();

                                            try {
                                                JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                                                if (result.getString("Success").equals("Success")) {

                                                    int indexOfAll = MyJSON.indexOfJSONArray(arrayData, items.getJSONObject(position));
                                                    if (indexOfAll != -1) {
                                                        arrayData = MyJSON.removeJSONArray(arrayData, indexOfAll);
                                                    }

                                                    arrayDataSource = MyJSON.removeJSONArray(arrayDataSource, position);


                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // TODO Auto-generated method stub

                                                            notifyDataSetChanged();
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
                        }).show();


                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    public static class ViewHolder {

        ImageView ivPhoto;

        TextView lbOriginPrice;
        LinearLayout subDecayView;
        TextView lbDecayOriginPrice;
        TextView lbDecaySpecialPrice;

        CustomFontTextView lbTagLine;
        CustomFontTextView lbDealName;
        CustomFontTextView lbDealDescription;
        CustomFontTextView lbDealSubCategory;

        LinearLayout subDecayTimeView;
        TextView lbDecayDuration;

        Button btnMap;

        Button btnEdit;
        Button btnDelete;
        Button btnSetting;

        CheckBox swEnable;
    }

    public class CustomTarget implements Target {

        ImageView imageView;

        public CustomTarget(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Log.d("InventoryFragment", "onBitmapLoaded");

            FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            int ivWidth = bitmap.getWidth();
            int ivHeight = bitmap.getHeight();
            int virturlHeight = 320;
            if (ivWidth != 0 && ivHeight != 0)
                virturlHeight= (getScreenWidth()/2) * ivHeight / ivWidth;
            param.height = virturlHeight;
            imageView.setLayoutParams(param);

            Log.d("InventoryFragment", "width = " + param.width + " height = " + param.height);

            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("InventoryFragment", "onBitmapFailed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("InventoryFragment", "onPrepareLoad");
//            imageView.setImageDrawable(placeHolderDrawable);
        }

//        @Override
//        public boolean equals(Object o) {
//            if(o instanceof CustomTarget) {
//                return ((CustomTarget) o).imageView.equals(this.imageView);
//            }
//            return super.equals(o);
//        }
    }


}
