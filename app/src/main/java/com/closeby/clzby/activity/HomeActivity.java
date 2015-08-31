package com.closeby.clzby.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.closeby.clzby.AppData;
import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.fragment.BaseFragment;
import com.closeby.clzby.fragment.BeaconsFragment;
import com.closeby.clzby.fragment.BusinessFragment;
import com.closeby.clzby.fragment.DashboardFragment;
import com.closeby.clzby.fragment.DealFragment;
import com.closeby.clzby.fragment.FavouriteFragment;
import com.closeby.clzby.fragment.HistoryFragment;
import com.closeby.clzby.fragment.InventoryFragment;
import com.closeby.clzby.fragment.MenuFragment;
import com.closeby.clzby.fragment.MyMapFragment;
import com.closeby.clzby.fragment.ProfileFragment;
import com.closeby.clzby.fragment.profile.BusinessProfileFragment;
import com.closeby.clzby.listener.OnClickMenuItemListener;
import com.closeby.clzby.model.BeaconItem;
import com.closeby.clzby.model.ProductItem;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by iGold on 6/3/15.
 */
public class HomeActivity extends SlidingFragmentActivity {

    SlidingMenu mSlidingMenu = null;

    MenuFragment mLeftFrag;
    BaseFragment mRightFrag;

    int mIndexSelect = 1;


    TextView titleView;
    ImageView actionBtn;

    private static final int REQUEST_CHOOSE_INTEREST = 1235;


    // Beacons config
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;
    private NotificationManager notificationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBehindContentView(R.layout.frame_menu);

        mLeftFrag = MenuFragment.newInstance();
        mLeftFrag.setOnClickMenuItemListener(new OnClickMenuItemListener() {
            @Override
            public void onClickMenuItem(int position) {
                if (mIndexSelect != position) {
                    String title = "";

                    hideNativationRightItem();


                    if (AppData.getInstance().isBusiness()) {
                        switch (position) {
                            case 0:
                                mRightFrag = ProfileFragment.newInstance("");
                                title = "Profile";

                                // set right button
                                showNativationRightItem(R.drawable.icon_edit);

                                break;
                            case 1:
                                mRightFrag = InventoryFragment.newInstance();
                                title = "Mechant Inventory";

                                showNativationRightItem(R.drawable.button_add);

                                break;
                            case 2:
                                mRightFrag = BeaconsFragment.newInstance();
                                title = "Beacons";
                                break;
                            case 3:
                                mRightFrag = DashboardFragment.newInstance();
                                title = "Dashboard";
                                break;
                            case 4:
                                mRightFrag = DealFragment.newInstance();
                                title = "Deals CloseBy";

                                // set right button
                                showNativationRightItem(R.drawable.icon_category_filter);

                                break;
                            case 5:
                                mRightFrag = BusinessFragment.newInstance();
                                title = "Shops CloseBy";
                                break;
                            case 6:
                                mRightFrag = MyMapFragment.newInstance();
                                title = "Map";
                                break;
                            case 7:
                                logout();
                                return;
                        }
                    } else { // customer
                        switch (position) {
                            case 0:
                                mRightFrag = DealFragment.newInstance();
                                title = "Deals";

                                // set right button
                                showNativationRightItem(R.drawable.icon_category_filter);

                                break;

                            case 1:
                                mRightFrag = BusinessFragment.newInstance();
                                title = "Shops CloseBy";
                                break;

                            case 2:
                                mRightFrag = MyMapFragment.newInstance();
                                title = "Map";
                                break;

                            case 3:
                                mRightFrag = FavouriteFragment.newInstance();
                                title = "Favourite";
                                break;

                            case 4:
                                mRightFrag = HistoryFragment.newInstance();
                                title = "History Notification";
                                break;
                            case 5:
                                logout();
                                return;
                        }
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, mRightFrag)
                            .commit();

                    setTitle(title);

                    mSlidingMenu.toggle();
                }

                mIndexSelect = position;
            }
        });

        boolean first = getIntent().getBooleanExtra("first", false);

        if (AppData.getInstance().isBusiness()) {
            if (first) {
                mIndexSelect = 0;
                mRightFrag = ProfileFragment.newInstance("");
            } else {
                mIndexSelect = 1;
                mRightFrag = InventoryFragment.newInstance();
            }
        } else {
            mIndexSelect = 0;
            mRightFrag = DealFragment.newInstance();
        }

        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, mLeftFrag).commit();

        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        mSlidingMenu.setShadowDrawable(R.drawable.reader_shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        // mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        getSlidingMenu().setMode(SlidingMenu.LEFT);

        setContentView(R.layout.frame_content);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, mRightFrag)
                .commit();


        // navigationg bar
        ImageView menuBtn = (ImageView) findViewById(R.id.nav_menu);
        menuBtn.setOnTouchListener(CustomButtonTouchListener.getInstance());
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggle();
            }
        });

        titleView = (TextView) findViewById(R.id.nav_title);


        actionBtn = (ImageView) findViewById(R.id.nav_action);
        actionBtn.setOnTouchListener(CustomButtonTouchListener.getInstance());
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleView.getText().toString();

                if (title.equalsIgnoreCase("profile")) {
                    // edit profile

                    gotoEditProfile();

                } else if (title.equalsIgnoreCase("Mechant Inventory")) {
                    // add inventory

                    gotoAddProduct(null);

                } else if (title.equalsIgnoreCase("Deals")) {

                    gotoInterest();

                }


            }
        });
        actionBtn.setVisibility(View.INVISIBLE);


        if (AppData.getInstance().isBusiness()) {

            if (first) {
                setTitle("Profile");

                // set right button
                showNativationRightItem(R.drawable.icon_edit);
            } else {
                setTitle("Mechant Inventory");
                showNativationRightItem(R.drawable.button_add);
            }
        } else {
            setTitle("Deals");
        }


//        setupBeacon();

    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void hideNativationRightItem() {
        actionBtn.setVisibility(View.INVISIBLE);
    }

    public void showNativationRightItem(int resID) {
        actionBtn.setVisibility(View.VISIBLE);
        actionBtn.setImageResource(resID);
    }


    public void logout() {

        AppData.getInstance().storeUsername("");
        AppData.getInstance().saveLoginUserID("");
        AppData.getInstance().storeUserImageUrl("");

        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }

    public void gotoInterest() {

        startActivityForResult(new Intent(this, ChooseInterestActivity.class), REQUEST_CHOOSE_INTEREST);

    }

    public void gotoEditProfile() {

        startActivity(new Intent(this, EditProfileActivity.class));

    }

    public void gotoProfile(String businessID) {

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("businessID", businessID);

        startActivity(intent);

    }

    public void gotoMap() {

        startActivity(new Intent(this, MapActivity.class));

    }

    public void gotoAddProduct(JSONObject product) {

        ProductItem item = new ProductItem();

        try {
            item.ID = product.getString("ID");
            item.ProductName = product.getString("ProductName");
            item.CategoryIds = product.getString("CategoryIds");
            item.OrigionalPrice = product.getInt("OrigionalPrice");
            item.ProductDescription = product.getString("ProductDescription");
            item.ProductPhoto = product.getString("ProductPhoto");

        } catch (Exception e) {
            e.printStackTrace();

            item = null;

        }

        Intent intent = new Intent(this, AddProductActivity.class);
        intent.putExtra("product", item);

        startActivity(intent);

    }

    public void gotoStandardDealSetting(JSONObject product) {

        ProductItem item = new ProductItem();

        try {
            item.ID = product.getString("ID");
//            item.ProductName = product.getString("ProductName");
//            item.CategoryIds = product.getString("CategoryIds");
//            item.OrigionalPrice = product.getInt("OrigionalPrice");
//            item.ProductDescription= product.getString("ProductDescription");
//            item.ProductPhoto = product.getString("ProductPhoto");

            item.DiscountedTagLing = product.getString("DiscountedTagLing");
            item.QuantityRemaining = product.getInt("QuantityRemaining");

        } catch (Exception e) {
            e.printStackTrace();

            item = null;

        }

        Intent intent = new Intent(this, SpecialSettingActivity.class);
        intent.putExtra("product", item);
        intent.putExtra("deal_option", 0);

        startActivity(intent);

    }

    public void gotoSpecialDealSetting(JSONObject product) {

        ProductItem item = new ProductItem();

        try {
            item.ID = product.getString("ID");
//            item.ProductName = product.getString("ProductName");
//            item.CategoryIds = product.getString("CategoryIds");
//            item.OrigionalPrice = product.getInt("OrigionalPrice");
//            item.ProductDescription= product.getString("ProductDescription");
//            item.ProductPhoto = product.getString("ProductPhoto");

            item.DiscountedTagLing = product.getString("DiscountedTagLing");
            item.QuantityRemaining = product.getInt("QuantityRemaining");
            item.SpecialPrice = product.getInt("SpecialPrice");
            item.DecayDuration = product.getInt("DecayDuration");

        } catch (Exception e) {
            e.printStackTrace();

            item = null;

        }

        Intent intent = new Intent(this, SpecialSettingActivity.class);
        intent.putExtra("product", item);
        intent.putExtra("deal_option", 1);

        startActivity(intent);

    }


    public void gotoFlashMessage(JSONObject beacon) {

        BeaconItem item = new BeaconItem();

        try {

            item.beaconUUID = beacon.getString("BeaconUUID");
            item.beaconMajor = beacon.getString("Major");
            item.beaconMinor = beacon.getString("Minor");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject beaconData = beacon.getJSONObject("BeaconData");

            item.MainTitle = beaconData.getString("MainTitle");
            item.TagLine = beaconData.getString("TagLine");
            item.Description = beaconData.getString("Description");
            item.FlashImage = beaconData.getString("FlashImage");

        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, FlashMessageActivity.class);
        intent.putExtra("beacon", item);

        startActivity(intent);

    }


    // Beacon
    private void setupBeacon() {
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.

//                        setTitle("Found beacons: " + beacons.size());

//                        getActionBar().setSubtitle("Found beacons: " + beacons.size());
//                        adapter.replaceWith(beacons);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (beaconManager != null)
            beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (beaconManager != null) {
            // Check if device supports Bluetooth Low Energy.
            if (!beaconManager.hasBluetooth()) {
                Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
                return;
            }

            // If Bluetooth is not enabled, let user enable it.
            if (!beaconManager.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                connectToBeacon();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_INTEREST) {
            if (resultCode == RESULT_OK) {
                boolean refresh = data.getBooleanExtra("refresh", false);

                if (refresh == true) {
                    if (mRightFrag instanceof DealFragment) {
                        DealFragment frag = (DealFragment) mRightFrag;
                        frag.refreshListView();
                    }
                }
            }
        }

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToBeacon();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToBeacon() {

        if (beaconManager == null)
            return;

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(HomeActivity.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void testBeacon(JSONObject item) {

        String UUID = "";
        int major = 0, minor = 0;

        try {
            UUID = item.getString("BeaconUUID");
            major = item.getInt("Major");
            minor = item.getInt("Minor");
        } catch (Exception e) {
            e.printStackTrace();
        }


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Region region = new Region("regionId", UUID, major, minor);

        if (beaconManager == null) {
            beaconManager = new BeaconManager(this);
        }

        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                postNotification("Entered region");
            }

            @Override
            public void onExitedRegion(Region region) {
                postNotification("Exited region");
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startMonitoring(region);
                } catch (RemoteException e) {
                    Log.d("", "Error while starting monitoring");
                }
            }
        });

    }

    private void postNotification(String msg) {
        Intent notifyIntent = new Intent(HomeActivity.this, HomeActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                HomeActivity.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(HomeActivity.this)
                .setSmallIcon(R.drawable.ic_clzby)
                .setContentTitle("Beacon entered")
                .setContentText(msg)
                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(123, notification);
    }
}

