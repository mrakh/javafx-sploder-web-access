package utils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageUtils {

	public static boolean isValidDimension(File f) throws IOException {
		Dimension d = getImageDimension(f);
		for (int i = 1; i <= 3; i++)
			if (d.getWidth() == 20 * i && d.getHeight() == 20 * i)
				return true;
		return false;
	}

	public static Dimension getImageDimension(File file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");

		try {
			int header = raf.readUnsignedShort();

			if (header == 0x8950) {
				// PNG!
				raf.seek(8 + 4 + 4); // thanks, Abuse

				return new Dimension(raf.readInt(), raf.readInt());
			}

			if (header == 0xffd8) {
				// JPG!

				// (see below)
			} else if (header == 0x424D) {
				// BMP!
				raf.seek(0x0012);

				int w = raf.read() | (raf.read() << 8) | (raf.read() << 16) | (raf.read() << 24);
				int h = raf.read() | (raf.read() << 8) | (raf.read() << 16) | (raf.read() << 24);
				return new Dimension(w, h);
			} else if (header == (('G' << 8) | ('I' << 0))) {
				// GIF!
				raf.seek(0x0006);
				int w = raf.read() | (raf.read() << 8);
				int h = raf.read() | (raf.read() << 8);
				return new Dimension(w, h);
			} else {
				throw new IllegalStateException("unexpected header: " + Integer.toHexString(header));
			}

			while (true) {
				int marker = raf.readUnsignedShort();

				switch (marker) {
				case 0xffd8: // SOI
				case 0xffd0: // RST0
				case 0xffd1: // RST1
				case 0xffd2: // RST2
				case 0xffd3: // RST3
				case 0xffd4: // RST4
				case 0xffd5: // RST5
				case 0xffd6: // RST6
				case 0xffd7: // RST7
				case 0xffd9: // EOI
					break;

				case 0xffdd: // DRI
					raf.readUnsignedShort();
					break;

				case 0xffe0: // APP0
				case 0xffe1: // APP1
				case 0xffe2: // APP2
				case 0xffe3: // APP3
				case 0xffe4: // APP4
				case 0xffe5: // APP5
				case 0xffe6: // APP6
				case 0xffe7: // APP7
				case 0xffe8: // APP8
				case 0xffe9: // APP9
				case 0xffea: // APPa
				case 0xffeb: // APPb
				case 0xffec: // APPc
				case 0xffed: // APPd
				case 0xffee: // APPe
				case 0xffef: // APPf
				case 0xfffe: // COM
				case 0xffdb: // DQT
				case 0xffc4: // DHT
				case 0xffda: // SOS
					raf.readFully(new byte[raf.readUnsignedShort() - 2]);
					break;

				case 0xffc0: // SOF0
				case 0xffc2: // SOF2
					raf.readUnsignedShort();
					raf.readByte();
					int height = raf.readUnsignedShort();
					int width = raf.readUnsignedShort();
					return new Dimension(width, height);

				default:
					throw new IllegalStateException("invalid jpg marker: " + Integer.toHexString(marker));
				}
			}
		} finally {
			raf.close();
		}
	}

	public static BufferedImage toImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean imagesEqual(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
			for (int x = 0; x < img1.getWidth(); x++)
				for (int y = 0; y < img1.getHeight(); y++)
					if (img1.getRGB(x, y) != img2.getRGB(x, y))
						return false;
		} else
			return false;
		return true;
	}
}
