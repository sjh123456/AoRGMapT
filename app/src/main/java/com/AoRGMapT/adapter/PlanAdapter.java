package com.AoRGMapT.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.CommencementAcceptanceActivity;
import com.AoRGMapT.FieldConstructionLoggingActivity;
import com.AoRGMapT.R;
import com.AoRGMapT.SiteConstructionWellDrillingActivity;
import com.AoRGMapT.WellLocationDeterminationActivity;
import com.AoRGMapT.WellSitePreparationActivity;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ProjectBean;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private List<PlanBean> itemList;
    private Context context;

    public PlanAdapter(List<PlanBean> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
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

        holder.tvWellNum.setText(item.getWellName());
        holder.tvPlanClass.setText(item.getTaskType());
        holder.tvComTime.setText(item.getRecordDate());
        holder.tvPlanPeople.setText(item.getRecorder());
        holder.tvUpdatePeople.setText(item.getUpdateUser());
        holder.tvUpdateTime.setText(item.getUpdateTime());
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
                } else if("钻井施工".equals(item.getTaskType())){
                    intent = new Intent(PlanAdapter.this.context, SiteConstructionWellDrillingActivity.class);
                }else {
                    intent = new Intent(PlanAdapter.this.context, FieldConstructionLoggingActivity.class);
                }
                intent.putExtra("id", item.getId());
                context.startActivity(intent);
            }
        });

        holder.tvWellNum.setText(item.getWellName());
        holder.tvPlanClass.setText(item.getTaskType());
        holder.tvComTime.setText(item.getRecordDate());
        holder.tvPlanPeople.setText(item.getRecorder());
        holder.tvUpdatePeople.setText(item.getUpdateUser());
        holder.tvUpdateTime.setText(item.getUpdateTime());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(PlanAdapter.this.context, WellLocationDeterminationActivity.class);
                intent.putExtra("id", item.getId());
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
        TextView tvUpdatePeople;
        TextView tvUpdateTime;
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
            tvUpdatePeople = view.findViewById(R.id.tv_update_people);

        }

    }
}
