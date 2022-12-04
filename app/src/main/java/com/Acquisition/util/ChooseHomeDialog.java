package com.Acquisition.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.Acquisition.BaseApplication;
import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.adapter.ChooseProjectAdapter;
import com.Acquisition.bean.ProjectBean;

public class ChooseHomeDialog {
    private Dialog mDialog;

    private static ChooseHomeDialog chooseImageDialog;

    private ProjectBean mProjectBean;

    public static ChooseHomeDialog getInstance() {
        synchronized (ChooseHomeDialog.class) {
            if (chooseImageDialog == null) {
                chooseImageDialog = new ChooseHomeDialog();
            }
        }
        return chooseImageDialog;
    }

    public ChooseHomeDialog setProjectBean(ProjectBean projectBean) {
        mProjectBean = projectBean;
        return this;
    }

    public void showDialog(Activity context, boolean isCurrent, View.OnClickListener onClickListener) {
        mDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_home, null, false);
        View llContent = view.findViewById(R.id.ll_content);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) llContent.getLayoutParams(); //取控件textView当前的布局参数

        linearParams.width = context.getResources().getDisplayMetrics().widthPixels;// 控件的宽强制设成30

        ChooseProjectAdapter adapter = new ChooseProjectAdapter(context);
        ListView listView = view.findViewById(R.id.ls_project);
        if (isCurrent) {
            adapter.setProjectBean(BaseApplication.currentProject);
        } else {
            adapter.setProjectBean(mProjectBean);
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (BaseApplication.projectBeanList != null && BaseApplication.projectBeanList.size() > position) {
                    if (isCurrent) {
                        BaseApplication.currentProject = BaseApplication.projectBeanList.get(position);
                    }
                    mDialog.dismiss();
                    view.setTag(BaseApplication.projectBeanList.get(position));
                    onClickListener.onClick(view);
                }

            }
        });

        llContent.setLayoutParams(linearParams);
        mDialog.setContentView(view);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = 20;
        dialogWindow.setAttributes(lp);
        mDialog.show();
    }
}
