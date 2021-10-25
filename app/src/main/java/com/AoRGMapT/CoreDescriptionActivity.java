package com.AoRGMapT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.AoRGMapT.adapter.ImageAdapter;
import com.AoRGMapT.bean.ImageBean;
import com.AoRGMapT.util.ChooseImageDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 岩心描述
 */
public class CoreDescriptionActivity extends AppCompatActivity {

    //记录时间
    private EditText mEditTime;

    //交通情况显示图片
    private GridView mGridSample;
    private ImageAdapter mSampleImageAdapter;
    List<ImageBean> mSampleImageBeans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_description);
        mEditTime = findViewById(R.id.ed_time);
        mGridSample = findViewById(R.id.grid_sample);
        setCurrentTime();

        //交通情况的图片
        mSampleImageBeans.add(new ImageBean(null, null, 1));
        mSampleImageAdapter = new ImageAdapter(mSampleImageBeans, this);
        mGridSample.setAdapter(mSampleImageAdapter);
        mSampleImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mSampleImageBeans.remove(position);
                mGridSample.setAdapter(mSampleImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSampleImageBeans.size() == 7) {
                    Toast.makeText(CoreDescriptionActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    ChooseImageDialog.getInstance().show(CoreDescriptionActivity.this);
                }

            }
        });
    }

    /**
     * 设置当前时间
     */
    private void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mEditTime.setText(simpleDateFormat.format(date));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String picturePath = cursor.getString(columnIndex);

            cursor.close();


            Glide.with(this).asBitmap().load(new File(picturePath)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    mSampleImageBeans.add(mSampleImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                    mGridSample.setAdapter(mSampleImageAdapter);

                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");

                mSampleImageBeans.add(mSampleImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                mGridSample.setAdapter(mSampleImageAdapter);
               
            }
        }

    }
}