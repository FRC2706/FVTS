package ca.team2706.vision.core.params;

public class AttributeOptions {
	private String name;
	private boolean required;
	public AttributeOptions(String name, boolean required) {
		this.name = name;
		this.required = required;
	}
	public String getName() {
		return name;
	}
	public boolean isRequired() {
		return required;
	}
}
