package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class AddScenarioAttributeFeature extends AbstractAddFeature implements
IAddFeature {

	public AddScenarioAttributeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context)
	{
		if (context.getNewObject() instanceof ScenarioAttribute)
		{
			//object
			if (getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof ScenarioObject)
			{
				return true;
			}
			//link
			else if (getBusinessObjectForPictogramElement(context.getTargetConnection()) instanceof ScenarioLink)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		// get the services
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		ScenarioAttribute addedScenarioAttribute = (ScenarioAttribute) context.getNewObject();

		//check if attribute is added to a scenario object
		ContainerShape objectShape = context.getTargetContainer();
		if (getBusinessObjectForPictogramElement(objectShape) instanceof ScenarioObject)
		{
			ScenarioObject scenarioObject = (ScenarioObject) getBusinessObjectForPictogramElement(objectShape);
			Shape shape = peCreateService.createShape(objectShape, true);

			// pos
			int x = 10;
			int y = (scenarioObject.getAttributes().size()) * 20;
			int width = objectShape.getGraphicsAlgorithm().getWidth() - 20;
			int height = 20;

			// resize object shape
			int containerHeight = objectShape.getGraphicsAlgorithm().getHeight();
			if (containerHeight < 20 + 20 * (scenarioObject.getAttributes().size())) {
				objectShape.getGraphicsAlgorithm().setHeight(20 + 20 * (scenarioObject.getAttributes().size()));
			}

			// default size
			{
				String attributeText = addedScenarioAttribute.getAttributeType().getName() + "=" + addedScenarioAttribute.getValue();
				Text text = gaService.createText(shape, attributeText);
				text.setFont(gaService.manageDefaultFont(getDiagram(), false, false));
				text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
				gaService.setLocationAndSize(text, x, y, width, height);
			}

			peCreateService.createChopboxAnchor(shape);
			
			link(shape, addedScenarioAttribute);

			return shape;
		}
		//added to link
		else
		{
			Connection connection = context.getTargetConnection();
			
			// line
			ConnectionDecorator lineDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
			Polyline line = gaService.createPlainPolyline(lineDecorator, new int[]{0, 0, 0, 50});
			line.setForeground(manageColor(IColorConstant.BLACK));
			line.setLineWidth(3);
			
			// arrow
			ConnectionDecorator arrowDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
			Polyline polyline = gaService.createPolyline(arrowDecorator, new int[] { -10, 15, 0, 0, 10, 15});
			polyline.setForeground(manageColor(IColorConstant.BLACK));
			polyline.setLineWidth(3);

			// rectangle
			ConnectionDecorator rectangleDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
			Rectangle rectangle = gaService.createRectangle(rectangleDecorator);
			gaService.setLocationAndSize(rectangle, 0, 50, 100, 30);
			rectangle.setForeground(manageColor(IColorConstant.BLACK));
			rectangle.setBackground(manageColor(IColorConstant.WHITE));
			rectangle.setFilled(false);
			rectangle.setLineWidth(3);

			ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);

			// text
			String attributeText = addedScenarioAttribute.getAttributeType().getName() + "=" + addedScenarioAttribute.getValue();
			Text text = gaService.createText(textDecorator, attributeText);
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setForeground(manageColor(IColorConstant.BLACK));
			text.setFont(gaService.manageDefaultFont(getDiagram(), false, false));
			gaService.setLocationAndSize(text, (rectangle.getHeight()/2-text.getHeight()/2) , (rectangle.getWidth()/2-text.getWidth()/2), rectangle.getWidth(), rectangle.getHeight());

			link(textDecorator, addedScenarioAttribute);

			return textDecorator;
		}
	}
}
