package com.danielkashin.taskorganiser.data_layer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import com.danielkashin.taskorganiser.BuildConfig;
import com.danielkashin.taskorganiser.data_layer.contracts.local.connections_tables.DayToMiniContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.connections_tables.MonthToWeekContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.connections_tables.TaskToTagContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.connections_tables.WeekToDayContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TagContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TaskDayContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TaskMiniContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TaskMonthContract;
import com.danielkashin.taskorganiser.data_layer.contracts.local.data_tables.TaskWeekContract;


public class DatabaseOpenHelper extends SQLiteOpenHelper {

  public DatabaseOpenHelper(Context context) {
    super(context, BuildConfig.DATABASE_NAME, null, BuildConfig.DATABASE_BUILD_NUMBER);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    // tasks
    sqLiteDatabase.execSQL(TaskMonthContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(TaskWeekContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(TaskDayContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(TaskMiniContract.SQL_CREATE_TABLE);

    // tags
    sqLiteDatabase.execSQL(TagContract.SQL_CREATE_TABLE);

    // additional
    sqLiteDatabase.execSQL(MonthToWeekContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(WeekToDayContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(DayToMiniContract.SQL_CREATE_TABLE);
    sqLiteDatabase.execSQL(TaskToTagContract.SQL_CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(TaskDayContract.SQL_DELETE_TABLE);
    sqLiteDatabase.execSQL(TaskWeekContract.SQL_DELETE_TABLE);
    sqLiteDatabase.execSQL(TaskMonthContract.SQL_DELETE_TABLE);
    onCreate(sqLiteDatabase);
  }
}
