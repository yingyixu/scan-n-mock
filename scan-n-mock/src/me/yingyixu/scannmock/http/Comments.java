
package me.yingyixu.scannmock.http;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Comments {

    public static void getProduct(String code, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("barcode", code);
        ScanNMockClient.get("barcode/query", params, responseHandler);
    }

    public static void getCommnets(String product, int page,
            AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("q", product);
        params.put("page", String.valueOf(page));
        ScanNMockClient.get("comment/query", params, responseHandler);
    }

    public static void getNews(String product, int page, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("q", product);
        params.put("page", String.valueOf(page));
        ScanNMockClient.get("news/query", params, responseHandler);
    }

}
