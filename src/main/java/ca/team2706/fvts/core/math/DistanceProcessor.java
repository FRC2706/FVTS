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
		 * 
		 */

		for (Target t : visionData.targetsFound) {
			double y = t.boundingBox.height;

			double x = (y - visionParams.getByName("distYIntercept").getValueD())
					/ visionParams.getByName("distSlope").getValueD();

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
		
		AttributeOptions slope = new AttributeOptions("distSlope", true);
		AttributeOptions yIntercept = new AttributeOptions("distYIntercept", true);
		
		ret.add(slope);
		ret.add(yIntercept);
		
		return ret;
	}

}
