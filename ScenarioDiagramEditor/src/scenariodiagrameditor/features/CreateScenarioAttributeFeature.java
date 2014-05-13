package scenariodiagrameditor.features;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

import scenariodiagrameditor.util.Util;
import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;

public class CreateScenarioAttributeFeature extends AbstractCreateFeature implements ICreateFeature {

	private final String DEFAULT_TAG = "tag";
	private final String DEFAULT_VALUE = "value";
	private final String DEFAULT_TYPE = "STRING";

	public CreateScenarioAttributeFeature(IFeatureProvider fp) {
		super(fp, "ScenarioAttribute", "Creates a new ScenarioAttribute");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		if (getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof ScenarioObject) {
			return true;
		}
		if (getBusinessObjectForPictogramElement(context.getTargetConnection()) instanceof ScenarioLink) {
			return true;
		}
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// new viewModel element
		ScenarioAttribute newScenarioAttribute = ScenariodiagramFactory.eINSTANCE.createScenarioAttribute();
		newScenarioAttribute.setValue(DEFAULT_VALUE);

		// scenario object
		updateModels(context, newScenarioAttribute);

		addGraphicalRepresentation(context, newScenarioAttribute);

		return new Object[] { newScenarioAttribute };
	}

	private void updateModels(ICreateContext context, ScenarioAttribute newScenarioAttribute) {
		if (getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof ScenarioObject) {
			ScenarioObject object = (ScenarioObject) getBusinessObjectForPictogramElement(context.getTargetContainer());

			// viewModel
			object.getAttributes().add(newScenarioAttribute);

			// genModel
			EStructuralFeature existingAttribute = object.getObjectType().getEStructuralFeature(DEFAULT_TAG);
			if (existingAttribute == null) {
				// new genModel element
				EAttribute newAttributeType = EcoreFactory.eINSTANCE.createEAttribute();
				newScenarioAttribute.setAttributeType(newAttributeType);
				newAttributeType.setName(DEFAULT_TAG);
				newAttributeType.setDefaultValueLiteral(DEFAULT_TAG);
				newAttributeType.setEType(Util.getType(DEFAULT_TYPE));
				object.getObjectType().getEStructuralFeatures().add(newAttributeType); 
			} else {
				newScenarioAttribute.setAttributeType((EAttribute) existingAttribute);
			}
		}
		// scenario link
		else {
			ScenarioLink link = (ScenarioLink) getBusinessObjectForPictogramElement(context.getTargetConnection());

			// viewModel
			link.getAttributes().add(newScenarioAttribute);

			// genModel
			EStructuralFeature existingAttribute = link.getSource().getObjectType().getEStructuralFeature(DEFAULT_TAG);
			if (existingAttribute == null) {
				// new genModel element
				EAttribute newAttributeType = EcoreFactory.eINSTANCE.createEAttribute();
				newScenarioAttribute.setAttributeType(newAttributeType);
				newAttributeType.setName(DEFAULT_TAG);
				newAttributeType.setDefaultValueLiteral(DEFAULT_TAG);
				newAttributeType.setEType(Util.getType(DEFAULT_TYPE));
				link.getSource().getObjectType().getEStructuralFeatures().add(newAttributeType);
				newScenarioAttribute.setAttributeType((EAttribute) existingAttribute);
			}
		}
	}
}
