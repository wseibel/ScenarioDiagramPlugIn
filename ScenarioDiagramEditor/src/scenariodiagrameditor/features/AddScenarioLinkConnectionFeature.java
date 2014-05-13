package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import de.uks.se.scenariodiagram.ScenarioLink;

public class AddScenarioLinkConnectionFeature extends AbstractAddFeature implements IAddFeature {

	public AddScenarioLinkConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return context instanceof IAddConnectionContext && context.getNewObject() instanceof ScenarioLink;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		ScenarioLink scenarioLink = (ScenarioLink) context.getNewObject();

		// if source and target are not parent of each other
		Anchor sourceAnchor = addConContext.getSourceAnchor();
		Anchor targetAnchor = addConContext.getTargetAnchor();

		// create new connection and set anchors
		Connection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(sourceAnchor);
		connection.setEnd(targetAnchor);

		// create polyline as representation of the connection
		Polyline polyline = gaService.createPlainPolyline(connection);
		polyline.setForeground(manageColor(IColorConstant.BLACK));
		polyline.setLineWidth(3);

		link(connection, scenarioLink);

		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		Text text = gaService.createDefaultText(getDiagram(), textDecorator);
		text.setForeground(manageColor(IColorConstant.BLACK));
		gaService.setLocation(text, 10, 0);

		// linkname
		String value = scenarioLink.getName();
		text.setValue(value);

		link(textDecorator, scenarioLink);

		return connection;
	}
}
