package iae.home.x10.client;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.util.Base64;
import android.util.Log;

import iae.home.x10.G;
import iae.home.x10.SyncServerException;
import iae.home.x10.Serialization.IDeserialization;
import iae.home.x10.Serialization.ISerialization;

public class RestClient {
	private final String m_username;
	private final String m_password;
	private final Boolean m_enable;

	public static String AGENT_VERSION="";
	public static String AGENT_NAME="";
	
	public RestClient() {
		m_username="";
		m_password="";
		m_enable=false;
	}
	
	public RestClient(String username,String password) {
		m_username=username;
		m_password=password;
		m_enable=true;
	}
	
	protected void beforeRequest(AbstractHttpMessage request) {
		
	}
	
	public void getMethod(String url,IDeserialization outData) throws Exception {
		DefaultHttpClient client=getHttpClient();

		HttpGet request=new HttpGet(G.BASE_URL+url);
		request.addHeader("User-Agent", "Rest Client v"+AGENT_VERSION+" ("+AGENT_NAME+")");

		if(m_enable) {
			request.addHeader("Authorization", "Basic "+Base64.encodeToString((m_username+":"+m_password).getBytes(),Base64.NO_WRAP));
	        //client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(m_username,m_password));
		}

		beforeRequest(request);
		HttpResponse response=client.execute(request);
        Log.i("WEB", "Status: "+response.getStatusLine().getStatusCode()+" "+response.getStatusLine().getReasonPhrase());
        for (Header h : response.getAllHeaders()) {
			Log.i("WEB","Header: "+h.getName()+" "+h.getValue());
		}

        if(response.getStatusLine().getStatusCode()!=200)
        	throw new SyncServerException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()); 
		
        outData.Deserialization(response.getEntity());
	}
	
	public void postMethod(String url,ISerialization inData, IDeserialization outData) throws Exception {
		DefaultHttpClient client=getHttpClient();
		
		HttpPost request=new HttpPost(G.BASE_URL+url);

		request.addHeader("User-Agent", "Rest Client v"+AGENT_VERSION+" ("+AGENT_NAME+")");

		if(m_enable) {
			request.addHeader("Authorization", "Basic "+Base64.encodeToString((m_username+":"+m_password).getBytes(),Base64.NO_WRAP));
	        //client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(m_username,m_password));
		}

		beforeRequest(request);
		request.setEntity(inData.Serialization());
		
		HttpResponse response=client.execute(request);
        Log.i(this.getClass().getName(), "Status: "+response.getStatusLine().getStatusCode()+" "+response.getStatusLine().getReasonPhrase());
        for (Header h : response.getAllHeaders()) {
			Log.i(this.getClass().getName(),"Header: "+h.getName()+" "+h.getValue());
		}

        if(response.getStatusLine().getStatusCode()!=200)
        	throw new SyncServerException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()); 
		
        outData.Deserialization(response.getEntity());
	}
	
	private DefaultHttpClient getHttpClient() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
		return new DefaultHttpClient(cm, params);		
	}
}
