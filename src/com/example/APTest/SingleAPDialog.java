package com.example.APTest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: hgong
 * Date: 24/05/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleAPDialog  extends Dialog {
    public SingleAPDialog(Context context, int theme) {
        super(context, theme);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_ap_config);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Timer closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismiss();
            }
        }, 60000);

        setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                closeTimer.cancel();
            }
        });
    }

    public SingleAPDialog(Context context) {
        this(context, 0);
    }
}
