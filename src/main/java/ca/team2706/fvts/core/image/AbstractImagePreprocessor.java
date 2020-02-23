package ca.team2706.fvts.core.image;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.AttributeOptions;

public abstract class AbstractImagePreprocessor {
	public abstract void init(MainThread thread);
	public abstract List<AttributeOptions> getOptions();
	public abstract Mat process(Mat src, MainThread thread);
	
	private String name;
	
	public String getName() {
		return name;
	}
	public AbstractImagePreprocessor(String name) {
		this.name = name;
	}
	public static List<AbstractImagePreprocessor> imageProcessors = null;
	public static AbstractImagePreprocessor getByName(String name) {
		if(imageProcessors == null) {
			imageProcessors = new ArrayList<AbstractImagePreprocessor>();
			imageProcessors.add(new ImageCropPreprocessor());
			imageProcessors.add(new ImageResizingProcessor());
		}
		
		for(AbstractImagePreprocessor p : imageProcessors) {
			if(p.getName().equalsIgnoreCase(name))
				return p;
		}
		
		return null;
	}
}
