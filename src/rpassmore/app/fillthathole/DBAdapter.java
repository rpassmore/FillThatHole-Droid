/**
 * File: DBAdapter.java Project: FillThatHole
 *
 * Created: 20 Feb 2011
 *
 * Copyright (C) 2011 Robert Passmore
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package rpassmore.app.fillthathole;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {
	public static final String KEY_ID = "_id";
	public static final String KEY_CREATED_DATE = "createdDate";
	public static final String KEY_UPDATED_DATE = "updatedDate";
	public static final String KEY_LAT = "lattitude";
	public static final String KEY_LONG = "longitude";
	public static final String KEY_LOCATION_DESC = "locationDesc";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_HAZARD_TYPE = "hazardType";
	public static final String KEY_HAZARD_DESC = "hazardDesc";
	public static final String KEY_HAS_PHOTO = "hasPhoto";
	public static final String KEY_PHOTO = "photo";
	public static final String KEY_PHOTO_URL = "photoUrl";	
	public static final String KEY_STATE = "state";
	public static final String KEY_HAS_ADDITIONAL_INFO = "hasAdditionalInfo";
	public static final String KEY_DEPTH = "depth";
	public static final String KEY_SIZE = "size";
	public static final String KEY_DIST_FROM_KERB = "distFromKerb";
	public static final String KEY_ON_RED_ROUTE = "onRedRoute";
	public static final String KEY_ON_LEVEL_CROSSING = "onLevelCrossing";
	public static final String KEY_ON_TOW_PATH = "onTowPath";
	public static final String KEY_HAZARD_ID = "hazardId";
	public static final String KEY_REPORTER_KEY = "reporterKey";
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "FillThatHole";
	private static final String DATABASE_TABLE = "hazards";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" 
	+ KEY_ID +" integer primary key autoincrement, "
	+ KEY_CREATED_DATE + " integer,"
	+ KEY_UPDATED_DATE + " integer,"
	+ KEY_LAT + " real,"
	+ KEY_LONG + " real,"
	+ KEY_LOCATION_DESC + " text,"
	+ KEY_ADDRESS + " text,"
	+ KEY_HAZARD_TYPE + " integer,"
	+ KEY_HAZARD_DESC + " text,"
	+ KEY_HAS_PHOTO + " integer,"
	+ KEY_PHOTO + " BLOB,"
	+ KEY_PHOTO_URL + " text,"
	+ KEY_STATE + " text,"
	+ KEY_HAS_ADDITIONAL_INFO + " integer,"
	+ KEY_DEPTH + " real,"
	+ KEY_SIZE + " real,"
	+ KEY_DIST_FROM_KERB + " real,"
	+ KEY_ON_RED_ROUTE + " integer,"
	+ KEY_ON_LEVEL_CROSSING + " integer,"
	+ KEY_ON_TOW_PATH + " integer,"
	+ KEY_HAZARD_ID + " text,"
	+ KEY_REPORTER_KEY + " text"
	+ ");";

	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	protected DBAdapter() {
	  context = null;
	}
	
	public DBAdapter(Context ctx) {
		context = ctx;
		dbHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
	  
	  private DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		dbHelper.close();
	}

	// insert a hazard into the database---
	public long insert(Hazard hazard) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CREATED_DATE, hazard.getCreatedDate());
		initialValues.put(KEY_UPDATED_DATE, hazard.getUpdatedDate());
		initialValues.put(KEY_LAT, hazard.getLattitude());
		initialValues.put(KEY_LONG, hazard.getLongitude());
		initialValues.put(KEY_LOCATION_DESC, hazard.getLocationDesc());
		initialValues.put(KEY_ADDRESS, hazard.getAddress());
		initialValues.put(KEY_HAZARD_TYPE, hazard.getHazardType());
		initialValues.put(KEY_HAZARD_DESC, hazard.getHazardDesc());
		initialValues.put(KEY_HAS_PHOTO, hazard.isHasPhoto());
		initialValues.put(KEY_PHOTO, hazard.getPhoto());
		initialValues.put(KEY_PHOTO_URL, hazard.getPhotoUrl());
		initialValues.put(KEY_STATE, hazard.getState().toString());
		initialValues.put(KEY_HAS_ADDITIONAL_INFO, hazard.isHasAdditionalInfo());
		initialValues.put(KEY_DEPTH, hazard.getDepth());
		initialValues.put(KEY_SIZE, hazard.getSize());
		initialValues.put(KEY_DIST_FROM_KERB, hazard.getDistFromKerb());
		initialValues.put(KEY_ON_RED_ROUTE, hazard.isOnRedRoute());
		initialValues.put(KEY_ON_LEVEL_CROSSING, hazard.isOnLevelCrossing());
		initialValues.put(KEY_ON_TOW_PATH, hazard.isOnTowPath());		
		initialValues.put(KEY_HAZARD_ID, hazard.getHazardId());
		initialValues.put(KEY_REPORTER_KEY, hazard.getreporterKey());
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---deletes a particular title---
	public boolean delete(Hazard hazard) {
		return delete(hazard.getId());
	}
	
	public boolean delete(long id) {
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	// ---retrieves all the titles---
	public Cursor getAll() {
		return db.query(DATABASE_TABLE, null, null, null, null, null, null);
	}

	// ---retrieves a particular title---
  public Hazard load(long rowId) throws SQLException {	   	  	  
    Hazard hazard = cursorToHazard( loadToCursor(rowId) );	  
		return hazard;
	}
	
	public Cursor loadToCursor(long rowId) {        
    Cursor mCursor = db.query(true, DATABASE_TABLE, null, KEY_ID
        + "=" + rowId, null, null, null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();           
    }
    return mCursor;
  }
	
	public Hazard cursorToHazard(Cursor data) {
		Hazard hazard = new Hazard();
		hazard.setId( data.getLong( data.getColumnIndex(KEY_ID) ) );
		hazard.setCreatedDate( data.getLong( data.getColumnIndex(KEY_CREATED_DATE) ) );
		hazard.setUpdatedDate( data.getLong( data.getColumnIndex(KEY_UPDATED_DATE) ) );
		hazard.setLattitude( data.getDouble( data.getColumnIndex(KEY_LAT) ) );
		hazard.setLongitude( data.getDouble( data.getColumnIndex(KEY_LONG) ) );
		hazard.setLocationDesc( data.getString( data.getColumnIndex(KEY_LOCATION_DESC) ) );
		hazard.setAddress( data.getString( data.getColumnIndex(KEY_ADDRESS) ) );
		hazard.setHazardType( data.getInt( data.getColumnIndex(KEY_HAZARD_TYPE) ) );
		hazard.setHazardDesc( data.getString( data.getColumnIndex(KEY_HAZARD_DESC) ) );
		hazard.setHasPhoto( (data.getInt( data.getColumnIndex(KEY_HAS_PHOTO) ) != 0)  );
		hazard.setPhoto( data.getBlob( data.getColumnIndex(KEY_PHOTO) ) );
		hazard.setPhotoUrl( data.getString( data.getColumnIndex(KEY_PHOTO_URL) ) );
		hazard.setState( Hazard.State.valueOf( data.getString( data.getColumnIndex(KEY_STATE) ) ));
		hazard.setHasAdditionalInfo( (data.getInt( data.getColumnIndex(KEY_HAS_ADDITIONAL_INFO) ) != 0) );
		hazard.setDepth( data.getDouble( data.getColumnIndex(KEY_DEPTH) ) );
		hazard.setSize( data.getDouble( data.getColumnIndex(KEY_SIZE) ) );
		hazard.setDistFromKerb( data.getDouble( data.getColumnIndex(KEY_DIST_FROM_KERB) ) );
		hazard.setOnRedRoute( (data.getInt( data.getColumnIndex(KEY_ON_RED_ROUTE) ) != 0) );
		hazard.setOnLevelCrossing( (data.getInt( data.getColumnIndex(KEY_ON_LEVEL_CROSSING) ) != 0)  );		
		hazard.setOnTowPath( (data.getInt( data.getColumnIndex(KEY_ON_TOW_PATH) ) != 0) );
		hazard.setHazardId( (data.getString( data.getColumnIndex(KEY_HAZARD_ID) )) );
		hazard.setReporterKey( (data.getString( data.getColumnIndex(KEY_REPORTER_KEY) )) );
		return hazard;
	}

	// ---updates a title---
	public boolean update(Hazard hazard) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CREATED_DATE, hazard.getCreatedDate());
		initialValues.put(KEY_UPDATED_DATE, hazard.getUpdatedDate());
		initialValues.put(KEY_LAT, hazard.getLattitude());
		initialValues.put(KEY_LONG, hazard.getLongitude());
		initialValues.put(KEY_LOCATION_DESC, hazard.getLocationDesc());
		initialValues.put(KEY_ADDRESS, hazard.getAddress());
		initialValues.put(KEY_HAZARD_TYPE, hazard.getHazardType());
		initialValues.put(KEY_HAZARD_DESC, hazard.getHazardDesc());
		initialValues.put(KEY_HAS_PHOTO, hazard.isHasPhoto());
		initialValues.put(KEY_PHOTO, hazard.getPhoto());
		initialValues.put(KEY_PHOTO_URL, hazard.getPhotoUrl());
		initialValues.put(KEY_STATE, hazard.getState().toString());
		initialValues.put(KEY_HAS_ADDITIONAL_INFO, hazard.isHasAdditionalInfo());
		initialValues.put(KEY_DEPTH, hazard.getDepth());
		initialValues.put(KEY_SIZE, hazard.getSize());
		initialValues.put(KEY_DIST_FROM_KERB, hazard.getDistFromKerb());
		initialValues.put(KEY_ON_RED_ROUTE, hazard.isOnRedRoute());
		initialValues.put(KEY_ON_LEVEL_CROSSING, hazard.isOnLevelCrossing());
		initialValues.put(KEY_ON_TOW_PATH, hazard.isOnTowPath());
		initialValues.put(KEY_HAZARD_ID, hazard.getHazardId());
		initialValues.put(KEY_REPORTER_KEY, hazard.getreporterKey());   
		return db.update(DATABASE_TABLE, initialValues, KEY_ID + "=" + hazard.getId(), null) > 0;
	}	
	
	public long save(Hazard hazard) {
		if(hazard.getId() != -1) {
			update(hazard);
			return hazard.getId();
		} else {
			return insert(hazard);
		}
	}
}
