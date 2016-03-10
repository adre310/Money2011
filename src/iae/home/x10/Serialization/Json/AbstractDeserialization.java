/**
 * 
 */
package iae.home.x10.Serialization.Json;

import iae.home.x10.Serialization.IDeserialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author aisaev
 *
 */
public abstract class AbstractDeserialization implements IDeserialization {
	
	protected StringBuilder inputStreamToString(InputStream is) throws IOException {     
    	String line = "";     
    	StringBuilder total = new StringBuilder();          
    	// Wrap a BufferedReader around the InputStream     
    	BufferedReader rd = new BufferedReader(new InputStreamReader(is));      
    	// Read response until the end     
    	while ((line = rd.readLine()) != null) {          
    		total.append(line);      
    		}          
    	// Return full string
    	Log.i(this.getClass().getName(),"Response: "+total);
    	return total; 
    }
	
	/* (non-Javadoc)
	 * @see iae.home.x10.Serialization.IDeserialization#Deserialization(org.apache.http.HttpEntity)
	 */
	@Override
	public void Deserialization(HttpEntity response) throws Exception {
		handleJsonRequest(new JSONObject(inputStreamToString(response.getContent()).toString()));
	}
	
	protected abstract void handleJsonRequest(JSONObject request) throws Exception;
}
