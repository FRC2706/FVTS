package ca.team2706.vision.vision2019.params;

public class Attribute {
	private String value;
	private String name;
	public Attribute(String value, String name) {
		this.value = value;
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public String getName() {
		return name;
	}
	public int getValueI() throws NumberFormatException{
		return Integer.valueOf(value);
	}
	public double getValueD() throws NumberFormatException{
		return Double.valueOf(value);
	}
	public boolean getValueB(){
		return Boolean.valueOf(value);
	}
	public void setValue(String value) {
		this.value = value;
	}
}
