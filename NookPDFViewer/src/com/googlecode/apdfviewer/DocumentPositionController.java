package com.googlecode.apdfviewer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.util.Log;

public class DocumentPositionController {
	private static final String TAG="DocumentPositionController";
	
	private DocumentPositionDatabaseHelper helper;

	public DocumentPositionController(Context context) {
		helper = new DocumentPositionDatabaseHelper(context);
	}

	public void close() {
		helper.close();
	}

	public void savePosition(String fileName, ScreenRectangle position){
		Log.d(TAG,"savePosition pageNr:"+position.getPageNr());
		boolean doUpdate=(getPosition(fileName)!=null);
		
		SQLiteDatabase database=helper.getWritableDatabase();
		database.beginTransaction();
		try{
			// create values
			ContentValues values=new ContentValues();
			values.put(DocumentPositionDatabaseHelper.RowData, serialize(position));
			
			// determine wether to update the existing data or to insert a new row
			if (doUpdate){
				Log.d(TAG,"savePosition: update");
				// update existing data
				database.update(
						DocumentPositionDatabaseHelper.TablePositions,
						values, 
						DocumentPositionDatabaseHelper.RowFileName+"=?", 
						new String[]{fileName});
			}
			else{
				Log.d(TAG,"savePosition: insert");
				values.put(DocumentPositionDatabaseHelper.RowFileName, fileName);
				// insert new row
				database.insertOrThrow(DocumentPositionDatabaseHelper.TablePositions, null, values);
			}
			Log.d(TAG,"savePosition: success");
			database.setTransactionSuccessful();
		} finally 
		{
			database.endTransaction();
		}
		database.close();
	}

	public byte[] serialize(ScreenRectangle rect){
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try{
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(rect);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return byteArrayOutputStream.toByteArray();
	}
	
	public ScreenRectangle getPosition(String fileName) {
		Log.d(TAG,"getPosition fileName: "+fileName);
		SQLiteDatabase database=helper.getWritableDatabase();
		database.beginTransaction();
		try{
			Cursor c = database.query(
					DocumentPositionDatabaseHelper.TablePositions,
					new String[] { DocumentPositionDatabaseHelper.RowData},
					DocumentPositionDatabaseHelper.RowFileName + "=?",
					new String[] { fileName }, null, null, null);
			if (c == null)
				return null;
			if (!c.moveToFirst())
				return null;
	
			Log.d(TAG,"success");
			database.setTransactionSuccessful();
			
			byte[] blob = c.getBlob(0);
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(
						new ByteArrayInputStream(blob));
				ScreenRectangle result = (ScreenRectangle) objectInputStream.readObject();
				Log.d(TAG,"return result");
				return result;
	
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		finally{
			database.endTransaction();
			database.close();
		}
		
	}
	
	public void clearPositions(){
		SQLiteDatabase database=helper.getWritableDatabase();
		database.beginTransaction();
		try{
			database.delete(DocumentPositionDatabaseHelper.TablePositions, "", null);
			database.setTransactionSuccessful();
		}
		finally{
			database.endTransaction();
			database.close();
		}
	}
}
