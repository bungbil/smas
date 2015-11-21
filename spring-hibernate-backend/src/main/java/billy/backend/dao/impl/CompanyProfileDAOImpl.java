package billy.backend.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import billy.backend.dao.BillyBasisDAO;

import billy.backend.dao.CompanyProfileDAO;
import billy.backend.model.CompanyProfile;

@Repository
public class CompanyProfileDAOImpl extends BillyBasisDAO<CompanyProfile> implements CompanyProfileDAO {

	@Override
	public CompanyProfile getNewCompanyProfile() {
		return new CompanyProfile();
	}

	@Override
	public CompanyProfile getCompanyProfileById(Long id) {
		return get(CompanyProfile.class, id);
	}

	@SuppressWarnings("unchecked")
	public CompanyProfile getCompanyProfileByParamName(String paramName) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CompanyProfile.class);
		criteria.add(Restrictions.eq("paramName", paramName));

		return (CompanyProfile) DataAccessUtils.uniqueResult(getHibernateTemplate().findByCriteria(criteria));
	}

	@Override
	public List<CompanyProfile> getAllCompanyProfiles() {
		return getHibernateTemplate().loadAll(CompanyProfile.class);
	}

	@Override
	public void deleteCompanyProfileById(long id) {
		CompanyProfile CompanyProfile = getCompanyProfileById(id);
		if (CompanyProfile != null) {
			delete(CompanyProfile);
		}
	}

	@Override
	public int getCountAllCompanyProfiles() {
		return DataAccessUtils.intResult(getHibernateTemplate().find("select count(*) from CompanyProfile"));
	}

}