package com.Acquisition.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Acquisition.AchievementAcceptanceActivity;
import com.Acquisition.AnalysisAssayActivity;
import com.Acquisition.AnalysisTestActivity;
import com.Acquisition.CommencementAcceptanceActivity;
import com.Acquisition.CoreDescriptionActivity;
import com.Acquisition.DrillingReportActivity;
import com.Acquisition.FieldAcceptanceActivity;
import com.Acquisition.FieldConstructionLoggingExplainActivity;
import com.Acquisition.FieldConstructionLoggingSiteDailyActivity;
import com.Acquisition.FracturingTestActivity;
import com.Acquisition.GeophysicalGeochemicalExplorationActivity;
import com.Acquisition.LocalDispatchingRouteActivity;
import com.Acquisition.QualityTestingActivity;
import com.Acquisition.Data.Acquisition.R;
import com.Acquisition.ReclamationActivity;
import com.Acquisition.SiteConstructionInputCollectionContentActivity;
import com.Acquisition.SiteConstructionInputSiteDailyActivity;
import com.Acquisition.SiteConstructionWellDrillingActivity;
import com.Acquisition.WasteDisposalActivity;
import com.Acquisition.WellLocationDeterminationActivity;
import com.Acquisition.WellSitePatrolInspectionActivity;
import com.Acquisition.WellSitePreparationActivity;
import com.Acquisition.bean.PlanBean;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private final static String TAG = "PlanAdapter";

    private List<PlanBean> itemList;
    private Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isLocal = false;


    public PlanAdapter(List<PlanBean> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public void setLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_plan, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PlanBean item = itemList.get(position);
//        if (item.isComplete()) {
//            holder.llOperation.setVisibility(View.GONE);
//        } else {
//            holder.llOperation.setVisibility(View.VISIBLE);
//        }

        if (!TextUtils.isEmpty(item.getWellName())) {
            holder.tvWellNum.setVisibility(View.VISIBLE);
            holder.tvWellNum.setText(item.getWellName());
            holder.lbWellName.setVisibility(View.VISIBLE);
        } else {
            holder.tvWellNum.setVisibility(View.GONE);
            holder.lbWellName.setVisibility(View.INVISIBLE);
        }

        holder.tvPlanClass.setText(item.getTaskType());


        String recordertime = item.getRecordDate();
        try {
            recordertime = simpleDateFormat.format(simpleDateFormat.parse(recordertime));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        holder.tvComTime.setText(recordertime);
        holder.tvPlanPeople.setText(item.getRecorder());
        //holder.tvUpdatePeople.setText(item.getUpdateUser());
        String updateTime = item.getUpdateTime();
        try {
            updateTime = simpleDateFormat.format(simpleDateFormat.parse(updateTime));
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        holder.tvUpdateTime.setText(updateTime);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if ("井位确定".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, WellLocationDeterminationActivity.class);
                } else if ("井场准备".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, WellSitePreparationActivity.class);
                } else if ("开工验收".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, CommencementAcceptanceActivity.class);
                } else if ("钻井施工".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, SiteConstructionWellDrillingActivity.class);
                } else if ("测井施工-解释结论".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, FieldConstructionLoggingExplainActivity.class);
                } else if ("测井施工-现场日报".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, FieldConstructionLoggingSiteDailyActivity.class);
                } else if ("岩心描述".equals(item.getTaskType())) {
                    intent = new Intent(PlanAdapter.this.context, CoreDescriptionActivity.class);
                } else if ("野外验收".equals(item.getTaskType())) {
                    //野外验收
                    intent = new Intent(PlanAdapter.this.context, FieldAcceptanceActivity.class);
                } else if ("压裂试油".equals(item.getTaskType())) {
                    //压裂试油
                    intent = new Intent(PlanAdapter.this.context, FracturingTestActivity.class);
                } else if ("质量检查".equals(item.getTaskType())) {
                    //质量检查
                    intent = new Intent(PlanAdapter.this.context, QualityTestingActivity.class);
                } else if ("复耕复垦".equals(item.getTaskType())) {
                    //复耕复垦
                    intent = new Intent(PlanAdapter.this.context, ReclamationActivity.class);
                } else if ("录井施工-采集内容".equals(item.getTaskType())) {
                    //录井施工
                    intent = new Intent(PlanAdapter.this.context, SiteConstructionInputCollectionContentActivity.class);
                } else if ("录井施工-现场日报".equals(item.getTaskType())) {
                    //录井施工
                    intent = new Intent(PlanAdapter.this.context, SiteConstructionInputSiteDailyActivity.class);
                } else if ("成果验收".equals(item.getTaskType())) {
                    //成果验收
                    intent = new Intent(PlanAdapter.this.context, AchievementAcceptanceActivity.class);
                } else if ("地调路线".equals(item.getTaskType())) {
                    //地调路线
                    intent = new Intent(PlanAdapter.this.context, LocalDispatchingRouteActivity.class);
                } else if ("物化探".equals(item.getTaskType())) {
                    //物化探
                    intent = new Intent(PlanAdapter.this.context, GeophysicalGeochemicalExplorationActivity.class);
                } else if ("分析化验".equals(item.getTaskType())) {
                    //分析化验
                    intent = new Intent(PlanAdapter.this.context, AnalysisAssayActivity.class);
                }else if ("钻井日报".equals(item.getTaskType())) {
                    //钻井日报
                    intent = new Intent(PlanAdapter.this.context, DrillingReportActivity.class);
                }else if ("井场巡检".equals(item.getTaskType())) {
                    //井场巡检
                    intent = new Intent(PlanAdapter.this.context, WellSitePatrolInspectionActivity.class);
                }else if ("分析测试".equals(item.getTaskType())) {
                    //分析化验
                    intent = new Intent(PlanAdapter.this.context, AnalysisTestActivity.class);
                } else {
                    //废弃物处理
                    intent = new Intent(PlanAdapter.this.context, WasteDisposalActivity.class);
                }
                if (isLocal) {
                    intent.putExtra("key", item.getKey());
                } else {
                    intent.putExtra("id", item.getId());
                }
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanClass;
        TextView tvComTime;
        TextView tvWellNum;
        TextView tvPlanPeople;
        TextView tvEdit;
        TextView tvCommit;
        TextView tvDelete;
        //TextView tvUpdatePeople;
        TextView tvUpdateTime;
        TextView lbWellName;
        View item;

        LinearLayout llOperation;

        public ViewHolder(View view) {
            super(view);
            item = view;
            tvPlanClass = (TextView) view.findViewById(R.id.tv_plan_class);
            tvComTime = view.findViewById(R.id.tv_com_time);
            tvWellNum = view.findViewById(R.id.tv_well_num);
            tvPlanPeople = (TextView) view.findViewById(R.id.tv_plan_people);
            tvEdit = view.findViewById(R.id.tv_edit);
            tvCommit = view.findViewById(R.id.tv_commit);
            tvDelete = view.findViewById(R.id.tv_delete);
            llOperation = view.findViewById(R.id.ll_operation);
            tvUpdateTime = view.findViewById(R.id.tv_update_time);
            lbWellName=view.findViewById(R.id.lb_well_name);
            //  tvUpdatePeople = view.findViewById(R.id.tv_update_people);

        }

    }
}
