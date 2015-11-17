package billy.backend.dao;

import java.util.List;

import billy.backend.model.Parameter;

public interface ParameterDAO {

	public Parameter getNewParameter();

	public Parameter getParameterById(Long paramId);

	public Parameter getParameterByParamName(String paramName);

	public List<Parameter> getAllParameters();

	public int getCountAllParameters();

	public List<Parameter> getParametersLikeParamValue(String string);

	public List<Parameter> getParametersLikeDescription(String string);	

	public void deleteParameterById(long id);

	public void saveOrUpdate(Parameter entity);

	public void delete(Parameter entity);

	public void save(Parameter entity);

}
