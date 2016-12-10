package game_creators.physics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import game_creators.physics.enums.Constraint;
import game_creators.physics.enums.Material;
import game_creators.physics.enums.Shape;
import game_creators.physics.enums.Strength;

public class PhysicsObjectProperties {
	
	public static final String[] PROPERTY_NAMES = {
		"shape",
		"width",
		"height",
		"vertices",
		"constraint",
		"material",
		"strength",
		"locked",
		"collisions",
		"passthrough",
		"sensors",
		"fill color",
		"line color",
		"texture",
		"z layer",
		"opacity",
		"jagged edges",
		"actions",
		"graphic number",
		"graphic version",
		"graphic flip",
		"animation",
		"custom texture"
	};
	
	private Map<String, String> objProperties = new LinkedHashMap<>();
	
	public PhysicsObjectProperties(String... properties) {
		if(properties.length % 2 != 0)
			throw new IllegalArgumentException("Invalid parameter for PhysicsObjectProperties constructor. Arguments should alternate between property name and property value.");
		
		populateParams();
		
		for(int i = 0; i < properties.length; i += 2)
			if(objProperties.containsKey(properties[i]))
				objProperties.put(properties[i], properties[i+1]);
	}
	
	public String getProperty(String property) {
		return objProperties.get(property);
	}
	
	public void setProperty(String property, String value) {
		if(objProperties.containsKey(property))
			objProperties.put(property, value);
	}

	private void populateParams() {
		objProperties.put("shape", Shape.NONE.num());
		objProperties.put("width", "100");
		objProperties.put("height", "100");
		objProperties.put("vertices", "");
		objProperties.put("constraint", Constraint.FREE.num());
		objProperties.put("material", Material.WOOD.num());
		objProperties.put("strength", Strength.PERM.num());
		objProperties.put("locked", "0");
		objProperties.put("collisions", Integer.toString(0b11111));
		objProperties.put("passthrough", "-1");
		objProperties.put("sensors", Integer.toString(0b00000));
		objProperties.put("fill color", 0xffffff + ""); // Adding empty string to convert to string
		objProperties.put("line color", 0xffffff + "");
		objProperties.put("texture", "0");
		objProperties.put("z layer", "3");
		objProperties.put("opacity", "1");
		objProperties.put("jagged edges", "0");
		objProperties.put("actions", "0");
		objProperties.put("graphic number", "0");
		objProperties.put("graphic version", "0");
		objProperties.put("graphic flip", "0");
		objProperties.put("animation", "0");
		objProperties.put("custom texture", "");
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(";");
		objProperties.values().forEach(sj::add);
		return sj.toString() + "$";
	}
}