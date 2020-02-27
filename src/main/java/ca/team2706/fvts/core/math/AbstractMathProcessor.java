package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;

public abstract class AbstractMathProcessor {
	public abstract void process(VisionData data, MainThread main);
	public abstract void init(MainThread main);
	public abstract List<AttributeOptions> getOptions();
	private String name;
	public String getName() {
		return name;
	}
	public AbstractMathProcessor(String name) {
		this.name = name;
	}
	public static List<AbstractMathProcessor> maths = null;
	public static AbstractMathProcessor getByName(String name) {
		if(maths == null) {
			// Init
			maths = new ArrayList<AbstractMathProcessor>();
			maths.add(new AngleOffsetProcessor());
			maths.add(new DistanceProcessor());
			maths.add(new GroupProcessor());
			maths.add(new PrefferedTargetProcessor());
		}
		for(AbstractMathProcessor p : maths) {
			if(p.getName().equalsIgnoreCase(name))
				return p;
		}
		Log.e("Failed to find math processor by the name of "+name, true);
		return null;
	}
}
