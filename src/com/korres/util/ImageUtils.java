package com.korres.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.springframework.util.Assert;

import com.korres.Setting.WatermarkPosition;

/*
 * 类名：ImageUtils.java
 * 功能说明：图片工具类
 * 创建日期：2013-8-14 下午04:12:06
 * 作者：weiyuanhua
 * 版权：korres
 * 更新时间：$Date$
 * 标签：$Name$
 * CVS版本：$Revision$
 * 最后更新者：$Author$
*/
public final class ImageUtils {
	private static ImageUtilsType imageType = ImageUtilsType.auto;
	private static String searchPath;
	private static String IIIlllII;
	private static final Color color = Color.white;
	private static final int IIIllllI = 88;

	static {
		// String system;
		// String path;
		// String[] localObject3;
		// Object localObject4;
		// File localFile1;
		// File localFile2;
		if (searchPath == null) {
			String system = System.getProperty("os.name").toLowerCase();
			if (system.indexOf("windows") >= 0) {
				String path = System.getenv("Path");
				if (path != null) {
					String[] sp = path.split(";");
					for (String str : sp) {
						File file1 = new File(str.trim() + "/gm.exe");
						File file2 = new File(str.trim() + "/gmdisplay.exe");
						if ((file1.exists()) && (file2.exists())) {
							searchPath = str.trim();
							break;
						}
					}
				}
			}
		}
		if (IIIlllII == null) {
			String system = System.getProperty("os.name").toLowerCase();
			if (system.indexOf("windows") >= 0) {
				String path = System.getenv("Path");
				if (path != null) {
					String[] sp = path.split(";");
					for (String str : sp) {
						File file1 = new File(str.trim() + "/convert.exe");
						File file2 = new File(str.trim() + "/composite.exe");
						if ((file1.exists()) && (file2.exists())) {
							IIIlllII = str.trim();
							break;
						}
					}
				}
			}
		}
		if (imageType == ImageUtilsType.auto)
			try {
				IMOperation imOperation = new IMOperation();
				imOperation.version();
				IdentifyCmd identifyCmd = new IdentifyCmd(true);
				if (searchPath != null) {
					identifyCmd.setSearchPath(searchPath);
				}
				identifyCmd.run(imOperation, new Object[0]);
				imageType = ImageUtilsType.graphicsMagick;
			} catch (Throwable localThrowable1) {
				try {
					IMOperation imOperation = new IMOperation();
					imOperation.version();
					IdentifyCmd identifyCmd = new IdentifyCmd(false);
					identifyCmd.run(imOperation, new Object[0]);
					if (IIIlllII != null) {
						identifyCmd.setSearchPath(IIIlllII);
					}

					imageType = ImageUtilsType.imageMagick;
				} catch (Throwable localThrowable2) {
					imageType = ImageUtilsType.jdk;
				}
			}
	}

	public static void zoom(File srcFile, File destFile, int destWidth,
			int destHeight) {
		Assert.notNull(srcFile);
		Assert.notNull(destFile);
		Assert.state(destWidth > 0);
		Assert.state(destHeight > 0);
		// Object localObject1;
		// Object localObject2;
		if (imageType == ImageUtilsType.jdk) {
			Graphics2D g = null;
			ImageOutputStream out = null;
			ImageWriter imageWriter = null;
			try {
				BufferedImage localBufferedImage1 = ImageIO.read(srcFile);
				int i = localBufferedImage1.getWidth();
				int j = localBufferedImage1.getHeight();
				int k = destWidth;
				int m = destHeight;
				if (j >= i)
					k = (int) Math.round(destHeight * 1.0D / j * i);
				else
					m = (int) Math.round(destWidth * 1.0D / i * j);
				BufferedImage localBufferedImage2 = new BufferedImage(
						destWidth, destHeight, 1);
				g = localBufferedImage2.createGraphics();
				g.setBackground(color);
				g.clearRect(0, 0, destWidth, destHeight);
				g.drawImage(localBufferedImage1.getScaledInstance(k, m, 4),
						destWidth / 2 - k / 2, destHeight / 2 - m / 2, null);
				out = ImageIO.createImageOutputStream(destFile);
				imageWriter = (ImageWriter) ImageIO
						.getImageWritersByFormatName(
								FilenameUtils.getExtension(destFile.getName()))
						.next();
				imageWriter.setOutput(out);
				ImageWriteParam localImageWriteParam = imageWriter
						.getDefaultWriteParam();
				localImageWriteParam.setCompressionMode(2);
				localImageWriteParam.setCompressionQuality(0.88F);
				imageWriter.write(null, new IIOImage(localBufferedImage2, null,
						null), localImageWriteParam);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				if (g != null)
					g.dispose();
				if (imageWriter != null)
					imageWriter.dispose();
				if (out == null)
					return;
				try {
					out.close();
				} catch (IOException e1) {
				}
			} finally {
				if (g != null)
					g.dispose();
				if (imageWriter != null)
					imageWriter.dispose();
				if (out != null)
					try {
						out.close();
					} catch (IOException e) {
					}
			}
			try {
				out.close();
			} catch (IOException e) {
			}
		} else {
			IMOperation imOperation = new IMOperation();
			imOperation.thumbnail(Integer.valueOf(destWidth), Integer
					.valueOf(destHeight));
			imOperation.gravity("center");
			imOperation.background(getColor(color));
			imOperation.extent(Integer.valueOf(destWidth), Integer
					.valueOf(destHeight));
			imOperation.quality(Double.valueOf(88.0D));
			imOperation.addImage(new String[] { srcFile.getPath() });
			imOperation.addImage(new String[] { destFile.getPath() });
			if (imageType == ImageUtilsType.graphicsMagick) {
				ConvertCmd convertCmd = new ConvertCmd(true);
				if (searchPath != null)
					convertCmd.setSearchPath(searchPath);
				try {
					convertCmd.run(imOperation, new Object[0]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IM4JavaException e) {
					e.printStackTrace();
				}
			} else {
				ConvertCmd convertCmd = new ConvertCmd(false);
				if (IIIlllII != null)
					convertCmd.setSearchPath(IIIlllII);
				try {
					convertCmd.run(imOperation, new Object[0]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IM4JavaException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void addWatermark(File srcFile, File destFile,
			File watermarkFile, WatermarkPosition watermarkPosition, int alpha) {
		Assert.notNull(srcFile);
		Assert.notNull(destFile);
		Assert.state(alpha >= 0);
		Assert.state(alpha <= 100);
		if ((watermarkFile == null) || (!watermarkFile.exists())
				|| (watermarkPosition == null)
				|| (watermarkPosition == WatermarkPosition.no)) {
			try {
				FileUtils.copyFile(srcFile, destFile);
			} catch (IOException localIOException) {
				localIOException.printStackTrace();
			}
			return;
		}
		// Object localObject1;
		// Object localObject2;
		Graphics2D g = null;
		ImageOutputStream out = null;
		ImageWriter imageWriter = null;

		if (imageType == ImageUtilsType.jdk) {
			try {
				BufferedImage bufferedImage1 = ImageIO.read(srcFile);
				int i = bufferedImage1.getWidth();
				int j = bufferedImage1.getHeight();
				BufferedImage bufferedImage2 = new BufferedImage(i, j, 1);
				g = bufferedImage2.createGraphics();
				g.setBackground(color);
				g.clearRect(0, 0, i, j);
				g.drawImage(bufferedImage1, 0, 0, null);
				g.setComposite(AlphaComposite.getInstance(10, alpha / 100.0F));
				BufferedImage localBufferedImage3 = ImageIO.read(watermarkFile);
				int k = localBufferedImage3.getWidth();
				int m = localBufferedImage3.getHeight();
				int n = i - k;
				int i1 = j - m;
				if (watermarkPosition == WatermarkPosition.topLeft) {
					n = 0;
					i1 = 0;
				} else if (watermarkPosition == WatermarkPosition.topRight) {
					n = i - k;
					i1 = 0;
				} else if (watermarkPosition == WatermarkPosition.center) {
					n = (i - k) / 2;
					i1 = (j - m) / 2;
				} else if (watermarkPosition == WatermarkPosition.bottomLeft) {
					n = 0;
					i1 = j - m;
				} else if (watermarkPosition == WatermarkPosition.bottomRight) {
					n = i - k;
					i1 = j - m;
				}
				g.drawImage(localBufferedImage3, n, i1, k, m, null);
				out = ImageIO.createImageOutputStream(destFile);
				imageWriter = ImageIO.getImageWritersByFormatName(
						FilenameUtils.getExtension(destFile.getName())).next();
				imageWriter.setOutput(out);
				ImageWriteParam localImageWriteParam = imageWriter
						.getDefaultWriteParam();
				localImageWriteParam.setCompressionMode(2);
				localImageWriteParam.setCompressionQuality(0.88F);
				imageWriter.write(null,
						new IIOImage(bufferedImage2, null, null),
						localImageWriteParam);
				out.flush();
			} catch (IOException localIOException7) {
				localIOException7.printStackTrace();
				if (g != null)
					g.dispose();
				if (imageWriter != null)
					imageWriter.dispose();
				if (out == null)
					return;
				try {
					out.close();
				} catch (IOException localIOException2) {
				}
			} finally {
				if (g != null)
					g.dispose();
				if (imageWriter != null)
					imageWriter.dispose();
				if (out != null)
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String position = "SouthEast";
			if (watermarkPosition == WatermarkPosition.topLeft)
				position = "NorthWest";
			else if (watermarkPosition == WatermarkPosition.topRight)
				position = "NorthEast";
			else if (watermarkPosition == WatermarkPosition.center)
				position = "Center";
			else if (watermarkPosition == WatermarkPosition.bottomLeft)
				position = "SouthWest";
			else if (watermarkPosition == WatermarkPosition.bottomRight)
				position = "SouthEast";
			IMOperation imOperation = new IMOperation();
			imOperation.gravity(position);
			imOperation.dissolve(Integer.valueOf(alpha));
			imOperation.quality(Double.valueOf(88.0D));
			imOperation.addImage(new String[] { watermarkFile.getPath() });
			imOperation.addImage(new String[] { srcFile.getPath() });
			imOperation.addImage(new String[] { destFile.getPath() });
			if (imageType == ImageUtilsType.graphicsMagick) {
				CompositeCmd compositeCmd = new CompositeCmd(true);
				if (searchPath != null)
					compositeCmd.setSearchPath(searchPath);
				try {
					compositeCmd.run(imOperation, new Object[0]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IM4JavaException e) {
					e.printStackTrace();
				}
			} else {
				CompositeCmd compositeCmd = new CompositeCmd(false);
				if (IIIlllII != null)
					compositeCmd.setSearchPath(IIIlllII);
				try {
					compositeCmd.run(imOperation, new Object[0]);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IM4JavaException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void initialize() {
	}

	private static String getColor(Color paramColor) {
		StringBuffer localStringBuffer = new StringBuffer();
		String str1 = Integer.toHexString(paramColor.getRed());
		String str2 = Integer.toHexString(paramColor.getGreen());
		String str3 = Integer.toHexString(paramColor.getBlue());
		str1 = str1.length() == 1 ? "0" + str1 : str1;
		str2 = str2.length() == 1 ? "0" + str2 : str2;
		str3 = str3.length() == 1 ? "0" + str3 : str3;
		localStringBuffer.append("#");
		localStringBuffer.append(str1);
		localStringBuffer.append(str2);
		localStringBuffer.append(str3);
		return localStringBuffer.toString();
	}

	enum ImageUtilsType {
		auto, jdk, graphicsMagick, imageMagick;
	}
}