package iae.home.x10.Serialization.Json;

import iae.home.x10.model.IAccountServer;
import iae.home.x10.model.ICategoryServer;
import iae.home.x10.model.IPayServer;
import iae.home.x10.model.ISyncServer;
import iae.home.x10.xml.ISO8601DateHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class SyncDeserialization extends AbstractDeserialization {
	private final ISyncServer m_data;
	
	public SyncDeserialization(ISyncServer data) {
		m_data=data;
	}
	
	protected void AccountDeserialization(JSONObject account) throws Exception {
		Log.i(this.getClass().getName(),"Account: "+account.toString());
//{\"uuid\":\"a-u1\",\"is_deleted\":false,\"notes\":\"line1\\n     line2\",\"name\":\"acc1\",\"currency\":\"RUB\",\"monthly_limit\":423.5678,\"limit_notification\":true		
		IAccountServer dbAccount=m_data.getAccountByUUID(account.getString("uuid"));
		dbAccount.setDeleted(account.getBoolean("is_deleted"));
		dbAccount.setDescription(account.optString("notes"));
		dbAccount.setName(account.getString("name"));
		dbAccount.setCurrency(account.getString("currency"));
		dbAccount.setMonthlyLimit(account.optDouble("monthly_limit", 0));
		dbAccount.setLimitNotification(account.optBoolean("limit_notification", false));
		
		m_data.updateAccount(dbAccount);
	}

	protected void CategoryDeserialization(JSONObject category) throws Exception {
//{\"uuid\":\"c-u1\",\"is_deleted\":false,\"notes\":\"line1\\n     line2\",\"name\":\"cat1\",\"is_default\":false}
		Log.i(this.getClass().getName(),"Category: "+category.toString());
		ICategoryServer dbCategory=m_data.getCategoryByUUID(category.getString("uuid"));
		dbCategory.setDeleted(category.getBoolean("is_deleted"));
		dbCategory.setDescription(category.optString("notes"));
		dbCategory.setName(category.getString("name"));
		dbCategory.setDefault(category.optBoolean("is_default",false));
		dbCategory.setThemeId(category.optInt("style",0));
		
		m_data.updateCategory(dbCategory);		
	}
	
	protected void PayDeserialization(JSONObject pay) throws Exception {
//{\"uuid\":\"p-u1\",\"is_deleted\":false,\"notes\":\"Privet\\n          Medved!\",
//\"pay_value\":2345.789,\"pay_date\":\"2011-10-28T18:52:54Z\",
//\"account_uuid\":\"a-u2\",\"category_uuid\":\"c-u1\",\"is_system\":false}		
		Log.i(this.getClass().getName(),"Pay: "+pay.toString());
		IPayServer dbPay=m_data.getPayByUUID(pay.getString("uuid"));
		dbPay.setDeleted(pay.getBoolean("is_deleted"));
		dbPay.setDescription(pay.optString("notes"));
		dbPay.setValue(pay.getDouble("pay_value"));
		dbPay.setDate(ISO8601DateHelper.stringToDate(pay.getString("pay_date"), ISO8601DateHelper.DATE_TIME));
		String accountUUID=pay.optString("account_uuid");
		if((accountUUID!=null) && !accountUUID.equals("")) {
			dbPay.setAccount(m_data.getAccountByUUID(accountUUID));
		} else {
			dbPay.setAccount(null);			
		}

		String categoryUUID=pay.optString("category_uuid");
		if((categoryUUID!=null) && !categoryUUID.equals("")) {
			dbPay.setCategory(m_data.getCategoryByUUID(categoryUUID));
		} else {
			dbPay.setCategory(null);			
		}
		
		dbPay.setSystem(pay.optBoolean("is_system", false));
		m_data.updatePay(dbPay);		
	}

	@Override
	protected void handleJsonRequest(JSONObject jsonResponse) throws Exception {
		String sError=jsonResponse.optString("error");
		if(!sError.equals(""))
			throw new Exception(sError);
		
		JSONObject responseData=jsonResponse.optJSONObject("data");
		if(responseData!=null) {
			Log.i(this.getClass().getName(),"Response: "+responseData.toString());
			m_data.setLastSyncTime(ISO8601DateHelper.stringToDate(responseData.optString("date"), ISO8601DateHelper.DATE_TIME));
			
			JSONArray accounts=responseData.optJSONArray("accounts");
			for(int i=0;i<accounts.length();i++)
				AccountDeserialization(accounts.getJSONObject(i));
			
			JSONArray categories=responseData.optJSONArray("categories");
			for(int i=0;i<categories.length();i++)
				CategoryDeserialization(categories.getJSONObject(i));
			
			JSONArray pays=responseData.optJSONArray("pays");
			for(int i=0;i<pays.length();i++)
				PayDeserialization(pays.getJSONObject(i));			
		}
	}
}
