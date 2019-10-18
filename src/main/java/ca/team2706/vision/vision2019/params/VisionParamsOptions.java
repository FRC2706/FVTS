package ca.team2706.vision.vision2019.params;

import java.util.ArrayList;
import java.util.List;

public class VisionParamsOptions {
	private List<AttributeOptions> options = new ArrayList<AttributeOptions>();
	private List<Attribute> attribs = new ArrayList<Attribute>();
	public VisionParamsOptions(List<AttributeOptions> options) {
		this.options = options;
		
	}
	public List<Attribute> getAttribs() {
		return attribs;
	}
	public void setAttribs(List<Attribute> attribs) {
		this.attribs = attribs;
	}
	public boolean isValid() {
		for(AttributeOptions o : options) {
			if(o.isRequired()) {
				Attribute a = getByName(o.getName());
				if(a == null)
					return false;
			}
		}
		return true;
	}
	public Attribute getByName(String s) {
		for(Attribute a : attribs) {
			if(a.getName().equals(s))
				return a;
		}
		return null;
	}
}
