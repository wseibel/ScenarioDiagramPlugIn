package scenariodiagrameditor.features;

import java.util.HashSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import de.uks.se.scenariodiagram.ScenarioObject;
import scenariodiagrameditor.util.Util;

public class QuickfixObjectTypeFeature extends AbstractCustomFeature {
	private final String DEFAULT_OBJECT_TYPE = "DefaultType";

	public QuickfixObjectTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		final Object businessElement = getBusinessObjectForPictogramElement(context.getPictogramElements()[0]);
		if (businessElement instanceof ScenarioObject && Util.isObjectTypeUndefined(businessElement)) {
			ret = true;
		}
		return ret;
	}

	@Override
	public String getName() {
		return "Quickfix object type";
	}

	@Override
	public String getDescription() {

		return "Edit object type";
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
		HashSet<String> proposals = new HashSet<String>();
		if (businessElement instanceof ScenarioObject) {
			String value = ((ScenarioObject) businessElement).getName();

			String[] tagAndValue = value.split(":");

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
						if (tagAndValue.length > 0 && name != "") {
							proposals.add(tagAndValue[0] + ":" + name);
						}
					}
				}
			}
			String[] result = proposals.toArray(new String[proposals.size()]);

			MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Edit Object Type", null, "Edit the object type", 0, result, 0);
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
		if (businessElement instanceof ScenarioObject) {
			ScenarioObject scenarioObject = (ScenarioObject) businessElement;
			scenarioObject.setName(selectedValue);

			// should always have an old type
			// if (scenarioObject.getObjectType() != null) {
			// EPackage classModel = null;
			// for(EObject object : getDiagram().eResource().getContents()){
			// if(object instanceof EPackage){
			// classModel = (EPackage) object;
			// }
			// }
			//
			// // always set type to null
			// scenarioObject.setObjectType(null);

			// is type name defined
			if (selectedValue.contains(":") && selectedValue.split(":").length > 1) {
				String typeName = selectedValue.split(":")[1];

				scenarioObject.getObjectType().setName(typeName);

				// for (Object tmpType : classModel.getEClassifiers()) {
				// if (tmpType instanceof EClass) {
				// // existing?
				// if (((EClass) tmpType).getInstanceTypeName().equals(typeName)) {
				// scenarioObject.setObjectType((EClass) tmpType);
				// break;
				// }
				// }
				// }
				//
				// // new type
				// if (scenarioObject.getObjectType() == null) {
				// EClass newScenarioObjectType = EcoreFactory.eINSTANCE.createEClass();
				// newScenarioObjectType.setInstanceTypeName(typeName);
				// newScenarioObjectType.setName(typeName);
				// scenarioObject.setObjectType(newScenarioObjectType);
				// classModel.getEClassifiers().add(newScenarioObjectType);
				// }
			} else {
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
	}
}
