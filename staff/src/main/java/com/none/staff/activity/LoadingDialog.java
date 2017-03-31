
package com.none.staff.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.none.staff.R;

public class LoadingDialog extends ProgressDialog {
    public LoadingDialog(Context context,String message) {
        super(context,R.style.dialog_not_dim);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog_progress);
    }
}
