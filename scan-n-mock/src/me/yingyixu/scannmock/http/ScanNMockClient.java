
package me.yingyixu.scannmock.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.HttpClientParams;

public class ScanNMockClient {
    private static final String BASE_URL = "http://74.82.1.95:9999/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        HttpClientParams.setRedirecting(client.getHttpClient().getParams(), true);
    }

    public static void get(String url, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
