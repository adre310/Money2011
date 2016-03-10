package iae.home.money2011.v2.widget;

import iae.home.utils.text.CurrencyCode;

import java.io.Serializable;
import java.util.HashSet;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AccountFilter implements Serializable {

	private static final long serialVersionUID = 7083509985856842313L;
	private String m_currency;
	private Integer m_accountId;

	public static final String PREFIX="widget_filter_";
	public static final String PREFS_NAME="iae.home.money2011.v2.widget.AccountFilter";
	
	public String getCurrency() { return m_currency; }
	public void setCurrency(String currency) { m_currency=currency; }
	
	public Integer getAccountId() { return m_accountId; }
	public void setAccountId(Integer accountId) { m_accountId=accountId; }

	public void save(Context context,int appWidgetId) {
		SharedPreferences.Editor prefs=context.getSharedPreferences(PREFS_NAME, 0).edit();
		prefs.putString(PREFIX+appWidgetId+"_currency", m_currency);
		prefs.putInt(PREFIX+appWidgetId+"_account", m_accountId);
		prefs.commit();
	}
	
	public void load(Context context,int appWidgetId) {
		SharedPreferences prefs=context.getSharedPreferences(PREFS_NAME, 0);
		m_currency=prefs.getString(PREFIX+appWidgetId+"_currency", CurrencyCode.getDefaultCurrency());
		m_accountId=prefs.getInt(PREFIX+appWidgetId+"_account", -1);
	}
	
	public static HashSet<Integer> getWidgetList(Context context) {
		SharedPreferences prefs=context.getSharedPreferences(PREFS_NAME, 0);
		HashSet<Integer> list=new HashSet<Integer>();
		Integer count=prefs.getInt("count", 0);
		for(int i=0;i<count;i++) {
			int tmp=prefs.getInt("w_"+i, AppWidgetManager.INVALID_APPWIDGET_ID);
			if(tmp!=AppWidgetManager.INVALID_APPWIDGET_ID && !list.contains(tmp))
				list.add(tmp);
		}
		return list;
	}
	
	public static void putWidgetList(Context context, HashSet<Integer> list) {
		SharedPreferences.Editor prefs=context.getSharedPreferences(PREFS_NAME, 0).edit();
		int k=0;
		for(int i : list) {
			prefs.putInt("w_"+k++, i);
		}
		prefs.putInt("count", k);
		prefs.commit();
	}
	
	public static void addWidget(Context context,int appWidgetId) {
		Log.i(AccountFilter.class.getName(),"Add widget "+appWidgetId);
		HashSet<Integer> list=getWidgetList(context);
		if(!list.contains(appWidgetId))
			list.add(appWidgetId);
		putWidgetList(context, list);
	}

	public static void deleteWidget(Context context,int appWidgetId) {
		Log.i(AccountFilter.class.getName(),"Delete widget "+appWidgetId);
		HashSet<Integer> list=getWidgetList(context);
		if(list.contains(appWidgetId))
			list.remove(appWidgetId);
		putWidgetList(context, list);
	}
}
