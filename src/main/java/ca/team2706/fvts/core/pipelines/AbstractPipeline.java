package ca.team2706.fvts.core.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public abstract class AbstractPipeline {
	public abstract VisionData process(Mat src, VisionParams visionParams);
	public abstract void selectPreferredTarget(VisionData visionData, VisionParams visionParams);
	public abstract void drawPreferredTarget(Mat src, VisionData visionData);
	public abstract List<AttributeOptions> getOptions();
	private String name;
	public AbstractPipeline(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	private static List<AbstractPipeline> pipelines = null;
	public static AbstractPipeline getByName(String name) {
		if(pipelines == null) {
			// Init
			pipelines = new ArrayList<AbstractPipeline>();
			pipelines.add(new BlobDetectPipeline());
		}
		for(AbstractPipeline p : pipelines) {
			if(p.getName().equalsIgnoreCase(name))
				return p;
		}
		Log.e("Failed to find pipeline by the name of "+name, true);
		return null;
	}
}
