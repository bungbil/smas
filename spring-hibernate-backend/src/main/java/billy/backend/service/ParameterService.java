package billy.backend.service;

import java.util.List;

import billy.backend.model.Parameter;

public interface ParameterService {

	public int getCountAllParameters();

	public List<Parameter> getAllParameters();

	public void saveOrUpdate(Parameter parameter);

}
