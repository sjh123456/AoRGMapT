package com.AoRGMapT.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.R;
import com.AoRGMapT.bean.PlanBean;
import com.AoRGMapT.bean.ProjectBean;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    private List<PlanBean> itemList;

    public PlanAdapter(List<PlanBean> itemList) {
        this.itemList = itemList;
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
        if (item.isComplete()) {
            holder.llOperation.setVisibility(View.GONE);
        } else {
            holder.llOperation.setVisibility(View.VISIBLE);
        }

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
        TextView tvRecordTime;
        TextView tvEdit;
        TextView tvCommit;
        TextView tvDelete;
        LinearLayout llOperation;

        public ViewHolder(View view) {
            super(view);
            tvPlanClass = (TextView) view.findViewById(R.id.tv_plan_class);
            tvComTime = view.findViewById(R.id.tv_com_time);
            tvWellNum = view.findViewById(R.id.tv_well_num);
            tvPlanPeople = (TextView) view.findViewById(R.id.tv_plan_people);
            tvRecordTime = view.findViewById(R.id.tv_record_time);
            tvEdit = view.findViewById(R.id.tv_edit);
            tvCommit = view.findViewById(R.id.tv_commit);
            tvDelete = view.findViewById(R.id.tv_delete);
            llOperation = view.findViewById(R.id.ll_operation);
        }

    }
}
