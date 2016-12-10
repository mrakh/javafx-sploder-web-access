package game_creators.platformer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import game_creators.XMLNode;
import game_creators.platformer.level_enums.Avatar;
import game_creators.platformer.level_enums.Background;

public class PlatformerLevel {
	public static final PlatformerLevel DEFAULT = new PlatformerLevel("", Avatar.DEFAULT, Background.DEFAULT, Color.CYAN, Color.BLACK, 100, "");
	
	private PlatformerXML parent;
	private String levelTitle;
	private Avatar avatar;
	private Background background;
	private Color skyColor;
	private Color groundColor;
	private int lightLevel;
	private String music;
	
	private Collection<PlatformerObject> playfieldObjects = new ArrayList<>();
	
	public PlatformerLevel(String title, PlatformerObject... objects) {
		this(title, Avatar.DEFAULT, Background.DEFAULT, Color.CYAN, Color.BLACK, 100, "");
		playfieldObjects.addAll(Arrays.asList(objects));
	}
	
	public PlatformerLevel(String title, Avatar a, Background b, Color sc, Color gc, int light, String music) {
		levelTitle = title;
		avatar = a;
		background = b;
		skyColor = sc;
		groundColor = gc;
		lightLevel = light;
		this.music = music;
	}
	
	public PlatformerLevel childOf(PlatformerXML parent) {
		this.parent = parent;
		return this;
	}
	
	public String getTitle() {
		return levelTitle;
	}

	public void setTitle(String levelTitle) {
		this.levelTitle = levelTitle;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public String getSky() {
		return String.format("%02x%02x%02x", skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue());
	}

	public void setSky(Color skyColor) {
		this.skyColor = skyColor;
	}

	public String getGround() {
		return String.format("%02x%02x%02x", groundColor.getRed(), groundColor.getGreen(), groundColor.getBlue());
	}

	public void setGround(Color groundColor) {
		this.groundColor = groundColor;
	}

	public int getLight() {
		return lightLevel;
	}

	public void setLight(int lightLevel) {
		this.lightLevel = lightLevel;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}
	
	public void add(PlatformerObject... pos) {
		playfieldObjects.addAll(Arrays.asList(pos));
	}
	
	public void add(Collection<PlatformerObject> pos) {
		playfieldObjects.addAll(pos);
	}
	
	public void add(String... objs) {
		add(Arrays.stream(objs).map(PlatformerObject::new).collect(Collectors.toList()));
	}
	
	public void add(String playfieldContent) {
		add(playfieldContent.split("\\|"));
	}
	
	public Collection<PlatformerObject> getObjects() {
		return playfieldObjects;
	}
}