package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import de.uks.se.scenariodiagram.ScenarioObject;

public class AddScenarioObjectFeature extends AbstractAddFeature implements IAddFeature {
	private static final IColorConstant OBJECT_FOREGROUND = IColorConstant.BLACK;
	private static final IColorConstant OBJECT_BACKGROUND = IColorConstant.LIGHT_LIGHT_GRAY;

	private static final int DEFAULT_WIDTH = 180;
	private static final int DEFAULT_HEIGHT = 60;

	public AddScenarioObjectFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return context.getNewObject() instanceof ScenarioObject;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		ScenarioObject scenarioObject = (ScenarioObject) context.getNewObject();
		ContainerShape targetContainer = context.getTargetContainer();

		ContainerShape containerShape = peCreateService.createContainerShape(targetContainer, true);
		peCreateService.createChopboxAnchor(containerShape);
		link(containerShape, scenarioObject);

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		final int width = context.getWidth() <= 0 ? DEFAULT_WIDTH : context.getWidth();
		final int height = context.getHeight() <= 0 ? DEFAULT_HEIGHT : context.getHeight();

		// rectangle
		{
			RoundedRectangle rectangle = gaService.createRoundedRectangle(containerShape, 10, 10);
			gaService.setLocationAndSize(rectangle, context.getX(), context.getY(), width, height);
//			rectangle.setFilled(false);
			rectangle.setLineWidth(2);
			rectangle.setForeground(manageColor(OBJECT_FOREGROUND));
			rectangle.setBackground(manageColor(OBJECT_BACKGROUND));
		}

		// line
		{
			Shape lineShape = peCreateService.createShape(containerShape, false);
			// create and set graphics algorithm
			Polyline polyline = gaService.createPolyline(lineShape, new int[] { 0, 20, DEFAULT_WIDTH, 20 });
			polyline.setForeground(manageColor(OBJECT_FOREGROUND));
			polyline.setBackground(manageColor(OBJECT_BACKGROUND));
			polyline.setLineWidth(2);
		}

		// text
		{
			Shape textShape = peCreateService.createShape(containerShape, false);

			// object text
			String value = scenarioObject.getName();

			Text text = gaService.createText(textShape, value);
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
			gaService.setLocationAndSize(text, 0, 0, DEFAULT_WIDTH, 20);

			link(textShape, scenarioObject);

			// provide information to support direct-editing directly after object creation (must be activated additionally)
			IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
			// set container shape for direct editing after object creation
			directEditingInfo.setMainPictogramElement(containerShape);
			// set shape and graphics algorithm where the editor for direct editing shall be opened after object creation
			directEditingInfo.setPictogramElement(textShape);
			directEditingInfo.setGraphicsAlgorithm(text);
		}

		// call the layout feature
		layoutPictogramElement(containerShape);
		layoutPictogramElement(targetContainer);

		return containerShape;
	}
}
