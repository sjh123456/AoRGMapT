package com.AoRGMapT.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.AoRGMapT.R;
import com.AoRGMapT.bean.ImageBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {


    private List<ImageBean> mImageBeans = new ArrayList<>();
    private Context mContext;

    public ImageAdapter(List<ImageBean> imageBeans, Context context) {
        mImageBeans = imageBeans;
        mContext = context;

    }


    @Override
    public int getCount() {
        return mImageBeans.size() == 7 ? 6 : mImageBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.grid_png_item, null, false);
        ViewHolder viewHolder;
        if (root.getTag() == null) {
            viewHolder = new ViewHolder(root);
            root.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) root.getTag();
        }

        // viewHolder.ivpng.setBackground();

        if (mImageBeans.get(i).getType() == 0) {

            if (TextUtils.isEmpty(mImageBeans.get(i).getImageUrl())) {
                viewHolder.ivpng.setImageBitmap(mImageBeans.get(i).getBitmap());
            } else {
                Glide.with(mContext).
                        load(mImageBeans.get(i).getImageUrl()).into(viewHolder.ivpng);
            }
            viewHolder.ivcancle.setVisibility(View.VISIBLE);
            viewHolder.ivcancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onImageClickListtener != null) {
                        onImageClickListtener.onCancleClick(i, view);
                    }
                }
            });
        } else {
            viewHolder.ivcancle.setVisibility(View.GONE);
            viewHolder.ivpng.setBackgroundResource(R.drawable.add);
            viewHolder.ivpng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onImageClickListtener != null) {
                        onImageClickListtener.onAddClick(view);
                    }
                }
            });
        }

        return root;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivpng;
        ImageView ivcancle;

        public ViewHolder(View view) {
            super(view);
            ivpng = view.findViewById(R.id.iv_png);
            ivcancle = view.findViewById(R.id.iv_png_cancle);
        }

    }

    public interface OnImageClickListener {
        public void onCancleClick(int position, View view);

        public void onAddClick(View view);
    }

    private OnImageClickListener onImageClickListtener;

    public OnImageClickListener getOnImageClickListtener() {
        return onImageClickListtener;
    }

    public void setOnImageClickListtener(OnImageClickListener onCancleImageListtener) {
        this.onImageClickListtener = onCancleImageListtener;
    }
}
