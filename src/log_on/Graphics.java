package log_on;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import utils.GifEncoder;
import utils.PNGEncoder;

public class Graphics {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	private List<BufferedImage> images = new ArrayList<BufferedImage>();
	private List<File> thumbFiles = new ArrayList<File>();
	
	public Graphics(String... paths) throws IOException {
		if(paths.length > 6)
			throw new IOException("You can only add up to six images.");
		
		for(String path : paths) {
			File f = new File(path);
			thumbFiles.add(f);
			images.add(ImageIO.read(f));
		}
		
		if(!(dimensionsAre(20, images) || dimensionsAre(40, images) || dimensionsAre(60, images)))
			throw new IOException("At least one graphic contains an invalid size./nGraphics sizes can only be 20x20 px, 40x40 px or 60x60px.");
	}
	
	public Graphics(javafx.scene.image.Image image) {
		images.add(SwingFXUtils.fromFXImage(image, null));
	}
	
	public byte[] octetStream() throws IOException { // buildDocument
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(2); // Editor Version
		dos.writeInt(0); // If project ID is not non-zero positive number, then write 0
		dos.writeUTF("demo"); // If author is defined, name of author, else, demo
		dos.writeUTF(FORMAT.format(new Date())); // "Monday, February 8, 2016"
		dos.writeUTF("icon_template"); // Editor's pattern chooser
		dos.writeInt(1); // Is it private? 1 if true, 0 if false
		dos.write(serializeImage()); // editor.image.serialize()
		dos.close();
		
		return baos.toByteArray();
	}
	
	public byte[] thumbnailStream() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GifEncoder encoder = new GifEncoder();
		encoder.setRepeat(0);
		encoder.setDelay(180);
		encoder.start(baos);
		for(BufferedImage i : images)
			encoder.addFrame(i);
		encoder.finish();
		
		return baos.toByteArray();
	}

	public byte[] pngStream() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PNGEncoder encoder = new PNGEncoder(baos, PNGEncoder.COLOR_MODE);
		encoder.encode(images.get(0));
		return baos.toByteArray();
	}
	
	public static void splitImage(BufferedImage bi) throws IOException {
		final int tileWidth = (int) Math.floor(bi.getWidth()/60.0); // TODO: 60 is hardcoded, make it so that it works with 20, 40 or 60, depending on an additional method parameter
		final int tileHeight = (int) Math.floor(bi.getHeight()/60.0);
		for(int tileY = 0; tileY < tileHeight; tileY++)
			for(int tileX = 0; tileX < tileWidth; tileX++) {
				BufferedImage piece = bi.getSubimage(tileX*60, tileY*60, 60, 60);
				ImageIO.write(piece, "png", new File(System.getProperty("user.home") + "/Desktop/Partitions/image_" + tileX + "_" + tileY + ".png"));
				System.out.println(tileX + " " + tileY + " done");
			}
	}
	
	private byte[] serializeImage() throws IOException { // Image#serialize()
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeInt(images.get(0).getWidth()); // Graphics width
		dos.writeInt(images.get(0).getHeight()); // Graphics height
		dos.writeInt(0); // (boolean condition) ? ChildIndex of this current canvas : zero
		dos.writeInt(2); // Canvas.nextCanvasNum (probably number of canvasses + 1)
		
		int counter = 0;
		for(BufferedImage bi : images) {
			byte[] frameData = serializeFrame(bi, ++counter);
			dos.writeInt(frameData.length);
			dos.write(frameData);
		}
		dos.close();
		
		return baos.toByteArray();
	}
	
	private byte[] serializeFrame(BufferedImage frame, int frameNum) throws IOException { // Canvas#serialize()
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeUTF("Frame " + frameNum);
		dos.writeInt(images.get(0).getWidth()); // Graphics width
		dos.writeInt(images.get(0).getHeight()); // Graphics height
		dos.writeInt(480 / images.get(0).getWidth()); // Scale = 480/(Graphics width or height)
		dos.writeInt(0); // frameNum - 1
		dos.writeInt(2); // frameNum + 1
		byte[] layer = createLayer(frame);
		dos.writeInt(layer.length);
		dos.write(layer);
		dos.close();
		
		return baos.toByteArray();
	}
	
	private byte[] createLayer(BufferedImage frameImage) throws IOException { // Layer#serialize()
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeUTF("Layer 1");
		dos.writeInt(0); // ?
		dos.writeInt(0); // z index
		dos.writeInt(100); // opacity
		dos.writeInt(images.get(0).getWidth()); // Graphics width
		dos.writeInt(images.get(0).getHeight()); // Graphics height
		dos.writeInt(1); // Visible ? 1 : 0
		for(int y = 0; y < frameImage.getHeight(); y++)
			for(int x = 0; x < frameImage.getWidth(); x++) {
				int pixel = frameImage.getRGB(x, y);
				int alpha = (pixel >> 24) & 0xff;
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel >> 0) & 0xff;
				dos.write(alpha);
				dos.write(red);
				dos.write(green);
				dos.write(blue);
			}
		
		return baos.toByteArray();
	}
	
	private static boolean dimensionsAre(int dimension, List<BufferedImage> biList) {
		for(BufferedImage bi : biList)
			if(!(bi.getWidth() == dimension && bi.getHeight() == dimension))
				return false;
		return true;
	}
}
