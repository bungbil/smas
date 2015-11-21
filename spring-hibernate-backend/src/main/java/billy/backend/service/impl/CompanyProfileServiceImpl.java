package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.CompanyProfileDAO;
import billy.backend.model.CompanyProfile;
import billy.backend.service.CompanyProfileService;

public class CompanyProfileServiceImpl implements CompanyProfileService {

	private CompanyProfileDAO CompanyProfileDAO;

	public CompanyProfileDAO getCompanyProfileDAO() {
		return CompanyProfileDAO;
	}

	public void setCompanyProfileDAO(CompanyProfileDAO CompanyProfileDAO) {
		this.CompanyProfileDAO = CompanyProfileDAO;
	}

	@Override
	public CompanyProfile getNewCompanyProfile() {
		return getCompanyProfileDAO().getNewCompanyProfile();
	}

	@Override
	public CompanyProfile getCompanyProfileByID(Long id) {
		return getCompanyProfileDAO().getCompanyProfileById(id);
	}

	@Override
	public List<CompanyProfile> getAllCompanyProfiles() {
		return getCompanyProfileDAO().getAllCompanyProfiles();
	}

	@Override
	public void saveOrUpdate(CompanyProfile CompanyProfile) {
		getCompanyProfileDAO().saveOrUpdate(CompanyProfile);
	}

	@Override
	public void delete(CompanyProfile CompanyProfile) {
		getCompanyProfileDAO().delete(CompanyProfile);
	}

	@Override
	public int getCountAllCompanyProfiles() {
		return getCompanyProfileDAO().getCountAllCompanyProfiles();
	}

}
