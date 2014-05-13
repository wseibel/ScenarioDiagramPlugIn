package scenariodiagrameditor.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CodeGenHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public CodeGenHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.open();

		if (dialog.getFirstResult() instanceof IFile) {
			IFile cmFile = (IFile) dialog.getFirstResult();

			EcoreFactory.eINSTANCE.eClass();

			// Register the XMI resource factory for extension

			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
			Map<String, Object> m = reg.getExtensionToFactoryMap();
			m.put("cm", new XMIResourceFactoryImpl());

			// Obtain a new resource set
			ResourceSet resSet = new ResourceSetImpl();

			// Get the resource
			Resource resource = resSet.getResource(URI.createURI(cmFile.getLocationURI().toString()), true);
			// Get the first model element and cast it to the right type, in my
			// example everything is hierarchical included in this first node
			EPackage rootPackage = (EPackage) resource.getContents().get(0);

			{
				// try to build the genmodel
				String fileURI = "./" + cmFile.getProject().getName() + "/" + cmFile.getProjectRelativePath().toString();
				URI genModelURI = URI.createFileURI(fileURI);

				resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
						.put("genmodel", new XMLResourceFactoryImpl());

				Resource genModelResource = resSet.createResource(genModelURI);

				GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();

				genModelResource.getContents().add(genModel);

				resSet.getResources().add(genModelResource);

				genModel.setModelDirectory("./" + cmFile.getProject().getName() + "/src-gen/");
				genModel.getForeignModel().add(fileURI);
				genModel.initialize(Collections.singleton(rootPackage));

				GenPackage genPackage = genModel.getGenPackages().get(0);

				genModel.setModelName(genModelURI.trimFileExtension().lastSegment());

				genPackage.setPrefix(rootPackage.getName() + "genmodel");
				genPackage.setBasePackage(rootPackage.getName() + "");

				try {
					genModelResource.save(Collections.EMPTY_MAP);
				} catch (IOException e) {
//					e.printStackTrace();
				}

				IStatus status = genModel.validate();
				if (!status.isOK()) {
					System.out.println(status);
				}
				// now generate Java
				CodeGenEcorePlugin codeGenPlugin = CodeGenEcorePlugin.INSTANCE;

				genModel.setCanGenerate(true);

				org.eclipse.emf.codegen.ecore.generator.Generator gen = new org.eclipse.emf.codegen.ecore.generator.Generator();

				gen.setInput(genModel);

				boolean flag = gen.canGenerate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE);

				if (!flag) {
					System.out.println("Cannot generate");
				}

				BasicMonitor basicMonitor = new BasicMonitor();
				gen.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, basicMonitor);
			}
		}
		return null;
	}
}
