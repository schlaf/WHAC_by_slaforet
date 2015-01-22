package com.schlaf.steam.activities.donation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.schlaf.steam.R;
import com.schlaf.steam.util.IabHelper;
import com.schlaf.steam.util.IabHelper.OnConsumeFinishedListener;
import com.schlaf.steam.util.IabHelper.OnIabPurchaseFinishedListener;
import com.schlaf.steam.util.IabHelper.OnIabSetupFinishedListener;
import com.schlaf.steam.util.IabHelper.QueryInventoryFinishedListener;
import com.schlaf.steam.util.IabResult;
import com.schlaf.steam.util.Inventory;
import com.schlaf.steam.util.Purchase;

public class DonationActivity extends ActionBarActivity implements
		OnIabSetupFinishedListener, QueryInventoryFinishedListener,
		OnIabPurchaseFinishedListener, OnConsumeFinishedListener {

	IabHelper mHelper;
	private static final String TAG = "DonationActivity";

	private static final String SKU_SMALL_DONATION = "small_donation";
	private static final String SKU_MEDIUM_DONATION = "medium_donation";
	private static final String SKU_BIG_DONATION = "big_donation";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donation);

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmmvhRuXbIzTIK2Ilx51w79YuH4fRYTBpe20E3HjBTMZ/IxHi6uF2GzSrW/EPE2Q9cPnDI0hG+5mwmZbF5lUH2IxXgc0hUHO2Qx89Ju0KhtHKsBdyDkVmefG3h2wAJUjtjXm7MVnuCxlk5Hwvr3ewWKih1BjHWHZYH3DqU4+J5J5lRoxVHkZp7ZX8k3Qzn2Pkq7pEjwfkagC9R7GcjL1EnFsBaIz2nkPu0zGOXAX0tMLVyl/BGgzNlhoV3uQGJ1x/0wlXluqxUMEHz6s3ddMhhLpWI/80U5KmIjHJyUSc+/B8Obp99th0wrEMC51znISXYpYpRhMuXbv2Pwadyg5LGwIDAQAB";
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		getSupportActionBar().setTitle(R.string.donate);
		getSupportActionBar().setLogo(R.drawable.donate);

		mHelper.startSetup(this);

		findViewById(R.id.button_small_donate).setClickable(false);
		findViewById(R.id.button_medium_donate).setClickable(false);
		findViewById(R.id.button_big_donate).setClickable(false);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) {
			try {
				mHelper.dispose();	
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
			
		mHelper = null;
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		// TODO Auto-generated method stub
		if (result.isFailure()) {
			// handle error
			Toast.makeText(this, R.string.in_app_billing_failure,
					Toast.LENGTH_SHORT).show();
			return;
		}

		// update the UI
		if (inventory.hasDetails(SKU_SMALL_DONATION)) {
			String smallPrice = inventory.getSkuDetails(SKU_SMALL_DONATION)
					.getPrice();
			findViewById(R.id.button_small_donate).setClickable(true);
			((Button) findViewById(R.id.button_small_donate))
					.setText(getResources().getText(R.string.small_donation)
							+ " (" + smallPrice + ")");
		}
		if (inventory.hasDetails(SKU_MEDIUM_DONATION)) {
			String mediumPrice = inventory.getSkuDetails(SKU_MEDIUM_DONATION)
					.getPrice();
			findViewById(R.id.button_medium_donate).setClickable(true);
			((Button) findViewById(R.id.button_medium_donate))
					.setText(getResources().getText(R.string.medium_donation)
							+ " (" + mediumPrice + ")");
		}
		if (inventory.hasDetails(SKU_BIG_DONATION)) {
			String bigPrice = inventory.getSkuDetails(SKU_BIG_DONATION)
					.getPrice();
			findViewById(R.id.button_big_donate).setClickable(true);
			((Button) findViewById(R.id.button_big_donate))
					.setText(getResources().getText(R.string.big_donation)
							+ " (" + bigPrice + ")");
		}

	}

	public void purchaseSmall(View v) {
		mHelper.launchPurchaseFlow(this, SKU_SMALL_DONATION, 454645, this,
				"small_donation_done");
	}

	public void purchaseMedium(View v) {
		mHelper.launchPurchaseFlow(this, SKU_MEDIUM_DONATION, 454646, this,
				"medium_donation_done");
	}

	public void purchaseBig(View v) {
		mHelper.launchPurchaseFlow(this, SKU_BIG_DONATION, 454647, this,
				"big_donation_done");
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if (result.isFailure()) {
			Log.d(TAG, "Error purchasing: " + result);
			Toast.makeText(this, "Error while purchasing: " + result, Toast.LENGTH_SHORT).show();
			return;
		} else if (info.getSku().equals(SKU_SMALL_DONATION)) {
			Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
		} else if (info.getSku().equals(SKU_MEDIUM_DONATION)) {
			Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
		} else if (info.getSku().equals(SKU_BIG_DONATION)) {
			Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
		}
		
		if (result.isSuccess()) {
			// immediately consume..
			mHelper.consumeAsync(info, this);
		}
 
	}

	@Override
	public void onIabSetupFinished(IabResult result) {
		if (!result.isSuccess()) {
			// Oh noes, there was a problem.
			Toast.makeText(this, R.string.in_app_billing_not_available,
					Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Problem setting up In-app Billing: " + result);

		} else {
			List<String> products = new ArrayList<String>();
			products.add(SKU_SMALL_DONATION);
			products.add(SKU_MEDIUM_DONATION);
			products.add(SKU_BIG_DONATION);

			mHelper.queryInventoryAsync(true, products, this);
		}
	}

	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		// do nothing
	}

}
