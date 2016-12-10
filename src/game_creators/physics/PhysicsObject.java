package game_creators.physics;

import java.util.StringJoiner;

public class PhysicsObject {
	private Coordinate center;
	private Coordinate pin;
	private int rotation = 0;
	private int groupID = 0;
	
	private PhysicsObjectProperties properties;
	
	public PhysicsObject(Coordinate center, Coordinate pin, int rotation, int groupID, PhysicsObjectProperties properties) {
		this.center = center;
		this.pin = pin;
		this.rotation = rotation;
		this.groupID = groupID;
		this.properties = properties;
	}
	
	public PhysicsObject(int centerX, int centerY, int pinX, int pinY, int rotation, int groupID, PhysicsObjectProperties properties) {
		this(new Coordinate(centerX, centerY), new Coordinate(pinX, pinY), rotation, groupID, properties);
	}
	
	public PhysicsObjectProperties properties() {
		return properties;
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("#");
		sj.add(center.toString())
		.add(pin.toString())
		.add(Integer.toString(rotation))
		.add(Integer.toString(groupID))
		.add(properties.toString());
		return sj.toString();
	}
}
