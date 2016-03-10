package iae.home.money2011.v2.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.R.style;
import android.graphics.Color;
import android.view.View;

public class StyleUtility {
	private static HashMap<Integer, StyleItem> m_dictionary;
	private static HashMap<Integer,Integer> m_dictStyleToView;
	private static Integer m_count;
	
	public static void applyStyle(Integer styleId,View view) {
		if(m_dictionary.containsKey(styleId)) {
			StyleItem style=m_dictionary.get(styleId);
			if(style.getBackgroundColor()!=null)
				view.setBackgroundColor(style.getBackgroundColor());
			//else
			//	view.setBackgroundColor(Color.BLACK);
				
		}
	}
	
	public static List<Integer> getAvaliableList() {
		List<Integer> ret=new ArrayList<Integer>();
		
		for (Integer k : m_dictionary.keySet()) {
			ret.add(k);
		}
		return ret;
	}

	
	public static int getItemViewType(int styleId) 
	{
		if(m_dictStyleToView.containsKey(styleId))
			return m_dictStyleToView.get(styleId);
		else
			return 0; 
	}  
	
	public static int getViewTypeCount() 
	{     
		return m_count; 
	}
	
	static {
		m_dictionary=new HashMap<Integer, StyleItem>();
		StyleItem style=new StyleItem();
		//style.setBackgroundColor(Color.BLACK);
		m_dictionary.put(0, style);
		style=new StyleItem();
		style.setBackgroundColor(Color.RED);
		m_dictionary.put(1, style);
		style=new StyleItem();
		style.setBackgroundColor(Color.BLUE);
		m_dictionary.put(2, style);
		style=new StyleItem();
		style.setBackgroundColor(Color.CYAN);
		m_dictionary.put(3, style);
		style=new StyleItem();
		style.setBackgroundColor(Color.YELLOW);
		m_dictionary.put(4, style);
		style=new StyleItem();
		style.setBackgroundColor(Color.GRAY);
		m_dictionary.put(5, style);

		m_count=m_dictionary.size();
		
		m_dictStyleToView=new HashMap<Integer, Integer>();
		int view=0;
		for (Integer k : m_dictionary.keySet()) {
			m_dictStyleToView.put(k, view++);
		}
	}
}
