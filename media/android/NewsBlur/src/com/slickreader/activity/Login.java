package com.slickreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.slickreader.R;
import com.slickreader.fragment.LoginRegisterFragment;
import com.slickreader.util.PrefConstants;

public class Login extends FragmentActivity {
	
	private FragmentManager fragmentManager;
	private final static String currentTag = "currentFragment";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferenceCheck();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		fragmentManager = getSupportFragmentManager();
		
		if (fragmentManager.findFragmentByTag(currentTag) == null) {
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			LoginRegisterFragment login = new LoginRegisterFragment();
			transaction.add(R.id.login_container, login, currentTag);
			transaction.commit();
		}
	}

	private void preferenceCheck() {
		final SharedPreferences preferences = getSharedPreferences(PrefConstants.PREFERENCES, Context.MODE_PRIVATE);
		if (preferences.getString(PrefConstants.PREF_COOKIE, null) != null) {
			final Intent mainIntent = new Intent(this, Main.class);
			startActivity(mainIntent);
		}
	}
	

}
