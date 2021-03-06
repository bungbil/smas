package billy.backend.dao;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BasisDAO<T> {
	private HibernateOperations hibernateTemplate;

	/**
	 * constructor
	 */
	protected BasisDAO() {
	}

	protected HibernateOperations getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateOperations hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	protected void initialize(final Object proxy) throws DataAccessException {
		hibernateTemplate.initialize(proxy);
	}

	protected T merge(T entity) throws DataAccessException {
		return (T) hibernateTemplate.merge(entity);
	}

	protected void persist(T entity) throws DataAccessException {
		hibernateTemplate.persist(entity);
	}

	public void refresh(T entity) throws DataAccessException {
		hibernateTemplate.refresh(entity);
	}

	public void save(T entity) throws DataAccessException {
		hibernateTemplate.save(entity);
	}

	public void saveOrUpdate(T entity) throws DataAccessException {
		hibernateTemplate.saveOrUpdate(entity);
	}

	public void update(T entity) throws DataAccessException {
		hibernateTemplate.update(entity);
	}

	public void delete(T entity) throws DataAccessException {
		hibernateTemplate.delete(entity);
	}

	protected void deleteAll(Collection<T> entities) throws DataAccessException {
		hibernateTemplate.deleteAll(entities);
	}

	protected T get(Class<T> entityClass, Serializable id) throws DataAccessException {
		return (T) hibernateTemplate.get(entityClass, id);
	}
}
