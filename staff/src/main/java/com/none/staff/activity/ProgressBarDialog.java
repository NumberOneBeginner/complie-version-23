package com.none.staff.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.none.staff.R;

public class ProgressBarDialog extends ProgressDialog {
    private ProgressBar progressbar=null;
    private TextView persentageText=null;

    public ProgressBarDialog(Context context,String message) {
        super(context, R.style.dialog_not_dim);
    }

    public ProgressBarDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_progress_dialog);
        progressbar=((ProgressBar) findViewById(R.id.progressBar));
        persentageText = ((TextView) findViewById(R.id.progressPercent));
        persentageText.setText("0%");
        progressbar.setMax(100);
        progressbar.setProgress(0);
        ((TextView)findViewById(R.id.progressBarTitle)).setText(R.string.update_download_msg);
    }

    public void updateProgress(int progress){
        progress=progressbar.getProgress()+progress;
        progressbar.setProgress(progress);
        persentageText.setText(progress+"%");
    }
    public void setProgress(int progress){
        progressbar.setProgress(progress);
        persentageText.setText(progress+"%");
    }
}
