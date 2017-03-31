package com.hsbc.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ShareSysUtils {
    public static ShareSysUtils sysShareUtils;
    private Activity activity;
    private OnShareDataOkListener listener;
    public static int TYPE_TEXT = 0, TYPE_IMAGE = 1,TYPE_PDF=2;

    public ShareSysUtils(Activity activity) {
        this.activity = activity;
    }


    public static ShareSysUtils get(Activity activity) {
        if (sysShareUtils == null) {
            synchronized (ShareSysUtils.class) {
                if (sysShareUtils == null) {
                    sysShareUtils = new ShareSysUtils(activity);
                }
            }
        }
        return sysShareUtils;
    }

    /**
     * 作为接受分享的一方,处理分享来的数据
     *
     */
    public void handleShare(OnShareDataOkListener listener) {
        this.listener = listener;
        Intent intent = activity.getIntent();
        String action = intent.getAction();

        String type = intent.getType();
        if (null == action || type == null) {
            Log.e("chendong", "没有检索到分享数据");
            return;
        }
        if (Intent.ACTION_SEND.equals(action) && type != null) {
             if (type.startsWith("image/")) {
                handleSendImage(intent);

            }else if (type.startsWith("application/")){

                handleSendPdf(intent);

            }
        } 
    }


    /**
     * 分享单张图片
     *
     * @param path 图片的路径
     */
    public void shareSingleImage(String path) {
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(path));
//        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    


    private void handleListener(int type, List<String> list, String title, String content) {
        if (listener != null) {
            listener.OnHandleOk(type, list, title, content);
        }
    }

    

    /**
     * 处理分享的单张照片
     *
     * @param intent
     */
    private void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri == null)
            return;
        List<String> list = new ArrayList<String>();
        list.add(ShareUtils.getRealPathFromURI(activity, imageUri));
        handleListener(TYPE_IMAGE, list, null, null);
    }

    /**
     * 处理分享的pdf
     *
     * @param intent
     */
    private void  handleSendPdf(Intent intent){

        Uri pdfUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

//        Log.e("pdfUri=",pdfUri.toString());

        if (pdfUri == null)
            return;
        List<String> list = new ArrayList<String>();
        list.add(ShareUtils.getRealPathFromURI(activity, pdfUri));
        handleListener(TYPE_PDF, list, null, null);

    }

    public interface OnShareDataOkListener {
        void OnHandleOk(int type, List<String> list, String title, String content);
    }
}