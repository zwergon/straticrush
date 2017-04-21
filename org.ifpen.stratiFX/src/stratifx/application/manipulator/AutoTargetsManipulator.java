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
package stratifx.application.manipulator;

import java.util.ArrayList;
import java.util.List;

import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.IRigidItem;
import fr.ifp.jdeform.deformation.constraint.ExactTargetsComputer;
import fr.ifp.jdeform.deformation.items.PatchIntersectionItem;
import fr.ifp.jdeform.deformation.items.TranslateItem;
import fr.ifp.jdeform.scene.Scene;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.InfinitePolylineIntersection;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.IPolylineProvider;
import fr.ifp.kronosflow.polyline.LineIntersection;
import fr.ifp.kronosflow.polyline.LinePointPair;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

public class AutoTargetsManipulator  extends CompositeManipulator {
	
	
	protected PatchInterval  selectedHorizon = null;
	
	protected PatchInterval  selectedFault = null;
	
	protected List<IPolylineProvider> potentialFaults;
	
	protected TargetData targetData;
	
	class TargetData {
		int nbTargets;
		PatchIntersectionItem[] targets = new PatchIntersectionItem[2];
		
		void reset(){
			nbTargets = 0;
			targets[0] = null;
			targets[1] = null;
		}
		
		void setTarget( PatchIntersectionItem lp ){
			nbTargets = 1;
			targets[0] = lp;
		}
		
		void setTarget( PatchIntersectionItem lp1, PatchIntersectionItem lp2 ){
			nbTargets = 2;
			targets[0] = lp1;
			targets[1] = lp2;
		}
	};
	

	public AutoTargetsManipulator( GScene gscene, DeformationControllerCaller caller ){
		 super( gscene, caller );
		 targetData = new TargetData();
	}
	
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		super.onMousePress(event);
		
		double[] wc = gscene.getTransformer().deviceToWorld(event.x, event.y);
		selectedHorizon = targetsExtractor.findHorizonFeature( wc );
		selectedFault   = targetsExtractor.findFaultFeature( wc );

		
		if ( null != selectedHorizon ){
			selectedHorizon.getInterval().createExtension();	
			selectedPatchGraphic.addInterval(selectedHorizon);
		}
		
		if ( null != selectedFault ){
			selectedPatchGraphic.addInterval(selectedFault);
		}
		
		selectedPatchGraphic.redraw();
		
	}

	@Override
	public void onMouseRelease( GMouseEvent event ){
		
	}
	
	@Override
	public boolean canDeform() {
		items = new ArrayList<IDeformationItem>();
		rigidItems = new ArrayList<IRigidItem>();
		
		for( int i = 0; i< targetData.nbTargets; i++ ){
			items.add( targetData.targets[i] );
		}
		
		Patch selectedPatch = deformationCaller.getScene().getSelected();
		rigidItems.add(new TranslateItem(selectedPatch, Vector2D.substract(prev, start)));

		// then restore initial geometry
		translateTo(start);
	
		return super.canDeform();
	}
	

	

	@Override
	protected void computeTargets() {
		selectedPatchGraphic.clearTargets();
		targetData.reset();

		LinePointPair IH = null;
		for( IPolylineProvider provider : potentialHorizon ){

			LineIntersection lineInter = new LineIntersection( provider.getPolyline() );

			IH = lineInter.getFirstIntersection(selectedHorizon.getInterval());
			
			if ( null != IH ){
			
				break;
			}
		}
		
		LinePointPair IF = null;
		if ( selectedFault != null ){
			for( IPolylineProvider provider : potentialFaults ){

				IPolyline fault = provider.getPolyline() ;
				
				LineIntersection lineInter = new LineIntersection( fault );
				
				IF = lineInter.getFirstIntersection(selectedFault.getInterval());
				
				if ( null != IF ){
					
					break;
				}
			}
		}
		
		//exactly one feature is targeted
		if ( ( null != IH ) != /*XOR*/ ( null != IF ) ){
			
			if ( null != IH ) {
				GColor color = GColor.fromAWTColor(selectedHorizon.getColor());
				//selectedPatchGraphic.addTarget( IH.getPoint(), color.brighter() );
				
                                PatchIntersectionItem pi = new PatchIntersectionItem(selectedHorizon, IH);
				targetData.setTarget( pi );
                                
                                ExactTargetsComputer computer = new ExactTargetsComputer(pi.getInterval(), pi.getIntersection());
                                computer.compute();
                                
                                selectedPatchGraphic.addTarget(computer.getTargetLine(), color );
                                
			}
			
			if ( null != IF )  {
				GColor color = GColor.fromAWTColor(selectedFault.getColor());
				selectedPatchGraphic.addTarget( IF.getPoint(), color.brighter() );
				
				targetData.setTarget(  new PatchIntersectionItem(selectedFault, IF) );
			}
			
		}
		//both features are targeted
		else if ( ( null != IH ) && ( null != IF ) ){
			
			//computes intersection between targets lines
			InfinitePolylineIntersection horizonLineTarget = new InfinitePolylineIntersection(
					IH.getPoint().getLine() );
			
			LinePointPair linesI = horizonLineTarget
					.getFirstIntersection( IF.getPoint().getLine());
			
			//if an intersection between exists, this is the point where selected intervals should be located.
			if ( linesI == null ){
				return;
			}
			
			
			//computes intersection between feature intervals on selected patch.
			InfinitePolylineIntersection horizonFaultInter = new InfinitePolylineIntersection(
					selectedHorizon.getPolyline() );
			
			LinePointPair horizonFaultI = horizonFaultInter
					.getFirstIntersection(selectedFault.getPolyline());
			
			if ( horizonFaultI == null ){
				return;
			}
			
			//target Horizon Graphic
			selectedPatchGraphic.addTarget( linesI.getPoint(), GColor.BLUE );	
			//target Fault graphic
			selectedPatchGraphic.addTarget( linesI.getMatePoint(), GColor.BLUE );
			
			
			PatchIntersectionItem faultItem = new PatchIntersectionItem( selectedFault,  
					new LinePointPair( linesI.getMatePoint(), horizonFaultI.getMatePoint()  ) 
					); 
			//faultItem.setComputerType("Proportional");
			//store IDeformationItem
			targetData.setTarget(
					//constraint for horizon
					new PatchIntersectionItem( selectedHorizon, 
							new LinePointPair( linesI.getPoint(), horizonFaultI.getPoint()  ) 
							),
					//constraint for fault
					faultItem 
					);
			
		}

	}
	
	
	/**
	 * Create a vertical {@link IPolyline} from intersection point I to bottom
	 * of selectedComposite {@link Patch}
	 */
	private ExplicitPolyLine createDefaultTarget(LinePointPair I) {
		
		Scene scene = deformationCaller.getScene();
		Patch selected = scene.getSelected();
		double length = 1.5*selected.getBorder().getBoundingBox().height();
		List<Point2D> pts = new ArrayList<Point2D>();
		Point2D origin = I.getPoint().getPosition();
		Point2D target = new Point2D(origin);
		
		origin.setY( origin.y() + length );
		
		target.setY( target.y() - length );
		pts.add( origin );
		pts.add( target );
		
		ExplicitPolyLine faultTarget = new ExplicitPolyLine();
		faultTarget.initialize(pts);
		
		return faultTarget;
	}

	
	@Override
	protected void createPotentialTargets(){
		super.createPotentialTargets();
		potentialFaults = targetsExtractor.getFaultTargets();
	}



	
	

}
