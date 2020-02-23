package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class PrefferedTargetProcessor extends AbstractMathProcessor {

	public PrefferedTargetProcessor() {
		super("prefferedtarget");
	}

	@Override
	public void process(VisionData visionData, MainThread main) {
		if (visionData.targetsFound.size() == 0)
			return;
		VisionParams visionParams = visionData.params;

		// loop over the targets to find the largest area of any target found.
		// this is so we can give the largest a score of 1.0, and each other target a
		// score that is a
		// percentage of the area of the largest.
		double largestAreaNorm = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {
			if (target.areaNorm > largestAreaNorm)
				largestAreaNorm = target.areaNorm;
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {

			// Give each target a score, and select the one with the highest score.

			double areaScore = target.areaNorm / largestAreaNorm;
			double distFromCentrePenalty = Math.abs(target.xCentreNorm);

			double score = (1 - visionParams.getByName("distToCentreImportance").getValueD()) * areaScore
					- visionParams.getByName("distToCentreImportance").getValueD() * distFromCentrePenalty;

			if (bestScore < score) {
				visionData.preferredTarget = target;
				bestScore = score;
			}
		}
	}

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions distToCentreImportance = new AttributeOptions("distToCentreImportance", true);
		
		ret.add(distToCentreImportance);
		
		return ret;
	}

}
