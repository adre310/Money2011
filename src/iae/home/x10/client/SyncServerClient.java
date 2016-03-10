package iae.home.x10.client;

import java.util.concurrent.Callable;

import iae.home.x10.Serialization.Json.SyncDeserialization;
import iae.home.x10.Serialization.Json.SyncSerialization;
import iae.home.x10.model.ISyncServer;

public class SyncServerClient extends RestClient implements Callable<Void> {
	private final ISyncServer m_data;
	
	public SyncServerClient(ISyncServer data, String username, String password) {
		super(username,password);
		this.m_data=data;
	}
	
	@Override
	public Void call() throws Exception {
		postMethod("/api/v1/sync.json", new SyncSerialization(m_data), new SyncDeserialization(m_data));
		
		return null;
	}
}
