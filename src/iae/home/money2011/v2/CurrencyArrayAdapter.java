package iae.home.money2011.v2;

import iae.home.utils.text.CurrencyCode;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class CurrencyArrayAdapter extends ArrayAdapter<CurrencyCode> {
	private final Activity m_context; 
	public CurrencyArrayAdapter(Activity context) {
		super(context, android.R.layout.simple_spinner_item, CurrencyCode.getList());
		m_context=context;
	}

	public CurrencyArrayAdapter(Activity context, CurrencyCode[] list) {
		super(context, android.R.layout.simple_spinner_item, list);
		m_context=context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {			
		TextView rowView=(TextView)convertView;
		if(rowView==null) {
			LayoutInflater inflater=m_context.getLayoutInflater();
			rowView=(TextView)inflater.inflate(android.R.layout.simple_spinner_item, null, true);
		}
		CurrencyCode it=getItem(position);
		rowView.setText(it.getCode()+" ("+it.getName()+")");
		
		return rowView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent){
		CheckedTextView rowView=(CheckedTextView)convertView;
		if(rowView==null) {
			LayoutInflater inflater=m_context.getLayoutInflater();
			rowView=(CheckedTextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null, true);
		}

		CurrencyCode it=getItem(position);
		rowView.setText(it.getCode()+" ("+it.getName()+")");
		
		return rowView;
	}
}
