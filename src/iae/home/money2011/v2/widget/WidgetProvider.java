package iae.home.money2011.v2.widget;

import java.util.HashSet;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	Log.i(this.getClass().getName(),"onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetId);
        }    	
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	Log.i(this.getClass().getName(),"onUpdate");
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            AccountFilter.deleteWidget(context, appWidgetId);
        }    	
    }
    
    public static void updateAppWidget(Context context, int appWidgetId) {
    	Log.i(WidgetProvider.class.getName(),"updateAppWidget["+appWidgetId+"]");
        Intent intent=new Intent(context, WidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        context.startService(intent);    	
    }
    
    public static void updateAllAppWidget(Context context) {
    	HashSet<Integer> list=AccountFilter.getWidgetList(context);
    	for(int appWidgetId : list)
    		updateAppWidget(context, appWidgetId);
    }
}
