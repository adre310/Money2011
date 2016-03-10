package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class AccountMergeCallable implements Callable<Void> {
	private DatabaseHelper m_dbhelper;
	private final Integer m_from;
	private final Integer m_to;

	public AccountMergeCallable(DatabaseHelper DBhelper,Integer idFrom,Integer idTo) {
		m_dbhelper=DBhelper;
		m_from=idFrom;
		m_to=idTo;
	}
	
	@Override
	public Void call() throws Exception {
		Dao<AccountEntity,Integer> accDao=m_dbhelper.getAccountsDao();
		accDao.executeRaw("UPDATE "+PayEntity.TABLE+" SET "+PayEntity.ACCOUNT+"="+m_to+", "+PayEntity.MODIFIED+"=1 WHERE "+PayEntity.ACCOUNT+"="+m_from);
		accDao.executeRaw("UPDATE "+AccountEntity.TABLE+" SET "+AccountEntity.MODIFIED+"=1, "+AccountEntity.DELETED+"=1 WHERE _id="+m_from);
		return null;
	}

}
