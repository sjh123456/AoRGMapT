package com.AoRGMapT.util;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AoRGMapT.R;

public class ChooseModeDialog {

    private AlertDialog alertDialog;

    private static ChooseModeDialog chooseModeDialog;

    public static ChooseModeDialog getIntent() {

        synchronized (ChooseModeDialog.class) {
            if (chooseModeDialog == null) {
                chooseModeDialog = new ChooseModeDialog();
            }
        }


        return chooseModeDialog;
    }

    public void showDialog(Context context, String title1, String title2, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_mode, null, false);
        View llContent = view.findViewById(R.id.ll_content);
//        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) llContent.getLayoutParams(); //取控件textView当前的布局参数
//        linearParams.width = context.getResources().getDisplayMetrics().widthPixels-300;// 控件的宽强制设成30
//        llContent.setLayoutParams(linearParams);
        builder.setView(view);
        TextView tv1 = view.findViewById(R.id.title1);
        if (!TextUtils.isEmpty(title1)) {
            tv1.setText(title1);
        }
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener1 != null) {
                    onClickListener1.onClick(v);
                }
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            }
        });
        TextView tv2 = view.findViewById(R.id.title2);
        if (!TextUtils.isEmpty(title2)) {
            tv2.setText(title2);
        }
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener2 != null) {
                    onClickListener2.onClick(v);
                }
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

}
