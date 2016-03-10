package iae.home.money2011.v2;


import java.util.HashMap;
import java.util.Map;

import iae.home.money2011.v2.bll.GenericTask;
import iae.home.money2011.v2.datamodel.DatabaseHelper;
import iae.home.money2011.v2.sync.DeviceInfo;
import iae.home.utils.task.OnTaskCompleted;
import iae.home.utils.task.Task;
import iae.home.x10.client.CreateUserClient;
import iae.home.x10.client.RestClient;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.DefaultExceptionHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class aRegisterWeb extends GenericActivity<String> {
	private EditText m_username;
	private EditText m_email;
	private EditText m_password;
	private EditText m_confirmation;
	private Button m_button;
	private Boolean m_success=false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DeviceInfo info=DeviceInfo.getInstance(this);
		RestClient.AGENT_NAME=info.getPackageName();
		RestClient.AGENT_VERSION=info.getVersion();
        
        if(savedInstanceState!=null) {
        	m_success=savedInstanceState.getBoolean("success");
        }

        if(!m_success) {
        setContentView(R.layout.register_web);
        
        m_username=(EditText)findViewById(R.id.reg_ed_username);
        m_email=(EditText)findViewById(R.id.reg_ed_email);
        m_password=(EditText)findViewById(R.id.reg_ed_password);
        m_confirmation=(EditText)findViewById(R.id.reg_ed_confirmation);
        m_button=(Button)findViewById(R.id.reg_btn_registration);
        m_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(validateForm() && !m_taskManager.isWorking())
					m_taskManager.setupTask(new RegisterTask(getHelper(), aRegisterWeb.this));
			}
		});
        
        m_taskManager.setOnTaskCompletedListener(new OnTaskCompleted<Void, String>() {

			@Override
			public void onTaskComplete(Task<Void, String> task, String result) {
				if(task.isCancelled())
					return;
				
				Map<String,String> mapEvent=new HashMap<String, String>();
				mapEvent.put("status", result);
				
		        FlurryAgent.onEvent("Registration",mapEvent);
				if(result.equals("OK")) {
					m_success=true;
			        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(aRegisterWeb.this);
			        Editor editor=preferences.edit();
			        editor.putBoolean("online_enable", true);
			        editor.putString("online_username", m_username.getText().toString());
			        editor.putString("online_password", m_password.getText().toString());
			        editor.commit();
					
					setContentView(R.layout.register_successful);
				} else {
					Toast.makeText(aRegisterWeb.this, result, Toast.LENGTH_SHORT).show();
				}
				
			}
		});
        } else {
        	setContentView(R.layout.register_successful);
        }
	}

    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean("success", m_success);
    }
	    
	private Boolean validateForm() {
		if(m_password.getText().toString().equals(m_confirmation.getText().toString())) {
			return true;
		} else {
			Toast.makeText(aRegisterWeb.this, R.string.reg_password_mismatch, Toast.LENGTH_LONG).show();
			return false;
		}
	}
	
	protected class RegisterTask extends GenericTask<String> {

		protected RegisterTask(DatabaseHelper dbhelper, Context context) {
			super(dbhelper, context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String doInBackground(Void... params) {
			publishProgress(R.string.register);
			try {
				CreateUserClient client=new CreateUserClient(
						m_username.getText().toString(), 
						m_password.getText().toString(),
						m_email.getText().toString());
				return client.call();
			} catch(Exception e) {
				DefaultExceptionHandler.reportException(e);
				return "Error";
			}
		}
		
	}
}
