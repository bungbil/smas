package billy.backend.service;

import java.util.List;

import billy.backend.model.Parameter;
public interface ParameterService {

	public Parameter getNewParameter();

	public int getCountAllParameters();

	public Parameter getParameterByID(Long id);
	
	public Parameter getParameterByParamName(String string);

	public List<Parameter> getAllParameters();

	public List<Parameter> getParametersLikeParamValue(String string);
	
	public List<Parameter> getParametersLikeDescription(String string);

	public void saveOrUpdate(Parameter entity);

	public void delete(Parameter entity);

}
