package scenariodiagrameditor.features;

import java.util.HashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
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

public class DirectEditScenarioObjectFeature extends AbstractDirectEditingFeature {
	private final String DEFAULT_OBJECT_TYPE = "DefaultType";

	public DirectEditScenarioObjectFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ScenarioObject scenarioObject = (ScenarioObject) getBusinessObjectForPictogramElement(pe);
		String value = scenarioObject.getName();

		return value;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object object = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		if (object instanceof ScenarioObject && ga instanceof Text) {
			return true;
		}

		return false;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ScenarioObject scenarioObject = (ScenarioObject) getBusinessObjectForPictogramElement(pe);

		if (scenarioObject != null) {
			updateModel(value, scenarioObject);

			Text text = (Text) context.getGraphicsAlgorithm();
			text.setValue(value);

			updatePictogramElement(((Shape) pe).getContainer());

			getDiagramBehavior().refreshContent();
		}
	}

	private void updateModel(String value, ScenarioObject scenarioObject) {
		scenarioObject.setName(value);

		// should always have an old type
		// EPackage classModel = null;
		// if (scenarioObject.getObjectType() != null) {
		// for (EObject object : getDiagram().getLink().getBusinessObjects()) {
		// if (object instanceof EPackage) {
		// classModel = (EPackage) object;
		// }
		// }
		// String oldInstanceTypeName = scenarioObject.getObjectType().getInstanceTypeName();
		// // always set type to null
		// scenarioObject.setObjectType(null);

		// is type name defined
		if (value.contains(":") && value.split(":").length > 1) {
			String typeName = value.split(":")[1];

			// for (Object tmpType : classModel.getEClassifiers()) {
			// if (tmpType instanceof EClass) {
			// if (((EClass) tmpType).getInstanceTypeName().equals(typeName)
			// || ((EClass) tmpType).getInstanceTypeName().equals(oldInstanceTypeName)) {
			// scenarioObject.setObjectType((EClass) tmpType);
			// break;
			// }
			// }
			// }
			// // new type
			// if (scenarioObject.getObjectType() == null) {
			// EClass newScenarioObjectType = EcoreFactory.eINSTANCE.createEClass();
			// newScenarioObjectType.setInstanceTypeName(typeName);
			// newScenarioObjectType.setName(typeName);
			// scenarioObject.setObjectType(newScenarioObjectType);
			// classModel.getEClassifiers().add(newScenarioObjectType);
			// }
			// if(scenarioObject.getObjectType().getInstanceTypeName().equals(oldInstanceTypeName)) {
			scenarioObject.getObjectType().setName(typeName);
		}
		// }
		// default object type
		else {
			// for (Object tmpType : classModel.getEClassifiers()) {
			// if (tmpType instanceof EClass) {
			// if (((EClass) tmpType).getInstanceTypeName().equals(DEFAULT_OBJECT_TYPE)) {
			// scenarioObject.setObjectType((EClass) tmpType);
			// }
			// }
			// }
			scenarioObject.getObjectType().setName(DEFAULT_OBJECT_TYPE);
		}
		// }
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

		// no type expected
		if (!value.contains(":")) {
			return new String[0];
		}

		// type expected
		String[] tagAndValue = value.split(":");
		if (tagAndValue.length > 1) {
			// given part of type name
			typeName = tagAndValue[1];
		}

		HashSet<String> proposals = new HashSet<String>();
		EPackage classModel = null;
		for (EObject object : getDiagram().getLink().getBusinessObjects()) {
			if (object instanceof EPackage) {
				classModel = (EPackage) object;
			}
		}

		for (Object tmpType : classModel.getEClassifiers()) {
			if (tmpType instanceof EClass) {
				String name = ((EClass) tmpType).getName();
				// is there an objectName and (show all type names or matching type names)
				if (!name.equals(DEFAULT_OBJECT_TYPE)) {
					if (tagAndValue.length > 0 && (typeName.equals("") || name.startsWith(typeName))) {
						proposals.add(tagAndValue[0] + ":" + name);
					}
				}
			}
		}

		String[] result = proposals.toArray(new String[proposals.size()]);

		return result;
	}
}
