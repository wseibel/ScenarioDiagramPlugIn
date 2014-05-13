package scenariodiagrameditor.features;

import java.util.HashSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import scenariodiagrameditor.util.Util;
import scenariodiagrameditor.util.Util.ScenarioDiagramTypes;
import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class QuickfixLinkTypeFeature extends AbstractCustomFeature {

	public QuickfixLinkTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		final Object businessElement = getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);
		if (businessElement instanceof ScenarioObject) {
			ScenarioObject scenarioObject = ((ScenarioObject) businessElement);
			if (!scenarioObject.getOutgoingLinks().isEmpty()) {
				for (ScenarioLink scenarioLink : scenarioObject.getOutgoingLinks()) {
					if (Util.isLinkNameDefault(scenarioLink)) {
						ret = true;
					}
				}
			}
		}
		return ret;
	}

	@Override
	public String getName() {
		return "Quickfix link type";
	}

	@Override
	public String getDescription() {
		return "Edit link type";
	}

	@Override
	public String getImageId() {
		return IPlatformImageConstants.IMG_EDIT_COLLAPSE;
	}

	@Override
	public boolean isAvailable(IContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		final PictogramElement pictogramElement = context.getPictogramElements()[0];
		final Object businessElement = getBusinessObjectForPictogramElement(pictogramElement);

		// container without duplicates
		HashSet<String> proposals = new HashSet<String>();
		HashSet<ScenarioLink> links = new HashSet<ScenarioLink>();
		if (businessElement instanceof ScenarioObject) {
			ScenarioObject object = ((ScenarioObject) businessElement);
			// Search every ScenarioObject in the diagram
			for (ScenarioObject scenarioObject : object.getParentModel().getContainedScenarioObjects()) {
				if (!scenarioObject.getOutgoingLinks().isEmpty()) {
					for (ScenarioLink link : scenarioObject.getOutgoingLinks()) {
						if (link != null) {
							links.add(link);
						}
					}
				} else {
					continue;
				}

				// Search every link of each ScenarioObject in the diagram
				for (ScenarioLink scenarioLink : links) {
					EClass source = (EClass) scenarioLink.getOutgoingReference().getEType();
					EClass target = (EClass) scenarioLink.getIncomingReference().getEType();
					EList<EReference> sourceReferences = source.getEReferences();
					EList<EReference> targetReferences = target.getEReferences();

					// add sourceReference names
					for (EReference tmpFeature : sourceReferences) {
						String linkType = ((EReference) tmpFeature).getDefaultValueLiteral();
						proposals.add(linkType);
					}

					// add targetReference names
					for (EReference tmpFeature : targetReferences) {
						String linkType = ((EReference) tmpFeature).getDefaultValueLiteral();
						proposals.add(linkType);
					}
				}
			}

			String[] result = proposals.toArray(new String[proposals.size()]);
			MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Edit Link Type", null, "Edit the link type", 0, result, 0);
			int selectionID = dialog.open();
			if (selectionID == SWT.DEFAULT) {
				return;
			}
			final String selectedValue = result[selectionID];

			updateModel(pictogramElement, selectedValue);
			updatePictogramElement(pictogramElement);
			getDiagramBehavior().refreshContent();
		}
	}

	private void updateModel(PictogramElement pictogramElement, String selectedValue) {
		final Object businessElement = getBusinessObjectForPictogramElement(pictogramElement);

		// container without duplicates
		if (businessElement instanceof ScenarioObject) {
			ScenarioObject scenarioObject = ((ScenarioObject) businessElement);
			ScenarioLink scenarioLink = null;
			for (ScenarioLink link : scenarioObject.getOutgoingLinks()) {
				if (!scenarioObject.getOutgoingLinks().isEmpty()) {
					if (Util.isLinkNameDefault(scenarioLink)) {
						scenarioLink = link;
					}
				}
			}

			// viewmodel
			scenarioLink.setName(selectedValue);
			scenarioLink.getIncomingReference().setName(selectedValue);
			scenarioLink.getIncomingReference().setDefaultValueLiteral(selectedValue);
			scenarioLink.getOutgoingReference().setName(selectedValue);
			scenarioLink.getOutgoingReference().setDefaultValueLiteral(selectedValue);

			// genmodel
			createEcoreStructure(selectedValue, scenarioLink);
		}
	}

	private void createEcoreStructure(String value, ScenarioLink scenarioLink) {
		EList<EReference> eReferences = scenarioLink.getSource().getObjectType().getEReferences();
		for (EReference tmpReference : eReferences) {
			if (scenarioLink.getTarget().getObjectType() == tmpReference.getEType()) {
				tmpReference.setDefaultValueLiteral(value);
				tmpReference.setName(value);
				tmpReference.getEOpposite().setDefaultValueLiteral(value);
				tmpReference.getEOpposite().setName(value);
				break;
			}
		}
	}
}
