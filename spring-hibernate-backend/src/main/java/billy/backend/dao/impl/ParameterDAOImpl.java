package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BasisDAO;
import billy.backend.dao.ParameterDAO;
import billy.backend.model.Parameter;

@Repository
public class ParameterDAOImpl extends BasisDAO<Parameter> implements ParameterDAO {

	@Override
	public List<Parameter> getAllParameters() {
		return getHibernateTemplate().loadAll(Parameter.class);
	}
	
	@Override
	public Parameter getParameterByName(String paramName) {
		return get(Parameter.class, paramName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parameter> getParametersLikeParamName(String aParamName) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Parameter.class);
		criteria.add(Restrictions.ilike("paramName", aParamName, MatchMode.ANYWHERE));

		return getHibernateTemplate().findByCriteria(criteria);
	}

	@Override
	public int getCountAllParameters() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from Parameter"));
	}

}
