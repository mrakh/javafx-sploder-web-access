package game_creators.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PhysicsObjectPool {
	private List<PhysicsObject> objPool = new ArrayList<>();
	
	public PhysicsObjectPool(PhysicsObject... physObjs) {
		addToPool(physObjs);
	}
	
	public void addToPool(PhysicsObject... physObjs) {
		if(physObjs != null)
			for(PhysicsObject obj : physObjs)
				if(obj != null)
					objPool.add(obj);
	}
	
	public void removeFromPool(PhysicsObject... physObjs) {
		if(physObjs != null)
			for(PhysicsObject obj : physObjs)
				if(obj != null)
					objPool.remove(obj);
	}
	
	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner("|");
		for(int i = 0; i < objPool.size(); i++)
			sj.add((i+1) + "#" + objPool.get(i).toString());
		return sj.toString();
	}
}
