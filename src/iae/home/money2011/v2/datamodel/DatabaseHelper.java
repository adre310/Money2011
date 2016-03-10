package iae.home.money2011.v2.datamodel;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.nullwire.trace.DefaultExceptionHandler;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "money.db";
    private static final int DATABASE_VERSION = 9;
    
	private Dao<PayEntity, Integer> m_paysDao;
	private Dao<CategoryEntity, Integer> m_categoriesDao;
	private Dao<AccountEntity, Integer> m_accountsDao;
	private Dao<SyncEntity, Integer> m_syncDao;
	
//	private final Context m_context;

/*
 * 	
 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		m_context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			//Map<String,String> mapEvent=new HashMap<String, String>();
			FlurryAgent.onEvent("DB_onCreate");
			
			database.execSQL("CREATE TABLE pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT NULL, pay_value REAL, pay_date INTEGER, account_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER, UNIQUE(r_guid))");
			database.execSQL("CREATE TABLE accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT NULL, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER, UNIQUE(r_guid))");
			database.execSQL("CREATE TABLE categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT NULL, name TEXT NOT NULL, is_default INTEGER, theme_id INTEGER, UNIQUE(r_guid))");
			database.execSQL("CREATE TABLE sync_info (_id INTEGER PRIMARY KEY AUTOINCREMENT, sync_date INTEGER)");

		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Невозможно создать БД", e);
			DefaultExceptionHandler.reportException(e);
		}
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		//database.beginTransaction();
		try {
			Map<String,String> mapEvent=new HashMap<String, String>();
			//mapEvent.put("new", Integer.toString(newVersion));
			mapEvent.put("old", Integer.toString(oldVersion));
			FlurryAgent.onEvent("DB_onUpgrade",mapEvent);
			
			switch(oldVersion) {
				case 1:
					Upgrade1(database, connectionSource);
					Upgrade2(database, connectionSource);
					Upgrade3(database, connectionSource);
					Upgrade4(database, connectionSource);
					Upgrade5(database, connectionSource);
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 2:
					Upgrade2(database, connectionSource);
					Upgrade3(database, connectionSource);
					Upgrade4(database, connectionSource);
					Upgrade5(database, connectionSource);
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 3:
					Upgrade3(database, connectionSource);
					Upgrade4(database, connectionSource);
					Upgrade5(database, connectionSource);
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 4:
					Upgrade4(database, connectionSource);
					Upgrade5(database, connectionSource);
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 5:
					Upgrade5(database, connectionSource);
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 6:
					Upgrade6(database, connectionSource);
					Upgrade7(database, connectionSource);
					break;
				case 7:
					Upgrade7(database, connectionSource);
					Upgrade8(database, connectionSource);
					break;
				case 8:
					Upgrade8(database, connectionSource);
					break;
			}
			//database.setTransactionSuccessful();
		} catch (SQLException e) {
			//database.endTransaction();
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new "
					+ newVersion, e);
			DefaultExceptionHandler.reportException(e);
		}
	}
	
	private void Upgrade1(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		database.execSQL("CREATE TABLE t1_pays (`_id` INTEGER PRIMARY KEY AUTOINCREMENT , `budget_id` INTEGER , `category_id` INTEGER , `pay_date` BIGINT , `linked_id` INTEGER , `modified` BIGINT , `is_system` SMALLINT , `r_guid` VARCHAR , `pay_value` BIGINT ) ");
		database.execSQL("INSERT INTO t1_pays SELECT * FROM pays");
		database.execSQL("DROP TABLE pays");
		database.execSQL("CREATE TABLE pays (_id INTEGER PRIMARY KEY AUTOINCREMENT , budget_id INTEGER , category_id INTEGER , pay_date INTEGER , linked_id INTEGER , modified INTEGER , is_system INTEGER , r_guid TEXT NOT NULL , pay_value REAL , UNIQUE(r_Guid))");
		database.execSQL("INSERT INTO pays SELECT * FROM t1_pays");
		database.execSQL("DROP TABLE t1_pays");
	}

	private void Upgrade2(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		
		
		database.execSQL("CREATE TABLE t2_pays (_id INTEGER PRIMARY KEY AUTOINCREMENT , budget_id INTEGER , category_id INTEGER , pay_date INTEGER , linked_id INTEGER , modified INTEGER , is_system INTEGER , r_guid TEXT, pay_value REAL)");
		database.execSQL("CREATE TABLE t2_budgets (_id INTEGER PRIMARY KEY AUTOINCREMENT , currency INTEGER , modified INTEGER , name TEXT, r_guid TEXT)");
		database.execSQL("CREATE TABLE t2_categories (_id INTEGER PRIMARY KEY AUTOINCREMENT , is_default INTEGER , modified INTEGER , name TEXT, r_guid TEXT)");
		
		Cursor c;
		c=database.rawQuery("SELECT * FROM pays", null);
		if(c!=null && c.moveToFirst()) {
			do {
		        ContentValues iv=new ContentValues(); 
		        iv.put("_id", c.getInt(c.getColumnIndex("_id")));
		        String uuid=c.getString(c.getColumnIndex("r_guid"));
		        if( uuid == null || uuid.equals(""))
		        	uuid=UUID.randomUUID().toString();
		        iv.put("r_guid", uuid);
		        iv.put("modified", c.getLong(c.getColumnIndex("modified")));
		
		        iv.put("pay_value", c.getDouble(c.getColumnIndex("pay_value")));
		        iv.put("pay_date", c.getLong(c.getColumnIndex("pay_date")));
		        iv.put("budget_id", c.getInt(c.getColumnIndex("budget_id")));
		        iv.put("category_id", c.getInt(c.getColumnIndex("category_id")));
		        iv.put("linked_id", c.getInt(c.getColumnIndex("linked_id")));
		        iv.put("is_system", c.getInt(c.getColumnIndex("is_system")));
				
		        database.insert("t2_pays", null, iv);
			} while(c.moveToNext());
		}
		c.close();

		c=database.rawQuery("SELECT * FROM budgets", null);
		if(c!=null && c.moveToFirst()) {
			do {
		        ContentValues iv=new ContentValues(); 
		        iv.put("_id", c.getInt(c.getColumnIndex("_id")));
		        String uuid=c.getString(c.getColumnIndex("r_guid"));
		        if( uuid == null || uuid.equals(""))
		        	uuid=UUID.randomUUID().toString();
		        iv.put("r_guid", uuid);
		        iv.put("modified", c.getLong(c.getColumnIndex("modified")));

		        String name=c.getString(c.getColumnIndex("name"));
		        if(name==null || name.equals(""))
		        	name=c.getString(c.getColumnIndex("_id"));
		        iv.put("name", name.trim());
		        iv.put("currency", c.getInt(c.getColumnIndex("currency")));
		        
		        database.insert("t2_budgets", null, iv);
			} while(c.moveToNext());
		}
		c.close();
		
		c=database.rawQuery("SELECT * FROM categories", null);
		if(c!=null && c.moveToFirst()) {
			do {
		        ContentValues iv=new ContentValues(); 
		        iv.put("_id", c.getInt(c.getColumnIndex("_id")));
		        String uuid=c.getString(c.getColumnIndex("r_Guid"));
		        if( uuid == null || uuid.equals(""))
		        	uuid=UUID.randomUUID().toString();
		        iv.put("r_guid", uuid);
		        iv.put("modified", c.getLong(c.getColumnIndex("modified")));

		        String name=c.getString(c.getColumnIndex("name"));
		        if(name==null || name.equals(""))
		        	name=c.getString(c.getColumnIndex("_id"));
		        iv.put("name", name.trim());

		        Integer isDefault=c.getInt(c.getColumnIndex("is_default"));
		        if(isDefault==null)
		        	isDefault=0;
		        iv.put("is_default", isDefault);
		        
		        database.insert("t2_categories", null, iv);
			} while(c.moveToNext());
		}
		c.close();
		
		
		database.execSQL("DROP TABLE pays");
		database.execSQL("DROP TABLE budgets");
		database.execSQL("DROP TABLE categories");

		database.execSQL("CREATE TABLE pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, pay_value REAL, pay_date INTEGER, budget_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE budgets (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, name TEXT NOT NULL, is_default INTEGER, UNIQUE(name), UNIQUE(r_guid))");
		
		database.execSQL("UPDATE t2_categories SET name=name+' '+_id WHERE name IN (SELECT name FROM t2_categories GROUP BY name HAVING COUNT(*) > 1)");
		
		database.execSQL("INSERT INTO pays(_id, r_guid, modified, pay_value, pay_date, budget_id, category_id, linked_id, is_system) SELECT _id, r_guid, modified, pay_value, pay_date, budget_id, category_id, linked_id, is_system FROM t2_pays");
		database.execSQL("INSERT INTO categories(_id, r_guid, modified, name, is_default) SELECT _id, r_guid, modified, name, is_default FROM t2_categories"); 

		HashMap<Integer, String> currencyMap=new HashMap<Integer, String>();
		currencyMap.put(643,"RUB");
		currencyMap.put(840,"USD"); 
		currencyMap.put(978,"EUR"); 
		currencyMap.put(36,"AUD");
		currencyMap.put(944,"AZN"); 
		currencyMap.put(51,"AMD");
		currencyMap.put(974,"BYR"); 
		currencyMap.put(975,"BGN");
		currencyMap.put(986,"BRL"); 
		currencyMap.put(348,"HUF"); 
		currencyMap.put(410,"KRW"); 
		currencyMap.put(208,"DKK");
		currencyMap.put(356,"INR"); 
		currencyMap.put(398,"KZT");
		currencyMap.put(124,"CAD"); 
		currencyMap.put(417,"KGS"); 
		currencyMap.put(156,"CNY"); 
		currencyMap.put(428,"LVL");
		currencyMap.put(440,"LTL");
		currencyMap.put(498,"MDL");
		currencyMap.put(946,"RON"); 
		currencyMap.put(934,"TMT"); 
		currencyMap.put(578,"NOK");
		currencyMap.put(985,"PLN");
		currencyMap.put(702,"SGD"); 
		currencyMap.put(972,"TJS");
		currencyMap.put(949,"TRY");
		currencyMap.put(860,"UZS");
		currencyMap.put(980,"UAH"); 
		currencyMap.put(826,"GBP"); 
		currencyMap.put(203,"CZK");
		currencyMap.put(752,"SEK");
		currencyMap.put(756,"CHF"); 
		currencyMap.put(710,"ZAR"); 
		currencyMap.put(392,"JPY");
		
		c=database.rawQuery("SELECT * FROM t2_budgets", null);
		if(c!=null && c.moveToFirst()) {
			do {
		        ContentValues iv=new ContentValues(); 
		        iv.put("_id", c.getInt(c.getColumnIndex("_id")));
		        iv.put("r_guid", c.getString(c.getColumnIndex("r_guid")));
		        iv.put("modified", c.getLong(c.getColumnIndex("modified")));

		        iv.put("name", c.getString(c.getColumnIndex("name")));
		        if(currencyMap.containsKey(c.getInt(c.getColumnIndex("currency"))))
		        	iv.put("currency", currencyMap.get(c.getInt(c.getColumnIndex("currency"))));
		        else
		        	iv.put("currency", "USD");

		        iv.put("monthly_limit", 0);
		        iv.put("limit_notification", 0);
		        
		        database.insert("budgets", null, iv);
			} while(c.moveToNext());
		}
		c.close();

		database.execSQL("DROP TABLE t2_pays");
		database.execSQL("DROP TABLE t2_budgets");
		database.execSQL("DROP TABLE t2_categories");		
	}

	private void Upgrade3(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {

		try {
			database.execSQL("DROP TABLE t2_pays");
		} catch(Exception e) {}
		try {
			database.execSQL("DROP TABLE t2_accounts");		
		} catch(Exception e) {}
		
		database.execSQL("CREATE TABLE t2_pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, pay_value REAL, pay_date INTEGER, account_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER)");
		database.execSQL("CREATE TABLE t2_accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER)");

		database.execSQL("INSERT INTO t2_pays(_id, r_guid, modified, pay_value, pay_date, account_id, category_id, linked_id, is_system) SELECT _id, r_guid, modified, pay_value, pay_date, budget_id, category_id, linked_id, is_system FROM pays");
		database.execSQL("INSERT INTO t2_accounts(_id, r_guid, modified, name, currency, monthly_limit, limit_notification) SELECT _id, r_guid, modified, TRIM(name), currency, monthly_limit, limit_notification FROM budgets");

		database.execSQL("DROP TABLE pays");
		database.execSQL("DROP TABLE budgets");
		
		database.execSQL("CREATE TABLE pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, pay_value REAL, pay_date INTEGER, account_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER, UNIQUE(r_guid))");
		
		database.execSQL("INSERT INTO pays(_id, r_guid, modified, pay_value, pay_date, account_id, category_id, linked_id, is_system) SELECT _id, r_guid, modified, pay_value, pay_date, account_id, category_id, linked_id, is_system FROM t2_pays");
		database.execSQL("INSERT INTO accounts(_id, r_guid, modified, name, currency, monthly_limit, limit_notification) SELECT _id, r_guid, modified, name, currency, monthly_limit, limit_notification FROM t2_accounts");

		database.execSQL("DROP TABLE t2_pays");
		database.execSQL("DROP TABLE t2_accounts");		
	}

	private void Upgrade4(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		
		try {
			database.execSQL("DROP TABLE t2_pays");
		} catch(Exception e) {}
		try {
			database.execSQL("DROP TABLE t2_accounts");
		} catch(Exception e) {}
		try {
			database.execSQL("DROP TABLE t2_categories");		
		} catch(Exception e) {}
		
		database.execSQL("CREATE TABLE t2_pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, pay_value REAL, pay_date INTEGER, account_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE t2_accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE t2_categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, name TEXT NOT NULL, is_default INTEGER, UNIQUE(name), UNIQUE(r_guid))");

		database.execSQL("INSERT INTO t2_pays (_id, r_guid, modified, deleted, notes, pay_value, pay_date, account_id, category_id, linked_id, is_system) SELECT _id, r_guid, 1, 0, '', pay_value, pay_date, account_id, category_id, linked_id, is_system FROM pays");
		database.execSQL("INSERT INTO t2_accounts (_id, r_guid, modified, deleted, notes, name, currency, monthly_limit, limit_notification) SELECT _id, r_guid, 1, 0, '', name, currency, monthly_limit, limit_notification FROM accounts");
		database.execSQL("INSERT INTO t2_categories (_id, r_guid, modified, deleted, notes, name, is_default) SELECT _id, r_guid, 1, 0, '', name, is_default FROM categories");

		database.execSQL("DROP TABLE pays");
		database.execSQL("DROP TABLE accounts");
		database.execSQL("DROP TABLE categories");		

		database.execSQL("CREATE TABLE pays (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, pay_value REAL, pay_date INTEGER, account_id INTEGER, category_id INTEGER, linked_id INTEGER, is_system INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE accounts (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, name TEXT NOT NULL, currency TEXT, monthly_limit REAL, limit_notification INTEGER, UNIQUE(r_guid))");
		database.execSQL("CREATE TABLE categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT, name TEXT NOT NULL, is_default INTEGER, UNIQUE(r_guid))");

		database.execSQL("INSERT INTO pays (_id, r_guid, modified, deleted, notes, pay_value, pay_date, account_id, category_id, linked_id, is_system) SELECT _id, r_guid, modified, deleted, notes, pay_value, pay_date, account_id, category_id, linked_id, is_system FROM t2_pays");
		database.execSQL("INSERT INTO accounts (_id, r_guid, modified, deleted, notes, name, currency, monthly_limit, limit_notification) SELECT _id, r_guid, modified, deleted, notes, name, currency, monthly_limit, limit_notification FROM t2_accounts");
		database.execSQL("INSERT INTO categories (_id, r_guid, modified, deleted, notes, name, is_default) SELECT _id, r_guid, modified, deleted, notes, name, is_default FROM t2_categories");

		database.execSQL("DROP TABLE t2_pays");
		database.execSQL("DROP TABLE t2_accounts");
		database.execSQL("DROP TABLE t2_categories");		
	}

	private void Upgrade5(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		database.execSQL("CREATE TABLE sync_info (_id INTEGER PRIMARY KEY AUTOINCREMENT, sync_date INTEGER)");
	}

	private void Upgrade6(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		try {
			database.execSQL("DROP TABLE t2_categories");		
		} catch(Exception e) {}

		database.execSQL("CREATE TABLE t2_categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT NULL, name TEXT NOT NULL, is_default INTEGER, theme_id INTEGER, UNIQUE(r_guid))");
		database.execSQL("INSERT INTO t2_categories (_id, r_guid, modified, deleted, notes, name, is_default, theme_id) SELECT _id, r_guid, modified, deleted, notes, name, is_default, 0 FROM categories");
		database.execSQL("DROP TABLE categories");		
		database.execSQL("CREATE TABLE categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, r_guid TEXT NOT NULL, modified INTEGER, deleted INTEGER, notes TEXT NULL, name TEXT NOT NULL, is_default INTEGER, theme_id INTEGER, UNIQUE(r_guid))");
		database.execSQL("INSERT INTO categories (_id, r_guid, modified, deleted, notes, name, is_default, theme_id) SELECT _id, r_guid, modified, deleted, notes, name, is_default, theme_id FROM t2_categories");
		database.execSQL("DROP TABLE t2_categories");		
	}

	private void Upgrade7(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		database.execSQL("DELETE FROM pays WHERE account_id IS NULL");
	}

	private void Upgrade8(SQLiteDatabase database,
			ConnectionSource connectionSource) throws SQLException {
		database.execSQL("UPDATE accounts SET modified=1 WHERE deleted<>0");
	}
	
	public Dao<PayEntity, Integer> getPaysDao() throws SQLException {
		if (m_paysDao == null) {
			m_paysDao = getDao(PayEntity.class);
		}
		return m_paysDao;
	}

	public Dao<CategoryEntity, Integer> getCategoriesDao() throws SQLException {
		if (m_categoriesDao == null) {
			m_categoriesDao = getDao(CategoryEntity.class);
		}
		return m_categoriesDao;
	}

	public Dao<AccountEntity, Integer> getAccountsDao() throws SQLException {
		if (m_accountsDao == null) {
			m_accountsDao = getDao(AccountEntity.class);
		}
		return m_accountsDao;
	}

	public Dao<SyncEntity, Integer> getSyncDao() throws SQLException {
		if (m_syncDao == null) {
			m_syncDao = getDao(SyncEntity.class);
		}
		return m_syncDao;
	}
}
