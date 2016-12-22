package stratifx.application;

public interface IUIController {
	
	public static enum Type {
		MAIN,
		PLOT
	};
	
	public boolean handleAction( UIAction action );
}
