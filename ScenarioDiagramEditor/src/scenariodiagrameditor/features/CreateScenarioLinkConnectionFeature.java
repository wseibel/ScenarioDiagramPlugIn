package scenariodiagrameditor.features;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;

public class CreateScenarioLinkConnectionFeature extends AbstractCreateConnectionFeature implements ICreateConnectionFeature {
	private final String DEFAULT_VALUE = "has";

	public CreateScenarioLinkConnectionFeature(IFeatureProvider fp) {
		super(fp, "ScenarioLink", "Creates a new ScenarioLink between two ScenarioObjects");
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		return getBusinessObjectForPictogramElement(context.getSourcePictogramElement()) instanceof ScenarioObject;
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		PictogramElement sourcePictogramElement = context.getSourcePictogramElement();
		PictogramElement targetPictogramElement = context.getTargetPictogramElement();

		if (getBusinessObjectForPictogramElement(sourcePictogramElement) instanceof ScenarioObject
				&& getBusinessObjectForPictogramElement(targetPictogramElement) instanceof ScenarioObject) {
			return true;
		}

		return false;
	}

	@Override
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		ScenarioLink newScenarioLink = ScenariodiagramFactory.eINSTANCE.createScenarioLink();
		ScenarioObject source = (ScenarioObject) getBusinessObjectForPictogramElement(context.getSourcePictogramElement());
		ScenarioObject target = (ScenarioObject) getBusinessObjectForPictogramElement(context.getTargetPictogramElement());
		newScenarioLink.setSource(source);
		newScenarioLink.setTarget(target);
		newScenarioLink.setName(DEFAULT_VALUE);

		getDiagram().eResource().getContents().add(newScenarioLink);

		createEcoreStructure(newScenarioLink, source, target);

		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(newScenarioLink);

		PictogramElement newPictogramElement = getFeatureProvider().addIfPossible(addContext);

		if (newPictogramElement instanceof Connection) {
			newConnection = (Connection) newPictogramElement;
		}

		return newConnection;
	}

	private void createEcoreStructure(ScenarioLink newScenarioLink, ScenarioObject source, ScenarioObject target) {
		EReference outgoingRef = EcoreFactory.eINSTANCE.createEReference();
		EClass sourceObjectType = source.getObjectType();
		EReference incomingRef = EcoreFactory.eINSTANCE.createEReference();
		EClass targetObjectType = target.getObjectType();

		// outgoing
		outgoingRef.setDefaultValueLiteral(DEFAULT_VALUE);
		outgoingRef.setName(DEFAULT_VALUE);
		sourceObjectType.getEStructuralFeatures().add(outgoingRef);
		outgoingRef.setEType(targetObjectType);
		newScenarioLink.setOutgoingReference(outgoingRef);

		// opposites
		outgoingRef.setEOpposite(incomingRef);
		incomingRef.setEOpposite(outgoingRef);

		// incoming
		incomingRef.setDefaultValueLiteral(DEFAULT_VALUE);
		incomingRef.setName(DEFAULT_VALUE);
		targetObjectType.getEStructuralFeatures().add(incomingRef);
		incomingRef.setEType(sourceObjectType);
		newScenarioLink.setIncomingReference(incomingRef);
	}
}
