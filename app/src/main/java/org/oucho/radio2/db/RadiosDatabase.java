package org.oucho.radio2.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.oucho.radio2.radio.RadioKeys;

public class RadiosDatabase extends SQLiteOpenHelper implements RadioKeys {

	private static final int DB_VERSION = 2;

	public RadiosDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE" + " " + TABLE_NAME + " " + "(url TEXT PRIMARY KEY, name TEXT, image BLOB)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + "image BLOB");
	}

}
