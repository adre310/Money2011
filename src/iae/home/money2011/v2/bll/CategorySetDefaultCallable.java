package iae.home.money2011.v2.bll;

import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;

import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;

public class CategorySetDefaultCallable implements Callable<Void> {
	private final DatabaseHelper m_dbhelper;
	private final Integer m_id;
	
	public CategorySetDefaultCallable(DatabaseHelper DBhelper,Integer Id) {
		m_dbhelper=DBhelper;
		m_id=Id;
	}
	
	@Override
	public Void call() throws Exception {
		Dao<CategoryEntity,Integer> dao=m_dbhelper.getCategoriesDao();
		
		dao.executeRaw("UPDATE "+CategoryEntity.TABLE+" SET "+CategoryEntity.MODIFIED+"=1,"+CategoryEntity.IS_DEFAULT+"=0 WHERE "+CategoryEntity.IS_DEFAULT+"=1");
		CategoryEntity ent=dao.queryForId(m_id);
		ent.setDefault(true);
		
		BaseAction.updateObject(dao, ent);
		
		return null;
	}

}
