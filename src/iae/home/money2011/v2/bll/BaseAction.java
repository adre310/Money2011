package iae.home.money2011.v2.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import iae.home.money2011.v2.datamodel.IBaseEntity;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;
import iae.home.money2011.v2.datamodel.PayEntity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.nullwire.trace.DefaultExceptionHandler;

public class BaseAction {

	public static <T extends IBaseEntity> T updateObject(Dao<T,Integer> dao,T dataObject) throws SQLException {
		dataObject.setModified(true);
		
		if(dataObject instanceof PayEntity) {
			PayEntity p=(PayEntity)dataObject;
			try {
			if(p.getAccount()==null || p.getDate() == null)
				throw new Exception("Assert: Account==null or Date==null");
			} catch(Exception e) {
				DefaultExceptionHandler.reportException(e);
				return dataObject;
			}
		}
			
		if(dao.queryForId(dataObject.getId())!=null) {
			dao.update(dataObject);
		} else {
			dataObject.setUUID();
			dao.create(dataObject);
		}
		
		return dataObject;
	}

	public static <T extends IBaseEntity> T updateObjectInTransaction(ConnectionSource connection, Dao<T,Integer> dao,T dataObject) throws SQLException {
		return TransactionManager.callInTransaction(connection, new BaseActionUpdate<T>(dao, dataObject));
	}
	
	public static <T extends IBaseEntity> T createObject(Dao<T,Integer> dao,T dataObject) throws SQLException {
		dataObject.setModified(true);
		dataObject.setDeleted(false);
		dataObject.setUUID();
		dao.create(dataObject);
		
		return dataObject;
	}
	
	public static <T extends ILookupEntity> List<ILookupEntity> getLookup(Dao<T,Integer> dao) throws SQLException {
		List<T> list=
			dao
				.queryBuilder()
					.orderBy(ILookupEntity.NAME, true)
					.where()
						.eq(IBaseEntity.DELETED, false)
				.query();

		List<ILookupEntity> ret=new ArrayList<ILookupEntity>();
		for(T t : list) {
			ret.add(new LookupEntity(t.getId(), t.getName(), t.getThemeId()));
		}
		
		return ret;
	}
	
	public static ILookupEntity[] toArray(List<ILookupEntity> list) {
		ILookupEntity[] ret=new LookupEntity[list.size()];
		list.toArray(ret);
		return ret;
	}
	
	public static <T extends ILookupEntity> Boolean validateLookup(Dao<T, Integer> dao,T obj)  throws SQLException  {
		QueryBuilder<T, Integer> qb=dao.queryBuilder();
		Where<T,Integer> where=qb.where();
		
		SelectArg arg0=new SelectArg();
		
		where
			.not().eq("_id", obj.getId())
		.and()
			.eq(IBaseEntity.DELETED, false)
		.and()
			.eq(ILookupEntity.NAME, arg0);
		
		arg0.setValue(obj.getName());
			
		List<T> list=dao.query(qb.prepare());
				
		return list.size() == 0;
	}

	public static <T extends ILookupEntity> Boolean validateLookup(Dao<T, Integer> dao,String name)  throws SQLException  {
		List<T> list=
			dao
				.queryBuilder()
					.where()
						.eq(ILookupEntity.NAME, name)
						.and().eq(IBaseEntity.DELETED, false)
				.query();

		return list.size() == 0;
	}
	
}
