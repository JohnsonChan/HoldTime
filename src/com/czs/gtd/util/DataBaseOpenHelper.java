package com.czs.gtd.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.czs.gtd.R;

public class DataBaseOpenHelper extends SQLiteOpenHelper
{
    private static final String DBNAME = "gtd.db";
    private static final int version = 1; 
    private Context context = null;
    
    public DataBaseOpenHelper(Context context)
    {
        super(context, DBNAME, null, version);
        this.context = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table task (id integer not null primary key autoincrement,type integer(11) not null,priority integer(1) not null, date varchar(10) not null, time varchar(10) not null, context varchar(250)not null, finish integer(1) not null)");
        db.execSQL("create table task_type(id integer not null default  " + R.drawable.type_default_other
                + ", name varchar(10)not null, play integer(3) not null default 7)");
        String arrayString[] = context.getResources().getStringArray(R.array.types);
        db.execSQL("insert into task_type(id, name) values(" + R.drawable.type_default_study + ", '" + arrayString[0] + "')");
        db.execSQL("insert into task_type(id, name) values(" + R.drawable.type_default_work + ", '" + arrayString[1] + "')");
        db.execSQL("insert into task_type(id, name) values(" + R.drawable.type_default_personal + ", '" + arrayString[2] + "')");
        db.execSQL("insert into task_type(id, name) values(" + R.drawable.type_default_life + ", '" + arrayString[3] + "')");
        db.execSQL("insert into task_type(id, name) values(" + R.drawable.type_default_other + ", '" + arrayString[4] + "')");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS task");
        db.execSQL("DROP TABLE IF EXISTS task_type");
        onCreate(db);
    }
}
