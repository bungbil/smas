package billy.backend.service;

import java.util.List;

import billy.backend.model.CompanyProfile;
public interface CompanyProfileService {

	public CompanyProfile getNewCompanyProfile();

	public int getCountAllCompanyProfiles();

	public CompanyProfile getCompanyProfileByID(Long id);

	public List<CompanyProfile> getAllCompanyProfiles();

	public void saveOrUpdate(CompanyProfile entity);

	public void delete(CompanyProfile entity);

}
