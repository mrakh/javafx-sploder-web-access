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
	
	public static void main(String[] args) throws Exception {
		SploderClient c = new SploderClient("username", "password");
		c.logIn();
		
		// Replace null with an image object that satisfies the 20x20/40x40/60x60 precondition
		c.saveGraphicAs(true, false, null);
	}
}
