package no.geosoft.cc.graphics;

import java.util.TimerTask;

public class GTooltip extends GScene {
	
	public static class Task extends TimerTask {

		@Override
		public void run() {
			System.out.println("GTooltip.Task");
		}
		
	}

	public GTooltip(GWindow window) {
		super(window);
	}

}
