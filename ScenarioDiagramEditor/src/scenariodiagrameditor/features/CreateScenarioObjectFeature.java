package scenariodiagrameditor.features;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import de.uks.se.scenariodiagram.ScenarioModel;
import de.uks.se.scenariodiagram.ScenarioObject;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;

public class CreateScenarioObjectFeature extends AbstractScenarioCreateFeature implements ICreateFeature {

	private final String DEFAULT_TAG = "scenarioObjectText";
	private final String DEFAULT_VALUE = "ScenarioObject";
	private final String DEFAULT_TYPE = "DefaultType";

	public CreateScenarioObjectFeature(IFeatureProvider fp) {
		super(fp, "ScenarioObject", "Creates a new ScenarioObject");
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		ContainerShape targetContainer = context.getTargetContainer();

		if (targetContainer instanceof Diagram) {
			return true;
		} 
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		ScenarioObject newScenarioObject = ScenariodiagramFactory.eINSTANCE.createScenarioObject();
		newScenarioObject.setName(DEFAULT_VALUE);
		
		createEcoreStructure(newScenarioObject);
		ScenarioModel sm = getOrCreateScenarioModelFromDiagram(getDiagram());
		sm.getContainedScenarioObjects().add(newScenarioObject);

		addGraphicalRepresentation(context, newScenarioObject);

		return new Object[] { newScenarioObject };
	}

	private void createEcoreStructure(ScenarioObject newScenarioObject) {
		// find or create DefaultType
		EClass defaultScenarioObjectType = null;
		EPackage classModel = getOrCreateClassModelFromDiagram(getDiagram());
//		for (Object tmpType : classModel.getEClassifiers()) {
//			if (tmpType instanceof EClass) {
//				// existing?
//				if (((EClass) tmpType).getInstanceTypeName().equals(DEFAULT_TYPE)) {
//					defaultScenarioObjectType = (EClass) tmpType;
//					break;
//				}
//			}
//		}		

		// new
		if (defaultScenarioObjectType == null) {
			defaultScenarioObjectType = EcoreFactory.eINSTANCE.createEClass();
			defaultScenarioObjectType.setName(DEFAULT_TYPE);
			newScenarioObject.setObjectType(defaultScenarioObjectType);
		}
		newScenarioObject.setObjectType(defaultScenarioObjectType);
		classModel.getEClassifiers().add(defaultScenarioObjectType);
	}
}
