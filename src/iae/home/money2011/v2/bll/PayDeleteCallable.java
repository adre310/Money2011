package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class PayDeleteCallable implements Callable<AccountEntity> {
	private final DatabaseHelper m_dbhelper;
	private final Integer m_payId;
	private final Integer m_accountId;
	
	public PayDeleteCallable(DatabaseHelper DBhelper,Integer payId,Integer accountId) {
		m_dbhelper=DBhelper;
		m_payId=payId;
		m_accountId=accountId;
	}
	
	@Override
	public AccountEntity call() throws Exception {
		Dao<PayEntity,Integer> payDao=m_dbhelper.getPaysDao();
		PayEntity payTmp=payDao.queryForId(m_payId);
		
		payTmp.setDeleted(true);
		BaseAction.updateObject(payDao, payTmp);
		if(payTmp.getLinked()!=null) {
			PayEntity link=payTmp.getLinked();
			payDao.refresh(link);

			link.setDeleted(true);
			BaseAction.updateObject(payDao, link);
		}
		return AccountActions.getAccountByIdWithPayList(m_dbhelper, m_accountId);
	}

}
