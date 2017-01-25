package stratifx.application;

public interface IUIController {
	
	public static enum Type {
		MAIN,
		TREE,
		PLOT
	};
	
	public boolean handleAction( UIAction action );
}
