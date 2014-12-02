package uni.apps.responsetesting.database;

import java.util.Calendar;

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
			create = "CREATE TABLE " + resources.getString(R.string.table_name_questionaire) +
					"(" + resources.getString(R.string.timestamp) + " INTEGER PRIMARY KEY, " + 
					resources.getString(R.string.total_sleep) + " TEXT, " + 
					resources.getString(R.string.light_sleep) + " TEXT, " + 
					resources.getString(R.string.sound_sleep) + " TEXT, " + 
					resources.getString(R.string.ratings) + " TEXT, " + 
					resources.getString(R.string.sent) + " INTEGER)"; 
			db.execSQL(create);
			create = "CREATE TABLE " + resources.getString(R.string.table_name_multi_settings) +
					"(" + resources.getString(R.string.user_id) + " INTEGER PRIMARY KEY, " + 
					resources.getString(R.string.user_group) + " TEXT, " + 
					resources.getString(R.string.user_name) + " TEXT, " +
					resources.getString(R.string.user_settings) + " TEXT, " +
					resources.getString(R.string.user_email) + " TEXT)";
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
			query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name_questionaire);
			db.execSQL(query);
			query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name_multi_settings);
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

	public void insertQuestionarie(ContentValues values){
		this.getWritableDatabase().insert(resources.getString(R.string.table_name_questionaire), 
				null, values);
	}

	public void insertMultiSettings(ContentValues values){
		this.getWritableDatabase().insert(resources.getString(R.string.table_name_multi_settings), 
				null, values);
	}

	//--------------------------------------------------------------------------------------------
	//UPDATE

	//Update Single Row
	public int updateSingle(String selection, String[] selectionArgs,
			ContentValues values) {

		String get = "SELECT " + resources.getString(R.string.timestamp) + " FROM " + resources.getString(R.string.table_name) + " WHERE " + 
				resources.getString(R.string.event_name) + "=? ORDER BY " + resources.getString(R.string.timestamp);

		Cursor cursor = this.getReadableDatabase().rawQuery(get, new String[] {selectionArgs[0]});
		long time = 0;
		if(cursor.moveToFirst()){
			time = cursor.getLong(0);
		}

		return this.getWritableDatabase().update(resources.getString(R.string.table_name),
				values, selection, new String[] {selectionArgs[0], Long.toString(time)});

	}

	//Update All Unsent to Sent
	private void updateMostRecent() {
		ContentValues values =  new ContentValues();
		values.put(resources.getString(R.string.sent), 1);
		this.getWritableDatabase().update(resources.getString(R.string.table_name), values,
				resources.getString(R.string.sent) + "=?", new String[] {"0"});

	}

	public void updateMultiSettings(String id, ContentValues values){
		this.getWritableDatabase().update(resources.getString(R.string.table_name_multi_settings), values,
				resources.getString(R.string.user_id) + "=?", new String[] {id});
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
		String query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name);
		this.getWritableDatabase().execSQL(query);
		query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name_questionaire);
		this.getWritableDatabase().execSQL(query);
		onCreate(this.getWritableDatabase());
		return 0;
	}
	
	public void removeMulitUser(String id) {
		this.getWritableDatabase().delete(resources.getString(R.string.table_name_multi_settings),
				resources.getString(R.string.user_id) + "=?", new String[] {id});
	}

	//--------------------------------------------------------------------------------------------
	//QUERY

	//All rows for single Event
	public Cursor getSingle(String testName) {
		return this.getReadableDatabase().query(resources.getString(R.string.table_name), null, 
				resources.getString(R.string.event_name) + "=?", new String[] {testName}, null, 
				null, resources.getString(R.string.timestamp));
	}	

	//All rows for single Event ordered by score
	public Cursor getSingleBest(String testName) {
		return this.getReadableDatabase().query(resources.getString(R.string.table_name), null, 
				resources.getString(R.string.event_name) + "=?", new String[] {testName}, null, 
				null, resources.getString(R.string.event_score));
	}

	//All Rows not yet sent
	public Cursor getMostRecent() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) +
				" WHERE " + resources.getString(R.string.sent) + "=0 ORDER BY " + resources.getString(R.string.timestamp);
		Cursor cursor =  this.getReadableDatabase().rawQuery(sql, null);
		updateMostRecent();
		return cursor;
	}

	//All Rows
	public Cursor getAllResults() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name);
		return this.getReadableDatabase().rawQuery(sql, null);
	}


	//All Questionaire Rows
	public Cursor getAllQuestionaireResults() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_questionaire);
		return this.getReadableDatabase().rawQuery(sql, null);
	}

	public boolean checkRecent(String eventName) {
		/*String sql = "SELECT max(" +  resources.getString(R.string.timestamp) + ") FROM " + 
				resources.getString(R.string.table_name) + " WHERE " + 
				resources.getString(R.string.event_name) + "=?";
		Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] {eventName});
		if(cursor.getCount() == 0)
			return true;		
		if(cursor.moveToFirst()){
			long i = cursor.getLong(0);
			long fiveMin = 5 * 60000; 
			Calendar current = Calendar.getInstance();
			if(current.getTimeInMillis() - i < fiveMin)
				return true;
			Calendar c1 = Calendar.getInstance();
			c1.setTimeInMillis(i);
			if(c1.get(Calendar.DATE) != current.get(Calendar.DATE))
				return true;

			if(c1.get(Calendar.MONTH) !=  current.get(Calendar.MONTH))
				return true;
		}
		return false;*/


		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) + " WHERE " + 
				resources.getString(R.string.event_name) + "=?" + " ORDER BY " + 
				resources.getString(R.string.timestamp) + " LIMIT 3";

		Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] {eventName});
		if(cursor.getCount() == 0)
			return true;
		if(cursor.getCount() < 3){
			Calendar c1 = Calendar.getInstance();
			long t1 = 0;
			if(cursor.moveToLast()){
				t1 = cursor.getLong(2);

				Calendar c2 = Calendar.getInstance();
				c2.setTimeInMillis(t1);

				if(checkDate(c1, c2))
					return true;
			}
			return c1.getTimeInMillis() - t1 < 5 * 60000;
		} else	{		
			Calendar c1 = Calendar.getInstance();
			long t1 = 0;
			if(cursor.moveToLast()){
				t1 = cursor.getLong(2);

				Calendar c2 = Calendar.getInstance();
				c2.setTimeInMillis(t1);

				if(checkDate(c1, c2))
					return true;

				if(c1.getTimeInMillis() - t1 < 5 * 60000){
					if(cursor.moveToPrevious()){
						t1 = cursor.getLong(2);
						c1.setTimeInMillis(t1);

						if(checkDate(c1, c2))
							return true;

						if(c2.getTimeInMillis() - t1 < 5 * 60000){
							if(cursor.moveToPrevious()){
								t1 = cursor.getLong(2);
								c2.setTimeInMillis(t1);

								if(checkDate(c1, c2))
									return true;
								if(c1.getTimeInMillis() - t1 < 5 * 60000){
									return false;
								}
							}
						}					
					}
				}
			}
		}
		return false;
	}

	private boolean checkDate(Calendar c1, Calendar c2){
		if(c1.get(Calendar.DATE) != c2.get(Calendar.DATE))
			return true;
		if(c1.get(Calendar.MONTH) !=  c2.get(Calendar.MONTH))
			return true;
		return false;
	}

	public boolean checkQuestionaire(String eventName){
		String sql = "SELECT max(" +  resources.getString(R.string.timestamp) + ") FROM " + 
				resources.getString(R.string.table_name_questionaire);
		Cursor cursor = this.getWritableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			long i = cursor.getLong(0);
			long current = Calendar.getInstance().getTimeInMillis();
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTimeInMillis(i);
			c2.setTimeInMillis(current);
			if(c1.get(Calendar.DATE) != c2.get(Calendar.DATE))
				return true;

			if(c1.get(Calendar.MONTH) !=  c2.get(Calendar.MONTH))
				return true;
		}
		return false;
	}

	public Cursor getMultiSettings(String id){
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_id) + "=?";
		return this.getReadableDatabase().rawQuery(sql, new String[] {id});
	}

	public Cursor getMultiUsers() {
		String sql = "SELECT " + resources.getString(R.string.user_group) + "," +
				resources.getString(R.string.user_name) + "," + resources.getString(R.string.user_id) +
				" FROM " + resources.getString(R.string.table_name_multi_settings) + " GROUP BY " + 
				resources.getString(R.string.user_group);
		return this.getReadableDatabase().rawQuery(sql, null);
	}

	public int checkUserName(String name) {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_name) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {name});
		if(cursor.moveToFirst()){
			return cursor.getInt(0);
		}
		return -1;
	}

	public int getNewUserId(){
		String sql = "SELECT max(" + resources.getString(R.string.user_id) + ") FROM " +
				resources.getString(R.string.table_name_multi_settings);
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor.getInt(0) + 1;
		}
		return 1;
	}



}
