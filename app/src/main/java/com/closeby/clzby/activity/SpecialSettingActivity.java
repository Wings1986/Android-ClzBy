package com.closeby.clzby.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.closeby.clzby.R;
import com.closeby.clzby.customcontrol.CustomButtonTouchListener;
import com.closeby.clzby.fragment.AddProductFragment;
import com.closeby.clzby.fragment.SpecialDealFragment;
import com.closeby.clzby.fragment.StandardDealFragment;
import com.closeby.clzby.model.ProductItem;

public class SpecialSettingActivity extends FragmentActivity {

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


		ProductItem product = (ProductItem) getIntent().getExtras().getSerializable("product");
		int option = getIntent().getExtras().getInt("deal_option");

		if (option == 0) { // standard
			StandardDealFragment newFragment = StandardDealFragment.newInstance(product);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, newFragment)
					.commit();
		}
		else {
			SpecialDealFragment newFragment = SpecialDealFragment.newInstance(product);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, newFragment)
					.commit();
		}


	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

}
