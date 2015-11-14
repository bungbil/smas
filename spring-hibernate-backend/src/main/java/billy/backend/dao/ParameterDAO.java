package billy.backend.dao;

import java.util.List;

import billy.backend.model.Parameter;

public interface ParameterDAO {

	public List<Parameter> getAllParameters();

	public int getCountAllParameters();

	public Parameter getParameterByName(String paramName);
	
	public List<Parameter> getParametersLikeParamName(String aParamName);

	public void saveOrUpdate(Parameter entity);

	
}
