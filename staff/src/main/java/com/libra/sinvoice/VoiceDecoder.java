/*
 * Copyright (C) 2014 gujicheng
 * 未经作者许可，禁止将该程序用于商业用途
 * 
 *************************************************************************
 **                   作者信息                                                            **
 *************************************************************************
 ** Email: gujicheng197@126.com                                        **
 ** QQ   : 29600731                                                                 **
 ** Weibo: http://weibo.com/gujicheng197                          **
 *************************************************************************
 */
package com.libra.sinvoice;

public class VoiceDecoder {
    private Callback mCallback;

    public final static int VOICE_DECODER_START = -1;
    public final static int VOICE_DECODER_END = -2;

    public static interface Callback {
        void onVoiceDecoderResult(int index);
    }

    public native void initVR(String companyId, String appId);

    public native void startVR(int sampleRate, int tokenLen);

    public native void putData(byte[] data, int bytesLen);

    public native void stopVR();

    public native void uninitVR();

    public VoiceDecoder(Callback callback) {
        mCallback = callback;
    }

    private void onRecognized(int index) {
        LogHelper.d("VoiceRecognition", "onRecognized:" + index);
        if (null != mCallback) {
            mCallback.onVoiceDecoderResult(index);
        }
    }

}
