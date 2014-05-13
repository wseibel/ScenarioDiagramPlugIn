package scenariodiagrameditor.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import scenariodiagrameditor.util.Util;
import de.uks.se.scenariodiagram.ScenarioObject;

public class ValidateScenarioObjectTypes extends AbstractModelConstraint{

	@Override
	public IStatus validate(IValidationContext ctx) {
		ScenarioObject object = (ScenarioObject) ctx.getTarget();
		
		if(object.getObjectType() == null)
		{
			String objectText = Util.getAttributeWithTag(object, "text").getValue();
			return ctx.createFailureStatus(new Object[]{object, objectText});
		}
		else
		{
			return ctx.createSuccessStatus();
		}
	}
}
