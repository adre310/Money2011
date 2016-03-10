package iae.home.money2011.v2;

import iae.home.money2011.v2.datamodel.ILookupEntity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class LookupItemSelectedListener implements OnItemSelectedListener {

	public static interface OnChangeListener {
		public void onChange(ILookupEntity lookup);
	}
	
	private final OnChangeListener m_listener;
	
	public LookupItemSelectedListener(OnChangeListener listener) {
		m_listener=listener;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long row) {
		m_listener.onChange((ILookupEntity)parent.getAdapter().getItem(position));
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		m_listener.onChange(null);
	}

}
