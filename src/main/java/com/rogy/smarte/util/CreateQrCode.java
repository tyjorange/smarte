package com.rogy.smarte.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

/**
 *
 */
public class CreateQrCode {

	private static int width = 800;
	private static int height = 800;
	private static int fontsize = 100;
	private static String format = "png";

	private static BitMatrix createBitMatrix(String content)
			throws Exception {
		HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.MARGIN, 8);
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,
				BarcodeFormat.QR_CODE, width, height, hints);
		return bitMatrix;
	}
	
	public static void createImg(String content, String path) throws Exception {
		BitMatrix bm = createBitMatrix(content);
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bm.get(x, y) ? Color.BLACK.getRGB()
						: Color.WHITE.getRGB());
			}
		}
		File file = new File(path);
		ImageIO.write(image, format, file);
	}
	
	public static ServletOutputStream createImg(String content, String title, HttpServletResponse resp) throws Exception {
		BitMatrix bm = createBitMatrix(content);
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bm.get(x, y) ? Color.BLACK.getRGB()
						: Color.WHITE.getRGB());
			}
		}
		Graphics g = image.createGraphics();
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, fontsize);
		g.setFont(font);
		if (title != null && !title.trim().isEmpty()) {
			int startX = (width - (g.getFontMetrics().stringWidth(title))) / 2;
			int startY = g.getFontMetrics().getHeight();
			g.drawImage(image, 0, 0, width, height, null);
			g.setColor(Color.black);
			g.drawString(title, startX, startY);
		}
		if (content != null && !content.trim().isEmpty()) {
			int startX = (width - (g.getFontMetrics().stringWidth(content))) / 2;
			int startY = height - g.getFontMetrics().getHeight() / 2;
			g.drawImage(image, 0, 0, width, height, null);
			g.setColor(Color.black);
			g.drawString(content, startX, startY);
		}
		ServletOutputStream stream = resp.getOutputStream(); 
		ImageIO.write(image, format, stream);
//		MatrixToImageWriter.writeToStream(image, format, stream);
		return stream;
	}
}
