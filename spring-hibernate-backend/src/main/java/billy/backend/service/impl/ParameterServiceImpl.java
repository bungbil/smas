package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.ParameterDAO;
import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;

public class ParameterServiceImpl implements ParameterService {

	private ParameterDAO ParameterDAO;

	public ParameterDAO getParameterDAO() {
		return ParameterDAO;
	}

	public void setParameterDAO(ParameterDAO ParameterDAO) {
		this.ParameterDAO = ParameterDAO;
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
	public List<Parameter> getAllParameters() {
		return getParameterDAO().getAllParameters();
	}

	@Override
	public void saveOrUpdate(Parameter Parameter) {
		getParameterDAO().saveOrUpdate(Parameter);
	}

	@Override
	public void delete(Parameter Parameter) {
		getParameterDAO().delete(Parameter);
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
