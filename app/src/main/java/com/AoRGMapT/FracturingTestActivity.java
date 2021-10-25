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
 * 压裂试油
 */
public class FracturingTestActivity extends AppCompatActivity {

    //记录时间
    private EditText mEditTime;

    //压裂工作方法总结
    private GridView mGridFracture;
    private ImageAdapter mFractureImageAdapter;
    List<ImageBean> mFractureImageBeans = new ArrayList<>();

    //试油工作总结
    private GridView mGridOilTest;
    private ImageAdapter mOilTestImageAdapter;
    List<ImageBean> mOilTestImageBeans = new ArrayList<>();

    //现场照片
    private GridView mGridScene;
    private ImageAdapter mSceneImageAdapter;
    List<ImageBean> mSceneImageBeans = new ArrayList<>();


    //0压裂工作方法总结 1试油工作总结 2 现场照片
    private int mChooseImageType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fracturing_test);
        mEditTime = findViewById(R.id.ed_time);
        mGridFracture = findViewById(R.id.grid_fracture);
        mGridOilTest = findViewById(R.id.grid_oil_test);
        mGridScene = findViewById(R.id.grid_scene);

        setCurrentTime();

        //验收意见与专家名单
        mFractureImageBeans.add(new ImageBean(null, null, 1));
        mFractureImageAdapter = new ImageAdapter(mFractureImageBeans, this);
        mGridFracture.setAdapter(mFractureImageAdapter);
        mFractureImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mFractureImageBeans.remove(position);
                mGridFracture.setAdapter(mFractureImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mFractureImageBeans.size() == 7) {
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 0;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
                }

            }
        });

        //整改情况说明
        mOilTestImageBeans.add(new ImageBean(null, null, 1));
        mOilTestImageAdapter = new ImageAdapter(mOilTestImageBeans, this);
        mGridOilTest.setAdapter(mOilTestImageAdapter);
        mOilTestImageAdapter.setOnImageClickListtener(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onCancleClick(int position, View view) {
                //点击取消
                mOilTestImageBeans.remove(position);
                mGridOilTest.setAdapter(mOilTestImageAdapter);
            }

            @Override
            public void onAddClick(View view) {
                //点击添加
                if (mOilTestImageBeans.size() == 7) {
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 1;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
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
                    Toast.makeText(FracturingTestActivity.this, "照片不能超过6张", Toast.LENGTH_SHORT).show();
                } else {
                    mChooseImageType = 2;
                    ChooseImageDialog.getInstance().show(FracturingTestActivity.this);
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
                        mFractureImageBeans.add(mFractureImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridFracture.setAdapter(mFractureImageAdapter);
                    } else if (mChooseImageType == 1) {
                        mOilTestImageBeans.add(mOilTestImageBeans.size() - 1, new ImageBean(null, BitmapFactory.decodeFile(picturePath), 0));
                        mGridOilTest.setAdapter(mOilTestImageAdapter);
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
                    mFractureImageBeans.add(mFractureImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridFracture.setAdapter(mFractureImageAdapter);
                } else if (mChooseImageType == 1) {
                    mOilTestImageBeans.add(mOilTestImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridOilTest.setAdapter(mOilTestImageAdapter);
                } else if (mChooseImageType == 2) {
                    mSceneImageBeans.add(mSceneImageBeans.size() - 1, new ImageBean(null, bitmap, 0));
                    mGridScene.setAdapter(mSceneImageAdapter);
                }
            }
        }

    }


}