package org.cat.core.util3.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.cat.core.util3.string.ArchFontConstants;

/**
 * 
 * @author 王云龙
 * @date 2017年2月8日 下午6:40:13
 * @version 1.0
 * @description  验证码生成器类
 * 		可生成数字、大写、小写字母及三者混合类型的验证码。
 * 		支持自定义验证码字符数量；
 * 		支持自定义验证码图片的大小；
 * 		支持自定义需排除的特殊字符；
 * 		支持自定义干扰线的数量；
 * 		支持自定义验证码图文颜色
 *
 */
public class ImageValidateCodeUtil {
	public static final class Type{
		public static final int TYPE_NUM_ONLY = 0; //验证码类型为仅数字 0~9
		public static final int TYPE_LETTER_ONLY = 1; //验证码类型为仅字母，即大写、小写字母混合
		public static final int TYPE_ALL_MIXED = 2;//验证码类型为数字、大写字母、小写字母混合
		public static final int TYPE_NUM_UPPER = 3; //验证码类型为数字、大写字母混合
		public static final int TYPE_NUM_LOWER = 4; //验证码类型为数字、小写字母混合
		public static final int TYPE_UPPER_ONLY = 5; //验证码类型为仅大写字母
		public static final int TYPE_LOWER_ONLY = 6; //验证码类型为仅小写字母
	}
  
	private ImageValidateCodeUtil() {

	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午5:42:05
	 * @version 1.0
	 * @description 生成验证码字符串
	 * @param type 验证码类型，参见本类的静态属性 {@linkplain ImageValidateCodeUtil.Type} 
	 * @param length 验证码长度，大于0的整数
	 * @param exChars 需排除的特殊字符（无需排除则为null）
	 * @return
	 */
	public static String generateTextCode(int type,int length,String exChars) {
		 if (length <= 0) {
			 throw new IllegalArgumentException("需要生成的验证码长度比较大于0");
		 }
		 
		 StringBuffer code = new StringBuffer();
		 int i = 0;
		 Random random = new Random();
		 
		 switch (type) {
			case Type.TYPE_NUM_ONLY: //仅数字
				while(i < length) {
					int tempChar = random.nextInt(10);
					if (exChars == null || exChars.indexOf(tempChar + "") < 0) { //排除特殊字符
						code.append(tempChar);
						i++;
					}
				}
				break;
			case Type.TYPE_LETTER_ONLY: //仅字母（即大写字母、小写字母混合）
				while (i < length) {
					int temp = random.nextInt(123);
					if ( (temp >= 97 || (temp >= 65 && temp <= 90)) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			case Type.TYPE_UPPER_ONLY: //仅大写字母
				while (i < length) {
					int temp = random.nextInt(91);
					if ( (temp >= 65) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			case Type.TYPE_LOWER_ONLY: //仅小写字母
				while (i < length) {
					int temp = random.nextInt(123);
					if ( (temp >= 97) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			case Type.TYPE_ALL_MIXED: //数字、大写字母、小写字母混合
				while (i < length) {
					int temp = random.nextInt(123);
					if ( (temp >= 97 || (temp >= 65 && temp <= 90) || (temp >= 48 && temp <= 57)) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			case Type.TYPE_NUM_UPPER: //数字、大写字母混合
				while (i < length) {
					int temp = random.nextInt(91);
					if ( (temp >= 65 || (temp >= 48 && temp <= 57)) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			case Type.TYPE_NUM_LOWER: //数字、小写字母混合
				while (i < length) {
					int temp = random.nextInt(123);
					if ( (temp >= 97 || (temp >= 48 && temp <= 57)) && (exChars == null || exChars.indexOf( (char) temp) < 0)) { //排除特殊字符
						code.append( (char) temp);
						i++;
					}
				}
				break;
			default:
				break;
		 }
		 return code.toString();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午6:01:38
	 * @version 1.0
	 * @description 传入验证码字符串，生成验证码图片 
	 * @param textCode 文本验证码
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param interLine 图片中干扰线的条数
	 * @param randomLocation 每个字符的高低位置是否随机
	 * @param backColor 图片颜色，若为null，则采用随机颜色
	 * @param foreColor 字体颜色，若为null，则采用随机颜色
	 * @param lineColor 干扰线颜色，若为null，则采用随机颜色
	 * @return 图片缓存对象BufferedImage
	 */
	public static BufferedImage generateImageCode(String textCode,int width,
            int height,int interLine,
            boolean randomLocation,
            Color backColor,Color foreColor,
            Color lineColor) {
		
		BufferedImage bim = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
		Graphics g = bim.getGraphics();
		
		//画背景图
		g.setColor(backColor == null ? getRandomColor() : backColor);
		g.fillRect(0,0,width,height);
		
		//画干扰线
		Random r = new Random();
		if (interLine > 0) {
			int x = 0,y = 0,x1 = width,y1 = 0;
			for (int i = 0; i < interLine; i++) {
				g.setColor(lineColor == null ? getRandomColor() : lineColor);
				y = r.nextInt(height);
				y1 = r.nextInt(height);
				g.drawLine(x,y,x1,y1);
			}
		}
		
		//写验证码
		//字体大小为图片高度的80%
		int fsize = (int) (height * 0.8);
		int fx = 0;
		int fy = fsize;
		g.setFont(new Font(ArchFontConstants.SONG_TI,Font.PLAIN,fsize));
		//写验证码字符
		for (int i = 0; i < textCode.length(); i++) {
			fy = randomLocation ? (int) ( (Math.random() * 0.3 + 0.6) * height) : fy; //每个字符高低是否随机
			g.setColor(foreColor == null ? getRandomColor() : foreColor);
			g.drawString(textCode.charAt(i) + "",fx,fy);
			fx += (width / textCode.length()) * (Math.random() * 0.3 + 0.8); //依据宽度浮动
		}
		g.dispose();
		
		return bim;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午6:09:49
	 * @version 1.0
	 * @description 产生随机颜色 
	 * @return Color
	 */
	private static Color getRandomColor() {
		Random r = new Random();
		Color c = new Color(r.nextInt(256),r.nextInt(256),r.nextInt(256));
		return c;
	}

}
