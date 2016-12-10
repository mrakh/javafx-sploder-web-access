package game_creators.physics;

import java.util.StringJoiner;

import game_creators.physics.enums.Effect;
import game_creators.physics.enums.Extents;
import game_creators.physics.enums.Size;

public class PhysicsLevel {
	
	private int bgBottom;
	private int bgTop;
	private Effect bgEffect = Effect.NONE;
	private Extents extents = Extents.ENCLOSED;
	private boolean gravity = true;
	private boolean resistance = false;
	private Size size = Size.NORMAL;
	private int totalLives = 3;
	private int totalPenalties = 3;
	private int totalScore = 10;
	private int totalTime = 0;
	private String instructions = "";
	private String music = "";
	private boolean wrap = false;
	
	public PhysicsLevel(int bgBottom, int bgTop, Effect bgEffect, Extents extents, boolean gravity, boolean resistance, Size size, int totalLives, int totalPenalties, int totalScore, int totalTime, String instructions, String music, boolean wrap) {
		if(bgTop < 0 || bgTop > 0xffffff || bgBottom < 0 || bgBottom > 0xffffff)
			throw new IllegalArgumentException("Color values out of bounds");
		
		this.bgBottom = bgBottom;
		this.bgTop = bgTop;
		this.bgEffect = bgEffect == null ? Effect.NONE : bgEffect;
		this.extents = extents == null ? Extents.ENCLOSED : extents;
		this.gravity = gravity;
		this.resistance = resistance;
		this.size = size == null ? Size.NORMAL : size;
		this.totalLives = totalLives;
		this.totalPenalties = totalPenalties;
		this.totalScore = totalScore;
		this.totalTime = totalTime;
		this.instructions = instructions;
		this.music = music;
		this.wrap = wrap;
	}

	public Effect getBackgroundEffect() {
		return bgEffect;
	}

	public void setBackgroundEffect(Effect bgEffect) {
		this.bgEffect = bgEffect;
	}

	public Extents getExtents() {
		return extents;
	}

	public void setExtents(Extents extents) {
		this.extents = extents;
	}

	public boolean isResistance() {
		return resistance;
	}

	public void setResistance(boolean resistance) {
		this.resistance = resistance;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public boolean isWrap() {
		return wrap;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
	public boolean isGravity() {
		return gravity;
	}

	public void setGravity(boolean gravity) {
		this.gravity = gravity;
	}

	public int getTotalLives() {
		return totalLives;
	}

	public void setTotalLives(int totalLives) {
		this.totalLives = totalLives;
	}

	public int getTotalPenalties() {
		return totalPenalties;
	}

	public void setTotalPenalties(int totalPenalties) {
		this.totalPenalties = totalPenalties;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(";");
		sj.add(Integer.toString(bgBottom))
		.add(Integer.toString(bgTop))
		.add(bgEffect.toString())
		.add(extents.str())
		.add(gravity ? "1" : "0")
		.add(resistance ? "1" : "0")
		.add(size.str())
		.add(Integer.toString(totalLives))
		.add(Integer.toString(totalPenalties))
		.add(Integer.toString(totalScore))
		.add(Integer.toString(totalTime))
		.add(instructions)
		.add(music)
		.add(wrap ? "1" : "0");
		return sj.toString();
	}
}