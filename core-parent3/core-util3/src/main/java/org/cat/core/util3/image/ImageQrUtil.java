package org.cat.core.util3.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
 * 
 * @author 王云龙
 * @date 2021年8月12日 下午5:22:20
 * @version 1.0
 * @description 二维码工具
 *
 */
public class ImageQrUtil {
	public static final class Color {
		public static final int BLACK = 0xff000000;
		public static final int WHITE = 0xFFFFFFFF;
	}
	
	public static final class Size {
		public static final int NORMAL_WIDTH=300;
		public static final int NORMAL_HEIGHT=300;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午5:29:50
	 * @version 1.0
	 * @description 生成二维码图片 
	 * @param contents 二维码中现实的字符串
	 * @return BufferedImage
	 */
	public static BufferedImage getQRcodeBufferedImage(String contents) {
		if (StringUtils.isBlank(contents)) {
			throw new IllegalArgumentException("contents不能为空");
		}
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
		BitMatrix bitMatrix = null;
		try {
			bitMatrix = multiFormatWriter.encode(contents,
					BarcodeFormat.QR_CODE, Size.NORMAL_WIDTH, Size.NORMAL_HEIGHT, hints);
		} catch (WriterException e) {
			throw new QrIOExpcetion("生成BitMatrix发生WriterException错误", e);
		}
		BufferedImage image = toBufferedImage(bitMatrix);
		return image;
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2015-9-29 上午11:40:11
	 * @param @param matrix
	 * @param @return
	 * @version 1.0
	 * @description 将bit点阵转换为图片
	 */
	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK : Color.BLACK);
			}
		}
		return image;
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2015-9-29 上午11:55:45
	 * @param @param file
	 * @param @return
	 * @version 1.0
	 * @description 
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午5:35:25
	 * @version 1.0
	 * @description 二维码文件解码 
	 * @param file 二维码文件
	 * @return
	 * @throws IOException 
	 * @throws NotFoundException 
	 */
	public static String decode(File file) throws IOException, NotFoundException {
			BufferedImage image = null;
			image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Hashtable<DecodeHintType,String> hints = new Hashtable<DecodeHintType,String>();
			hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
			// 解码设置编码方式为：utf-8，
			Result result = new MultiFormatReader().decode(bitmap, hints);
			String resultStr = result.getText();
			return resultStr;
	}
}
