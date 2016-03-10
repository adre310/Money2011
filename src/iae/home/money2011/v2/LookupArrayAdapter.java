package iae.home.money2011.v2;

import iae.home.money2011.v2.datamodel.ILookupEntity;
import iae.home.money2011.v2.style.StyleUtility;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class LookupArrayAdapter  extends ArrayAdapter<ILookupEntity> {
	private final Activity m_context;
	private ILookupEntity[] m_objects;

	public LookupArrayAdapter(Activity context, ILookupEntity[] objects) {
		super(context, android.R.layout.simple_spinner_item, objects);
		// TODO Auto-generated constructor stub
		m_context=context;
		m_objects=objects;
	}

	public void setNewDataset(ILookupEntity[] objects) {
		m_objects=objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {			
		TextView rowView=(TextView)convertView;
		if(rowView==null) {
			LayoutInflater inflater=m_context.getLayoutInflater();
			rowView=(TextView)inflater.inflate(android.R.layout.simple_spinner_item, null, true);
		}
		ILookupEntity catEnt=m_objects[position];
		rowView.setText(catEnt.getName());
		//StyleUtility.applyStyle(catEnt.getThemeId(), rowView);
		return rowView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent){
		CheckedTextView rowView=(CheckedTextView)convertView;
		if(rowView==null) {
			LayoutInflater inflater=m_context.getLayoutInflater();
			rowView=(CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null, true);
		}
		ILookupEntity catEnt=m_objects[position];
		rowView.setText(catEnt.getName());
		//StyleUtility.applyStyle(catEnt.getThemeId(), rowView);
		return rowView;
	}
	
	@Override 
	public int getViewTypeCount() 
	{     
		return StyleUtility.getViewTypeCount(); 
	}  

	@Override 
	public int getItemViewType(int position) 
	{
		ILookupEntity catEnt=m_objects[position];
		return StyleUtility.getItemViewType(catEnt.getThemeId());
	}	
}
