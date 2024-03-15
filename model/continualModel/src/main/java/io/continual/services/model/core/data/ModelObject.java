package io.continual.services.model.core.data;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An in-memory object data structure
 */
public interface ModelObject 
{
	/**
	 * Get the keys in this object.
	 * @return a set of keys
	 */
	Set<String> getKeys ();

	/**
	 * Test for the existence of a key
	 * @param key
	 * @return true if the key is present
	 */
	default boolean hasKey ( String key )
	{
		return getKeys().contains ( key );
	}

	/**
	 * Get a value generically. ModelDataNullValue.NULL indicates an explicit
	 * null value. Java null indicates the key is not present.
	 * @param key
	 * @return a value, or null if the key is not present
	 */
	Object get ( String key );

	/**
	 * Get a value as a string
	 * @param key
	 * @return a string value
	 * @throws ClassCastException
	 */
	default String getString ( String key )
	{
		final Object val = get ( key );
		return val == null ? null : (String) val;
	}

	/**
	 * Get a value as a boolean
	 * @param key
	 * @return a boolean value
	 * @throws ClassCastException
	 */
	default boolean getBoolean ( String key )
	{
		return (Boolean) get ( key );
	}

	/**
	 * Get a value as a number
	 * @param key
	 * @return a number value
	 * @throws ClassCastException
	 */
	default Number getNumber ( String key )
	{
		return (Number) get ( key );
	}

	/**
	 * Get a value as a map
	 * @param key
	 * @return a map value
	 */
	default ModelObject getMap ( String key )
	{
		return (ModelObject) get ( key );
	}

	/**
	 * Get a value as a list
	 * @param key
	 * @return a list value
	 */
	default ModelList getList ( String key )
	{
		return (ModelList) get ( key );
	}

	/**
	 * Get an iterable over this data object
	 * @return an iterable
	 */
	default Iterable<Entry<String, Object>> entrySet ()
	{
		// simple implementation: just make a map and return its entry set
		final HashMap<String,Object> map = new HashMap<> ();
		for ( String key : getKeys () )
		{
			map.put ( key, get ( key ) );
		}
		return map.entrySet ();
	}

	/**
	 * Put a null value into the map
	 * @param key
	 * @return this writer
	 */
	ModelObject putNull ( String key );

	/**
	 * Put a string into this map
	 * @param key
	 * @param val
	 * @return this writer
	 */
	ModelObject put ( String key, String val );

	/**
	 * Put a number into this map
	 * @param key
	 * @param val
	 * @return this writer
	 */
	ModelObject put ( String key, Number val );

	/**
	 * Put a boolean into this map
	 * @param key
	 * @param val
	 * @return this writer
	 */
	ModelObject put ( String key, boolean val );

	/**
	 * Put a map into this map
	 * @param key
	 * @param val
	 * @return this writer
	 */
	ModelObject put ( String key, ModelObject val );

	/**
	 * Put a list into this map
	 * @param key
	 * @param val
	 * @return this writer
	 */
	ModelObject put ( String key, ModelList val );
}
