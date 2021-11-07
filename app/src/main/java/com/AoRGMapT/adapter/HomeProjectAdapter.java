package com.AoRGMapT.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.R;
import com.AoRGMapT.bean.ProjectBean;

import java.util.ArrayList;
import java.util.List;

public class HomeProjectAdapter extends RecyclerView.Adapter<HomeProjectAdapter.ViewHolder>{

    private List<ProjectBean> itemList=new ArrayList<>();

    public HomeProjectAdapter(List <ProjectBean> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_project,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ProjectBean item=itemList.get(position);
        holder.pname.setText(item.getProjectName());
        holder.comNum.setText(item.getTaskCount()+"");
        holder.imcomNum.setText(item.getTaskLocalCount()+"");
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView pname;
        TextView comNum;
        TextView imcomNum;

        public ViewHolder (View view)
        {
            super(view);
            pname = (TextView) view.findViewById(R.id.tv_project_name);
            comNum=view.findViewById(R.id.tv_com_num);
            imcomNum=view.findViewById(R.id.tv_imcom_num);
        }

    }
}
