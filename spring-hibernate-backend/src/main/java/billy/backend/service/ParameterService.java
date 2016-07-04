package billy.backend.service;

import java.util.List;

import billy.backend.model.Parameter;

public interface ParameterService {

  public void delete(Parameter entity);

  public List<Parameter> getAllParameters();

  public int getCountAllParameters();

  public Parameter getNewParameter();

  public Parameter getParameterByID(Long id);

  public Parameter getParameterByParamName(String string);

  public List<Parameter> getParametersLikeDescription(String string);

  public List<Parameter> getParametersLikeParamValue(String string);

  public void saveOrUpdate(Parameter entity);

}
