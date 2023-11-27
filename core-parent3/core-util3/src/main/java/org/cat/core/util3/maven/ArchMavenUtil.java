package org.cat.core.util3.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * 
 * @author 王云龙
 * @date 2022年5月11日 下午4:17:20
 * @version 1.0
 * @description Maven工具类
 *
 */
public class ArchMavenUtil {
	
	public static Model getPomModel(String pomFullPath) {
		Model model = getPomModel(new File(pomFullPath));
		return model;
	}
	
	public static Model getPomModel(File pomFile) {
		Model model;
		try {
			model = getPomModel(new FileReader(pomFile));
		} catch (FileNotFoundException e) {
			throw new ArchMavenUtilException("找不到pom文件("+pomFile.getPath()+")", e);
		}
		return model;
	}
	
	public static Model getPomModel(FileReader fileReader) {
		MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
		Model model = null;
		try {
			model = mavenXpp3Reader.read(fileReader);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
			throw new ArchMavenUtilException("获取pom文件异常(mavenXpp3Reader.read(fileReader))", e);
		}
		return model;
	}
	
	public static Model getPomModel(InputStream inputStream) {
		MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
		Model model = null;
		try {
			model = mavenXpp3Reader.read(inputStream);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
			throw new ArchMavenUtilException("获取pom文件异常(mavenXpp3Reader.read(inputStream))", e);
		}
		return model;
	}
}
