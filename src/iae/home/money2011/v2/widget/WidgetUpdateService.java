package iae.home.money2011.v2.widget;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import iae.home.money2011.v2.R;
import iae.home.money2011.v2.aAccountList;
import iae.home.money2011.v2.bll.AccountActions;
import iae.home.money2011.v2.datamodel.AccountEntity;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.datamodel.PayEntity;
import iae.home.utils.orm.service.OrmLiteBaseIntentService;
import iae.home.utils.text.textUtils;

public class WidgetUpdateService extends OrmLiteBaseIntentService<DatabaseHelper> {

	public WidgetUpdateService() {
		super("widgetupdate");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		  Context context = getApplicationContext();
		  Bundle extras=intent.getExtras();
		  if(extras!=null) {
			  AccountFilter filter=new AccountFilter();
			  int appWidgetId=extras.getInt(
        	            AppWidgetManager.EXTRA_APPWIDGET_ID, 
        	            AppWidgetManager.INVALID_APPWIDGET_ID);
			  filter.load(context, appWidgetId);
	          AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	          RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.appwidget);
	          
	          try {	          
	        	  if(filter.getAccountId()<0) {
	        		  // Total for currency
		        	  views.setTextViewText(R.id.widget_account_name, context.getResources().getString(R.string.all_accounts));
		        	  
		        	  final String sql="SELECT SUM(p."+PayEntity.VALUE+") FROM "+PayEntity.TABLE+" p "+
		        	  " INNER JOIN "+AccountEntity.TABLE+" a ON a._id=p."+PayEntity.ACCOUNT+
		        	  " WHERE a."+AccountEntity.DELETED+"=0 AND p."+PayEntity.DELETED+"=0 AND a."+AccountEntity.CURRENCY+"=?";
		        	  final DataType[] dataTypes=new DataType[] { DataType.DOUBLE };
		        	  
		      		GenericRawResults<Object[]> rawResult=
		      				getHelper().getPaysDao().queryRaw(sql, dataTypes, filter.getCurrency());
		      		
		      		Object[] sumRes=rawResult.getResults().get(0);
		        	views.setTextViewText(R.id.widget_info_value, textUtils.CurrencyToString((Double)sumRes[0], filter.getCurrency()));		        			        	  
		        	  
	        	  } else {
	        		  // Selected account
	        		  AccountEntity acc=AccountActions.getAccountByIdWithBalance(getHelper(), filter.getAccountId());
		        	  views.setTextViewText(R.id.widget_account_name, acc.getName());
		        	  views.setTextViewText(R.id.widget_info_value, textUtils.CurrencyToString(acc.getBalance(), filter.getCurrency()));		        			        	  
	        	  }
	          
	        	  //views.setTextViewText(R.id.widget_info_label, "label");
	        	  
	              // Create an Intent to launch ExampleActivity
	              Intent actIntent = new Intent(context, aAccountList.class);
	              PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, actIntent, 0);
	              views.setOnClickPendingIntent(R.id.widget_icon, pendingIntent);
	        	  
	          } catch(Exception e) {
	        	  Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
	        	  views.setTextViewText(R.id.widget_info_value, textUtils.CurrencyToString(0D, filter.getCurrency()));		        			        	  
	          }
	          appWidgetManager.updateAppWidget(appWidgetId, views);
		  }
	}

}
