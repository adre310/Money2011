package iae.home.money2011.v2;

import iae.home.money2011.v2.datamodel.ILookupEntity;
import android.app.Activity;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class LookupInit {

	public static void Init(Activity context,Spinner spinner, ILookupEntity[] list, Integer id, LookupItemSelectedListener.OnChangeListener listener) {
		spinner.setAdapter(new LookupArrayAdapter(context, list));
		if(id != null) {
			SpinnerAdapter adapter=spinner.getAdapter();
			int count=adapter.getCount();
			
			for(int indx=0;indx<count;indx++) {
				if(((ILookupEntity)adapter.getItem(indx)).getId().equals(id)) {
					spinner.setSelection(indx);
					break;
				}					
			}
		}
		
		spinner.setOnItemSelectedListener(new LookupItemSelectedListener(listener));
	}
}
