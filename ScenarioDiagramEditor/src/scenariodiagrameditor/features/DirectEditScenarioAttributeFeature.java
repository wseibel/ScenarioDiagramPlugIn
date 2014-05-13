package scenariodiagrameditor.features;

import java.util.HashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import scenariodiagrameditor.util.Util;
import scenariodiagrameditor.util.Util.ScenarioDiagramTypes;
import de.uks.se.scenariodiagram.ScenarioAttribute;

public class DirectEditScenarioAttributeFeature extends AbstractDirectEditingFeature {
	private final String DEFAULT_TYPE = "STRING";

	public DirectEditScenarioAttributeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ScenarioAttribute scenarioAttribute = (ScenarioAttribute) getBusinessObjectForPictogramElement(pe);
		String instanceTypeName = scenarioAttribute.getAttributeType().getEType().getInstanceTypeName();
		String initialValue = scenarioAttribute.getAttributeType().getName() + "=" + scenarioAttribute.getValue() + ":" + instanceTypeName;

		return initialValue;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object object = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		if (object instanceof ScenarioAttribute && ga instanceof Text) {
			return true;
		}

		return false;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		final PictogramElement pictogramElement = context.getPictogramElement();
		final Object businessElement = getBusinessObjectForPictogramElement(pictogramElement);
		if (businessElement instanceof ScenarioAttribute) {
			ScenarioAttribute scenarioAttribute = (ScenarioAttribute) businessElement;

			if (scenarioAttribute.getObject() != null && scenarioAttribute.getObject().getObjectType() != null
					|| scenarioAttribute.getLink() != null
					&& scenarioAttribute.getLink().getSource().getObjectType() != null) {

				updateModel(pictogramElement, value);
				updatePictogramElement(pictogramElement);
				getDiagramBehavior().refreshContent();
			}
		}
	}

	private void updateModel(PictogramElement pictogramElement, String newText) {
		// retrieve name from pictogram element
		String pictogramValue = newText, pictogramTag = null, pictogramType = null;
		if (pictogramValue.contains("=")) {
			pictogramTag = pictogramValue.split("=")[0];
			pictogramValue = pictogramValue.split("=")[1];
		}
		if (pictogramValue.contains(":")) {
			pictogramType = pictogramValue.split(":")[1];
			pictogramValue = pictogramValue.split(":")[0];
		}

		// change business model entries
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		ScenarioAttribute scenarioAttribute = null;
		if (bo instanceof ScenarioAttribute) {
			scenarioAttribute = (ScenarioAttribute) bo;
			scenarioAttribute.setAttributeType(null);
		} else {
			return;
		}

		if (pictogramType == null || pictogramType == "") {
			pictogramType = DEFAULT_TYPE;
		}
		EAttribute newAttributeType = EcoreFactory.eINSTANCE.createEAttribute();
		newAttributeType.setName(pictogramTag);
		newAttributeType.setDefaultValueLiteral(pictogramTag);
		newAttributeType.setEType(Util.getType(pictogramType.toUpperCase()));

		if (scenarioAttribute.getObject() != null) {
			EClass objectType = scenarioAttribute.getObject().getObjectType();
			objectType.getEStructuralFeatures().add(newAttributeType);
		} else {
			EClass objectType = scenarioAttribute.getLink().getSource().getObjectType();
			objectType.getEStructuralFeatures().add(newAttributeType);
		}
		scenarioAttribute.setValue(pictogramValue);
		scenarioAttribute.setAttributeType(newAttributeType);
	}

	@Override
	public boolean isAutoCompletionEnabled() {
		return true;
	}

	@Override
	public boolean isCompletionAvailable() {
		return true;
	}

	@Override
	public String[] getValueProposals(String value, int caretPos, IDirectEditingContext context) {

		String typeName = "";

		if (!value.contains(":")) {
			return new String[0];
		}

		String[] tagValueType = value.split(":");
		if (tagValueType.length > 1) {
			typeName = tagValueType[1];
		}

		HashSet<String> proposals = new HashSet<String>();
		for (ScenarioDiagramTypes tmpType : Util.ScenarioDiagramTypes.values()) {
			if (tagValueType.length > 0 && (typeName.equals("") || tmpType.toString().startsWith(typeName.toUpperCase()))) {
				proposals.add(tagValueType[0] + ":" + tmpType.toString());
			}
		}

		String[] result = proposals.toArray(new String[proposals.size()]);

		return result;
	}
}
