package iae.home.money2011.v2.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.misc.TransactionManager;

import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;

public class PayAction {

	public static PayEntity internalCreatePay(DatabaseHelper DBhelper,AccountEntity budget,Double value,Date createDate) throws SQLException {
		PayEntity pay=new PayEntity(-1);
		pay.setValue(value);
		pay.setDate(createDate);
		pay.setAccount(budget);
		pay.setSystem(true);
		BaseAction.createObject(DBhelper.getPaysDao(), pay);
		return pay;
	}
	
	public static List<PayEntity> getPayListByBalance(DatabaseHelper DBhelper, Integer AccountID)  throws SQLException {
		final String sSQL=
				"SELECT p._id,p."+PayEntity.VALUE+",p."+PayEntity.DATE+",c."+CategoryEntity.NAME+",IFNULL(c."+CategoryEntity.THEME+",0) AS "+CategoryEntity.THEME+" FROM "+PayEntity.TABLE+" p "+
			    " LEFT OUTER JOIN "+CategoryEntity.TABLE+" c ON c._id=p."+PayEntity.CATEGORY+
			    " WHERE p."+PayEntity.DELETED+"=0 AND p."+PayEntity.ACCOUNT+"=?"+
			    " ORDER BY p."+PayEntity.DATE+" DESC";
		
		final DataType[] arsqlColumnDataTypes=new DataType[] { 
				DataType.INTEGER, // _id 
				DataType.DOUBLE,  // _pay_value
				DataType.DATE_LONG, // pay_date
				DataType.STRING,  // category name
				DataType.INTEGER}; // category theme

		
		GenericRawResults<Object[]> rawResult=
				DBhelper.getPaysDao().queryRaw(sSQL, arsqlColumnDataTypes, String.valueOf(AccountID));
		
		List<PayEntity> ret=new ArrayList<PayEntity>();
		
		for(Object[] o : rawResult) {
			ret.add(new PayEntity(
					(Integer)o[0],
					(Double)o[1],
					(Date)o[2],
					(String)o[3],
					(Integer)o[4]
					));
		}
		return ret;
	}
	
	public static AccountEntity deletePay(DatabaseHelper DBhelper,Integer payId,Integer accountId)  throws SQLException {
		return TransactionManager.callInTransaction(DBhelper.getConnectionSource(), new PayDeleteCallable(DBhelper, payId, accountId));
	}
	
	public static PayEntity updatePayInTransaction(DatabaseHelper DBhelper, PayEntity pay)  throws SQLException {
		return TransactionManager.callInTransaction(DBhelper.getConnectionSource(), new PayUpdateCallable(DBhelper, pay));
	}
	
	public static PayEntity getPayByIdOrDefault(DatabaseHelper DBhelper,Integer Id,Integer budgetId)   throws SQLException {
		Dao<PayEntity,Integer> dao=DBhelper.getPaysDao();
		
		PayEntity pay=dao.queryForId(Id);
		if(pay==null) {
			pay=new PayEntity(-1);
			pay.setAccount(DBhelper.getAccountsDao().queryForId(budgetId));
			pay.setValue(0D);
			pay.setDate(new Date());
			pay.setSystem(false);
			pay.setModified(true);
			pay.setDeleted(false);
			CategoryEntity category=CategoryAction.getDefaultCategory(DBhelper);
			if(category!=null)
				pay.setCategory(category);
		}
		return pay;
	}
	
}
