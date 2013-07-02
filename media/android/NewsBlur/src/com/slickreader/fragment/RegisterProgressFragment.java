package com.slickreader.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.slickreader.R;
import com.slickreader.activity.AddSites;
import com.slickreader.activity.Login;
import com.slickreader.activity.LoginProgress;
import com.slickreader.network.APIManager;
import com.slickreader.network.domain.LoginResponse;

public class RegisterProgressFragment extends Fragment {

	private APIManager apiManager;
	private String TAG = "LoginProgress";

	private String username;
	private String password;
	private String email;
	private RegisterTask registerTask;
	private ViewSwitcher switcher;
	private Button next;
	private ImageView registerProgressLogo;

	public static RegisterProgressFragment getInstance(String username, String password, String email) {
		RegisterProgressFragment fragment = new RegisterProgressFragment();
		Bundle bundle = new Bundle();
		bundle.putString("username", username);
		bundle.putString("password", password);
		bundle.putString("email", email);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		apiManager = new APIManager(getActivity());

		username = getArguments().getString("username");
		password = getArguments().getString("password");
		email = getArguments().getString("email");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_registerprogress, null);
		switcher = (ViewSwitcher) v.findViewById(R.id.register_viewswitcher);

		registerProgressLogo = (ImageView) v.findViewById(R.id.registerprogress_logo);
		registerProgressLogo.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));

		next = (Button) v.findViewById(R.id.registering_next_1);

		if (registerTask != null) {
			switcher.showNext();
		} else {
			registerTask = new RegisterTask();
			registerTask.execute();
		}

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), AddSites.class);
				startActivity(i);
			}
		});

		return v;
	}

	private class RegisterTask extends AsyncTask<Void, Void, Boolean> {

		private LoginResponse response;

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// We include this wait simply as a small UX convenience. Otherwise the user could be met with a disconcerting flicker when attempting to register and failing.
				Thread.sleep(700);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error sleeping during login.");
			}
			response = apiManager.signup(username, password, email);
			return response.code != -1 && response.authenticated;
		}

		@Override
		protected void onPostExecute(Boolean hasFolders) {
			if (response.code != -1 && response.authenticated) {
				if (!hasFolders.booleanValue()) {
					switcher.showNext();
				} else {
					Intent i = new Intent(getActivity(), LoginProgress.class);
					i.putExtra("username", username);
					i.putExtra("password", password);
					startActivity(i);
				}
			} else {
				Toast.makeText(getActivity(), extractErrorMessage(response), Toast.LENGTH_LONG).show();
				startActivity(new Intent(getActivity(), Login.class));
			}
		}

		private String extractErrorMessage(LoginResponse response) {
			String errorMessage = null;
			if(response.errors != null) {
				if(response.errors.email != null && response.errors.email.length > 0) {
					errorMessage = response.errors.email[0];
				} else if(response.errors.username != null && response.errors.username.length > 0) {
					errorMessage = response.errors.username[0];
				} else if(response.errors.message != null && response.errors.message.length > 0) {
					errorMessage = response.errors.message[0];
				}
			}
			if(errorMessage == null) {
				errorMessage = getResources().getString(R.string.login_message_error);
			}
			return errorMessage;
		}
	}


}
