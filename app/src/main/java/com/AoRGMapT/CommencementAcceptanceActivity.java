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
 * 开工验收
 */
public class CommencementAcceptanceActivity extends AppCompatActivity {

    //记录时间
    private EditText mEditTime;

    //验收意见与专家名单
    private GridView mGridCheck;
    private ImageAdapter mCheckImageAdapter;
    List<ImageBean> mCheckImageBeans = new ArrayList<>();

    //环评记录
    private GridView mGridEia;
    private ImageAdapter mEiaImageAdapter;
    List<ImageBean> mEiaImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();

    //成果验收记录
    private GridView mGridRectification;
    private ImageAdapter mRectificationImageAdapter;
    List<ImageBean> mRectificationImageBeans = new ArrayList<>();

    //0验收意见与专家名单 1环评记录 2 现场照片 3成果验收记录
    private int mChooseImageType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commencement_acceptance);
        mEditTime = findViewById(R.id.ed_time);
        mGridCheck = findViewById(R.id.grid_check);
        mGridEia = findViewById(R.id.grid_eia);
        mGridScene = findViewById(R.id.grid_scene);
        mGridRectification = findViewById(R.id.grid_rectification);

        setCurrentTime();

        //验收意见与专家名单
        mCheckImageBeans.add(new ImageBean(null, null, 1));
        mCheckImageAdapter = new ImageAdapter(mCheckImageBeans, this);
        mGridCheck.setAdapter(mCheckImageAdapter);
        mCheckImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mCheckImageBeans.remove(position);
                mGridCheck.setAdapter(mCheckImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mCheckImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
                }

            }
        });

        //整改情况说明
        mEiaImageBeans.add(new ImageBean(null, null, 1));
        mEiaImageAdapter = new ImageAdapter(mEiaImageBeans, this);
        mGridEia.setAdapter(mEiaImageAdapter);
        mEiaImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mEiaImageBeans.remove(position);
                mGridEia.setAdapter(mEiaImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mEiaImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
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
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
                }

            }
        });
        //成果验收记录
        mRectificationImageBeans.add(new ImageBean(null, null, 1));
        mRectificationImageAdapter = new ImageAdapter(mRectificationImageBeans, this);
        mGridRectification.setAdapter(mRectificationImageAdapter);
        mRectificationImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mRectificationImageBeans.remove(position);
                mGridRectification.setAdapter(mRectificationImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mRectificationImageBeans.size() == 7) {
                    Toast.makeText(CommencementAcceptanceActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(CommencementAcceptanceActivity.this);
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
                        mCheckImageBeans.add(mCheckImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridCheck.setAdapter(mCheckImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mEiaImageBeans.add(mEiaImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridEia.setAdapter(mEiaImageAdapter);
                    } else if (mChooseImageType == 2) {
                        mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridScene.setAdapter(mSceneImageAdapter);
                    }else if (mChooseImageType == 3) {
                        mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridRectification.setAdapter(mRectificationImageAdapter);
                    }
                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mCheckImageBeans.add(mCheckImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridCheck.setAdapter(mCheckImageAdapter);
                } else if (mChooseImageType == 1) {
                    mEiaImageBeans.add(mEiaImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridEia.setAdapter(mEiaImageAdapter);
                } else if (mChooseImageType == 2) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1,new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                }else if (mChooseImageType == 3) {
                    mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridRectification.setAdapter(mRectificationImageAdapter);
                }
            }
        }

    }
}