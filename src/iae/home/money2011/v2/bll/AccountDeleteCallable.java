package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;
//import iae.home.money2011.v2.datamodel.PayEntity;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class AccountDeleteCallable  implements Callable<AccountEntity> {
	private DatabaseHelper m_dbhelper;
	private AccountEntity m_account;  
	
	public AccountDeleteCallable(DatabaseHelper dbHelper, AccountEntity Account) {
		m_dbhelper=dbHelper;
		m_account=Account;
	}
	
	@Override
	public AccountEntity call() throws Exception {
		Dao<AccountEntity,Integer> daoAccounts=m_dbhelper.getAccountsDao();		
		Dao<PayEntity,Integer> daoPay=m_dbhelper.getPaysDao();
		
		AccountEntity deleteAccount=daoAccounts.queryForId(m_account.getId());
		
		daoPay.executeRaw("UPDATE "+PayEntity.TABLE+" SET "+PayEntity.MODIFIED+"=1, "+PayEntity.DELETED+"=1 WHERE "+PayEntity.ACCOUNT+"="+deleteAccount.getId());
		deleteAccount.setDeleted(true);
		BaseAction.updateObject(daoAccounts, deleteAccount);

		return null;
	}

}
