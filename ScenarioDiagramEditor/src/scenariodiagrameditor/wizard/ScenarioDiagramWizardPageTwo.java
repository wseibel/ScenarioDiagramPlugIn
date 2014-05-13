package scenariodiagrameditor.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class ScenarioDiagramWizardPageTwo extends WizardNewFileCreationPage {

	public ScenarioDiagramWizardPageTwo(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle(pageName);
		setDescription(pageName);
	}

}
