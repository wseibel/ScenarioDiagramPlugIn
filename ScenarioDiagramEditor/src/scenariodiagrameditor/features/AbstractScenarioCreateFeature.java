package scenariodiagrameditor.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import de.uks.se.scenariodiagram.ScenarioModel;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;

public abstract class AbstractScenarioCreateFeature extends AbstractCreateFeature {

	public AbstractScenarioCreateFeature(IFeatureProvider fp, String name, String description) {
		super(fp, name, description);
	}

	public ScenarioModel getOrCreateScenarioModelFromDiagram(final Diagram diag) {
		Object obj = getBusinessObjectForPictogramElement(diag);
		if (obj == null || !(obj instanceof ScenarioModel)) {
			ScenarioModel scenarioModel = null;
			for (EObject object : diag.getLink().getBusinessObjects()) {
        			if (object instanceof ScenarioModel) {
        				scenarioModel = (ScenarioModel) object;
        			}
        		}
			if(scenarioModel == null){
				scenarioModel = ScenariodiagramFactory.eINSTANCE.createScenarioModel();
				diag.eResource().getContents().add(scenarioModel);
				link(diag, scenarioModel);
			}
			return scenarioModel;
		}
		return (ScenarioModel) obj;
	}

	public EPackage getOrCreateClassModelFromDiagram(final Diagram diag) {
		Object obj = getBusinessObjectForPictogramElement(diag);
		if (obj instanceof ScenarioModel) {
			EPackage classModel = null;
			for (EObject object : diag.getLink().getBusinessObjects()) {
				if (object instanceof EPackage) {
					classModel = (EPackage) object;
				}
			}
			if (classModel == null) {
				classModel = EcoreFactory.eINSTANCE.createEPackage();
				diag.eResource().getContents().add(classModel);
				link(diag, classModel);
			}
			return classModel;
		}
		return null;
	}
}
