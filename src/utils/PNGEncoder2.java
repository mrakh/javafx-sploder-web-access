package utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class PNGEncoder2 {

	private static int[] crcTable;
	private static boolean crcTableComputed = false;
	
	public static byte[] encode(BufferedImage img) throws IOException {

		int p;

		ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
		DataOutputStream pngDos = new DataOutputStream(pngBaos);
		ByteArrayOutputStream ihdrBaos = new ByteArrayOutputStream();
		DataOutputStream ihdrDos = new DataOutputStream(ihdrBaos);
		ByteArrayOutputStream idatBaos = new ByteArrayOutputStream();
		DataOutputStream idatDos = new DataOutputStream(idatBaos);

		pngDos.writeInt(Integer.MAX_VALUE + 156257864); //2303741511
		pngDos.writeInt(218765834);
		pngDos.close();
		ihdrDos.writeInt(img.getWidth());
		ihdrDos.writeInt(img.getHeight());
		ihdrDos.writeInt(134610944);
		ihdrDos.write(0);
		ihdrDos.close();
		byte[] ihdrArray = ihdrBaos.toByteArray();

		byte[] png = pngBaos.toByteArray();
		writeChunk(png, 1229472850, ihdrArray);

		for(int y = 0; y < img.getHeight(); y++) {
			idatDos.write(0);
			if (img.getTransparency() == BufferedImage.OPAQUE) { // if not transparent
				for(int x = 0; x < img.getWidth(); x++) {
					p = img.getRGB(x, y);
					idatDos.writeInt(((p & 0xFFFFFF) << 8) | 0xFF); // Write pixel data w/o alpha info
				}
			}
			else {
				for(int x = 0; x < img.getWidth(); x++) {
					p = img.getRGB(x, y);
					idatDos.writeInt(((p & 0xFFFFFF) << 8) | (p >>> 24)); // Write pixel data w/ alpha info
				}
			}
		}

		byte[] idatArray = idatBaos.toByteArray();
		idatDos.close();
		Deflater compresser = new Deflater();
		compresser.setInput(idatArray);
		compresser.finish();
		byte[] outputIdatCompression = new byte[idatArray.length];
		int compressedDataLength = compresser.deflate(outputIdatCompression);
		compresser.end();

		byte[] trimmedOutputCompression = new byte[compressedDataLength];
		for(int c = 0; c < compressedDataLength; c++)
			trimmedOutputCompression[c] = outputIdatCompression[c];

		writeChunk(png, 1229209940, trimmedOutputCompression);
		writeChunk(png, 1229278788, null);

		return png;
	}
	
	private static void writeChunk(byte[] png, int type, byte[] data) throws IOException {
		ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
		DataOutputStream pngDos = new DataOutputStream(pngBaos);
		pngBaos.write(png);

		int c;
		
		if(!crcTableComputed) {
			crcTableComputed = true;
			crcTable = new int[4096];

			for(int n = 0; n < 4096; n++) {
				c = n;

				for(int k = 0; k < 8; k++) {
					if ((c & 1) == 1)
						c = (Integer.MAX_VALUE + 1840808737) ^ (c >>> 1);
					else
						c >>>= 1;
				}

				crcTable[n] = c;
			}	
		}

		int len = 0;

		if(data != null)
			len = data.length;

		ByteArrayOutputStream subBaos = new ByteArrayOutputStream();
		DataOutputStream subDos = new DataOutputStream(subBaos);

		pngDos.writeInt(len);

		subDos.writeInt(type);
		if(data != null)
			subDos.write(data);

		byte[] inputData = subBaos.toByteArray();

		c = 0xFFFFFFFF;
		for(byte b : inputData)
			c = crcTable[(c^b) & 0xFF] ^ (c >>> 8);
		c ^= 0xFFFFFFFF;

		pngDos.write(inputData);
		pngDos.writeInt(c);

		subDos.close();
		pngDos.close();

		png = pngBaos.toByteArray();
	}
}
