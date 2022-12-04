package com.Acquisition.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Acquisition.BaseApplication;
import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.bean.ProjectBean;

import java.util.ArrayList;
import java.util.List;

public class ChooseProjectAdapter extends BaseAdapter {


    private List<ProjectBean> projectBeanList = new ArrayList<>();
    private Context context;
    private ProjectBean currentBean;

    public ChooseProjectAdapter(Context context) {
        if (BaseApplication.projectBeanList != null) {
            projectBeanList = BaseApplication.projectBeanList;
        }
        this.context = context;
    }

    public void setProjectBean(ProjectBean projectBean) {
        this.currentBean = projectBean;
    }


    @Override
    public int getCount() {
        return projectBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(this.context, R.layout.dialog_choose_home_item, null);
        TextView textView = view.findViewById(R.id.tv_name);
        ImageView imageView = view.findViewById(R.id.iv_choose);
        ProjectBean bean = BaseApplication.projectBeanList.get(position);
        if (this.currentBean != null && TextUtils.equals(bean.getId(), this.currentBean.getId())) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
        textView.setText(bean.getProjectName());

        return view;
    }
}
