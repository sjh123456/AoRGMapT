package com.AoRGMapT.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.AoRGMapT.R;
import com.AoRGMapT.WellLocationDeterminationActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class ChooseImageDialog {

    private Dialog mImageDialog;

    private static ChooseImageDialog chooseImageDialog;

    public static ChooseImageDialog getInstance() {
        synchronized (ChooseImageDialog.class) {
            if (chooseImageDialog == null) {
                chooseImageDialog = new ChooseImageDialog();
            }
        }
        return chooseImageDialog;
    }


    public void showDialog(Activity context) {
        mImageDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_image, null, false);
        View llContent = view.findViewById(R.id.ll_content);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) llContent.getLayoutParams(); //取控件textView当前的布局参数

        linearParams.width = context.getResources().getDisplayMetrics().widthPixels - 100;// 控件的宽强制设成30
        view.findViewById(R.id.tv_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                context.startActivityForResult(i, 100);
                if (mImageDialog != null) {
                    mImageDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                context.startActivityForResult(intent, 200);
                if (mImageDialog != null) {
                    mImageDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageDialog != null) {
                    mImageDialog.dismiss();
                }
            }
        });
        llContent.setLayoutParams(linearParams);
        mImageDialog.setContentView(view);
        Window dialogWindow = mImageDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = 20;
        dialogWindow.setAttributes(lp);
        mImageDialog.show();

    }


    /*
  检测文件权限
   */
    public  void show(Activity activity) {

        if (!XXPermissions.isGranted(activity, Permission.MANAGE_EXTERNAL_STORAGE)) {
            XXPermissions.with(activity)
                    // 不适配 Android 11 可以这样写
                    //.permission(Permission.Group.STORAGE)
                    // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            showDialog(activity);
                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            Toast.makeText(activity, "请先打开文件权限", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            showDialog(activity);
        }
    }


}
