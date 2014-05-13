package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import scenariodiagrameditor.util.Util;
import de.uks.se.scenariodiagram.ScenarioAttribute;

public class UpdateScenarioAttributeFeature extends AbstractUpdateFeature {

	public UpdateScenarioAttributeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof ScenarioAttribute);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {

		// retrieve name from pictogram element
		String pictogramValue = null, pictogramType = null, pictogramTag = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof Shape) {
			Shape shape = (Shape) pictogramElement;
			if (shape.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) shape.getGraphicsAlgorithm();
				pictogramValue = text.getValue();
			}
		}
		if (pictogramValue.contains("=")) {
			pictogramTag = pictogramValue.split("=")[0];
			pictogramValue = pictogramValue.split("=")[1];
		}
		if (pictogramValue.contains(":")) {
			pictogramType = pictogramValue.split(":")[1];
			pictogramValue = pictogramValue.split(":")[0];
		}

		// retrieve name from business model
		String businessValue = null, businessTag = null, businessType = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof ScenarioAttribute) {
			ScenarioAttribute scenarioAttribute = (ScenarioAttribute) bo;
			businessValue = scenarioAttribute.getValue();
			businessTag = scenarioAttribute.getAttributeType().getName();
			businessType = scenarioAttribute.getAttributeType().getEType().getInstanceTypeName();
		}

		// update needed, if pictogram and business object are different
		boolean updateNameNeeded = ((pictogramValue != businessValue) || (pictogramTag != businessTag) || (pictogramType != businessType));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date");
		} else {
			return Reason.createFalseReason();
		}
	}

	@Override
	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);

		String businessValue = null, businessTag = null, businessType = null;
		if (bo instanceof ScenarioAttribute) {
			ScenarioAttribute scenarioAttribute = (ScenarioAttribute) bo;
			businessValue = scenarioAttribute.getValue();
			businessTag = scenarioAttribute.getAttributeType().getName();
			businessType = scenarioAttribute.getAttributeType().getEType().getInstanceTypeName();
		}

		String newText = "";
		if (businessTag != null) {
			newText += businessTag;
		}
		if (businessValue != null) {
			newText += "=";
			newText += businessValue;
		}
		if (businessType != null) {
			newText += ":";
			newText += businessType;
		}

		// Set name in pictogram model
		if (pictogramElement instanceof Shape) {
			Shape shape = (Shape) pictogramElement;
			if (shape.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) shape.getGraphicsAlgorithm();
				text.setValue(newText);
				return true;
			}
		}

		return false;
	}

}