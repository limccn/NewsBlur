package com.slickreader.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.slickreader.R;

public class ImportFeeds extends NbFragmentActivity {
	
	private WebView webContainer;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webcontainer);
		
		webContainer = (WebView) findViewById(R.id.webcontainer);
		webContainer.getSettings().setJavaScriptEnabled(true);
		
		webContainer.setWebViewClient(new WebViewClient() {
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		    	if (TextUtils.equals(url, "https://www.slickreader.com/")) {
		    		ImportFeeds.this.setResult(RESULT_OK);
		    		ImportFeeds.this.finish();
		    		return true;
		    	}
		        view.loadUrl(url);
		        return false;
		   }
		});
		
		webContainer.loadUrl("https://www.slickreader.com/import/authorize/");
		
	}

}
