package iae.home.money2011.v2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class aInitialAuthorization extends GenericActivity<Void> {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_authorization);
        //FlurryAgent.onEvent("InitialAuthorization");
        
        Button btn_ok=(Button)findViewById(R.id.init_btn_ok);
        btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(aInitialAuthorization.this);

		        String pin=preferences.getString("auth_pin_code", "");
		        EditText et=(EditText)findViewById(R.id.init_pin_code);
		        String enter_pin=et.getText().toString();
		        if(pin.equals(enter_pin)) {
		        	setResult(1);
		        	finish();
		        } else {
		        	Toast.makeText(aInitialAuthorization.this, R.string.init_authorization_failure, Toast.LENGTH_LONG);
		        	setResult(-1);
		        	finish();
		        }
			}
		});
    }
 }
