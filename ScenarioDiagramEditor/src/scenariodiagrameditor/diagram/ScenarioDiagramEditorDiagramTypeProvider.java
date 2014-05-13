package scenariodiagrameditor.diagram;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class ScenarioDiagramEditorDiagramTypeProvider extends AbstractDiagramTypeProvider {

	 private IToolBehaviorProvider[] toolBehaviorProviders;
	
	public ScenarioDiagramEditorDiagramTypeProvider() {
		super();
		setFeatureProvider(new ScenarioDiagramEditorFeatureProvider(this));
	}
	
	@Override
    public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
        if (toolBehaviorProviders == null) {
            toolBehaviorProviders =
                new IToolBehaviorProvider[] { new SDToolBehaviorProvider(
                    this) };
        }
        return toolBehaviorProviders;
    }
}
