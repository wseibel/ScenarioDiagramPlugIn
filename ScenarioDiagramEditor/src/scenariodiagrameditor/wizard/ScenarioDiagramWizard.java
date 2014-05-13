package scenariodiagrameditor.wizard;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import scenariodiagrameditor.util.WizardUtil;

public class ScenarioDiagramWizard extends Wizard implements INewWizard {
	private static final String PAGE_NAME_SCENARIO_DIAGRAM = "New Scenario Diagram";
	ScenarioDiagramWizardPageOne one;
	ScenarioDiagramWizardPageTwo two;

	private Diagram diagram;

	protected IStructuredSelection selection;
	protected IWorkbench workbench;

	public ScenarioDiagramWizard() {
		setWindowTitle(PAGE_NAME_SCENARIO_DIAGRAM);
	}

	@Override
	public boolean performFinish() {;
		final String packageName = one.getText1();
		final String fileName = two.getFileName();
		
		Object firstElement = selection.getFirstElement();
		IProject project = null;
		if(firstElement instanceof IFile){
			IFile file = (IFile) firstElement;
			project = file.getProject();
		} else if (firstElement instanceof IProject){
			project = (IProject) firstElement;
		} else if(firstElement instanceof IFolder){
			project = ((IFolder) firstElement).getProject();
		}else if(firstElement instanceof IJavaProject){
			project = ((IJavaProject) firstElement).getProject();
		}else if (firstElement instanceof IPackageFragmentRoot){
			IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot) firstElement;
			
			if(fragmentRoot.getParent() instanceof IJavaProject){
				project = ((IJavaProject) fragmentRoot.getParent()).getProject();
			}
		}
		this.diagram = WizardUtil.newDiagram(project, fileName, packageName);
		
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		return two;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	@Override
	public void addPages() {
		super.addPages();
		one = new ScenarioDiagramWizardPageOne(PAGE_NAME_SCENARIO_DIAGRAM, selection);
		two = new ScenarioDiagramWizardPageTwo(PAGE_NAME_SCENARIO_DIAGRAM, selection);
		addPage(one);
		addPage(two);
	}

}
