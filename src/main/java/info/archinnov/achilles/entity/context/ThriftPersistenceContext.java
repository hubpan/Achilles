package info.archinnov.achilles.entity.context;

import info.archinnov.achilles.dao.ThriftCounterDao;
import info.archinnov.achilles.dao.ThriftGenericEntityDao;
import info.archinnov.achilles.dao.ThriftGenericWideRowDao;
import info.archinnov.achilles.entity.metadata.EntityMeta;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PersistenceContext
 * 
 * @author DuyHai DOAN
 * 
 */
public class ThriftPersistenceContext<ID> extends AchillesPersistenceContext<ID>
{
	private static final Logger log = LoggerFactory.getLogger(ThriftPersistenceContext.class);

	private final DaoContext daoContext;
	private ThriftGenericEntityDao<ID> entityDao;
	private ThriftGenericWideRowDao<ID, ?> wideRowDao;
	private ThriftAbstractFlushContext thriftFlushContext;

	public ThriftPersistenceContext(EntityMeta<ID> entityMeta, //
			AchillesConfigurationContext configContext, //
			DaoContext daoContext, //
			ThriftAbstractFlushContext flushContext, //
			Object entity)
	{
		super(entityMeta, configContext, entity, flushContext);
		log.trace("Create new persistence context for instance {} of class {}", entity,
				entityMeta.getClassName());

		this.daoContext = daoContext;
		this.thriftFlushContext = flushContext;
		this.primaryKey = introspector.getKey(entity, entityMeta.getIdMeta());
		this.initDaos();
	}

	public ThriftPersistenceContext(EntityMeta<ID> entityMeta, //
			AchillesConfigurationContext configContext, //
			DaoContext daoContext, //
			ThriftAbstractFlushContext flushContext, //
			Class<?> entityClass, ID primaryKey)
	{
		super(entityMeta, configContext, entityClass, primaryKey, flushContext);
		log.trace("Create new persistence context for instance {} of class {}", entity,
				entityMeta.getClassName());

		this.daoContext = daoContext;
		this.thriftFlushContext = flushContext;
		this.initDaos();
	}

	@Override
	public <JOIN_ID> AchillesPersistenceContext<JOIN_ID> newPersistenceContext(
			EntityMeta<JOIN_ID> joinMeta, Object joinEntity)
	{
		log.trace("Spawn new persistence context for instance {} of join class {}", joinEntity,
				joinMeta.getClassName());
		return new ThriftPersistenceContext<JOIN_ID>(joinMeta, configContext, daoContext,
				thriftFlushContext, joinEntity);
	}

	@Override
	public <JOIN_ID> AchillesPersistenceContext<JOIN_ID> newPersistenceContext(
			Class<?> entityClass, EntityMeta<JOIN_ID> joinMeta, JOIN_ID joinId)
	{
		log.trace("Spawn new persistence context for primary key {} of join class {}", joinId,
				joinMeta.getClassName());

		return new ThriftPersistenceContext<JOIN_ID>(joinMeta, configContext, daoContext,
				thriftFlushContext, entityClass, joinId);
	}

	public ThriftGenericEntityDao<?> findEntityDao(String columnFamilyName)
	{
		return daoContext.findEntityDao(columnFamilyName);
	}

	public ThriftGenericWideRowDao<?, ?> findWideRowDao(String columnFamilyName)
	{
		return daoContext.findWideRowDao(columnFamilyName);
	}

	public ThriftCounterDao getCounterDao()
	{
		return daoContext.getCounterDao();
	}

	public Mutator<ID> getCurrentColumnFamilyMutator()
	{
		return thriftFlushContext.getWideRowMutator(entityMeta.getColumnFamilyName());
	}

	public Mutator<ID> getWideRowMutator(String columnFamilyName)
	{
		return thriftFlushContext.getWideRowMutator(columnFamilyName);
	}

	public Mutator<ID> getCurrentEntityMutator()
	{
		return thriftFlushContext.getEntityMutator(entityMeta.getColumnFamilyName());
	}

	public Mutator<ID> getEntityMutator(String columnFamilyName)
	{
		return thriftFlushContext.getEntityMutator(columnFamilyName);
	}

	public Mutator<Composite> getCounterMutator()
	{
		return thriftFlushContext.getCounterMutator();
	}

	public ThriftGenericEntityDao<ID> getEntityDao()
	{
		return entityDao;
	}

	public ThriftGenericWideRowDao<ID, ?> getColumnFamilyDao()
	{
		return wideRowDao;
	}

	@SuppressWarnings("unchecked")
	private void initDaos()
	{
		String columnFamilyName = entityMeta.getColumnFamilyName();
		if (entityMeta.isWideRow())
		{
			this.wideRowDao = (ThriftGenericWideRowDao<ID, ?>) daoContext
					.findWideRowDao(columnFamilyName);
		}
		else
		{
			this.entityDao = (ThriftGenericEntityDao<ID>) daoContext
					.findEntityDao(columnFamilyName);
		}
	}
}