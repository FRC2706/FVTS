package ca.team2706.fvts.core.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class DummyPipeline extends AbstractPipeline {

	public DummyPipeline() {
		super("dummy");
	}

	@Override
	public VisionData process(Mat src, VisionParams visionParams) {
		return new VisionData();
	}

	@Override
	public void selectPreferredTarget(VisionData visionData, VisionParams visionParams) {
		
	}

	@Override
	public void drawPreferredTarget(Mat src, VisionData visionData) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		return new ArrayList<AttributeOptions>();
	}

}
