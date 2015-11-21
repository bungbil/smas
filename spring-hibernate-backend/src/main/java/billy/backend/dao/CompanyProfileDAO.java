package billy.backend.dao;

import java.util.List;

import billy.backend.model.CompanyProfile;

public interface CompanyProfileDAO {

	public CompanyProfile getNewCompanyProfile();

	public CompanyProfile getCompanyProfileById(Long id);

	public List<CompanyProfile> getAllCompanyProfiles();

	public int getCountAllCompanyProfiles();

	public void deleteCompanyProfileById(long id);

	public void saveOrUpdate(CompanyProfile entity);

	public void delete(CompanyProfile entity);

	public void save(CompanyProfile entity);

}
