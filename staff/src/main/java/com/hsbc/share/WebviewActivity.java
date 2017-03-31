package com.hsbc.share;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.Push.uploadImage.domains.utils.HttpClientUtils;
import com.none.Push.uploadImage.domains.utils.MessageResult;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebviewActivity extends BaseActivity {
    private WebView webview;
    private static final String TAG = "WebviewActivity";

    private String getDataURL;
    private View back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_rules);
        webview = (WebView) findViewById(R.id.webview);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDataURL = getResources().getString(R.string.getInvoiceRules_url);
        HttpClientUtils.getInstance().post(getDataURL, null, null, this, new AsyncHttpResponseHandler() {


            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(WebviewActivity.this, "Networking failed", Toast.LENGTH_LONG).show();
                        finish();
                    }

                });


            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                // TODO Auto-generated method stub
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {
                Log.d(TAG, getDataURL);
                super.onStart();

            }


            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
                Log.d(TAG, getDataURL);
                String jsonString = new String(bytes);
                Log.e("result code=", jsonString);
                MessageResult messageResult = MessageResult.prase(jsonString);
                int code = messageResult.getCode();
                final String msg = messageResult.getMsg();
                final String data = messageResult.getData();
                try {
                    JSONObject dataobject = new JSONObject(data);
                    String result = dataobject.getString("result");
                    JSONArray array = new JSONArray(result);
                    String json = array.getString(0);
                    JSONObject object = new JSONObject(json);
                    final String content = object.getString("memo");

                     if (code == 0) {
                    //   if (result.equals("SuccessFul!")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            webview.loadData(content, "text/html; charset=utf-8", "UTF-8");
                        }

                    });

                    } else {
                         runOnUiThread(new Runnable() {
                             public void run() {
                                 Toast.makeText(WebviewActivity.this, msg, Toast.LENGTH_LONG).show();
                                 finish();
                             }
                         });

                     }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 动态获取设备NavigationBar的高度
     * @return
     */
    public int getNavigationBarHeight() {

        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        else{
            return 0;
        }
    }
}
