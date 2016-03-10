/**
 * 
 */
package iae.home.x10.model;

import java.util.UUID;

/**
 * @author isaev
 *
 */
public interface IEntityGenericFactory<T> {
	T getInstanceByUUID(UUID uuid);
}
