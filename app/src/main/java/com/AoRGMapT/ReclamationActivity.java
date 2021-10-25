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
 * 复耕复垦
 */
public class ReclamationActivity extends AppCompatActivity {

    //记录时间
    private EditText mEditTime;

    //复耕复垦方案
    private GridView mGridPogramme;
    private ImageAdapter mPogrammeImageAdapter;
    List<ImageBean> mPogrammeImageBeans = new ArrayList<>();

    //验收意见与专家名单
    private GridView mGridOpinion;
    private ImageAdapter mOpinionImageAdapter;
    List<ImageBean> mOpinionImageBeans = new ArrayList<>();

    //整改情况说明
    private GridView mGridRectification;
    private ImageAdapter mRectificationImageAdapter;
    List<ImageBean> mRectificationImageBeans = new ArrayList<>();

    //现场情况
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();

    //复耕复垦实施记录
    private GridView mGridImplementation;
    private ImageAdapter mImplementationAdapter;
    List<ImageBean> mImplementationBeans = new ArrayList<>();

    //复耕复垦转态及环评验收情况
    private GridView mGridState;
    private ImageAdapter mStateAdapter;
    List<ImageBean> mStateBeans = new ArrayList<>();

    //0复耕复垦方案 1验收意见与专家名单 2 整改情况说明  3环保保障措施 4复耕复垦实施记录 5复耕复垦转态及环评验收情况
    private int mChooseImageType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamation);
        mEditTime = findViewById(R.id.ed_time);
        mGridPogramme = findViewById(R.id.grid_programme);
        mGridOpinion = findViewById(R.id.grid_opinion);
        mGridRectification = findViewById(R.id.grid_rectification);
        mGridScene = findViewById(R.id.grid_scene);
        mGridImplementation = findViewById(R.id.grid_implementations);
        mGridState = findViewById(R.id.grid_state);

        //交通情况的图片
        mPogrammeImageBeans.add(new ImageBean(null, null, 1));
        mPogrammeImageAdapter = new ImageAdapter(mPogrammeImageBeans, this);
        mGridPogramme.setAdapter(mPogrammeImageAdapter);
        mPogrammeImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mPogrammeImageBeans.remove(position);
                mGridPogramme.setAdapter(mPogrammeImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mPogrammeImageBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //验收意见与专家名单
        mOpinionImageBeans.add(new ImageBean(null, null, 1));
        mOpinionImageAdapter = new ImageAdapter(mOpinionImageBeans, this);
        mGridOpinion.setAdapter(mOpinionImageAdapter);
        mOpinionImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mOpinionImageBeans.remove(position);
                mGridOpinion.setAdapter(mOpinionImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mOpinionImageBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //整改情况说明
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
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //环保保障措施
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
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 3;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });
        //复耕复垦实施记录
        mImplementationBeans.add(new ImageBean(null, null, 1));
        mImplementationAdapter = new ImageAdapter(mImplementationBeans, this);
        mGridImplementation.setAdapter(mImplementationAdapter);
        mImplementationAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mImplementationBeans.remove(position);
                mGridImplementation.setAdapter(mImplementationAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mImplementationBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 4;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        //青苗补偿情况
        mStateBeans.add(new ImageBean(null, null, 1));
        mStateAdapter = new ImageAdapter(mStateBeans, this);
        mGridState.setAdapter(mStateAdapter);
        mStateAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mStateBeans.remove(position);
                mGridState.setAdapter(mStateAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mStateBeans.size() == 7) {
                    Toast.makeText(ReclamationActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 5;
                    ChooseImageDialog.getInstance().show(ReclamationActivity.this);
                }

            }
        });

        setCurrentTime();
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
                        mPogrammeImageBeans.add(mPogrammeImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridPogramme.setAdapter(mPogrammeImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mOpinionImageBeans.add(mOpinionImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridOpinion.setAdapter(mOpinionImageAdapter);
                    } else if (mChooseImageType == 2) {
                        mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridRectification.setAdapter(mRectificationImageAdapter);
                    } else if (mChooseImageType == 3) {
                        mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridScene.setAdapter(mSceneImageAdapter);
                    } else if (mChooseImageType == 4) {
                        mImplementationBeans.add(mImplementationBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridImplementation.setAdapter(mImplementationAdapter);
                    }else if (mChooseImageType == 5) {
                        mStateBeans.add(mStateBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridState.setAdapter(mStateAdapter);
                    }
                }
            });


        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                if (mChooseImageType == 0) {
                    mPogrammeImageBeans.add(mPogrammeImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridPogramme.setAdapter(mPogrammeImageAdapter);
                } else if (mChooseImageType == 1) {
                    mOpinionImageBeans.add(mOpinionImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridOpinion.setAdapter(mOpinionImageAdapter);
                } else if (mChooseImageType == 2) {
                    mRectificationImageBeans.add(mRectificationImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridRectification.setAdapter(mRectificationImageAdapter);
                } else if (mChooseImageType == 3) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                } else if (mChooseImageType == 4) {
                    mImplementationBeans.add(mImplementationBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridImplementation.setAdapter(mImplementationAdapter);
                }else if (mChooseImageType == 5) {
                    mStateBeans.add(mStateBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridState.setAdapter(mStateAdapter);
                }
            }
        }

    }
}