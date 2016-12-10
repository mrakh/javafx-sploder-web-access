package application;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import game_creators.platformer.PlatFlag;
import game_creators.platformer.PlatformerLevel;
import game_creators.platformer.PlatformerObject;
import game_creators.platformer.PlatformerSettings;
import game_creators.platformer.PlatformerXML;
import log_on.SploderClient;

public class Main {
	
	public static int counter = -1;
	
	public static final Path DIRECTORY = Paths.get(System.getProperty("user.home"), "Desktop", "sploderwithinsploder");
	public static final int GRAPHIC_SIZE = 60;
	
	public static void main(String[] args) throws Exception {
		SploderClient c = new SploderClient("10000truths", "mai369iscool");
		c.logIn();
		BufferedImage smallThumb = ImageIO.read(DIRECTORY.resolve("smallThumb").toFile());
		BufferedImage largeThumb = ImageIO.read(DIRECTORY.resolve("largeThumb").toFile());
		int flags = PlatFlag.GRAPHICS | PlatFlag.COMMENTS;
		PlatformerSettings settings = new PlatformerSettings("Sploder within Sploder", "10000truths", flags);
		PlatformerXML xml = new PlatformerXML(settings);
		
		BufferedImage firstLevelImg = ImageIO.read(DIRECTORY.resolve("levels").resolve("level1").toFile());
		
		c.saveGame(false, smallThumb, largeThumb, null);
	}
	
	public static Map<Integer, BufferedImage> partitionPicture(BufferedImage bi, int graphicNoCounter) {
		Map<Integer, BufferedImage> imageMap = new HashMap<>();
		for(int y = 0; y < bi.getHeight()/GRAPHIC_SIZE; y++)
			for(int x = 0; x < bi.getWidth()/GRAPHIC_SIZE; x++) {
				imageMap.put(graphicNoCounter--, bi.getSubimage(GRAPHIC_SIZE*x, GRAPHIC_SIZE*y, GRAPHIC_SIZE, GRAPHIC_SIZE));
			}
		return imageMap;
	}
	
	public static void addImageToBackground(BufferedImage bi, int graphicNoCounter, int g) {
		
	}
}
