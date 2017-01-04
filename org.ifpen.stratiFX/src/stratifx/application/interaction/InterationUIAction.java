package stratifx.application.interaction;

import stratifx.application.UIAction;

public class InterationUIAction extends UIAction {
	
	public static final int Interaction = UIAction.Last+1;
	
	String deformationType;
	
	String manipulatorType;
	
	String callerType;

	public InterationUIAction( String deformationType, String manipulatorType, String callerType ) {
		super(Interaction);
		this.deformationType = deformationType;
		this.manipulatorType = manipulatorType;
		this.callerType = callerType;
	}
	
	public InterationUIAction( String deformationType, String manipulatorType ) {
		this( deformationType, manipulatorType, null );
	}
	
	public InterationUIAction( String deformationType ) {
		this( deformationType, null, null );
	}

	public String getDeformationType() {
		return deformationType;
	}
	
	

}
