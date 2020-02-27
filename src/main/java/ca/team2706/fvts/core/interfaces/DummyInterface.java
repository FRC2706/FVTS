package ca.team2706.fvts.core.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;

public class DummyInterface extends AbstractInterface {

	public Map<String,VisionData> lastFrame;
	public Map<String,Lock> locks = null;
	
	public DummyInterface() {
		super("dummy");
	}

	@Override
	public void publishData(VisionData data, MainThread thread) {
		String name = data.params.getByName("name").getValue();
		locks.get(name).lock();
		this.lastFrame.put(name,data);
		locks.get(name).unlock();
	}

	@Override
	public void init(MainThread thread) {
		if(locks == null)
			locks = new HashMap<String,Lock>();
		locks.put(thread.getVisionParams().getByName("name").getValue(), new ReentrantLock());
	}

	@Override
	public List<AttributeOptions> getOptions() {
		return new ArrayList<AttributeOptions>();
	}

}
