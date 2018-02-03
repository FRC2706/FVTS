package ca.team2706.vision.trackerboxreloaded;

public class Timer implements Runnable{
	
	public static final int MILLIS_PER_SECOND = 1000;
	public Timer(){
		new Thread(this).start();
	}
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(MILLIS_PER_SECOND);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Main.current_time_seconds++;
		}
	}

}
