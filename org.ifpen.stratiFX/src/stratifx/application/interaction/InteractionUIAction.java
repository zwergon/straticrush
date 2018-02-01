/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.application.interaction;

import stratifx.application.main.UIAction;

public class InteractionUIAction extends UIAction<String> {

	String manipulatorType;
	
	String callerType;

	public InteractionUIAction( String deformationType, String manipulatorType, String callerType ) {
		super(INTERACTION, deformationType);
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
		return getData();
	}

	public String getManipulatorType() {
		return manipulatorType;
	}
	
	

}
