package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.ParameterDAO;
import billy.backend.model.Parameter;

@Repository
public class ParameterDAOImpl extends BillyBasisDAO<Parameter> implements ParameterDAO {

	@Override
	public Parameter getNewParameter() {
		return new Parameter();
	}

	@Override
	public Parameter getParameterById(Long param_Id) {
		return get(Parameter.class, param_Id);
	}

	@SuppressWarnings("unchecked")
	public Parameter getParameterByParamName(String paramName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Parameter.class);
		criteria.add(Restrictions.eq("paramName", paramName));

		return (Parameter) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<Parameter> getAllParameters() {
		return getHibernateTemplate().loadAll(Parameter.class);
	}

	@Override
	public void deleteParameterById(long id) {
		Parameter Parameter = getParameterById(id);
		if (Parameter != null) {
			delete(Parameter);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersLikeParamValue(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Parameter.class);
		criteria.add(Restrictions.ilike("paramValue", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersLikeDescription(String string) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Parameter.class);
		criteria.add(Restrictions.ilike("description", string, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllParameters() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Parameter"));
	}

}
