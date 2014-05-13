package scenariodiagrameditor.features;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import scenariodiagrameditor.util.Util;

public class QuickfixAttributeTypeFeature extends AbstractCustomFeature {

	public QuickfixAttributeTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		final EObject businessElement = context.getPictogramElements()[0].getLink().getBusinessObjects().get(0);
		if (businessElement instanceof ScenarioAttribute && Util.isAttributeTypeUndefined(businessElement)) {
			ret = true;
		}
		return ret;
	}

	@Override
	public String getName() {
		return "Quickfix attribute type";
	}

	@Override
	public String getDescription() {
		return "Edit attribute type";
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
		String[] types = Util.getTypes();

		MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Edit Attribute Type", null, "Edit the attribute type", 0, types, 0);
		int selectionID = dialog.open();
		if (selectionID == SWT.DEFAULT) {
			return;
		}

		final PictogramElement pictogramElement = context.getPictogramElements()[0];
		final Object businessElement = getBusinessObjectForPictogramElement(pictogramElement);
		if (businessElement instanceof ScenarioAttribute) {
			ScenarioAttribute scenarioAttribute = (ScenarioAttribute) businessElement;
			if (scenarioAttribute.getObject() != null && scenarioAttribute.getObject().getObjectType() != null
					|| scenarioAttribute.getLink() != null
					&& scenarioAttribute.getLink().getSource().getObjectType() != null) {
				final String selectedType = types[selectionID];

				updateModel(pictogramElement, selectedType);
				updatePictogramElement(pictogramElement);
				getDiagramBehavior().refreshContent();
			}
		}
	}

	private void updateModel(PictogramElement pictogramElement, String selectedType) {
		// retrieve name from pictogram element
		String pictogramValue = null, pictogramTag = null;
		if (pictogramElement instanceof Shape) {
			Shape shape = (Shape) pictogramElement;
			if (shape.getGraphicsAlgorithm() instanceof Text) {
				Text text = (Text) shape.getGraphicsAlgorithm();
				pictogramValue = text.getValue();
			}
		} else {
			return;
		}
		if (pictogramValue.contains("=")) {
			pictogramTag = pictogramValue.split("=")[0];
			pictogramValue = pictogramValue.split("=")[1];
		}
		if (pictogramValue.contains(":")) {
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

		if (selectedType == null || selectedType == "") {
			selectedType.equals("STRING");
		}
		EAttribute newAttributeType = EcoreFactory.eINSTANCE.createEAttribute();
		newAttributeType.setName(pictogramTag);
		newAttributeType.setDefaultValueLiteral(pictogramTag);
		newAttributeType.setEType(Util.getType(selectedType.toUpperCase()));

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
}
