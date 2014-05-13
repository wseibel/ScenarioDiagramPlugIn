package scenariodiagrameditor.diagram;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import scenariodiagrameditor.features.AddScenarioAttributeFeature;
import scenariodiagrameditor.features.AddScenarioLinkConnectionFeature;
import scenariodiagrameditor.features.AddScenarioObjectFeature;
import scenariodiagrameditor.features.CreateScenarioAttributeFeature;
import scenariodiagrameditor.features.CreateScenarioLinkConnectionFeature;
import scenariodiagrameditor.features.CreateScenarioObjectFeature;
import scenariodiagrameditor.features.DirectEditScenarioAttributeFeature;
import scenariodiagrameditor.features.DirectEditScenarioLinkConnectionFeature;
import scenariodiagrameditor.features.DirectEditScenarioObjectFeature;
import scenariodiagrameditor.features.LayoutScenarioAttributeFeature;
import scenariodiagrameditor.features.LayoutScenarioObjectFeature;
import scenariodiagrameditor.features.QuickfixAttributeTypeFeature;
import scenariodiagrameditor.features.QuickfixLinkTypeFeature;
import scenariodiagrameditor.features.QuickfixObjectTypeFeature;
import scenariodiagrameditor.features.UpdateScenarioAttributeFeature;
import scenariodiagrameditor.features.UpdateScenarioLinkFeature;
import scenariodiagrameditor.features.UpdateScenarioObjectFeature;
import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class ScenarioDiagramEditorFeatureProvider extends DefaultFeatureProvider {

	public ScenarioDiagramEditorFeatureProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public ICreateFeature[] getCreateFeatures() {
		return new ICreateFeature[] {new CreateScenarioObjectFeature(this), new CreateScenarioAttributeFeature(this)};
	}
	
	@Override
	public ICreateConnectionFeature[] getCreateConnectionFeatures() {
		return new ICreateConnectionFeature[] {new CreateScenarioLinkConnectionFeature(this)};
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) {
		
		//link
		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof ScenarioLink)
		{
			return new AddScenarioLinkConnectionFeature(this);
		}
		//object
		else if (context instanceof IAddContext && context.getNewObject() instanceof ScenarioObject)
		{
			return new AddScenarioObjectFeature(this);
		}
		//attribute
		else if (context instanceof IAddContext  && context.getNewObject() instanceof ScenarioAttribute)
		{
			return new AddScenarioAttributeFeature(this);
		}

		return super.getAddFeature(context);
	}
	
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context){
		PictogramElement pe = context.getPictogramElement();
	    Object bo = getBusinessObjectForPictogramElement(pe);
	    if (bo instanceof ScenarioObject) {
	        return new DirectEditScenarioObjectFeature(this);
	    }
	    if (bo instanceof ScenarioLink){
	    	return new DirectEditScenarioLinkConnectionFeature(this);
	    }
	    if (bo instanceof ScenarioAttribute){
	    	return new DirectEditScenarioAttributeFeature(this);
	    }
	    return super.getDirectEditingFeature(context);
	}
	
	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context){
		
		return new ICustomFeature[]{new QuickfixObjectTypeFeature(this), new QuickfixAttributeTypeFeature(this), new QuickfixLinkTypeFeature(this)};
	}
	
	@Override
	public ILayoutFeature getLayoutFeature(ILayoutContext context) {
		if (context.getPictogramElement() instanceof ContainerShape && getBusinessObjectForPictogramElement(context.getPictogramElement()) instanceof ScenarioObject) {
			return  new LayoutScenarioObjectFeature(this);
		}
		else if (context.getPictogramElement() instanceof ContainerShape && getBusinessObjectForPictogramElement(context.getPictogramElement()) instanceof ScenarioAttribute) {
			return  new LayoutScenarioAttributeFeature(this);
		}
	
		return super.getLayoutFeature(context);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof ScenarioObject) {
				return new UpdateScenarioObjectFeature(this);
			}
		} else if (pictogramElement instanceof ConnectionDecorator) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof ScenarioLink) {
				return new UpdateScenarioLinkFeature(this);
			}
		} else if (pictogramElement instanceof Shape) {
			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
			if (bo instanceof ScenarioAttribute) {
				return new UpdateScenarioAttributeFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}
}
