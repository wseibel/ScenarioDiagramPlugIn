package scenariodiagrameditor.diagram;

import java.util.HashMap;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class SDToolBehaviorProvider extends DefaultToolBehaviorProvider {
	private final String DEFAULT_LINK_NAME = "has";
	private final String DEFAULT_ATTRIBUTE_TAG = "tag";
	private final String DEFAULT_OBJECT_TYPENAME = "DefaultType";

	public SDToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);

		PictogramElement pe = context.getPictogramElement();

		if (pe.getLink() != null && pe.getLink().getBusinessObjects().get(0) instanceof ScenarioObject) {
			ContextButtonEntry button = new ContextButtonEntry(null, context);

			button.setText("Quickfix Object Type");

			button.setIconId(IPlatformImageConstants.IMG_ECLIPSE_QUICKASSIST);

			CustomContext cc = new CustomContext();
			cc.setPictogramElements(new PictogramElement[] { context.getPictogramElement() });

			ICustomFeature[] features = getFeatureProvider().getCustomFeatures(cc);

			for (ICustomFeature feature : features) {
				if (feature.isAvailable(cc)) {
					button.addContextButtonMenuEntry(new ContextButtonEntry(feature, cc));
				}
			}
			data.getDomainSpecificContextButtons().add(button);
		} else if (pe.getLink() != null && pe.getLink().getBusinessObjects().get(0) instanceof ScenarioAttribute) {
			ContextButtonEntry button = new ContextButtonEntry(null, context);

			button.setText("Quickfix Attribute Type");

			button.setIconId(IPlatformImageConstants.IMG_ECLIPSE_QUICKASSIST);

			CustomContext cc = new CustomContext();
			cc.setPictogramElements(new PictogramElement[] { context.getPictogramElement() });

			ICustomFeature[] features = getFeatureProvider().getCustomFeatures(cc);

			for (ICustomFeature feature : features) {
				if (feature.isAvailable(cc)) {
					button.addContextButtonMenuEntry(new ContextButtonEntry(feature, cc));
				}
			}
			data.getDomainSpecificContextButtons().add(button);
		} else if (pe instanceof Anchor) {
			ContextButtonEntry button = new ContextButtonEntry(null, context);

			button.setText("Quickfix Link Type");

			button.setIconId(IPlatformImageConstants.IMG_ECLIPSE_QUICKASSIST);

			CustomContext cc = new CustomContext();
			cc.setPictogramElements(new PictogramElement[] { context.getPictogramElement() });

			ICustomFeature[] features = getFeatureProvider().getCustomFeatures(cc);

			for (ICustomFeature feature : features) {
				if (feature.isAvailable(cc)) {
					button.addContextButtonMenuEntry(new ContextButtonEntry(feature, cc));
				}
			}
			data.getDomainSpecificContextButtons().add(button);
		}
		return data;
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		IFeatureProvider featureProvider = getFeatureProvider();
		Object businessElement = featureProvider.getBusinessObjectForPictogramElement(pe);

		if (businessElement == null) {
			super.getDecorators(pe);
		}

		if (businessElement instanceof ScenarioObject) {
			IDecorator objectProblemDecorator = null;

			if (((ScenarioObject) businessElement).getObjectType() == null
					|| ((ScenarioObject) businessElement).getObjectType().getName()
							.equals(DEFAULT_OBJECT_TYPENAME)) {
				objectProblemDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
				objectProblemDecorator.setMessage("Object has no type.");

				return new IDecorator[] { objectProblemDecorator };
			}

			ScenarioObject sObject = (ScenarioObject) businessElement;
			if (differentTypeNames(sObject)) {
				objectProblemDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
				objectProblemDecorator.setMessage("Different object type for same link name.");

				return new IDecorator[] { objectProblemDecorator };
			}

			return new IDecorator[] { objectProblemDecorator };
		} else if (businessElement instanceof ScenarioLink
				&& (((ScenarioLink) businessElement).getIncomingReference() == null || ((ScenarioLink) businessElement)
						.getIncomingReference().getDefaultValueLiteral().equals(DEFAULT_LINK_NAME))) {
			IDecorator linkProblemDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
			linkProblemDecorator.setMessage("Linkname is still \"has\".");

			return new IDecorator[] { linkProblemDecorator };
		} else if (businessElement instanceof ScenarioAttribute
				&& (((ScenarioAttribute) businessElement).getAttributeType() == null)) {
			IDecorator attributeProblemDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
			attributeProblemDecorator.setMessage("Attribute has no type.");

			return new IDecorator[] { attributeProblemDecorator };
		}

		return super.getDecorators(pe);
	}

	private boolean differentTypeNames(ScenarioObject sObject) {
		HashMap<String, String> linkNamesToTypeNames = new HashMap<String, String>();

		for (EReference tmpReference : sObject.getObjectType().getEReferences()) {
			String typeOfOpposingObject = tmpReference.getEReferenceType().getName();

			String linkName = tmpReference.getDefaultValueLiteral();

			// already contained and different typeName
			String contained = linkNamesToTypeNames.put(linkName, typeOfOpposingObject);
			if (contained != null && contained != typeOfOpposingObject) {
				return true;
			}
		}
		return false;
	}
}