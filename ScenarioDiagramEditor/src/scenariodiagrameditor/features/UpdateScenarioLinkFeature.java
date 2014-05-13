package scenariodiagrameditor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class UpdateScenarioLinkFeature extends AbstractUpdateFeature {

	public UpdateScenarioLinkFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof ScenarioLink);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {

		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ConnectionDecorator) {
			ConnectionDecorator cd = (ConnectionDecorator) pictogramElement;
			if (cd.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) cd.getGraphicsAlgorithm();
				pictogramName = text.getValue();
			}
		}

		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof ScenarioLink) {
			ScenarioLink scenarioLink = (ScenarioLink) bo;
			businessName = scenarioLink.getName();
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
		if (bo instanceof ScenarioLink) {
			ScenarioLink scenarioLink = (ScenarioLink) bo;
			businessName = scenarioLink.getName();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ConnectionDecorator) {
			ConnectionDecorator cd = (ConnectionDecorator) pictogramElement;
			if (cd.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) cd.getGraphicsAlgorithm();
				text.setValue(businessName);
				return true;
			}
		}

		return false;
	}

}