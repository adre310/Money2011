package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.IBaseEntity;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class BaseActionUpdate<T extends IBaseEntity>  implements Callable<T>{
	private final Dao<T,Integer> m_dao;
	private final T m_dataObject;
	
	public BaseActionUpdate(Dao<T,Integer> dao, T dataObject) {
		m_dao=dao;
		m_dataObject=dataObject;
	}
	
	@Override
	public T call() throws Exception {
		BaseAction.updateObject(m_dao, m_dataObject);
		return m_dataObject;
	}

}
