package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioObject;
import de.uks.se.scenariodiagram.impl.ScenarioObjectImpl;

public class UpdateScenarioObjectFeature extends AbstractUpdateFeature {
	public UpdateScenarioObjectFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof ScenarioObject);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {

		PictogramElement pictogramElement = context.getPictogramElement();
		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof ScenarioObject) {
			ScenarioObject scenarioObject = (ScenarioObject) bo;
			businessName = scenarioObject.getName();
		}

		// retrieve name from pictogram model
		String pictogramName = null;
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (null != shape.getLink()) {
					if (getBusinessObjectForPictogramElement(shape.getLink().getPictogramElement()) instanceof ScenarioObject
							&& shape.getGraphicsAlgorithm() instanceof Text) {
						Text text = (Text) shape.getGraphicsAlgorithm();
						pictogramName = text.getValue();
					}
				}
			}
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName)));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date");
		} else {
			return Reason.createFalseReason();
		}
	}

	@Override
	public boolean update(IUpdateContext context) {

		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof ScenarioObject) {
			ScenarioObject scenarioObject = (ScenarioObject) bo;
			businessName = scenarioObject.getName();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (null != shape.getLink()) {
					if (getBusinessObjectForPictogramElement(shape.getLink().getPictogramElement()) instanceof ScenarioObject
							&& shape.getGraphicsAlgorithm() instanceof Text) {
						Text text = ((Text) shape.getGraphicsAlgorithm());
						text.setValue(businessName);
						return true;
					}
				}
			}
		}
		return false;
	}

}
