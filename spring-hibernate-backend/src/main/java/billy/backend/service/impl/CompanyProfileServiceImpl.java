package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.CompanyProfileDAO;
import billy.backend.model.CompanyProfile;
import billy.backend.service.CompanyProfileService;

public class CompanyProfileServiceImpl implements CompanyProfileService {

  private CompanyProfileDAO companyProfileDAO;

  @Override
  public void delete(CompanyProfile entity) {
    getCompanyProfileDAO().delete(entity);
  }

  @Override
  public List<CompanyProfile> getAllCompanyProfiles() {
    return getCompanyProfileDAO().getAllCompanyProfiles();
  }

  @Override
  public CompanyProfile getCompanyProfileByID(Long id) {
    return getCompanyProfileDAO().getCompanyProfileById(id);
  }

  public CompanyProfileDAO getCompanyProfileDAO() {
    return companyProfileDAO;
  }

  @Override
  public int getCountAllCompanyProfiles() {
    return getCompanyProfileDAO().getCountAllCompanyProfiles();
  }

  @Override
  public CompanyProfile getNewCompanyProfile() {
    return getCompanyProfileDAO().getNewCompanyProfile();
  }

  @Override
  public void saveOrUpdate(CompanyProfile entity) {
    getCompanyProfileDAO().saveOrUpdate(entity);
  }

  public void setCompanyProfileDAO(CompanyProfileDAO companyProfileDAO) {
    this.companyProfileDAO = companyProfileDAO;
  }

}
