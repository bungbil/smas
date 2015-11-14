package billy.backend.service.impl;

import java.util.List;

import billy.backend.dao.ParameterDAO;
import billy.backend.model.Parameter;
import billy.backend.service.ParameterService;

public class ParameterServiceImpl implements ParameterService {

	private ParameterDAO parameterDAO;

	@Override
	public int getCountAllParameters() {
		// TODO Auto-generated method stub
		return parameterDAO.getCountAllParameters();
	}

	@Override
	public List<Parameter> getAllParameters() {
		// TODO Auto-generated method stub
		return parameterDAO.getAllParameters();
	}

	@Override
	public void saveOrUpdate(Parameter parameter) {
		// TODO Auto-generated method stub
		parameterDAO.saveOrUpdate(parameter);
	}
	

}
