package ca.team2706.vision.vision2019.params;

import java.util.List;

/**
 * A class to hold calibration parameters for the image processing algorithm
 */
public class VisionParams {
	private VisionParamsOptions options;
	public VisionParams(List<Attribute> params, List<AttributeOptions> options) throws Exception{
		this.options = new VisionParamsOptions(options);
		this.options.setAttribs(params);
		if(!this.options.isValid()) {
			throw new Exception("Config is not valid!");
		}
	}
	public Attribute getByName(String s) {
		return options.getByName(s);
	}
	public List<Attribute> getAttribs(){
		return options.getAttribs();
	}
	public void putAttrib(Attribute a) {
		options.putAttrib(a);
	}
}