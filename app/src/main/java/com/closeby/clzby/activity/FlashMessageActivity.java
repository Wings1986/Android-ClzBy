package com.closeby.clzby.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.fragment.FlashMessageFragment;
import com.closeby.clzby.fragment.SpecialDealFragment;
import com.closeby.clzby.fragment.StandardDealFragment;
import com.closeby.clzby.model.BeaconItem;
import com.closeby.clzby.model.ProductItem;

public class FlashMessageActivity extends FragmentActivity {

	TextView tvTitle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_content);

		// navigation button
		ImageView btnBack = (ImageView) findViewById(R.id.nav_back);
		btnBack.setOnTouchListener(CustomButtonTouchListener.getInstance());
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
					getSupportFragmentManager().popBackStack();
				}
				else {
					onBackPressed();
				}
			}
		});

		tvTitle = (TextView) findViewById(R.id.nav_title);
		setTitle("");


		BeaconItem beacon = (BeaconItem) getIntent().getExtras().getSerializable("beacon");

		FlashMessageFragment newFragment = FlashMessageFragment.newInstance(beacon);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, newFragment)
				.commit();

	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

}
