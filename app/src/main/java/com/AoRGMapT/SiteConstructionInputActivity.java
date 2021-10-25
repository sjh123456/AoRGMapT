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
 * 录井
 */
public class SiteConstructionInputActivity extends AppCompatActivity {

    //记录时间
    private EditText mEditTime;

    //早会记录
    private GridView mGridMorningMeeting;
    private ImageAdapter mMorningMeetingImageAdapter;
    List<ImageBean> mMorningMeetingImageBeans = new ArrayList<>();

    //录井
    private GridView mGridLogging;
    private ImageAdapter mLogginfImageAdapter;
    List<ImageBean> mLoggingImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();


    //0早会记录 1录井 2 现场照片
    private int mChooseImageType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_construction_input);
        mEditTime = findViewById(R.id.ed_time);
        mGridMorningMeeting = findViewById(R.id.grid_morning_meeting);
        mGridLogging = findViewById(R.id.grid_logging);
        mGridScene = findViewById(R.id.grid_scene);

        setCurrentTime();

        //验收意见与专家名单
        mMorningMeetingImageBeans.add(new ImageBean(null, null, 1));
        mMorningMeetingImageAdapter = new ImageAdapter(mMorningMeetingImageBeans, this);
        mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
        mMorningMeetingImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mMorningMeetingImageBeans.remove(position);
                mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mMorningMeetingImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionInputActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputActivity.this);
                }

            }
        });

        //整改情况说明
        mLoggingImageBeans.add(new ImageBean(null, null, 1));
        mLogginfImageAdapter = new ImageAdapter(mLoggingImageBeans, this);
        mGridLogging.setAdapter(mLogginfImageAdapter);
        mLogginfImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mLoggingImageBeans.remove(position);
                mGridLogging.setAdapter(mLogginfImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mLoggingImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionInputActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputActivity.this);
                }

            }
        });
        //现场照片
        mSceneImageBeans.add(new ImageBean(null, null, 1));
        mSceneImageAdapter = new ImageAdapter(mSceneImageBeans, this);
        mGridScene.setAdapter(mSceneImageAdapter);
        mSceneImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mSceneImageBeans.remove(position);
                mGridScene.setAdapter(mSceneImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mSceneImageBeans.size() == 7) {
                    Toast.makeText(SiteConstructionInputActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(SiteConstructionInputActivity.this);
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

                    if (mChooseImageType == 0) {
                        mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mLoggingImageBeans.add(mLoggingImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridLogging.setAdapter(mLogginfImageAdapter);
                    } else if (mChooseImageType == 2) {
                        mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridScene.setAdapter(mSceneImageAdapter);
                    }
                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mMorningMeetingImageBeans.add(mMorningMeetingImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridMorningMeeting.setAdapter(mMorningMeetingImageAdapter);
                } else if (mChooseImageType == 1) {
                    mLoggingImageBeans.add(mLoggingImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridLogging.setAdapter(mLogginfImageAdapter);
                } else if (mChooseImageType == 2) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                }
            }
        }

    }

}