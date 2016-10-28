package straticrush.filters;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMPathElement;
import org.apache.batik.dom.svg.SVGPathSupport;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPoint;

import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.algo.ComputeContact;
import fr.ifp.kronosflow.model.algo.KronosHelper;
import fr.ifp.kronosflow.model.factory.ModelFactory;
import fr.ifp.kronosflow.model.factory.ModelFactory.ComplexityType;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.filters.ImportBorderFile;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.topology.Contact;
import fr.ifp.kronosflow.topology.ContactGraph;
import fr.ifp.kronosflow.utils.LOGGER;

public class ImportSVGFile extends ImportBorderFile {

	
	List< List<Point2D> > listBorders = new ArrayList<List<Point2D>>();
	
	int nbPtsByBorder = 50;

	static public class Builder implements ImportBorderFile.Builder {
		@Override
		public ImportBorderFile create() {
			return new ImportSVGFile();
		}
	}

	public ImportSVGFile() {
		super();
	}
	
	public void execute() {

		assert patchLib != null;

		if (fileName == null) {
			return;
		}

		readPathSvg(fileName);

		if (listBorders.isEmpty()) {
			LOGGER.debug("No borders", this.getClass());
			return;
		}

		List<Patch> patches = new ArrayList<Patch>();
		RectD sectionBoundingBox = new RectD();

		int i =0 ;
		for (List<Point2D> border :  listBorders ) {
			Patch patch = createPatch();
			patch.setName("Patch" + i++ );


			buildPatch(border, patch);
			//addPhysicals(patch);

			patches.add(patch);
			sectionBoundingBox.union(patch.getBorder().getBoundingBox());
		}


		Paleobathymetry paleo = new Paleobathymetry();
		paleo.setName("Paleobathymetry");
		patchLib.add(paleo);
		paleo.initPaleoLine();

		ContactGraph graph = new ContactGraph(patchLib);
		patchLib.add(graph);

		// Find all borders for each Patch
		KronosHelper.calculateBorders(patchLib);

	}

	private Patch createPatch() {
		return ModelFactory.createPatch(GridType.TRGL, NatureType.EXPLICIT, ComplexityType.SINGLE);
	}
	
	public void readPathSvg(String f){
		
		try{
			SVGDocument svgDoc;
			UserAgent userAgent;
			DocumentLoader loader;
			BridgeContext ctx;
			GVTBuilder builder;
			
			userAgent = new UserAgentAdapter();
			loader = new DocumentLoader(userAgent);
			ctx = new BridgeContext(userAgent, loader);
			ctx.setDynamicState(BridgeContext.DYNAMIC);
			builder = new GVTBuilder();
			
			URI fileURI = new File(f).toURI();
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
			svgDoc = (SVGDocument)svgf.createDocument(fileURI.toString());
			
			builder.build(ctx, svgDoc);
			
			NodeList paths = svgDoc.getElementsByTagName("path");
			
			for(int i=0; i < paths.getLength(); i++){
			
				SVGOMPathElement path = (SVGOMPathElement)paths.item(i);
				System.out.println(i +":" + path.getAttribute("d"));
				List<Point2D> pts = new ArrayList<Point2D>();
				
				System.out.println( path.getTotalLength() );
				
				float total_path_length = path.getTotalLength();
				float unit_length = total_path_length/(float)nbPtsByBorder;
				
				for(int j=0; j < nbPtsByBorder; j++){
										
					SVGPoint tmp_point = SVGPathSupport.getPointAtLength(path, unit_length*j);
					pts.add( new Point2D(tmp_point.getX(), tmp_point.getY() ) );
				}
				
				listBorders.add( pts );
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	protected void buildPatch( List<Point2D> border, Patch patch ) {

		Style style = patch.getStyle();
		style.setAttributeB("beautify", true );

		patch.createBorder(border);

		List<Patch> patches = patchLib.getPatches();
		patchLib.add(patch);

		ComputeContact computeContact = new ComputeContact(patch);

		for (Patch curPatch : patches) {
			List<Contact> contacts = computeContact.executeMultiContact(curPatch);
			for (Contact contact : contacts) {
				patchLib.add(contact);
			}
		}
	}

}
