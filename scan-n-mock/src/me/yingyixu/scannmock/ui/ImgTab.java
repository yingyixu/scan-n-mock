
package me.yingyixu.scannmock.ui;

import com.actionbarsherlock.app.SherlockFragment;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ImgTab extends SherlockFragment {

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        webView = new WebView(getActivity());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.requestFocus(View.FOCUS_DOWN);
        webView.loadUrl("http://74.82.1.95:9999/images/sim?filename=" + MainActivity.imgName);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        return webView;
    }

    // private void start() {
    // Comments.getImg(MainActivity.imgName, new AsyncHttpResponseHandler() {
    // @Override
    // public void onSuccess(String arg0) {
    // super.onSuccess(arg0);
    // webView.loadData(arg0, "text/html", "utf-8");
    // }
    //
    // @Override
    // public void onFailure(Throwable arg0, String arg1) {
    // super.onFailure(arg0, arg1);
    // }
    // });
    // }
}
