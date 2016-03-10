package iae.home.money2011.v2.charts;

import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;

public class ChartUtility {

	public static final int[] m_colorArray=new int[] {
		Color.YELLOW,
		Color.GREEN,
		Color.BLUE,
		Color.RED,
		Color.CYAN,
		Color.MAGENTA,		
		Color.WHITE,
		Color.DKGRAY,
		Color.GRAY,
		Color.LTGRAY,
	};
			
	public static DefaultRenderer buildDefaultRenderer(int series,int top,int left,int bottom,int right) {
		DefaultRenderer renderer = new DefaultRenderer();
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setMargins(new int[] { top, left, bottom, right });
	    for (int i=0; i< series; i++) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(m_colorArray[i % (m_colorArray.length)]);
	      renderer.addSeriesRenderer(r);
	    }
	    return renderer;
	}

	public static XYMultipleSeriesRenderer buildBarRenderer(int series) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setMargins(new int[] {10,10,10,10});
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
	    for (int i=0; i< series; i++) {
	        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		    r.setColor(m_colorArray[i % (m_colorArray.length)]);
		    renderer.addSeriesRenderer(r);
	    }
	    for (int i=0; i< series; i++) {
	    	renderer.getSeriesRendererAt(i).setDisplayChartValues(true);
	    }

	    return renderer;
	}
	
}
