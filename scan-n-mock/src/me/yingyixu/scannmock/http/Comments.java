
package me.yingyixu.scannmock.http;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

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

    public static void uploadImg(String filePath, String lang,
            AsyncHttpResponseHandler responseHandler) {
        File myFile = new File(filePath);
        RequestParams params = new RequestParams();
        try {
            params.put("pic", myFile);
            params.put("lang", lang);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScanNMockClient.post("images/upload", params, responseHandler);
    }

    public static void getImg(String name, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        try {
            params.put("filename", name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ScanNMockClient.get("images/sim", params, responseHandler);
    }
}
