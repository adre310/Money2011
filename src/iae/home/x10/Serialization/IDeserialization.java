package iae.home.x10.Serialization;

import org.apache.http.HttpEntity;

public interface IDeserialization {
	void Deserialization(HttpEntity response) throws Exception;
}
