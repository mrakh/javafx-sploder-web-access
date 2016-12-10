package game_creators.platformer;

import java.util.StringJoiner;

public class PlatformerObject {
	public String id = "0";
	public String x = "0";
	public String y = "0";
	private String[] extraData = null;
	
	public PlatformerObject(int id) {
		this.id = Integer.toString(id);
	}
	
	public PlatformerObject(int id, int x, int y) {
		this.id = Integer.toString(id);
		this.x = Integer.toString(x);
		this.y = Integer.toString(y);
	}
	
	public PlatformerObject(int id, int x, int y, String... extraData) {
		this(id, x, y);
		this.extraData = extraData;
	}
	
	public PlatformerObject(String s) {
		String[] info = s.split(",");
		id = info[0];
		x = info[1];
		y = info[2];
		if(info.length > 3)
			for(int i = 3; i < info.length; i++)
				extraData[i-3] = info[i];
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",");
		sj.add(id).add(x).add(y);
		if(extraData != null)
			for(String s : extraData)
				sj.add(s);
		return sj.toString();
	}
}
