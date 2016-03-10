package iae.home.x10.model;

import java.util.Date;
import java.util.List;

public interface ISyncServer {
	IDeviceInfo getDeviceInfo() throws Exception ;
	
	Date getLastSyncTime() throws Exception ;
	void setLastSyncTime(Date newDate) throws Exception ;
	
	List<IAccountServer> getAccountList()  throws Exception;
	List<ICategoryServer> getCategoryList() throws Exception;
	List<IPayServer> getPayList() throws Exception;
	
	IAccountServer getAccountByUUID(String uuid) throws Exception;
	void updateAccount(IAccountServer account) throws Exception;
	
	ICategoryServer getCategoryByUUID(String uuid) throws Exception;
	void updateCategory(ICategoryServer category) throws Exception;

	IPayServer getPayByUUID(String uuid) throws Exception;
	void updatePay(IPayServer pay) throws Exception;
}
