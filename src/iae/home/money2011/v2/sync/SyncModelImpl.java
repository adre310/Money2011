package iae.home.money2011.v2.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import com.j256.ormlite.dao.Dao;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.IBaseEntity;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.money2011.v2.datamodel.SyncEntity;
import iae.home.x10.model.IAccountServer;
import iae.home.x10.model.ICategoryServer;
import iae.home.x10.model.IDeviceInfo;
import iae.home.x10.model.IPayServer;
import iae.home.x10.model.ISyncServer;

public class SyncModelImpl implements ISyncServer {
	private final DatabaseHelper m_dbhelper;
	private static DeviceInfo m_info=null; 
	
	public SyncModelImpl(DatabaseHelper helper,Context context) {
		m_dbhelper=helper;
		if(m_info==null)
			m_info=DeviceInfo.getInstance(context);
	}
	
	@Override
	public IAccountServer getAccountByUUID(String uuid) throws Exception {
		Dao<AccountEntity,Integer> dao=m_dbhelper.getAccountsDao();
		List<AccountEntity> list=dao.queryBuilder().where().eq(IBaseEntity.R_GUID, uuid).query();
		if(list.size()!=0)
			return list.get(0);
		else {
			AccountEntity account=new AccountEntity(-1);
			account.setUUID(uuid);
			account.setModified(true);
			return account;
		}
	}

	@Override
	public List<IAccountServer> getAccountList() throws Exception {
		Dao<AccountEntity,Integer> dao=m_dbhelper.getAccountsDao();
		List<AccountEntity> queryAll=dao.queryBuilder().where().eq(IBaseEntity.MODIFIED, true).query();
		List<IAccountServer> res=new ArrayList<IAccountServer>();
		//int c=0;
		for (AccountEntity accountEntity : queryAll) {
			//c++;
			//if(c<4)
			res.add(accountEntity);
			
		}
		
		return res;
	}

	@Override
	public ICategoryServer getCategoryByUUID(String uuid) throws Exception {
		Dao<CategoryEntity,Integer> dao=m_dbhelper.getCategoriesDao();
		List<CategoryEntity> list=dao.queryBuilder().where().eq(IBaseEntity.R_GUID, uuid).query();
		if(list.size()!=0)
			return list.get(0);
		else {
			CategoryEntity category=new CategoryEntity(-1);
			category.setUUID(uuid);
			category.setModified(true);
			return category;
		}
	}

	@Override
	public List<ICategoryServer> getCategoryList() throws Exception {
		Dao<CategoryEntity,Integer> dao=m_dbhelper.getCategoriesDao();
		List<CategoryEntity> queryAll=dao.queryBuilder().where().eq(IBaseEntity.MODIFIED, true).query();
		List<ICategoryServer> res=new ArrayList<ICategoryServer>();
		//int c=0;
		for (CategoryEntity category : queryAll) {
			//c++;
			//if(c<4)
			res.add(category);
		}
		return res;
	}

	@Override
	public Date getLastSyncTime() throws Exception {
		Dao<SyncEntity, Integer> dao=m_dbhelper.getSyncDao();
		List<SyncEntity> list=dao.queryForAll();
		if(list.size()!=0)
			return list.get(0).getDate();
		else
			return new Date(100, 0, 1);
	}

	@Override
	public void setLastSyncTime(Date date) throws Exception {
		Dao<SyncEntity, Integer> dao=m_dbhelper.getSyncDao();
		List<SyncEntity> list=dao.queryForAll();
		SyncEntity sync;
		if(list.size()!=0)
			sync=list.get(0);
		else
			sync=new SyncEntity();
		sync.setDate(date);
		dao.createOrUpdate(sync);
	}

	@Override
	public IPayServer getPayByUUID(String uuid) throws Exception {
		Dao<PayEntity,Integer> dao=m_dbhelper.getPaysDao();
		List<PayEntity> list=dao.queryBuilder().where().eq(IBaseEntity.R_GUID, uuid).query();
		if(list.size()!=0) {
			return list.get(0);
		} else {
			PayEntity pay=new PayEntity(-1);
			pay.setUUID(uuid);
			pay.setModified(true);
			return pay;
		}
	}

	@Override
	public List<IPayServer> getPayList() throws Exception  {
		Dao<PayEntity,Integer> dao=m_dbhelper.getPaysDao();
		List<PayEntity> queryAll=dao.queryBuilder().where().eq(IBaseEntity.MODIFIED, true).query();
		List<IPayServer> res=new ArrayList<IPayServer>();
		
		//int c=0;
		for (PayEntity pay : queryAll) {
			if(pay.getCategory()!=null)
				m_dbhelper.getCategoriesDao().refresh(pay.getCategory());
			if(pay.getAccount()!=null)
				m_dbhelper.getAccountsDao().refresh(pay.getAccount());
			//c++;
			//if(c<4)
			  res.add(pay);
		}
		return res;
	}

	@Override
	public void updateAccount(IAccountServer account)  throws Exception {
		AccountEntity ent=(AccountEntity)account;
		ent.setModified(false);
		m_dbhelper.getAccountsDao().createOrUpdate(ent);
	}

	@Override
	public void updateCategory(ICategoryServer category)  throws Exception {
		CategoryEntity ent=(CategoryEntity)category;
		ent.setModified(false);
		Dao<CategoryEntity,Integer> dao=m_dbhelper.getCategoriesDao();
		if(ent.getDefault()) {
			dao.executeRaw("UPDATE "+CategoryEntity.TABLE+" SET "+CategoryEntity.MODIFIED+"=1,"+CategoryEntity.IS_DEFAULT+"=0 WHERE "+CategoryEntity.IS_DEFAULT+"=1");
		}
		dao.createOrUpdate(ent);
	}

	@Override
	public void updatePay(IPayServer pay)  throws Exception {
		PayEntity ent=(PayEntity)pay;
		ent.setModified(false);
		m_dbhelper.getPaysDao().createOrUpdate(ent);
	}

	@Override
	public IDeviceInfo getDeviceInfo()  throws Exception {
		return m_info;
	}

}
