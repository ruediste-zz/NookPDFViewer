package com.googlecode.apdfviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DocumentPositionDatabaseHelper extends SQLiteOpenHelper{
	public static final String DBName="DocumentPositionDB";
	public static final int DBVersion=1;
	public static final String TablePositions="positions";
	public static final String RowFileName="filename";
	public static final String RowData="data";
	
	public DocumentPositionDatabaseHelper(Context context) {
		super(context, DBName, null, DBVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String cmd="create table "+TablePositions+" (";
		cmd+=RowFileName+" text primary key,";
		cmd+=RowData+" blob);";
		db.execSQL(cmd);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
