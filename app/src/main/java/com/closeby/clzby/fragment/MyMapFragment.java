package com.closeby.clzby.fragment;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.closeby.clzby.AppData;
import com.closeby.clzby.MyLocation;
import com.closeby.clzby.R;
import com.closeby.clzby.activity.Global;
import com.closeby.clzby.activity.HomeActivity;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.customcontrol.CustomFontTextView;
import com.closeby.clzby.customcontrol.DialogCallBack;
import com.closeby.clzby.customcontrol.DialogHelper;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by iGold on 6/3/15.
 */


public class MyMapFragment extends BaseFragment {

    JSONArray arrayData = new JSONArray(), arrayDataSource = new JSONArray();

    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundle;

    private Marker marker;

    private Button btnRouter;
    private Marker mSelectedMarker;
    Polyline polyline;


    private Map<Marker, JSONObject> allMarkersMap = new HashMap<>();


    public static MyMapFragment newInstance() {
        MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView  = inflater.inflate(R.layout.frag_map, container, false);

        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) inflatedView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(inflatedView);

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void setUpMapIfNeeded(View inflatedView) {
        if (mMap == null) {
            mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
            mMap.getUiSettings().setZoomControlsEnabled(false);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {

//                    marker.showInfoWindow();

                    if (marker != null) {
                        btnRouter.setVisibility(View.VISIBLE);
                        mSelectedMarker = marker;
                    } else {
                        btnRouter.setVisibility(View.GONE);
                        mSelectedMarker = null;
                    }
                    return false;
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    btnRouter.setVisibility(View.GONE);
                    mSelectedMarker = null;
                }
            });

            mMap.setInfoWindowAdapter(new PopupAdapter(getActivity().getLayoutInflater()));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    JSONObject item = allMarkersMap.get(marker);

                    try {

                        ((HomeActivity) getActivity()).gotoProfile(item.getString("UserID"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            if (mMap != null) {
//                setUpMap();
            }
        }
    }



    private void initView(View view) {

        btnRouter = (Button) view.findViewById(R.id.btnRouter);
        btnRouter.setOnTouchListener(CustomButtonTouchListener.getInstance());
        btnRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSelectedMarker == null)
                    return;
                if (MyLocation.getInstance().getLongitude() == 0 || MyLocation.getInstance().getLatitude() == 0)
                    return;


                String startLocation = MyLocation.getInstance().getLatitude() + "," + MyLocation.getInstance().getLongitude();
                final String endLocation = mSelectedMarker.getPosition().latitude + "," + mSelectedMarker.getPosition().longitude;


                AndroidHttpClient httpClient = new AndroidHttpClient("");
                httpClient.setMaxRetries(3);
                ParameterMap params = httpClient.newParams()
                        .add("origin", startLocation)
                        .add("destination", endLocation)
                        .add("sensor", "false");


                httpClient.get("https://maps.googleapis.com/maps/api/directions/json", params, new AsyncCallback() {

                    @Override
                    public void onComplete(HttpResponse httpResponse) {

                        try {
                            JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                            final JSONArray polyLines = new JSONArray();


                            if (result.getString("status").equalsIgnoreCase("OK")) {

                                JSONArray routes = result.optJSONArray("routes");

                                if (routes != null) {
                                    JSONObject obj = routes.getJSONObject(0);

                                    JSONObject leg = obj.getJSONArray("legs").getJSONObject(0);

                                    JSONArray steps = leg.getJSONArray("steps");



                                    for (int i = 0 ; i < steps.length() ; i ++) {
                                        JSONObject point = steps.getJSONObject(i);

                                        if (point != null) {
                                            if (i == 0) {
                                                JSONObject start = point.getJSONObject("start_location");
                                                double lat = start.optDouble("lat");
                                                double lng = start.optDouble("lng");

                                                JSONObject customLocation = new JSONObject();
                                                customLocation.put("lat", lat);
                                                customLocation.put("lng", lng);

                                                polyLines.put(customLocation);
                                            }

                                            JSONObject end = point.getJSONObject("end_location");
                                            double lat = end.optDouble("lat");
                                            double lng = end.optDouble("lng");

                                            JSONObject customLocation = new JSONObject();
                                            customLocation.put("lat", lat);
                                            customLocation.put("lng", lng);

                                            polyLines.put(customLocation);
                                        }
                                    }

                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                        PolylineOptions polylineOptions = new PolylineOptions();
                                        polylineOptions.width(5);
                                        polylineOptions.color(getResources().getColor(R.color.AppColor));
                                        polylineOptions.geodesic(true);

                                        if (polyline != null) {
                                            polyline.remove();
                                        }

                                        for (int i = 0 ; i < polyLines.length() ; i ++) {
                                            try {

                                                JSONObject obj = polyLines.getJSONObject(i);
                                                double lat = obj.getDouble("lat");
                                                double lng = obj.getDouble("lng");

                                                polylineOptions.add( new LatLng( lat, lng ) );

                                            } catch (Exception e) {e.printStackTrace();}
                                        }

                                        polyline = mMap.addPolyline (polylineOptions);

                                        DialogHelper.getDialog(getActivity(), "", "Do you want to open in map app?", "YES", "NO", new DialogCallBack() {
                                            @Override
                                            public void onClick(int which) {
                                                if (which == 0) { // yes

                                                    String url = "http://maps.google.com/maps?f=d&daddr=" + endLocation;

                                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse(url));
                                                    intent.setComponent(new ComponentName("com.google.android.apps.maps",
                                                            "com.google.android.maps.MapsActivity"));
                                                    getActivity().startActivity(intent);
                                                }
                                            }
                                        }).show();

                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        btnRouter.setVisibility(View.GONE);


        // network

        final Dialog waitDialog = DialogHelper.getWaitDialog(getActivity());
        waitDialog.show();

        AndroidHttpClient httpClient = new AndroidHttpClient(Global.kServerURL);
        httpClient.setMaxRetries(3);
        ParameterMap params = httpClient.newParams()
                .add("guid", Global.kGUID)
                .add("UserID", AppData.getInstance().loadLoginUserID())
                .add("userlat", "" + MyLocation.getInstance().getLatitude())
                .add("userlong", "" + MyLocation.getInstance().getLongitude())
                .add("range", "" + AppData.RANGE)
                .add("fromrow", "0")
                .add("limit", "100")
                ;


        httpClient.get("/GetBusinessesWithinRange.aspx", params, new AsyncCallback() {

            @Override
            public void onComplete(HttpResponse httpResponse) {

                waitDialog.dismiss();

                try {
                    JSONObject result = new JSONObject(httpResponse.getBodyAsString());

                    if (result.getString("Success").equals("Success")) {

                        arrayData = result.getJSONObject("Data").getJSONArray("BusinessListing");
                        arrayDataSource = arrayData;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                addMarker();
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

    /**
     * Adds a marker to the map
     */
    private void addMarker(){

        /** Make sure that the map has been initialised **/
        if(null != mMap){

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0 ; i < arrayDataSource.length() ; i ++) {

                try {
                    JSONObject item = arrayDataSource.getJSONObject(i);

                    double lat = item.getDouble("latitude");
                    double lng = item.getDouble("longitude");
                    String title = item.getString("BusinessName");
                    String detail = item.getString("BusinessEmailAddress");

                    final Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon))
                                    .title(title)
                                    .snippet(detail)

                    );

                    allMarkersMap.put(marker, item);


/*
                    String url = Global.getURLEncoded(item.getString("LogoImageSmall"));
                    ImageLoader.getInstance().loadImage(url, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            // Do whatever you want with Bitmap



//                            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_back);
////                            Canvas canvas = new Canvas(bmp);
//                            Bitmap mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
//                            Canvas canvas = new Canvas(mutableBitmap);
//
//                            Log.d("pin image", "width = " + mutableBitmap.getWidth() + " height = " + mutableBitmap.getHeight());
//
//                            canvas.drawBitmap(loadedImage, new Rect(0, 0, loadedImage.getWidth(), loadedImage.getHeight()),
//                                    //new Rect(16, 14, 16+95, 14+75),
//                                    new Rect(16, 14, mutableBitmap.getWidth()-32, mutableBitmap.getHeight()-50),
//                                    null);

//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(loadedImage));

                            int width = 128, height = 128;
                            Bitmap mutableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(mutableBitmap);

                            canvas.drawBitmap(loadedImage, new Rect(0, 0, loadedImage.getWidth(), loadedImage.getHeight()),
                                    new Rect(0, 0, width, height - 28), null);

                            Paint myPaint = new Paint();
                            myPaint.setColor(Color.parseColor("#00a3e8"));
                            myPaint.setStyle(Paint.Style.STROKE);
                            myPaint.setStrokeWidth(10);
                            canvas.drawRect(0, 0, width, height-28, myPaint);

                            // triangle
                            Point a = new Point(width/2-14, height -28);
                            Point b = new Point(width/2+14, height -28);
                            Point c = new Point(width/2, height);

                            Path path = new Path();
                            myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            path.setFillType(Path.FillType.EVEN_ODD);
                            path.moveTo(a.x, a.y);
                            path.lineTo(b.x, b.y);
                            path.lineTo(c.x, c.y);
                            path.lineTo(a.x, a.y);
                            path.close();

                            canvas.drawPath(path, myPaint);

                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(mutableBitmap));
                        }
                    });
*/

//                    mMap.addGroundOverlay(new GroundOverlayOptions()
//                            .image(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_icon)).anchor(0, 1)
//                            .position(new LatLng(lat, lng), 6400f, 6400f));


                    builder.include(new LatLng(lat, lng));

                    //mMap.addGroundOverlay()


                } catch (Exception e) {e.printStackTrace();}

            }

            LatLngBounds bounds = builder.build();

            int padding = 50; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.moveCamera(cu);
            mMap.animateCamera(cu);

            mMapView.getOverlay();

        }
    }



    class PopupAdapter implements GoogleMap.InfoWindowAdapter {

        private View popup;

        PopupAdapter(LayoutInflater inflater) {

            popup = inflater.inflate(R.layout.map_pin_popup, null);
        }


        @Override
        public View getInfoContents(final Marker marker) {
            if (MyMapFragment.this.marker != null
                    && MyMapFragment.this.marker.isInfoWindowShown()) {
//                MyMapFragment.this.marker.hideInfoWindow();
                MyMapFragment.this.marker.showInfoWindow();
            }
            return null;

        }

        @Override
        public View getInfoWindow(final Marker marker) {
            MyMapFragment.this.marker = marker;


            CustomFontTextView tvTitle= (CustomFontTextView)popup.findViewById(R.id.title);
            CustomFontTextView tvSnippet = (CustomFontTextView)popup.findViewById(R.id.snippet);
            final ImageView ivthumb = (ImageView) popup.findViewById(R.id.ivThumb);


            tvTitle.setText(marker.getTitle());
            tvSnippet.setText(marker.getSnippet());

            JSONObject item = allMarkersMap.get(marker);

            try {

                String url = Global.getURLEncoded(item.getString("LogoImageSmall"));
//                Picasso.with(getActivity()).load(Uri.parse(url)).into(ivthumb/*, new InfoWindowRefresher(marker)*/);


//                ImageLoader.getInstance().displayImage(url, ivthumb);
                ivthumb.setImageBitmap(null);
                ImageLoader.getInstance().displayImage(url, ivthumb, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        ivthumb.setImageBitmap(bitmap);

                        getInfoContents(marker);

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });

            } catch (Exception e) {e.printStackTrace();}

            return(popup);
        }


    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }
}
