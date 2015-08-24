package com.closeby.clzby.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.fragment.ProfileFragment;
import com.closeby.clzby.fragment.profile.BusinessAddressFragment;
import com.closeby.clzby.fragment.profile.BusinessDescriptionFragment;
import com.closeby.clzby.fragment.profile.BusinessOperateFragment;
import com.closeby.clzby.fragment.profile.BusinessProfileFragment;
import com.closeby.clzby.fragment.profile.BusinessTypeFragment;

public class EditProfileActivity extends FragmentActivity {


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


		// first
		addBusinessType();
	}


	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void addBusinessType() {

		BusinessTypeFragment newFragment = BusinessTypeFragment.newInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, newFragment)
				.commit();

	}
	public void addBusinessDescription() {

		BusinessDescriptionFragment newFragment = BusinessDescriptionFragment.newInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, newFragment)
				.addToBackStack("description")
				.commit();

	}

	public void addBusinessOperationHour() {

		BusinessOperateFragment newFragment = BusinessOperateFragment.newInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, newFragment)
				.addToBackStack("operation")
				.commit();

	}

	public void addBusinessProfile() {

		BusinessProfileFragment newFragment = BusinessProfileFragment.newInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, newFragment)
				.addToBackStack("profile")
				.commit();

	}

	public void addBusinessAddress() {

		BusinessAddressFragment newFragment = BusinessAddressFragment.newInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, newFragment)
				.addToBackStack("address")
				.commit();

	}

}
