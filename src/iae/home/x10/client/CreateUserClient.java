package iae.home.x10.client;

import iae.home.x10.Serialization.Json.AbstractDeserialization;
import iae.home.x10.Serialization.Json.AbstractSerialization;

import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.http.message.AbstractHttpMessage;
import org.json.JSONObject;

public class CreateUserClient extends RestClient implements Callable<String> {
	private final String m_username;
	private final String m_password;
	private final String m_email;
	
	private String m_result;
	
	public CreateUserClient(String username, String password,String email) {
		m_username=username;
		m_password=password;
		m_email=email;
	}
	
	@Override
	public String call() throws Exception {
		postMethod("/user/register/api/v1/create.json", 
				new AbstractSerialization() {
					
					@Override
					protected JSONObject handleJsonRequest() throws Exception {
						JSONObject json=new JSONObject();
						json.put("username", m_username);
						json.put("password", m_password);
						json.put("email", m_email);
						return json;
					}
				}, 
				new AbstractDeserialization() {
					
					@Override
					protected void handleJsonRequest(JSONObject request) throws Exception {
						m_result=request.getString("error");
					}
				});
		
		return m_result;
	}

	@Override
	protected void beforeRequest(AbstractHttpMessage request) {
		Locale locale=Locale.getDefault();
		request.addHeader("Accept-Language",locale.toString());
	}
	
}
