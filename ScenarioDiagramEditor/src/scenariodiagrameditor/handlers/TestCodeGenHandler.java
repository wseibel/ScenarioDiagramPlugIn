package scenariodiagrameditor.handlers;

import java.util.HashMap;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.mwe2.language.ui.internal.Mwe2Activator;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.google.inject.Injector;

public class TestCodeGenHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public TestCodeGenHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(window.getShell(), new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.open();
		if (dialog.getFirstResult() instanceof IFile) {
			IFile cmFile = (IFile) dialog.getFirstResult();
			String fileURI = "./" + cmFile.getProject().getName() + "/" + cmFile.getProjectRelativePath().toString();
			/**
			 * Obtain the Eclipse-based activator
			 */
			Mwe2Activator mwe2Activator = Mwe2Activator.getInstance();
			assert mwe2Activator != null;

			/**
			 * Obtain the injector
			 */
			Injector injector = mwe2Activator.getInjector("org.eclipse.emf.mwe2.language.Mwe2");
			assert injector != null;

			/**
			 * Have the injector inject the runner
			 */
			Mwe2Runner mwe2Runner = injector.getInstance(Mwe2Runner.class);
//			URI moduleUri = URI.createPlatformPluginURI(pluginPathToModule, false);

			mwe2Runner.run("testCodegen", new HashMap<String, String>());
		}
		return null;
	}

}
