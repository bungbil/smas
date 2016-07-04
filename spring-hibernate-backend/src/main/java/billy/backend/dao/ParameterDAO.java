package billy.backend.dao;

import java.util.List;

import billy.backend.model.Parameter;

public interface ParameterDAO {

  public void delete(Parameter entity);

  public void deleteParameterById(long id);

  public List<Parameter> getAllParameters();

  public int getCountAllParameters();

  public Parameter getNewParameter();

  public Parameter getParameterById(Long id);

  public Parameter getParameterByParamName(String string);

  public List<Parameter> getParametersLikeDescription(String string);

  public List<Parameter> getParametersLikeParamValue(String string);

  public void save(Parameter entity);

  public void saveOrUpdate(Parameter entity);

}
