package uni.apps.responsetesting.database;

import java.util.Calendar;

import uni.apps.responsetesting.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

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
			createEventTables(db);
			String create = "CREATE TABLE " + resources.getString(R.string.table_name_multi_settings) +
					"(" + resources.getString(R.string.user_id) + " INTEGER PRIMARY KEY, " + 
					resources.getString(R.string.user_group) + " TEXT, " + 
					resources.getString(R.string.user_name) + " TEXT, " +
					resources.getString(R.string.user_settings) + " TEXT, " +
					resources.getString(R.string.group_icon) + " INTEGER)";
			db.execSQL(create);
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
	}

	public void createEventTables(SQLiteDatabase db){
		String create = "CREATE TABLE " + resources.getString(R.string.table_name) +
				"(" + resources.getString(R.string.event_name) + " TEXT," + 
				resources.getString(R.string.event_score) + " TEXT," + 
				resources.getString(R.string.timestamp) + " INTEGER, " + 
				resources.getString(R.string.sent) + " INTEGER, " +
				resources.getString(R.string.notes) + " TEXT, " +
				resources.getString(R.string.user_id) + " TEXT, " +
				resources.getString(R.string.tries) + " INTEGER, " +
				resources.getString(R.string.all_scores) + " TEXT, " +
				"PRIMARY KEY(" + resources.getString(R.string.event_name) + 
				", " + resources.getString(R.string.timestamp) + "))";
		db.execSQL(create);
		create = "CREATE TABLE " + resources.getString(R.string.table_name_questionaire) +
				"(" + resources.getString(R.string.timestamp) + " INTEGER PRIMARY KEY, " + 
				resources.getString(R.string.total_sleep) + " TEXT, " + 
				resources.getString(R.string.light_sleep) + " TEXT, " + 
				resources.getString(R.string.sound_sleep) + " TEXT, " + 
				resources.getString(R.string.ratings) + " TEXT, " + 
				resources.getString(R.string.sent) + " INTEGER, " +
				resources.getString(R.string.user_id) + " TEXT, " + 
				resources.getString(R.string.heart_rate) + " TEXT)"; 
		db.execSQL(create);
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

	public void updateSingleTries(String testName, ContentValues values,
			String id) {
		String sql = "SELECT max(" + resources.getString(R.string.timestamp) + ") FROM " + resources.getString(R.string.table_name) +
				" WHERE " + resources.getString(R.string.event_name) + "=? AND " + resources.getString(R.string.user_id) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {testName, id});
		if(cursor.moveToFirst()){
			this.getWritableDatabase().update(resources.getString(R.string.table_name), values, 
					resources.getString(R.string.timestamp) + "=?", new String[]{cursor.getString(0)});
		}
	}

	//Update All Unsent to Sent
	public void updateMostRecent() {
		ContentValues values =  new ContentValues();
		values.put(resources.getString(R.string.sent), 1);
		this.getWritableDatabase().update(resources.getString(R.string.table_name), values,
				resources.getString(R.string.sent) + "=?", new String[] {"0"});

	}

	public void updateMostRecentQuest() {
		ContentValues values =  new ContentValues();
		values.put(resources.getString(R.string.sent), 1);
		this.getWritableDatabase().update(resources.getString(R.string.table_name_questionaire), values,
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
	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			db.beginTransaction();
			String query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name);
			db.execSQL(query);
			query = "DROP TABLE IF EXISTS " + resources.getString(R.string.table_name_questionaire);
			db.execSQL(query);
			createEventTables(db);
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
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

	//All rows for single Event and user
	public Cursor getSingle(String testName, String userId) {
		return this.getReadableDatabase().query(resources.getString(R.string.table_name), null, 
				resources.getString(R.string.event_name) + "=? AND " + resources.getString(R.string.user_id) + "=?", 
				new String[] {testName, userId}, null, null, resources.getString(R.string.timestamp));
	}

	public String[] getSingleTries(String testName, String id, Activity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(resources.getString(R.string.pref_key_theme), 
				resources.getString(R.string.settings_theme_default));
		String sql = "SELECT max(" + resources.getString(R.string.timestamp) + ") FROM " + resources.getString(R.string.table_name) +
				" WHERE " + resources.getString(R.string.event_name) + "=? AND " + resources.getString(R.string.user_id) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {testName, id});
		if(cursor.moveToFirst()){
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(cursor.getLong(0));
			Calendar current = Calendar.getInstance();
			if(c.get(Calendar.DATE) == current.get(Calendar.DATE) && 
					c.get(Calendar.MONTH) == current.get(Calendar.MONTH)){
				if(theme.equals("Forestry") && checkAfternoon(current, c))
					return new String[] {"1", "", ""};
				else{
					sql = "SELECT " + resources.getString(R.string.tries) + ", " +  resources.getString(R.string.event_score) + ", " + 
							resources.getString(R.string.all_scores) + " FROM " + resources.getString(R.string.table_name) +
							" WHERE " + resources.getString(R.string.timestamp) + "=?";
					Cursor tries = this.getReadableDatabase().rawQuery(sql, new String[] {Long.toString(cursor.getLong(0))});
					if(tries.moveToFirst())
						return new String[] {Integer.toString(tries.getInt(0) + 1), tries.getString(1), tries.getString(2)};
				}
			}
		} 
		return new String[] {"1", "", ""};
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
				" WHERE " + resources.getString(R.string.sent) + "=?";
		Cursor cursor =  this.getReadableDatabase().rawQuery(sql, new String[]{"0"});
		//updateMostRecent();
		return cursor;
	}

	public Cursor getMostRecentQuestionaire() {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_questionaire) +
				" WHERE " + resources.getString(R.string.sent) + "=0";// ORDER BY " + resources.getString(R.string.timestamp);
		Cursor cursor =  this.getReadableDatabase().rawQuery(sql, null);
		//updateMostRecentQuest();
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

	//checks most recently played times for 3 goes in 1 day
	public boolean checkRecent(String eventName, String id, Activity activity) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = prefs.getString(resources.getString(R.string.pref_key_theme), 
				resources.getString(R.string.settings_theme_default));
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name) + " WHERE " + 
				resources.getString(R.string.event_name) + "=? AND " +
				resources.getString(R.string.user_id) + "=?";
		Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[] {eventName, id});
		if(cursor.getCount() == 0)
			return true;
		else{
			if(cursor.moveToLast()){
				Calendar current = Calendar.getInstance();
				Calendar last = Calendar.getInstance();
				last.setTimeInMillis(cursor.getLong(2));
				//check for same day
				if(checkDate(current, last))
					return true;
				if(theme.equals("Forestry")){
					//check if afternoon
					if(checkAfternoon(current, last))
						return true;
				}
				//check if more than 5 min has passed
				if(current.getTimeInMillis() - cursor.getLong(2) < 5 * 60 * 1000){
					//check amount of tries
					if(cursor.getInt(6) != 3)
						return true;
				}

			}
		}
		return false;
	}

	private boolean checkAfternoon(Calendar current, Calendar last) {
		return (last.get(Calendar.HOUR_OF_DAY) < 12 && current.get(Calendar.HOUR_OF_DAY) >= 12);
	}

	//checks if date is not the same
	private boolean checkDate(Calendar c1, Calendar c2){
		return (c1.get(Calendar.DATE) != c2.get(Calendar.DATE) ||
				c1.get(Calendar.MONTH) !=  c2.get(Calendar.MONTH) ||
				c1.get(Calendar.YEAR) !=  c2.get(Calendar.YEAR));
	}

	//checks if questionaire was done today
	public boolean checkQuestionaire(String eventName, String id){
		String sql = "SELECT max(" +  resources.getString(R.string.timestamp) + ") FROM " + 
				resources.getString(R.string.table_name_questionaire) + " WHERE " + 
				resources.getString(R.string.user_id) + "=?";
		Cursor cursor = this.getWritableDatabase().rawQuery(sql, new String[]{id});
		if(cursor.moveToFirst()){
			long i = cursor.getLong(0);
			long current = Calendar.getInstance().getTimeInMillis();
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.setTimeInMillis(i);
			c2.setTimeInMillis(current);
			return checkDate(c1, c2);
		}
		return false;
	}

	//gets multi users settings
	public Cursor getMultiSettings(String id){
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_id) + "=?";
		return this.getReadableDatabase().rawQuery(sql, new String[] {id});
	}

	//gets multi users settings
	public String getMultiSettingsString(String id) {
		Cursor settings = getMultiSettings(id);
		if(settings.moveToFirst()){
			return settings.getString(3);
		}
		return "";
	}

	//gets multi users settings user info
	public Cursor getMultiUsers() {
		String sql = "SELECT " + resources.getString(R.string.user_group) + "," +
				resources.getString(R.string.user_name) + "," + resources.getString(R.string.user_id) +
				"," + resources.getString(R.string.group_icon) + " FROM " + 
				resources.getString(R.string.table_name_multi_settings);
		return this.getReadableDatabase().rawQuery(sql, null);
	}

	////checks if user name exists
	public int checkUserName(String name) {
		String sql = "SELECT * FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_name) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {name});
		if(cursor.moveToFirst()){
			return cursor.getInt(0);
		}
		return -1;
	}

	//gets new user id
	public int getNewUserId(){
		String sql = "SELECT max(" + resources.getString(R.string.user_id) + ") FROM " +
				resources.getString(R.string.table_name_multi_settings);
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor.getInt(0) + 1;
		}
		return 1;
	}

	//checks if user exists
	public String checkUserExists(String name) {
		String sql = "SELECT " + resources.getString(R.string.user_id) +" FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_name) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {name});
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return "-1";
	}

	//gets username
	public String getMultiUserName(String id) {
		String sql = "SELECT " + resources.getString(R.string.user_name) +" FROM " + resources.getString(R.string.table_name_multi_settings) + 
				" WHERE " + resources.getString(R.string.user_id) + "=?";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, new String[] {id});
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return "single";
	}
	
	public String getFirstGroupName(){
		Cursor cursor = this.getReadableDatabase().query(resources.getString(R.string.table_name_multi_settings),
				new String[] {resources.getString(R.string.user_group)}, null, null, null, null, null, "1");
		if(cursor.moveToFirst())
			return cursor.getString(0);
		return "Unassigned";
	}


}
