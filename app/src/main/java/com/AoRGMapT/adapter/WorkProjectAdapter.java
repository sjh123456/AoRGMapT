package com.AoRGMapT.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.AchievementAcceptanceActivity;
import com.AoRGMapT.BaseApplication;
import com.AoRGMapT.CommencementAcceptanceActivity;
import com.AoRGMapT.CoreDescriptionActivity;
import com.AoRGMapT.FieldAcceptanceActivity;
import com.AoRGMapT.FieldConstructionLoggingActivity;
import com.AoRGMapT.FracturingTestActivity;
import com.AoRGMapT.QualityTestingActivity;
import com.AoRGMapT.R;
import com.AoRGMapT.ReclamationActivity;
import com.AoRGMapT.SiteConstructionInputActivity;
import com.AoRGMapT.SiteConstructionWellDrillingActivity;
import com.AoRGMapT.WellLocationDeterminationActivity;
import com.AoRGMapT.WellSitePreparationActivity;
import com.AoRGMapT.bean.UserInfo;
import com.AoRGMapT.bean.WorkItemBean;
import com.AoRGMapT.util.DataAcquisitionUtil;
import com.AoRGMapT.util.LocationUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WorkProjectAdapter extends RecyclerView.Adapter<WorkProjectAdapter.ViewHolder> {

    private List<WorkItemBean> itemList;
    private Context mContext;

    public WorkProjectAdapter(List<WorkItemBean> itemList, Context context) {
        this.itemList = itemList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        WorkItemBean item = itemList.get(position);
        holder.textView.setText(item.getName());
        holder.imageView.setBackgroundResource(item.getPng());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (position == 0) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WellLocationDeterminationActivity.class);
                } else if (position == 1) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WellSitePreparationActivity.class);
                } else if (position == 2) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, CommencementAcceptanceActivity.class);
                }else if (position == 3) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, SiteConstructionWellDrillingActivity.class);
                } else if (position == 4) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, FieldConstructionLoggingActivity.class);
                }else if (position == 5) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, SiteConstructionInputActivity.class);
                } else if (position == 6) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, CoreDescriptionActivity.class);
                } else if (position == 7) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, FracturingTestActivity.class);
                } else if (position == 8) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, QualityTestingActivity.class);
                } else if (position == 9) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, FieldAcceptanceActivity.class);
                } else if (position == 10) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, AchievementAcceptanceActivity.class);
                } else {
                    intent = new Intent(WorkProjectAdapter.this.mContext, ReclamationActivity.class);
                }
                WorkProjectAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.tv);
            imageView = view.findViewById(R.id.iv);
        }

    }
}
