package scenariodiagrameditor.features;

import java.util.HashSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

/**
 * Direct edit ScenarioLink name.
 */
public class DirectEditScenarioLinkConnectionFeature extends AbstractDirectEditingFeature {
	private final String DEFAULT_TAG = "scenarioLinkName";
	private String nameToReplace = "";

	/**
	 * Constructor
	 * 
	 * @param fp
	 *                FeatureProvider
	 */
	public DirectEditScenarioLinkConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		nameToReplace = "";

		PictogramElement pe = context.getPictogramElement();
		ScenarioLink scenarioLink = (ScenarioLink) getBusinessObjectForPictogramElement(pe);
		String value = "";

		value = scenarioLink.getName();

		// save name to replace for setValueMethod
		nameToReplace = value;

		return value;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object object = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		if (object instanceof ScenarioLink && ga instanceof Text) {
			return true;
		}

		return false;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ScenarioLink scenarioLink = (ScenarioLink) getBusinessObjectForPictogramElement(pe);

		scenarioLink.setName(value);
		scenarioLink.getIncomingReference().setName(value);
		scenarioLink.getIncomingReference().setDefaultValueLiteral(value);
		scenarioLink.getOutgoingReference().setName(value);
		scenarioLink.getOutgoingReference().setDefaultValueLiteral(value);
		ScenarioObject source = scenarioLink.getSource();
//		EStructuralFeature existingAttribute = source.getObjectType().getEStructuralFeature(value);
//		if (existingAttribute == null) {
//			// new genModel element
//			EAttribute newAttributeType = EcoreFactory.eINSTANCE.createEAttribute();
//			newAttributeType.setName(value);
//			newAttributeType.setDefaultValueLiteral(value);
//			source.getObjectType().getEStructuralFeatures().add(newAttributeType);
//		}

		// genmodel
		createEcoreStructure(value, scenarioLink);

		Text text = (Text) context.getGraphicsAlgorithm();
		text.setValue(value);

		updatePictogramElement(((Shape) pe).getContainer());

		getDiagramBehavior().refreshContent();
	}

	private void createEcoreStructure(String value, ScenarioLink scenarioLink) {
		
		EList<EReference> eReferences = scenarioLink.getSource().getObjectType().getEReferences();
		for (EReference tmpReference : eReferences) {
			if (scenarioLink.getTarget().getObjectType() == tmpReference.getEType()
					&& value.equals(tmpReference.getDefaultValueLiteral())) {
				return;
			}
		}
		for (EReference tmpReference : eReferences) {
			if (scenarioLink.getTarget().getObjectType() == tmpReference.getEType()
					&& nameToReplace.equals(tmpReference.getDefaultValueLiteral())) {
				tmpReference.setDefaultValueLiteral(value);
				tmpReference.setName(value);
				tmpReference.getEOpposite().setDefaultValueLiteral(value);
				tmpReference.getEOpposite().setName(value);
				break;
			}
		}
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
		PictogramElement pe = context.getPictogramElement();
		ScenarioLink scenarioLink = (ScenarioLink) getBusinessObjectForPictogramElement(pe);

		EClass source = (EClass) scenarioLink.getOutgoingReference().getEType();
		EClass target = (EClass) scenarioLink.getIncomingReference().getEType();
		EList<EReference> sourceReferences = source.getEReferences();
		EList<EReference> targetReferences = target.getEReferences();

		// container without duplicates
		HashSet<String> proposals = new HashSet<String>();

		// add sourceReference names
		for (EReference tmpFeature : sourceReferences) {
			String linkType = ((EReference) tmpFeature).getDefaultValueLiteral();
			if (linkType.startsWith(value)) {
				proposals.add(linkType);
			}
		}

		// add targetReference names
		for (EReference tmpFeature : targetReferences) {
			String linkType = ((EReference) tmpFeature).getDefaultValueLiteral();
			if (linkType.startsWith(value)) {
				proposals.add(linkType);
			}
		}

		String[] result = proposals.toArray(new String[proposals.size()]);

		return result;
	}
}
