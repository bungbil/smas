package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.ParameterDAO;
import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;

public class ParameterServiceImpl implements ParameterService {

	private ParameterDAO parameterDAO;

	public ParameterDAO getParameterDAO() {
		return parameterDAO;
	}

	public void setParameterDAO(ParameterDAO parameterDAO) {
		this.parameterDAO = parameterDAO;
	}

	@Override
	public Parameter getNewParameter() {
		return getParameterDAO().getNewParameter();
	}

	@Override
	public Parameter getParameterByID(Long id) {
		return getParameterDAO().getParameterById(id);
	}
	
	@Override
	public Parameter getParameterByParamName(String string) {
		return getParameterDAO().getParameterByParamName(string);
	}

	@Override
	public List<Parameter> getAllParameters() {
		return getParameterDAO().getAllParameters();
	}

	@Override
	public void saveOrUpdate(Parameter entity) {
		getParameterDAO().saveOrUpdate(entity);
	}

	@Override
	public void delete(Parameter entity) {
		getParameterDAO().delete(entity);
	}

	@Override
	public List<Parameter> getParametersLikeParamValue(String string) {
		return getParameterDAO().getParametersLikeParamValue(string);
	}

	@Override
	public List<Parameter> getParametersLikeDescription(String string) {
		return getParameterDAO().getParametersLikeDescription(string);
	}

	@Override
	public int getCountAllParameters() {
		return getParameterDAO().getCountAllParameters();
	}

}
