package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.VisionData.Target;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class AngleOffsetProcessor extends AbstractMathProcessor {

	public AngleOffsetProcessor() {
		super("angleoffset");
	}

	@Override
	public void process(VisionData visionData, MainThread main) {
		if(visionData.targetsFound.size() == 0)
			return;
		VisionParams visionParams = visionData.params;
		/*
		 * 
		 * Time to math the distance y = height of cube x = distance from cube
		 * 
		 * using y = mx+b we can determine that the formula to calculate x from y is x =
		 * (y-b)/m
		 * 
		 * 
		 */

		for (Target t : visionData.targetsFound) {
			// Do the offset math which is using quadratics and please let this work, ive been trying this for 3 hours and its 00:00, i am very tired but this code keeps me up at night
			double aoA = visionParams.getByName(getName()+"/"+"aoA").getValueD();
			double aoB = visionParams.getByName(getName()+"/"+"aoB").getValueD();
			double aoC = visionParams.getByName(getName()+"/"+"aoC").getValueD();
			double magic = Math.abs(t.xCentreNorm) / (t.areaNorm / (visionData.binMask.rows() * visionData.binMask.cols()));
			double xo = Math.pow(magic,2) * aoA + magic * aoB + aoC;
			t.distance += xo;
		}
	}

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions aoA = new AttributeOptions(getName()+"/"+"aoA", true);
		AttributeOptions aoB = new AttributeOptions(getName()+"/"+"aoB", true);
		AttributeOptions aoC = new AttributeOptions(getName()+"/"+"aoC", true);
		
		ret.add(aoA);
		ret.add(aoB);
		ret.add(aoC);
		
		return ret;
	}

}
