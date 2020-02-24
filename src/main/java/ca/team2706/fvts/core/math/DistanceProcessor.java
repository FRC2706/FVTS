package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.VisionData.Target;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class DistanceProcessor extends AbstractMathProcessor{

	public DistanceProcessor() {
		super("distance");
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
		 * NOTE: To keep things nice and constant across resolutions (and because I didn't want to break backwards
		 * compatibility because im too lazy to recallibrate the configs) 
		 * the resolution used to distance sensing is 640x480 and the height is just scaled from its native resolution to match
		 * 
		 */

		double scaleFactor = 480d/visionData.binMask.rows();
		
		for (Target t : visionData.targetsFound) {
			double y = t.boundingBox.height * scaleFactor;

			double x = (y - visionParams.getByName(getName()+"/"+"distYIntercept").getValueD())
					/ visionParams.getByName(getName()+"/"+"distSlope").getValueD();

			// Now we have the distance!!!
			t.distance = x;

		}
	}

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions slope = new AttributeOptions(getName()+"/"+"distSlope", true);
		AttributeOptions yIntercept = new AttributeOptions(getName()+"/"+"distYIntercept", true);
		
		ret.add(slope);
		ret.add(yIntercept);
		
		return ret;
	}

}
