package stratifx.application.interaction;

import stratifx.application.UIAction;

public class InteractionUIAction extends UIAction {
	
	String deformationType;
	
	String manipulatorType;
	
	String callerType;

	public InteractionUIAction( String deformationType, String manipulatorType, String callerType ) {
		super(Interaction);
		this.deformationType = deformationType;
		this.manipulatorType = manipulatorType;
		this.callerType = callerType;
	}
	
	public InteractionUIAction( String deformationType, String manipulatorType ) {
		this( deformationType, manipulatorType, null );
	}
	
	public InteractionUIAction( String deformationType ) {
		this( deformationType, null, null );
	}

	public String getDeformationType() {
		return deformationType;
	}

	public String getManipulatorType() {
		return manipulatorType;
	}
	
	

}
