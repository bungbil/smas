package billy.backend.service;

import java.util.List;

import billy.backend.model.CompanyProfile;

public interface CompanyProfileService {

  public void delete(CompanyProfile entity);

  public List<CompanyProfile> getAllCompanyProfiles();

  public CompanyProfile getCompanyProfileByID(Long id);

  public int getCountAllCompanyProfiles();

  public CompanyProfile getNewCompanyProfile();

  public void saveOrUpdate(CompanyProfile entity);

}
