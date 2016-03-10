package iae.home.x10.Serialization.Json;

import java.util.Date;
import java.util.List;

import iae.home.x10.model.IAccountServer;
import iae.home.x10.model.ICategoryServer;
import iae.home.x10.model.IDeviceInfo;
import iae.home.x10.model.IPayServer;
import iae.home.x10.model.ISyncServer;
import iae.home.x10.xml.ISO8601DateHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class SyncSerialization extends AbstractSerialization {
	private final ISyncServer m_data;
	
	public SyncSerialization(ISyncServer data) {
		m_data=data;
	}
		
	protected JSONObject AccountSerialization(IAccountServer account)  throws Exception {
//{\"uuid\":\"a-u1\",\"is_deleted\":false,\"notes\":\"line1\\n     line2\",\"name\":\"acc1\",\"currency\":\"RUB\",\"monthly_limit\":423.5678,\"limit_notification\":true		
		JSONObject jsonAccount=new JSONObject();
		jsonAccount.put("uuid",account.getUUID());
		jsonAccount.put("is_deleted",account.getDeleted());		
		jsonAccount.put("notes",account.getDescription());
		jsonAccount.put("name",account.getName());
		jsonAccount.put("currency",account.getCurrency());
		jsonAccount.put("monthly_limit",account.getMonthlyLimit());
		jsonAccount.put("limit_notification",account.getLimitNotification());
		
		return jsonAccount;
	}
	
	protected JSONObject CategorySerialization(ICategoryServer category) throws Exception {
//{\"uuid\":\"c-u1\",\"is_deleted\":false,\"notes\":\"line1\\n     line2\",\"name\":\"cat1\",\"is_default\":false}
		JSONObject jsonCategory=new JSONObject();
		jsonCategory.put("uuid",category.getUUID());
		jsonCategory.put("is_deleted",category.getDeleted());		
		jsonCategory.put("notes",category.getDescription());
		jsonCategory.put("name",category.getName());
		jsonCategory.put("is_default",category.getDefault());
		jsonCategory.put("style", category.getThemeId());
		
		return jsonCategory;
	}

	protected JSONObject PaySerialization(IPayServer pay) throws Exception {
//{\"uuid\":\"p-u1\",\"is_deleted\":false,\"notes\":\"Privet\\n          Medved!\",\"pay_value\":2345.789,\"pay_date\":\"2011-10-28T18:52:54Z\",\"account_uuid\":\"a-u2\",\"category_uuid\":\"c-u1\",\"is_system\":false}		
		JSONObject jsonPay=new JSONObject();
		jsonPay.put("uuid",pay.getUUID());
		jsonPay.put("is_deleted",pay.getDeleted());		
		jsonPay.put("notes",pay.getDescription());
		jsonPay.put("pay_value",pay.getValue());
		if(pay.getDate()!=null)
		jsonPay.put("pay_date",ISO8601DateHelper.dateToString(
					 pay.getDate(), 
					 ISO8601DateHelper.DATE_TIME));
		if(pay.getAccount()!=null)
			jsonPay.put("account_uuid", pay.getAccount().getUUID());
		if(pay.getCategory()!=null)
			jsonPay.put("category_uuid", pay.getCategory().getUUID());
		jsonPay.put("is_system",pay.getSystem());		
		if(pay.getLinked()!=null)
			jsonPay.put("linked_uuid", pay.getLinked().getUUID());
		
		return jsonPay;
	}
	
	protected JSONObject DeviceInfoSerialization(IDeviceInfo info) throws Exception {
		JSONObject jsonInfo=new JSONObject();
		jsonInfo.put("package", info.getPackageName());
		jsonInfo.put("version", info.getVersion());
		jsonInfo.put("phone_model", info.getPhoneModel());
		jsonInfo.put("phone_os", info.getPhoneOS());
		jsonInfo.put("phone_id", info.getPhoneId());
		return jsonInfo;
	}

	@Override
	protected JSONObject handleJsonRequest()  throws Exception {
		JSONObject requestObject=new JSONObject();
		requestObject.put("date", ISO8601DateHelper.dateToString(m_data.getLastSyncTime(), ISO8601DateHelper.DATE_TIME));
		requestObject.put("info", DeviceInfoSerialization(m_data.getDeviceInfo()));
		
		List<IAccountServer> accList=m_data.getAccountList();
		if((accList!=null) && (accList.size()!=0)) {
			JSONArray accListJson=new JSONArray();
			for (IAccountServer account : accList) {
				accListJson.put(AccountSerialization(account));
			}
			requestObject.put("accounts", accListJson);
		}

		List<ICategoryServer> catList=m_data.getCategoryList();
		if((catList!=null) && (catList.size()!=0)) {
			JSONArray catListJson=new JSONArray();
			for (ICategoryServer category : catList) {
				catListJson.put(CategorySerialization(category));
			}
			requestObject.put("categories", catListJson);
		}

		List<IPayServer> payList=m_data.getPayList();
		if((payList!=null)&&(payList.size()!=0)){
			JSONArray payListJson=new JSONArray();
			for (IPayServer pay : payList) {
				//if(pay.getAccount()!=null)
					payListJson.put(PaySerialization(pay));
			}
			
			requestObject.put("pays", payListJson);
		}
		return requestObject;
	}
}
