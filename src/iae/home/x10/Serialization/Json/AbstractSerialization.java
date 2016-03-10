/**
 * 
 */
package iae.home.x10.Serialization.Json;

import iae.home.x10.Serialization.ISerialization;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author aisaev
 *
 */
public abstract class AbstractSerialization implements ISerialization {

	/* (non-Javadoc)
	 * @see iae.home.x10.Serialization.ISerialization#Serialization()
	 */
	@Override
	public HttpEntity Serialization() throws Exception {
		String reqStr=handleJsonRequest().toString();
		Log.i(this.getClass().getName(),"Send: "+reqStr);
		return new StringEntity(reqStr,"UTF-8");
	}

	protected abstract JSONObject handleJsonRequest()  throws Exception;
}
