package iae.home.money2011.v2.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.misc.TransactionManager;

import iae.home.money2011.v2.R;
import iae.home.money2011.v2.datamodel.CategoryEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.datamodel.LookupEntity;

public class CategoryAction {

	public static CategoryEntity getDefaultCategory(DatabaseHelper DBHelper)  throws SQLException {
		List<CategoryEntity> list=
			DBHelper
				.getCategoriesDao()
					.queryBuilder()
						.where()
							.eq(CategoryEntity.IS_DEFAULT, true)
							.and()
								.eq(CategoryEntity.DELETED, false)
					.query();
		if(list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public static Boolean ValidateCategory(DatabaseHelper DBHelper, CategoryEntity category)  throws SQLException {
		//String sql="SELECT COUNT(*) FROM "+CategoryEntity.TABLE+" WHERE _id <> ? AND "+CategoryEntity.DELETED+"=0 AND "+CategoryEntity.NAME+"=?";		
		
		return BaseAction.validateLookup(DBHelper.getCategoriesDao(), category);
	}
	
	public static CategoryEntity[] getCategoryList(DatabaseHelper DBHelper)  throws SQLException {
		List<CategoryEntity> list= 
			DBHelper
				.getCategoriesDao()
					.queryBuilder()
						.orderBy(CategoryEntity.NAME, true)
						.where()
							.eq(CategoryEntity.DELETED, false)
				.query();
		
		CategoryEntity[] ret=new CategoryEntity[list.size()];
		list.toArray(ret);
		return ret;
	}
	
	public static CategoryEntity updateCategoryInTransaction(DatabaseHelper DBHelper,CategoryEntity category)   throws SQLException {
		return BaseAction.updateObjectInTransaction(DBHelper.getConnectionSource(), DBHelper.getCategoriesDao(), category);
	}
	
	public static void setDefaultCategory(DatabaseHelper DBHelper,Integer Id)  throws SQLException  {
		TransactionManager.callInTransaction(DBHelper.getConnectionSource(), new CategorySetDefaultCallable(DBHelper, Id));
	}
	
	public static ILookupEntity[] getLookup(DatabaseHelper DBHelper, Context context, Boolean isAllCategoriesInclude)  throws SQLException {
		List<ILookupEntity> db_list=BaseAction.getLookup(DBHelper.getCategoriesDao());

		if(isAllCategoriesInclude) {
			List<ILookupEntity> ret=new ArrayList<ILookupEntity>();

			ret.add(new LookupEntity(-1, context.getResources().getString(R.string.all_categories).toString(), 0));
			for(ILookupEntity l : db_list) {
				ret.add(l);
			}
		
			return BaseAction.toArray(ret);
		} else {
			return BaseAction.toArray(db_list);
		}
	}

}
