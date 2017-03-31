package com.hsbc.share;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.jaeger.library.StatusBarUtil;
import com.jaeger.library.StatusBarUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.Plugin.pdfUpload.InvoicePushEvent;
import com.none.Push.uploadImage.domains.utils.HttpClientUtils;
import com.none.Push.uploadImage.domains.utils.MessageResult;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;
import com.none.staff.activity.StaffApplication;
import com.none.staff.util.AndroidBug5497Workaround;
import com.none.staff.util.VerifyUtils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;


public class ShareMainActivity extends BaseActivity {
    private Button submit;
    private EditText invoiceNoTx;

    private EditText expenseNoTx;

    private EditText userName;

    private TextView PdfTv;

    private EditText markTv;

    private String pathUrl;

    private String invoiceNoStr;

    private String expenseNoStr;

    private String markStr;

    private String userNameStr;

    protected long currentTime;
    private InputMethodManager im;//键盘管理者对象声明
    //Activity最外层的Layout视图
    private View activityRootView;
    private boolean isClicked = false;
    private String uploadURL;
    private View layout_confirming;
    private View layout_confirmed;
    private View back;
    private TextView rule;
    private View txt1,txt2;
    LinearLayout ll;
//    private void setStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(false);
//            tintManager.setStatusBarTintResource(R.color.cricle_color);//通知栏所需颜色
//        }
//    }
//
//
//    @TargetApi(19)
//    private void setTranslucentStatus(boolean on) {
//        Window win = getWindow();
//        WindowManager.LayoutParams winParams = win.getAttributes();
//        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//        if (on) {
//            winParams.flags |= bits;
//        } else {
//            winParams.flags &= ~bits;
//        }
//        win.setAttributes(winParams);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBar();
        setContentView(R.layout.share);
//        StatusBarUtil.setTransparent(this);
//        AndroidBug5497Workaround.assistActivity(this);
        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        StaffApplication application = (StaffApplication) getApplication();
        application.getActivityStack().put(String.valueOf(this.hashCode()), this);
        // ShareUploadDialog.showProgressShow(ShareMainActivity.this, "upload");
        // InfomationDialog.showInfoDialog(this,0);//TODO test
        //  InfomationDialog.showInfoDialog(this,1);
        //   UploadDialog.showUploadDialog(this,"Test.pdf");
        submit = (Button) findViewById(R.id.submit);
        layout_confirming=findViewById(R.id.upload_confirming);
        layout_confirmed=findViewById(R.id.upload_confirmed);
        back=findViewById(R.id.back);
        rule=(TextView)findViewById(R.id.rules);

        uploadURL = getResources().getString(R.string.uploadPDF_url);

        invoiceNoTx = (EditText) findViewById(R.id.invoiceNO);

        expenseNoTx = (EditText) findViewById(R.id.expenseNo);

        markTv = (EditText) findViewById(R.id.markTv);
        PdfTv = (TextView) findViewById(R.id.pdfname);

        txt1=findViewById(R.id.text1);
        txt2=findViewById(R.id.text2);

        //userName=(EditText)findViewById(R.id.username);

        pathUrl = getIntent().getStringExtra("pathUrl");
        userNameStr = getIntent().getStringExtra("username");
        // Log.d("bob",pat)
        String fileName="the file is not pdf type";
        if (isPDFtyple(pathUrl)) {
            fileName = pathUrl.substring(pathUrl.lastIndexOf("/") + 1);
            PdfTv.setText(fileName);
        }
        layout_confirming.setVisibility(View.VISIBLE);
        layout_confirmed.setVisibility(View.GONE);
        UploadDialog.showUploadDialog(this,fileName,layout_confirming,layout_confirmed);
//        ImageView help0=(ImageView)findViewById(R.id.invoiceNO_help);
//        ImageView help1=(ImageView)findViewById(R.id.expenseNo_help);
        initListener();
//        keyBoard();

//        help0.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                 InfomationDialog.showInfoDialog(ShareMainActivity.this,0);
//            }
//        });
//        help1.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                InfomationDialog.showInfoDialog(ShareMainActivity.this,1);
//
//            }
//        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new InvoicePushEvent("elnvoice"));
               // Toast.makeText(ShareMainActivity.this, "elnvoice", 1).show();
                finish();
            }
        });
        rule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              //  EventBus.getDefault().post(new InvoicePushEvent("rule"));
              //  Toast.makeText(ShareMainActivity.this, "rule", 1).show();
                startActivity(new Intent(ShareMainActivity.this,WebviewActivity.class));
              //  finish();
            }
        });
    }

    private void initListener() {
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadService();
            }
        });
        invoiceNoTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                submitEnable();
            }
        });
          expenseNoTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                submitEnable();
            }
        });
        invoiceNoTx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable=invoiceNoTx.getCompoundDrawables()[2];
                if (event.getAction() == MotionEvent.ACTION_UP && drawable != null) {
                    Rect rBounds = drawable.getBounds();
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();
                    if (x >= (invoiceNoTx.getWidth() - rBounds.width()-10)
                            && x <= (invoiceNoTx.getWidth() - invoiceNoTx.getPaddingRight()+10)
                            && y >= (invoiceNoTx.getPaddingTop()-10)
                            && y <= (invoiceNoTx.getHeight() -invoiceNoTx.getPaddingBottom()+10)) {
                        InfomationDialog.showInfoDialog(ShareMainActivity.this,0);
                        return true;
                    }

                }
                return false;
            }
        });
        expenseNoTx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable=expenseNoTx.getCompoundDrawables()[2];
                if (event.getAction() == MotionEvent.ACTION_UP && drawable != null) {
                    Rect rBounds = drawable.getBounds();
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();
                    if (x >= (expenseNoTx.getWidth() - rBounds.width()-10)
                            && x <= (expenseNoTx.getWidth() - expenseNoTx.getPaddingRight()+10)
                            && y >= (expenseNoTx.getPaddingTop()-10)
                            && y <= (expenseNoTx.getHeight() -expenseNoTx.getPaddingBottom()+10)) {
                        InfomationDialog.showInfoDialog(ShareMainActivity.this,1);
                        return true;
                    }

                }
                return false;
            }
        });
        invoiceNoTx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               String str= invoiceNoTx.getText().toString().trim();
                if(!hasFocus) {
                    if (str.length() > 0 && !VerifyUtils.isNum(str)) {
                        txt1.setVisibility(View.VISIBLE);
                    }else if(str.length() > 0 && VerifyUtils.isNum(str)){
                        txt1.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        expenseNoTx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String str= expenseNoTx.getText().toString().trim();
                if(!hasFocus) {
                    if (str.length() > 0 && !VerifyUtils.isExpenseNumber(str)) {
                        txt2.setVisibility(View.VISIBLE);
                    }else if(str.length() > 0 && VerifyUtils.isExpenseNumber(str)){
                        txt2.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        //

    }
    void submitEnable()
    {
        if (invoiceNoTx.getText().toString().trim().length() > 0
                &&  expenseNoTx.getText().toString().trim().length() > 0
                && VerifyUtils.isNum(invoiceNoTx.getText().toString().trim())
                && VerifyUtils.isExpenseNumber(expenseNoTx.getText().toString().trim())) {
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
        }
    }


    public int s = 0;

    public void UploadService() {

        // userNameStr=userName.getText().toString();

        invoiceNoStr = invoiceNoTx.getText().toString();

        expenseNoStr = expenseNoTx.getText().toString();

        markStr = markTv.getText().toString();

        if (ValidateForm()) {

            currentTime = ShareUtils.getCurrentTime();

            HashMap<String, Object> params = new HashMap<String, Object>();

            params.put("invoiceNo", invoiceNoStr);

            params.put("expenseNo", toUpperCaseFirstOne(expenseNoStr));

            params.put("batch_flag", currentTime);

            params.put("userId", userNameStr);

            params.put("type", "eInvoice");

            params.put("remark", markStr);

            params.put("multipartFile", new File(pathUrl));

            Log.e("params", params + "");

            HttpClientUtils.getInstance().post(uploadURL, null, params, this, new AsyncHttpResponseHandler() {

                private ShareUploadDialog ProgressBar;

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                      Throwable arg3) {
                    Log.d("bob pdf", uploadURL);

                    ShareUploadDialog.stopProgressDiaog(false);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ShareMainActivity.this, getResources().getString(R.string.networkerror), 1).show();
                        }

                    });


                }

                @Override
                public void onProgress(int bytesWritten, int totalSize) {
                    // TODO Auto-generated method stub
                    super.onProgress(bytesWritten, totalSize);
/*					ProgressBar.dialog_cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
                            Log.d("bob pdf",uploadURL);


                            Log.i("0000000", "onProgress onProgress onProgress");
							//
							isClicked=true;

							ProgressBar.stopProgressDiaog();
						}
					});*/
                }

                @Override
                public void onStart() {
                    Log.d("bob pdf", uploadURL);


                    super.onStart();
                    //

                    ProgressBar = ShareUploadDialog.showProgressShow(ShareMainActivity.this, "upload");

	/*				ShareUploadDialog.dialog_cancel.setOnClickListener(new OnClickListener() {
                        @Override
						public void onClick(View arg0) {
							isClicked=true;
							ProgressBar.stopProgressDiaog();
						}
					});*/
                }


                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
                    Log.d("bob pdf", uploadURL);

                    String jsonString = new String(bytes);

                    Log.e("result code=", jsonString);
                    MessageResult messageResult = MessageResult.prase(jsonString);
                    int code = messageResult.getCode();
                    final String result = messageResult.getMsg();
                    if (code == 0) {

                        //	try {


                        // String serviceResult=	messageResult.getData();


                        //	if(!serviceResult.isEmpty()){

                        //	Log.e("serviceResult=", serviceResult) ;

                        //	JSONObject	object = new JSONObject(serviceResult) ;

                        //if(object.has("result")){

                        // final String result=object.getString("result");

                        if (result.equals("uploadSuccess")) {
								
		/*						runOnUiThread(new Runnable()
						        {
						            public void run()
						            {    ShareUploadDialog.stopProgressDiaog(true);
                                        EventBus.getDefault().post(new InvoicePushEvent(result));
						            	Toast.makeText(ShareMainActivity.this, result, 1).show();
						            	finish();
						            }

						        });   */
                            ShareUploadDialog.stopProgressDiaog(true);
                            ProgressBar.dialog_ok.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            EventBus.getDefault().post(new InvoicePushEvent(result));
                                            Toast.makeText(ShareMainActivity.this, "Upload Success", 1).show();
                                            finish();
                                        }

                                    });
                                }
                            });

                        } else {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ShareUploadDialog.stopProgressDiaog(false);
                                    Toast.makeText(ShareMainActivity.this, result, 1).show();
                                    //finish();
                                }

                            });
                        }
                        //	}
                        //	}

		/*			} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/

                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ShareUploadDialog.stopProgressDiaog(false);
                                Toast.makeText(ShareMainActivity.this, result, 1).show();
                                //   finish();
                            }

                        });
                    }
                }

            });

        }
    }

    //判断是不是pdf格式文件
    public boolean isPDFtyple(String str) {


        if (str == null || str.length() == 0) {

            Toast.makeText(this, "请选择出入范围！", Toast.LENGTH_SHORT).show();
            return false;
        }

        String fileType = str.substring(str.lastIndexOf(".") + 1);

        if (fileType.equals("pdf")) {


            FileInputStream fis = null;
            long fileSize = 0;
            try {
                fis = new FileInputStream(str);
                fileSize = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("fileSize=", fileSize + "");

            if (fileSize <= 2 * 1024 * 1024) {

                return true;

            } else {
                Toast.makeText(this, getResources().getString(R.string.choosepdfSize), Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {

            Toast.makeText(this, getResources().getString(R.string.choosepdf), Toast.LENGTH_SHORT).show();
            return false;
        }


    }


    /**
     * 判断 是否是数字和字符  并且长度为9
     *
     * @param str
     * @return
     */
    public boolean isNumberAndLetter(String str) {

        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{9}$");
        return pattern.matcher(str).matches();


    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    // 首字母是不是e and d
    public Boolean isEorD(String s) {

        String str = s.charAt(0) + "";

        String NeedStr = str.toLowerCase().trim();


        if (NeedStr.equals("e") && NeedStr.equals("d")) {

            return true;
        } else {

            return false;
        }

    }


    private boolean ValidateForm() {


       /* if (userNameStr == null || userNameStr.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.inputusername), Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if (invoiceNoStr == null || invoiceNoStr.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.inputinvoice), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (expenseNoStr == null || expenseNoStr.length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.inputexpenseNo), Toast.LENGTH_SHORT).show();
            return false;
        }

//        else if(!isEorD(expenseNoStr)){
//        	
//       	  Toast.makeText(this,getResources().getString(R.string.doaForm),Toast.LENGTH_SHORT).show();
//            return false;
//       	
//       }else 

//    	   if(!isNumberAndLetter(expenseNoStr)){
//        	
//             Toast.makeText(this,getResources().getString(R.string.doawrong),Toast.LENGTH_SHORT).show();
//            return false;
//
//        }


 /*       if (markStr == null || markStr.length() == 0) {

            Toast.makeText(this, getResources().getString(R.string.inputremarks), Toast.LENGTH_SHORT).show();

            return false;
        }
*/
        return true;

    }
    ScrollView scrollView;
//private void keyBoard(){
////    markTv.setOnClickListener(new OnClickListener() {
////        @Override
////        public void onClick(View v) {
////            markTv.setFocusable(true);
////        }
////    });
//    scrollView = (ScrollView) findViewById(R.id.ScrollView);
//    scrollView.setOnTouchListener(new View.OnTouchListener(){
//        @Override
//        public boolean onTouch(View arg0, MotionEvent arg1) {
//            markTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(hasFocus){//获得焦点
//                        scrollView.scrollTo(0,300);
//                        return;
//                    }else{//失去焦点
//                        scrollView.scrollTo(0,0);
//                        return;
//                    }
//                }
//            });
//            return true;
//
//        }
//
//    });
//    if (!im.isActive()){
//        scrollView.scrollTo(0,0);
//    }
//    markTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if(hasFocus){//获得焦点
//                            scrollView.scrollTo(0,300);
//                scrollView.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return false;
//                    }
//                });
//            }else{//失去焦点
//                scrollView.setOnTouchListener(new View.OnTouchListener(){
//                    @Override
//                    public boolean onTouch(View arg0, MotionEvent arg1) {
//                        return true;
//                    }
//
//                });
//                scrollView.scrollTo(0,0);
//
//            }
//        }
//    });
//    markTv.setOnClickListener(new OnClickListener() {
//        @Override
//        public void onClick(View v) {
////            if(!im.isActive()){
////                im.showSoftInput(markTv, 0);
////            }
//            markTv.setFocusable(true);//设置输入框可聚集
//            markTv.setFocusableInTouchMode(true);//设置触摸聚焦
//            markTv.requestFocus();//请求焦点
//            markTv.findFocus();//获取焦点
//            im.showSoftInput(markTv,0);
//            scrollView.scrollTo(0,300);
//        }
//    });
//}
//    /**
//     * 监听点击出edittext之外的区域 隐藏软键盘
//     * @param ev
//     * @return
//     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
//            View v = getCurrentFocus();
//            if (isShouldHideInput(v, ev)) {
//                markTv.setFocusable(false);//设置输入框不可聚焦，即失去焦点和光标
//                scrollView.scrollTo(0,0);
//                hideSoftInput(v.getWindowToken());
//            }
////            else{
////                markTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
////                    @Override
////                    public void onFocusChange(View v, boolean hasFocus) {
////                        if(hasFocus){//获得焦点
////                            scrollView.scrollTo(0,300);
////
////                        }else{//失去焦点
////
////                            scrollView.scrollTo(0,0);
////                        }
////                    }
////                });
////            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    /**
//     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
//     *
//     * @param v
//     * @param event
//     * @return
//     */
//    private boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] l = { 0, 0 };
//            v.getLocationInWindow(l);
//            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
//                    + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                // 点击EditText的事件，忽略它。
//                return false;
//            } else {
//                return true;
//            }
//        }
//        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
//        return false;
//    }
//
//    /**
//     * 多种隐藏软件盘方法的其中一种
//     *
//     * @param token
//     */
//    private void hideSoftInput(IBinder token) {
//        if (token != null) {
////           im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.hideSoftInputFromWindow(token,
//                    InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
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
    /**
     * statusbar
     *
     * @param enable
     *            false 显示，true 隐藏
     */
    private void full(boolean enable) {
        // TODO Auto-generated method stub
        WindowManager.LayoutParams p = this.getWindow().getAttributes();
        if (enable) {
            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;// |=：或等于，取其一
        } else {
            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);// &=：与等于，取其二同时满足，
            // ~ ：
            // 取反

        }
        getWindow().setAttributes(p);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
