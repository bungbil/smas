package billy.backend.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Parameter implements java.io.Serializable, Entity {

	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE +1;
	private int version;
	private String paramName;
	private String paramValue;
	private String description;
	

	public boolean isNew() {
		return (getId() == Long.MIN_VALUE +1);
	}

	public Parameter() {
	}

	public Parameter(long id, String paramName) {
		this.setId(id);
		this.paramName = paramName;
	}

	public Parameter(long id, String paramName, String paramValue, String description) {
		this.setId(id);
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.description = description;	
		
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}


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

	@Override
	public int hashCode() {
		return Long.valueOf(getId()).hashCode();
	}

	public boolean equals(Parameter Parameter) {
		return getId() == Parameter.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Parameter) {
			Parameter Parameter = (Parameter) obj;
			return equals(Parameter);
		}

		return false;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

}
