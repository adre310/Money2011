package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class PayUpdateCallable implements Callable<PayEntity> {
	private DatabaseHelper m_dbhelper;
	private PayEntity m_pay;

	public PayUpdateCallable(DatabaseHelper DBhelper,PayEntity pay) {
		m_dbhelper=DBhelper;
		m_pay=pay;
	}
	
	@Override
	public PayEntity call() throws Exception {
		Dao<PayEntity, Integer> dao=m_dbhelper.getPaysDao();
		BaseAction.updateObject(dao, m_pay);
		
		if(m_pay.getLinked()!=null) {
			PayEntity link=m_pay.getLinked();
			dao.refresh(link);

			link.setDescription(m_pay.getDescription());
			link.setValue(-m_pay.getValue());
			link.setDate(m_pay.getDate());
			if(m_pay.getCategory()!=null)
				link.setCategory(m_pay.getCategory());
			
			BaseAction.updateObject(dao, link);
		}
		
		return m_pay;
	}

}
