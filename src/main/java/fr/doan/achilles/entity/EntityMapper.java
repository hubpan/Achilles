package fr.doan.achilles.entity;

import static fr.doan.achilles.entity.metadata.PropertyType.SIMPLE;
import static fr.doan.achilles.serializer.Utils.BYTE_SRZ;
import static fr.doan.achilles.serializer.Utils.STRING_SRZ;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.prettyprint.hector.api.beans.DynamicComposite;

import org.apache.cassandra.utils.Pair;

import fr.doan.achilles.entity.metadata.EntityMeta;
import fr.doan.achilles.entity.metadata.ListMeta;
import fr.doan.achilles.entity.metadata.MapMeta;
import fr.doan.achilles.entity.metadata.PropertyMeta;
import fr.doan.achilles.entity.metadata.PropertyType;
import fr.doan.achilles.entity.metadata.SetMeta;
import fr.doan.achilles.entity.type.KeyValueHolder;
import fr.doan.achilles.exception.BeanMappingException;

@SuppressWarnings(
{
		"unchecked",
		"rawtypes"
})
public class EntityMapper
{

	public <T, ID> void mapColumnsToBean(ID key, List<Pair<DynamicComposite, Object>> columns, EntityMeta<ID> entityMeta, T entity)
	{

		Map<String, List> listProperties = new HashMap<String, List>();
		Map<String, Set> setProperties = new HashMap<String, Set>();
		Map<String, Map> mapProperties = new HashMap<String, Map>();

		mapIdToBean(key, entityMeta.getIdMeta(), entity);

		Map<String, PropertyMeta<?>> propertyMetas = entityMeta.getPropertyMetas();

		for (Pair<DynamicComposite, Object> pair : columns)
		{
			byte[] type = pair.left.get(0, BYTE_SRZ);
			String propertyName = pair.left.get(1, STRING_SRZ);

			if (Arrays.equals(type, SIMPLE.flag()))
			{
				mapSimplePropertyToBean(pair.right, propertyMetas.get(propertyName), entity);
			}

			else if (Arrays.equals(type, PropertyType.LIST.flag()))
			{
				ListMeta<?> listMeta = (ListMeta<?>) propertyMetas.get(propertyName);
				addToList(listProperties, listMeta, listMeta.get(pair.right));
			}

			else if (Arrays.equals(type, PropertyType.SET.flag()))
			{
				SetMeta<?> setMeta = (SetMeta<?>) propertyMetas.get(propertyName);
				addToSet(setProperties, setMeta, setMeta.get(pair.right));
			}

			else if (Arrays.equals(type, PropertyType.MAP.flag()))
			{
				MapMeta<?, ?> mapMeta = (MapMeta<?, ?>) propertyMetas.get(propertyName);
				addToMap(mapProperties, mapMeta, (KeyValueHolder) pair.right);
			}
		}

		for (Entry<String, List> entry : listProperties.entrySet())
		{
			mapListPropertyToBean(entry.getValue(), propertyMetas.get(entry.getKey()), entity);
		}

		for (Entry<String, Set> entry : setProperties.entrySet())
		{
			mapSetPropertyToBean(entry.getValue(), propertyMetas.get(entry.getKey()), entity);
		}

		for (Entry<String, Map> entry : mapProperties.entrySet())
		{
			mapMapPropertyToBean(entry.getValue(), propertyMetas.get(entry.getKey()), entity);
		}

	}

	protected void addToList(Map<String, List> listProperties, ListMeta<?> listMeta, Object value)
	{
		String propertyName = listMeta.getPropertyName();
		List list = null;
		if (!listProperties.containsKey(propertyName))
		{
			list = listMeta.newListInstance();
			listProperties.put(propertyName, list);
		}
		else
		{
			list = listProperties.get(propertyName);
		}
		list.add(value);
	}

	protected void addToSet(Map<String, Set> setProperties, SetMeta<?> setMeta, Object value)
	{
		String propertyName = setMeta.getPropertyName();

		Set set = null;
		if (!setProperties.containsKey(propertyName))
		{
			set = setMeta.newSetInstance();
			setProperties.put(propertyName, set);
		}
		else
		{
			set = setProperties.get(propertyName);
		}
		set.add(value);
	}

	protected <K, V> void addToMap(Map<String, Map> mapProperties, MapMeta<K, V> mapMeta, KeyValueHolder keyValueHolder)
	{
		String propertyName = mapMeta.getPropertyName();

		Map map = null;
		if (!mapProperties.containsKey(propertyName))
		{
			map = mapMeta.newMapInstance();
			mapProperties.put(propertyName, map);
		}
		else
		{
			map = mapProperties.get(propertyName);
		}
		map.put(keyValueHolder.getKey(), mapMeta.get(keyValueHolder.getValue()));
	}

	public <T, ID> void mapIdToBean(ID key, PropertyMeta<?> keyMeta, T entity)
	{

		try
		{
			keyMeta.getSetter().invoke(entity, key);
		}
		catch (Exception e)
		{
			throw new BeanMappingException("Cannot set value '" + key + "' to entity " + entity, e);
		}
	}

	public <T, ID> void mapSimplePropertyToBean(Object value, PropertyMeta<?> propertyMeta, T entity)
	{
		try
		{
			propertyMeta.getSetter().invoke(entity, propertyMeta.get(value));
		}
		catch (Exception e)
		{
			throw new BeanMappingException("Cannot set value '" + value + "' to entity " + entity, e);
		}
	}

	public <T, ID> void mapListPropertyToBean(List<?> list, PropertyMeta<?> listMeta, T entity)
	{
		try
		{
			listMeta.getSetter().invoke(entity, list);
		}
		catch (Exception e)
		{
			throw new BeanMappingException("Cannot set value '" + list + "' to entity " + entity, e);
		}
	}

	public <T, ID> void mapSetPropertyToBean(Set<?> set, PropertyMeta<?> setMeta, T entity)
	{
		try
		{
			setMeta.getSetter().invoke(entity, set);
		}
		catch (Exception e)
		{
			throw new BeanMappingException("Cannot set value '" + set + "' to entity " + entity, e);
		}
	}

	public <T, ID> void mapMapPropertyToBean(Map<?, ?> map, PropertyMeta<?> mapMeta, T entity)
	{
		try
		{
			mapMeta.getSetter().invoke(entity, map);
		}
		catch (Exception e)
		{
			throw new BeanMappingException("Cannot set value '" + map + "' to entity " + entity, e);
		}
	}
}