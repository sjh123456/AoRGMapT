package com.AoRGMapT.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.AoRGMapT.bean.PlanBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = "MyDatabaseHelper";


    private final String CREATE_TABLE_SQL;

    {
        CREATE_TABLE_SQL = "create table planInfo(_key integer primary key autoincrement," +
                "recorder varchar(255),extendData varchar(255),updateUser varchar(255),remark varchar(255)" +
                ",updateTime varchar(255),taskType varchar(255),isDeleted integer," +
                "createTime varchar(255),sitePhotos varchar(255),recordDate varchar(255)," +
                "createUser varchar(255),location varchar(255),sitePhotos3 varchar(255),wellName varchar(255)," +
                "id varchar(255),sitePhotos2 varchar(255),sitePhotos5 varchar(255)," +
                "projectId varchar(255),sitePhotos4 varchar(255),sitePhotos6 varchar(255),status integer)";
    }

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e(TAG, "oldVersion:" + oldVersion + "  newVersion:" + newVersion);
    }

    public void inserDate(PlanBean planBean) {
        this.onOpen(this.getWritableDatabase());
        String INSERT_DATA_SQL = "insert into planInfo VALUES (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        this.getWritableDatabase().execSQL(INSERT_DATA_SQL, new Object[]{planBean.getRecorder(), planBean.getExtendData(), planBean.getUpdateUser(), planBean.getRemark()
                , planBean.getUpdateTime(), planBean.getTaskType(), planBean.getIsDeleted(), planBean.getCreateTime(), planBean.getSitePhotos()
                , planBean.getRecordDate(), planBean.getCreateUser(), planBean.getLocation(), planBean.getSitePhotos3(), planBean.getWellName(),
                planBean.getId(), planBean.getSitePhotos2(), planBean.getSitePhotos5(),
                planBean.getProjectId(), planBean.getSitePhotos4(), planBean.getSitePhotos6(), planBean.getStatus()});
        this.close();
    }


    public List<PlanBean> queryPlanList(String projectId, int count, int pagesize) {

        this.onOpen(this.getReadableDatabase());
        String QUERY_DATA_SQL = "select * from planInfo where projectId=? limit ? offset ?";

        List<PlanBean> planBeans = new ArrayList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(QUERY_DATA_SQL, new String[]{projectId, pagesize + "", (count * pagesize) + ""});
        while (cursor.moveToNext()) {
            PlanBean planBean = new PlanBean();
            planBean.setKey(cursor.getInt(0));
            planBean.setRecorder(cursor.getString(1));
            planBean.setExtendData(cursor.getString(2));
            planBean.setUpdateUser(cursor.getString(3));
            planBean.setRemark(cursor.getString(4));
            planBean.setUpdateTime(cursor.getString(5));
            planBean.setTaskType(cursor.getString(6));
            planBean.setIsDeleted(cursor.getInt(7));
            planBean.setCreateTime(cursor.getString(8));
            planBean.setSitePhotos(cursor.getString(9));
            planBean.setRecordDate(cursor.getString(10));
            planBean.setCreateUser(cursor.getString(11));
            planBean.setLocation(cursor.getString(12));
            planBean.setSitePhotos3(cursor.getString(13));
            planBean.setWellName(cursor.getString(14));
            planBean.setId(cursor.getString(15));
            planBean.setSitePhotos2(cursor.getString(16));
            planBean.setSitePhotos5(cursor.getString(17));
            planBean.setProjectId(cursor.getString(18));
            planBean.setSitePhotos4(cursor.getString(19));
            planBean.setSitePhotos6(cursor.getString(20));
            planBean.setStatus(cursor.getInt(21));
            planBeans.add(planBean);
        }
        this.close();
        return planBeans;
    }


    public PlanBean queryLocalPlanInfoFromKey(int key) {
        this.onOpen(this.getReadableDatabase());
        PlanBean planBean = new PlanBean();
        String QUERY_DATA_ID_SQL = "select * from planInfo where _key=?";
        Cursor cursor = this.getReadableDatabase().rawQuery(QUERY_DATA_ID_SQL, new String[]{key + ""});
        while (cursor.moveToNext()) {
            planBean.setKey(cursor.getInt(0));
            planBean.setRecorder(cursor.getString(1));
            planBean.setExtendData(cursor.getString(2));
            planBean.setUpdateUser(cursor.getString(3));
            planBean.setRemark(cursor.getString(4));
            planBean.setUpdateTime(cursor.getString(5));
            planBean.setTaskType(cursor.getString(6));
            planBean.setIsDeleted(cursor.getInt(7));
            planBean.setCreateTime(cursor.getString(8));
            planBean.setSitePhotos(cursor.getString(9));
            planBean.setRecordDate(cursor.getString(10));
            planBean.setCreateUser(cursor.getString(11));
            planBean.setLocation(cursor.getString(12));
            planBean.setSitePhotos3(cursor.getString(13));
            planBean.setWellName(cursor.getString(14));
            planBean.setId(cursor.getString(15));
            planBean.setSitePhotos2(cursor.getString(16));
            planBean.setSitePhotos5(cursor.getString(17));
            planBean.setProjectId(cursor.getString(18));
            planBean.setSitePhotos4(cursor.getString(19));
            planBean.setSitePhotos6(cursor.getString(20));
            planBean.setStatus(cursor.getInt(21));
        }
        this.close();
        return planBean;
    }


    public Map<String, Integer> queryPlanInfoCount() {
        this.onOpen(this.getReadableDatabase());

        String QUERY_DATA_SQL = "select projectId,count(*) from planInfo  group by projectId";

        Map<String, Integer> planCount = new HashMap<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(QUERY_DATA_SQL, null);
        while (cursor.moveToNext()) {

            planCount.put(cursor.getString(0), cursor.getInt(1));
        }
        this.close();
        return planCount;

    }


    public void deletePlan(int key) {
        this.onOpen(this.getWritableDatabase());
        String DELETE_DATA_SQL = "delete from planInfo where _key = ?";

        this.getWritableDatabase().execSQL(DELETE_DATA_SQL, new Integer[]{key});
        this.close();
    }

    public void updatePlan(PlanBean planBean) {
        this.onOpen(this.getWritableDatabase());
        String UPDATE_DATA_SQL = "update planInfo set " +
                "recorder = ?,extendData = ?,updateUser = ?,remark = ?" +
                ",updateTime = ?,taskType = ?,isDeleted = ?," +
                "createTime = ?,sitePhotos = ?,recordDate = ?," +
                "createUser = ?,location = ?,sitePhotos3 = ?,wellName = ?," +
                "id = ?,sitePhotos2 = ?,sitePhotos5 = ?," +
                "projectId = ?,sitePhotos4 = ?,sitePhotos6 = ?,status = ? where _key = ?";
        this.getWritableDatabase().execSQL(UPDATE_DATA_SQL, new Object[]{planBean.getRecorder(), planBean.getExtendData(), planBean.getUpdateUser(), planBean.getRemark()
                , planBean.getUpdateTime(), planBean.getTaskType(), planBean.getIsDeleted(), planBean.getCreateTime(), planBean.getSitePhotos()
                , planBean.getRecordDate(), planBean.getCreateUser(), planBean.getLocation(), planBean.getSitePhotos3(), planBean.getWellName(),
                planBean.getId(), planBean.getSitePhotos2(), planBean.getSitePhotos5(),
                planBean.getProjectId(), planBean.getSitePhotos4(), planBean.getSitePhotos6(), planBean.getStatus(), planBean.getKey()});
        this.close();
    }


    public Map<String, Integer> queryTaskTypeCount(String projectId) {

        String QUERY_DATA_SQL = "select taskType,count(*) from planInfo where projectId=?  group by taskType";

        this.onOpen(this.getReadableDatabase());

        Map<String, Integer> planCount = new HashMap<>();

        Cursor cursor = this.getReadableDatabase().rawQuery(QUERY_DATA_SQL, new String[]{projectId});
        while (cursor.moveToNext()) {

            planCount.put(cursor.getString(0), cursor.getInt(1));
        }
        this.close();
        return planCount;

    }
}
