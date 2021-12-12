package com.AoRGMapT.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.AchievementAcceptanceActivity;
import com.AoRGMapT.AnalysisAssayActivity;
import com.AoRGMapT.CommencementAcceptanceActivity;
import com.AoRGMapT.CoreDescriptionActivity;
import com.AoRGMapT.FieldAcceptanceActivity;
import com.AoRGMapT.FieldConstructionLoggingExplainActivity;
import com.AoRGMapT.FieldConstructionLoggingSiteDailyActivity;
import com.AoRGMapT.FracturingTestActivity;
import com.AoRGMapT.GeophysicalGeochemicalExplorationActivity;
import com.AoRGMapT.LocalDispatchingRouteActivity;
import com.AoRGMapT.QualityTestingActivity;
import com.AoRGMapT.R;
import com.AoRGMapT.ReclamationActivity;
import com.AoRGMapT.SiteConstructionInputCollectionContentActivity;
import com.AoRGMapT.SiteConstructionInputSiteDailyActivity;
import com.AoRGMapT.SiteConstructionWellDrillingActivity;
import com.AoRGMapT.WasteDisposalActivity;
import com.AoRGMapT.WellLocationDeterminationActivity;
import com.AoRGMapT.WellSitePreparationActivity;
import com.AoRGMapT.bean.WorkItemBean;
import com.AoRGMapT.util.ChooseModeDialog;

import java.util.List;

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
                if (TextUtils.equals("井位确定", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WellLocationDeterminationActivity.class);
                } else if (TextUtils.equals("井场准备", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WellSitePreparationActivity.class);
                } else if (TextUtils.equals("开工验收", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, CommencementAcceptanceActivity.class);
                } else if (TextUtils.equals("钻井施工", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, SiteConstructionWellDrillingActivity.class);
                } else if (TextUtils.equals("测井施工", item.getName())) {
                    ChooseModeDialog.getIntent().showDialog(mContext, "现场日报", "解释结论", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(WorkProjectAdapter.this.mContext, FieldConstructionLoggingSiteDailyActivity.class);
                            WorkProjectAdapter.this.mContext.startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(WorkProjectAdapter.this.mContext, FieldConstructionLoggingExplainActivity.class);
                            WorkProjectAdapter.this.mContext.startActivity(intent);
                        }
                    });
                    return;
                } else if (TextUtils.equals("录井施工", item.getName())) {

                    ChooseModeDialog.getIntent().showDialog(mContext, "现场日报", "采集内容", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(WorkProjectAdapter.this.mContext, SiteConstructionInputSiteDailyActivity.class);
                            WorkProjectAdapter.this.mContext.startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(WorkProjectAdapter.this.mContext, SiteConstructionInputCollectionContentActivity.class);
                            WorkProjectAdapter.this.mContext.startActivity(intent);
                        }
                    });
                    return;
                } else if (TextUtils.equals("岩心描述", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, CoreDescriptionActivity.class);
                } else if (TextUtils.equals("压裂试油", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, FracturingTestActivity.class);
                } else if (TextUtils.equals("质量检查", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, QualityTestingActivity.class);
                } else if (TextUtils.equals("野外验收", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, FieldAcceptanceActivity.class);
                } else if (TextUtils.equals("成果验收", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, AchievementAcceptanceActivity.class);
                } else if (TextUtils.equals("复耕复垦", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, ReclamationActivity.class);
                } else if (TextUtils.equals("废物处理", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WasteDisposalActivity.class);
                } else if (TextUtils.equals("地调路线", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, LocalDispatchingRouteActivity.class);
                } else if (TextUtils.equals("物化探", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, GeophysicalGeochemicalExplorationActivity.class);
                } else if (TextUtils.equals("分析化验", item.getName())) {
                    intent = new Intent(WorkProjectAdapter.this.mContext, AnalysisAssayActivity.class);
                } else {
                    intent = new Intent(WorkProjectAdapter.this.mContext, WasteDisposalActivity.class);
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
