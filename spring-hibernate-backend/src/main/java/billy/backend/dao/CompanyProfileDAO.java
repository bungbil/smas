package billy.backend.dao;

import java.util.List;

import billy.backend.model.CompanyProfile;

public interface CompanyProfileDAO {

  public void delete(CompanyProfile entity);

  public void deleteCompanyProfileById(long id);

  public List<CompanyProfile> getAllCompanyProfiles();

  public CompanyProfile getCompanyProfileById(Long id);

  public int getCountAllCompanyProfiles();

  public CompanyProfile getNewCompanyProfile();

  public void save(CompanyProfile entity);

  public void saveOrUpdate(CompanyProfile entity);

}
