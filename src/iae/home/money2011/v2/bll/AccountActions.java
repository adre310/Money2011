package iae.home.money2011.v2.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.misc.TransactionManager;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.charts.GraphicsPieFilter;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.IBaseEntity;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;
import iae.home.money2011.v2.datamodel.PayEntity;

public class AccountActions {
	private static final String m_sqlJoin=
		"SELECT b._id,b."+AccountEntity.NAME+",b."+AccountEntity.DESCRIPTION+",b."+AccountEntity.CURRENCY+",IFNULL(t.balance,0), IFNULL(-mm.withdraw,0) FROM "+AccountEntity.TABLE+" b "+
		" LEFT OUTER JOIN (SELECT p."+PayEntity.ACCOUNT+",SUM(p."+PayEntity.VALUE+") AS balance FROM "+PayEntity.TABLE+" p WHERE p."+PayEntity.DELETED+"=0 GROUP BY p."+PayEntity.ACCOUNT+") t ON b._id=t."+PayEntity.ACCOUNT+
	    " LEFT OUTER JOIN (SELECT m."+PayEntity.ACCOUNT+",SUM(m."+PayEntity.VALUE+") AS withdraw FROM "+PayEntity.TABLE+" m WHERE m."+PayEntity.DELETED+"=0 AND m."+PayEntity.VALUE+"<0 AND m." +PayEntity.DATE+">=? AND m."+PayEntity.IS_SYSTEM+"=0 GROUP BY m."+PayEntity.ACCOUNT+") mm ON b._id=mm."+PayEntity.ACCOUNT;
	private static final String m_orderSQL=" order by b."+AccountEntity.NAME;
	private static DataType[] m_sqlColumnDataTypes=new DataType[] { DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.STRING, DataType.DOUBLE, DataType.DOUBLE};
	
	private static AccountEntity convertRawResult(Object[] rawResult) {
		return new AccountEntity(
				(Integer)rawResult[0], // id 
				(String)rawResult[1],  // name 
				(String)rawResult[2],  // description 
				(String)rawResult[3],  // currency
				(Double)rawResult[4],  // balance
				(Double)rawResult[5]   // Current month expenses
		); 
	}
	
	public static List<AccountEntity> getAccountList(DatabaseHelper DBhelper) throws SQLException {
		final String sWhere=" WHERE b."+AccountEntity.DELETED+"=0"; 
		Date now=new Date();
		Date month=new Date(now.getYear(),now.getMonth(), 1);
		
		GenericRawResults<Object[]> rawResult=
			DBhelper.getAccountsDao().queryRaw(m_sqlJoin+sWhere+m_orderSQL, 
					m_sqlColumnDataTypes, String.valueOf(month.getTime()));
		
		List<AccountEntity> list=new ArrayList<AccountEntity>();
		for(Object[] o : rawResult) {
			list.add(convertRawResult(o));
		}
		return list;
	}
	
	public static AccountEntity getAccountById(DatabaseHelper DBhelper, Integer Id) throws SQLException {
		return DBhelper.getAccountsDao().queryForId(Id);
	}
	
	public static AccountEntity getAccountByIdWithBalance(DatabaseHelper DBhelper, Integer Id) throws SQLException {
		Date now=new Date();
		Date month=new Date(now.getYear(),now.getMonth(), 1);
		
		GenericRawResults<Object[]> rawResult=
			DBhelper.getAccountsDao().queryRaw(m_sqlJoin+" WHERE b._id="+Id, 
					m_sqlColumnDataTypes, String.valueOf(month.getTime()));
		
		List<AccountEntity> list=new ArrayList<AccountEntity>();
		for(Object[] o : rawResult) {
			list.add(convertRawResult(o));
		}

		if(list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	public static AccountEntity getAccountByIdWithPayList(DatabaseHelper DBhelper, Integer Id) throws SQLException {
		Date now=new Date();
		Date month=new Date(now.getYear(),now.getMonth(), 1);
		
		GenericRawResults<Object[]> rawResult=
			DBhelper.getAccountsDao().queryRaw(m_sqlJoin+" WHERE b._id="+Id, 
					m_sqlColumnDataTypes, String.valueOf(month.getTime()));

		List<AccountEntity> list=new ArrayList<AccountEntity>();
		for(Object[] o : rawResult) {
			list.add(convertRawResult(o));
		}

		if(list.size() > 0) {
			AccountEntity budget=list.get(0);
			budget.setPaylist(PayAction.getPayListByBalance(DBhelper, budget.getId()));
			return budget;
		}
		else
			return null;
	}
	
	public static AccountEntity createAccount(DatabaseHelper DBhelper,String Name,String Currency,String description,Date createDate,Double balance)  throws SQLException {
		return TransactionManager.callInTransaction(DBhelper.getConnectionSource(), new AccountCreateCallable(DBhelper, Name, Currency, description, createDate, balance));
	}

	public static void deleteAccount(DatabaseHelper DBHelper, AccountEntity Account)  throws SQLException {
		TransactionManager.callInTransaction(DBHelper.getConnectionSource(), new AccountDeleteCallable(DBHelper, Account));
	}
	
	public static ReportByCategoryItem[] getReportItemsByPieFilter(DatabaseHelper DBhelper,GraphicsPieFilter filter)  throws SQLException {
		final String sqlMain=
				"SELECT c."+CategoryEntity.NAME+",IFNULL(SUM(p."+PayEntity.VALUE+"),0) FROM "+CategoryEntity.TABLE+" c "+
				" LEFT OUTER JOIN "+PayEntity.TABLE+" p on p."+PayEntity.CATEGORY+"=c._id ";
		
		final String sqlAccountInner=" INNER JOIN "+AccountEntity.TABLE+" a on p."+PayEntity.ACCOUNT+"=a._id ";
		
		final String sqlGroup=" GROUP BY c."+CategoryEntity.NAME;
		final String sqlOrder=" ORDER BY c."+CategoryEntity.NAME;
			
		String sqlWhere=" WHERE p."+PayEntity.IS_SYSTEM+"=0 AND p." +PayEntity.DELETED+"=0 AND c."+CategoryEntity.DELETED+"=0";

		if(filter.getAccountId()<0) {
			sqlWhere=sqlAccountInner+sqlWhere+" AND a."+AccountEntity.CURRENCY+"='"+filter.getCurrency()+"'";
		} else {
			sqlWhere+=" AND p."+PayEntity.ACCOUNT+"="+filter.getAccountId();
		}
		
		if(filter.getMode()==GraphicsPieFilter.DEPOSIT) {
			sqlWhere+=" AND p."+PayEntity.VALUE+">0";
		} else {
			sqlWhere+=" AND p."+PayEntity.VALUE+"<0";
		}
		
		sqlWhere += " AND p."+PayEntity.DATE+">="+String.valueOf(filter.getBeginDate().getTime());
		sqlWhere += " AND p."+PayEntity.DATE+"<="+String.valueOf(filter.getEndDate().getTime());
		
		GenericRawResults<Object[]> rawResult=
			DBhelper.getCategoriesDao().queryRaw(
						sqlMain+sqlWhere+sqlGroup+sqlOrder, 
						new DataType[] {DataType.STRING, DataType.DOUBLE });

		List<ReportByCategoryItem> list=new ArrayList<ReportByCategoryItem>();
		for(Object[] o : rawResult) {
			String catName=(String)o[0];
			Double catValue=(Double)o[1];
			catValue=(catValue<0)?(-catValue):catValue;
			list.add(new ReportByCategoryItem(catName, catValue));
		}
			
		ReportByCategoryItem[] ret=new ReportByCategoryItem[list.size()];
		list.toArray(ret);
		return ret;
	}
	
	public static ReportByCategoryItem[] getReportItemsByCategory(DatabaseHelper DBhelper,Integer budgetId, Date startDate, Date endDate) throws SQLException {
		final String sqlMain=
			"SELECT c."+CategoryEntity.NAME+",IFNULL(SUM(p."+PayEntity.VALUE+"),0) FROM "+CategoryEntity.TABLE+" c "+
			" LEFT OUTER JOIN "+PayEntity.TABLE+" p on p."+PayEntity.CATEGORY+"=c._id ";
		
		final String sqlGroup=" GROUP BY c."+CategoryEntity.NAME;
		final String sqlOrder=" ORDER BY c."+CategoryEntity.NAME;
		
		String sqlWhere=" WHERE p."+PayEntity.VALUE+"<0 AND p."+PayEntity.ACCOUNT+"="+budgetId+" AND p."+PayEntity.DELETED+"=0 AND c."+CategoryEntity.DELETED+"=0";

		if(startDate != null) {
			sqlWhere += " AND p."+PayEntity.DATE+">="+
			String.valueOf((new Date(startDate.getYear(),startDate.getMonth(),startDate.getDate())).getTime());
		}

		if(endDate != null) {
			sqlWhere += " AND p."+PayEntity.DATE+"<="+
			String.valueOf((new Date(endDate.getYear(),endDate.getMonth(),endDate.getDate())).getTime());
		}
		
		GenericRawResults<Object[]> rawResult=
			DBhelper.getCategoriesDao().queryRaw(
					sqlMain+sqlWhere+sqlGroup+sqlOrder, 
					new DataType[] {DataType.STRING, DataType.DOUBLE });

		List<ReportByCategoryItem> list=new ArrayList<ReportByCategoryItem>();
		for(Object[] o : rawResult) {
			list.add(new ReportByCategoryItem((String)o[0], -(Double)o[1]));
		}
		
		ReportByCategoryItem[] ret=new ReportByCategoryItem[list.size()];
		list.toArray(ret);
		return ret;
	}
	
	public static ILookupEntity[] getLookup(DatabaseHelper DBHelper, Context context)  throws SQLException {
		List<ILookupEntity> db_list=BaseAction.getLookup(DBHelper.getAccountsDao());
		List<ILookupEntity> ret=new ArrayList<ILookupEntity>();

		ret.add(new LookupEntity(-1, context.getResources().getString(R.string.all_accounts).toString(),0));
		for(ILookupEntity l : db_list) {
			ret.add(l);
		}
		
		return BaseAction.toArray(ret);
	}
	
	public static ILookupEntity[] getTransferAccountList(DatabaseHelper DBHelper,Integer id)   throws SQLException {
		Dao<AccountEntity,Integer> dao=DBHelper.getAccountsDao();
		AccountEntity account=dao.queryForId(id);
		List<AccountEntity> list=dao
				.queryBuilder()
					.orderBy(AccountEntity.NAME, true)
				.where()
					.not()
						.eq("_id", id)
				.and()
					.eq(IBaseEntity.DELETED, false)
				.and()
					.eq(AccountEntity.CURRENCY, account.getCurrency())
				.query();

		List<ILookupEntity> ret=new ArrayList<ILookupEntity>();
		for(AccountEntity t : list) {
			ret.add(new LookupEntity(t.getId(), t.getName(), 0));
		}
		
		return BaseAction.toArray(ret);
	}
	
	public static void createTransfer(DatabaseHelper DBhelper, Context context, Integer idFrom, Integer idTo, Double value, Date date) throws SQLException {
		TransactionManager.callInTransaction(DBhelper.getConnectionSource(), new AccountTransferCallable(DBhelper, context, idFrom, idTo, value, date));
	}
	
	public static void mergeAccount(DatabaseHelper DBhelper,Integer idFrom, Integer idTo) throws SQLException {
		TransactionManager.callInTransaction(DBhelper.getConnectionSource(), new AccountMergeCallable(DBhelper, idFrom, idTo));
	}
}
