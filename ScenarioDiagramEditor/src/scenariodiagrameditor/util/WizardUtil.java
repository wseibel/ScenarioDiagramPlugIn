package scenariodiagrameditor.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.examples.common.Messages;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.uks.se.scenariodiagram.ScenarioModel;
import de.uks.se.scenariodiagram.ScenariodiagramFactory;
import scenariodiagrameditor.Activator;

public class WizardUtil {
	private static final String SCENARIO_DIAGRAM_TYPE_ID = "ScenarioDiagramEditor";

	public static Diagram newDiagram(IProject project, String name, String packageName) {

		if (project == null || !project.isAccessible()) {
			String error = "project == null || !project.isAccessible()";
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, error);
			return null;
		}

		final Diagram diagram = Graphiti.getPeCreateService().createDiagram(SCENARIO_DIAGRAM_TYPE_ID, name, true);
		IFolder diagramFolder = project.getFolder("src/diagrams/");

		String editorId = DiagramEditor.DIAGRAM_EDITOR_ID;
		String editorExtension = "diagram";
		String diagramTypeProviderId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(SCENARIO_DIAGRAM_TYPE_ID);
		String namingConventionId = diagramTypeProviderId + ".editor";
		IEditorDescriptor specificEditor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(namingConventionId);

		if (specificEditor != null) {
			editorId = namingConventionId;
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint("org.explipse.ui.esitors");
			IExtension[] extensions = extensionPoint.getExtensions();
			for (IExtension ext : extensions) {
				IConfigurationElement[] configurationElements = ext.getConfigurationElements();
				for (IConfigurationElement ce : configurationElements) {
					String id = ce.getAttribute("id");
					if (editorId.equals(id)) {
						String fileExt = ce.getAttribute("extension");
						if (fileExt != null) {
							editorExtension = fileExt;
							break;
						}
					}
				}
			}
		}

		final URI diagramURI = URI.createPlatformResourceURI(diagramFolder.getFile(name + ".sd").getFullPath().toString(), true);
		final URI scenarioModelURI = URI.createPlatformResourceURI(diagramFolder.getFile(name + ".sm").getFullPath().toString(), true);
		final URI classModelURI = URI.createPlatformResourceURI(diagramFolder.getFile(name + ".cm").getFullPath().toString(), true);
		
		// diagram model
		FileService.createEmfFileForDiagram(diagramURI, diagram);
		ResourceSet resourceSet = diagram.eResource().getResourceSet();
		Resource diagramResource = resourceSet.getResource(diagramURI, true);
		diagramResource.setTrackingModification(true);
		diagramResource.getContents().add(diagram);
		diagram.setLink(PictogramsFactory.eINSTANCE.createPictogramLink());
		
		// scenario model
		final ScenarioModel scenarioModel = ScenariodiagramFactory.eINSTANCE.createScenarioModel();
		Resource scenarioModelResource = resourceSet.createResource(scenarioModelURI);
		scenarioModelResource.setTrackingModification(true);
		scenarioModelResource.getContents().add(scenarioModel);
		diagram.getLink().getBusinessObjects().add(scenarioModel);
		
		// class model
		final EPackage rootPackage = EcoreFactory.eINSTANCE.createEPackage();
		rootPackage.setName(packageName);
		Resource classModelResource = resourceSet.createResource(classModelURI);
		classModelResource.setTrackingModification(true);
		classModelResource.getContents().add(rootPackage);
		diagram.getLink().getBusinessObjects().add(rootPackage);
		
				
		try{
			scenarioModelResource.save(Collections.<Resource, Map<?,?>> emptyMap());
			diagramResource.save(Collections.<Resource, Map<?,?>> emptyMap());
			classModelResource.save(Collections.<Resource, Map<?,?>> emptyMap());
		} catch(IOException e){
//			e.printStackTrace();
		}
		
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
		DiagramEditorInput editorInput = new DiagramEditorInput(EcoreUtil.getURI(diagram), providerId);
		try{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput,editorId);
		} catch(PartInitException e){
			String error = e.getLocalizedMessage();
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, error, e);
			ErrorDialog.openError(null,e.getLocalizedMessage(),null, status);
		}
		
		return diagram;
	}	
}
