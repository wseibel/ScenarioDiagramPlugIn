package scenariodiagrameditor.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

import de.uks.se.scenariodiagram.ScenarioAttribute;
import de.uks.se.scenariodiagram.ScenarioLink;
import de.uks.se.scenariodiagram.ScenarioObject;

public class Util {

	private final static String DEFAULT_LINK_VALUE = "has";
	private final static String DEFAULT_ATTRIBUTE_TYPE = "STRING";
	private final static String DEFAULT_OBJECT_TYPENAME = "DefaultType";

	public enum ScenarioDiagramTypes {
		BOOLEAN(EcorePackage.eINSTANCE.getEBoolean()), CHAR(EcorePackage.eINSTANCE.getEChar()), DOUBLE(EcorePackage.eINSTANCE
				.getEDouble()), FLOAT(EcorePackage.eINSTANCE.getEFloat()), LONG(EcorePackage.eINSTANCE.getELong()), INT(
				EcorePackage.eINSTANCE.getEInt()), SHORT(EcorePackage.eINSTANCE.getEShort()), STRING(EcorePackage.eINSTANCE
				.getEString());

		private final EDataType eType;

		ScenarioDiagramTypes(EDataType type) {
			eType = type;
			eType.setInstanceTypeName(this.toString().toUpperCase());
		}

		public EDataType getEType() {
			return eType;
		}
	}

	public static String[] getTypes() {
		String[] types = new String[ScenarioDiagramTypes.values().length];
		int i = 0;
		for (ScenarioDiagramTypes type : ScenarioDiagramTypes.values()) {
			types[i] = type.toString();
			i++;
		}
		return types;
	}

	public static EDataType getType(String typeName) {
		if (typeName == null || typeName.equals("")) {
			return ScenarioDiagramTypes.STRING.getEType();
		}

		EDataType result = ScenarioDiagramTypes.STRING.getEType();

		switch (typeName) {
		case "BOOLEAN":
			result = ScenarioDiagramTypes.BOOLEAN.getEType();
			break;
		case "CHAR":
			result = ScenarioDiagramTypes.CHAR.getEType();
			break;
		case "DOUBLE":
			result = ScenarioDiagramTypes.DOUBLE.getEType();
			break;
		case "FLOAT":
			result = ScenarioDiagramTypes.FLOAT.getEType();
			break;
		case "LONG":
			result = ScenarioDiagramTypes.LONG.getEType();
			break;
		case "INT":
			result = ScenarioDiagramTypes.INT.getEType();
			break;
		case "SHORT":
			result = ScenarioDiagramTypes.SHORT.getEType();
			break;
		case "STRING":
			result = ScenarioDiagramTypes.STRING.getEType();
			break;
		default:
			break;
		}
		return result;
	}

	public static String getName(String eType) {
		String result = null;
		if (eType == ScenarioDiagramTypes.BOOLEAN.getEType().getName()) {
			result = "BOOLEAN";
		}
		if (eType == ScenarioDiagramTypes.CHAR.getEType().getName()) {
			result = "CHAR";
		}
		if (eType == ScenarioDiagramTypes.DOUBLE.getEType().getName()) {
			result = "DOUBLE";
		}
		if (eType == ScenarioDiagramTypes.FLOAT.getEType().getName()) {
			result = "FLOAT";
		}
		if (eType == ScenarioDiagramTypes.LONG.getEType().getName()) {
			result = "LONG";
		}
		if (eType == ScenarioDiagramTypes.INT.getEType().getName()) {
			result = "INT";
		}
		if (eType == ScenarioDiagramTypes.SHORT.getEType().getName()) {
			result = "SHORT";
		}
		if (eType == ScenarioDiagramTypes.STRING.getEType().getName()) {
			result = "STRING";
		}
		return result;
	}

	public static ScenarioAttribute getAttributeWithTag(ScenarioObject object, String tag) {
		ScenarioAttribute result = null;

		EList<ScenarioAttribute> attributes = object.getAttributes();
		for (ScenarioAttribute tmpAttribute : attributes) {
			if (tag.equals(tmpAttribute.getAttributeType().getName())) {
				result = tmpAttribute;
				break;
			}
		}
		return result;
	}

	public static boolean isLinkNameDefault(EObject businessElement) {
		return(((ScenarioLink) businessElement) == null || ((ScenarioLink) businessElement).getName().equals(DEFAULT_LINK_VALUE));
	}

	public static boolean isObjectTypeUndefined(Object businessElement) {
		return ((ScenarioObject) businessElement).getObjectType() == null
				|| ((ScenarioObject) businessElement).getObjectType().getName().equals(DEFAULT_OBJECT_TYPENAME);
	}

	public static boolean isAttributeTypeUndefined(EObject businessElement) {
		return ((ScenarioAttribute) businessElement).getAttributeType() == null
				|| ((ScenarioAttribute) businessElement).getAttributeType().getEType().getInstanceTypeName().equals((DEFAULT_ATTRIBUTE_TYPE));
	}
}
