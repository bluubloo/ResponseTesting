package uni.apps.responsetesting.database;

import uni.apps.responsetesting.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class handles any operations the database requires
 * 
 * 
 * @author Mathew Andela
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ResponseTestingDatabase";
	private static final int SCHEMA_VERSION = 1;
	private static DatabaseHelper singleton = null;
	private static Resources resources = null;
		
	//constructor
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	//create new object or return existing
	synchronized public static DatabaseHelper getInstance(Context context, Resources res){
		if(singleton == null){
			singleton = new DatabaseHelper(context.getApplicationContext());
			resources = res;
		}
		return singleton;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.beginTransaction();
			String create = "CREATE TABLE " + resources.getString(R.string.table_name) +
					"(" + resources.getString(R.string.event_name) + " TEXT," + 
					resources.getString(R.string.event_score) + " TEXT," + 
					resources.getString(R.string.timestamp) + " INTEGER, " + 
					resources.getString(R.string.sent) + " INTEGER, " +
					resources.getString(R.string.notes) + 
					" TEXT, PRIMARY KEY(" + resources.getString(R.string.event_name) + 
					", " + resources.getString(R.string.timestamp) + "))";
			db.execSQL(create);
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			//update database
			db.beginTransaction();
			String query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name);
			db.execSQL(query);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		onCreate(db);	
	}
	
	//--------------------------------------------------------------------------------------------
	//INSERT
	
	public void insert(ContentValues values){
		this.getWritableDatabase().insert(resources.getString(R.string.table_name), 
				null, values);
	}
	
	//--------------------------------------------------------------------------------------------
	//UPDATE
	
	//Update Single Row
	public int updateSingle(String selection, String[] selectionArgs,
			ContentValues values) {
		return this.getWritableDatabase().update(resources.getString(R.string.table_name),
				values, selection, selectionArgs);
	}
	
	//Update All Unsent to Sent
	private void updateMostRecent() {
		ContentValues values =  new ContentValues();
		values.put(resources.getString(R.string.sent), 1);
		this.getWritableDatabase().update(resources.getString(R.string.table_name), values,
				resources.getString(R.string.sent) + "=?", new String[] {"0"});
		
	}
	
	//--------------------------------------------------------------------------------------------
	//DELETE
	
	//Delete Single Row/Event
	public int deleteSingle(String selection, String[] selectionArgs) {
		return this.getWritableDatabase().delete(resources.getString(R.string.table_name),
				selection, selectionArgs);
	}

	//Delete All Rows
	public int deleteAll() {
		this.getWritableDatabase().delete(resources.getString(R.string.table_name), 
				"1=?", new String[] {"1"});
		return 0;
	}
	
	//--------------------------------------------------------------------------------------------
	//QUERY
		
	//All rows for single Event
	public Cursor getSingle(String testName) {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) + " WHERE " +
				resources.getString(R.string.event_name) + "=" + testName + " ORDER BY " + 
				resources.getString(R.string.timestamp);
		return this.getReadableDatabase().rawQuery(sql, null);
	}

	//All Rows not yet sent
	public Cursor getMostRecent() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) + " WHERE " +
				resources.getString(R.string.sent) + "=0" + " GROUP BY" +
				resources.getString(R.string.event_name) + " ORDER BY " + resources.getString(R.string.timestamp);
		Cursor cursor =  this.getReadableDatabase().rawQuery(sql, null);
		updateMostRecent();
		return cursor;
	}

	//All Rows
	public Cursor getAllResults() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) + " GROUP BY" +
				resources.getString(R.string.event_name) + " ORDER BY " + resources.getString(R.string.timestamp);
		return this.getReadableDatabase().rawQuery(sql, null);
	}


}
