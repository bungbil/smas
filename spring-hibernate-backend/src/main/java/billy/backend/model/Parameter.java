package billy.backend.model;

public class Parameter implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String paramName;
	private String paramValue;
	private String description;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
