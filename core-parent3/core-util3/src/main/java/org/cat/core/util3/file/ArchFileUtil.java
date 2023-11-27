package org.cat.core.util3.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author 王云龙
 * @date 2021年7月14日 下午3:07:51
 * @version 1.0
 * @description 文件以及路径处理的工具类
 *
 */
public class ArchFileUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午3:48:00
	 * @version 1.0
	 * @description 非递归获取目录下的所有符合正则表达式的文件（不包含目录中的目录）  
	 * @param dirPath 目录
	 * @param regex 当该正则表达式为""或者null的时候，则不会使用正则表达式匹配，即匹配全部
	 * @return 只包含所有文件的List
	 */
	public static List<File> getFileList(String dirPath,String regex){
		List<File> fileList=new ArrayList<File>();
		
		File fileDir=new File(dirPath);
		File[] fileArray=fileDir.listFiles();
		if(fileArray==null||fileArray.length==0){
			return fileList;
		}
		for (File file: fileArray) {
			if(file.isFile()){
				String fileName=file.getName();
				if(StringUtils.isNotBlank(regex)){
					if(fileName.matches(regex)){
						fileList.add(file);
					}
				}else{
					fileList.add(file);
				}
			}
		}
		return fileList;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午3:51:09
	 * @version 1.0
	 * @description 非递归获取目录下的所有符合正则表达式的文件（不包含目录中的目录），该方法会进行一些验证并抛出异常 
	 * @param dirPath 目录
	 * @param regex 当该正则表达式为""或者null的时候，则不会使用正则表达式匹配，即匹配全部
	 * @return 只包含所有文件的List
	 */
	public static List<File> getFileListAndVerify(String dirPath, String regex) {
		File fileDir=new File(dirPath);
		if(!fileDir.exists()){
			throw new IllegalArgumentException("目录["+dirPath+"]不存在");
		}
		if(!fileDir.isDirectory()) {
			throw new IllegalArgumentException("["+dirPath+"]不是一个目录");
		}
		
		List<File> fileList = getFileList(dirPath, regex);
		return fileList;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午3:28:26
	 * @version 1.0
	 * @description 非递归获取目录下的所有的文件（不包含目录中的目录） ，该方法会进行一些验证并抛出异常  
	 * @param dirPath 目录
	 * @return List<File>，不会为null
	 */
	public static List<File> getFileList(String dirPath){
		return getFileList(dirPath, null);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午3:52:00
	 * @version 1.0
	 * @description 非递归获取目录下的所有的文件（不包含目录中的目录）  
	 * @param dirPath 目录
	 * @return List<File>，不会为null
	 */
	public static List<File> getFileListAndVerify(String dirPath) {
		return getFileListAndVerify(dirPath, null);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午3:31:46
	 * @version 1.0
	 * @description 非递归获取目录下的所有符合正则表达式的文件（不包含目录中的目录） ,并排序 
	 * @param dirPath 目录
	 * @param regex 当该正则表达式为""或者null的时候，则不会使用正则表达式匹配
	 * @return List<File>，不会为null
	 */
	public static List<File> getSortFileList(String dirPath,String regex){
		List<File> fileList=getFileList(dirPath,regex);
		fileList.sort(new Comparator<File>() {
			/**
			 * 如果要按照升序排序，
			 * 则o1 小于o2，返回-1（负数），相等返回0，o1大于02返回1（正数）
			 * 如果要按照降序排序
			 * 则o1 小于o2，返回1（正数），相等返回0，01大于02返回-1（负数）
			 */
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
		            return -1;
		        if (o1.isFile() && o2.isDirectory())
		            return 1;
		        return o1.getName().compareTo(o2.getName());
			}
		    
		});
		return fileList;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午3:32:43
	 * @version 1.0
	 * @description 非递归获取目录下的所有的文件（不包含目录中的目录） ,并排序  
	 * @param dirPath 目录
	 * @return List<File>，不会为null
	 */
	public static List<File> getSortFileList(String dirPath){
		return getSortFileList(dirPath, null);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午4:07:33
	 * @version 1.0
	 * @description 创建目录，当目录已经存在会返回true，当目录不存在则创建，创建完成后返回true 
	 * 			如果该path为一个file，则抛出异常
	 * @param path 目录路径名称
	 * @return 目录是否可用的结果，true or false
	 */
	public static void mkDir(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				throw new IllegalArgumentException("["+path+"]已存在，并且不是一个目录");
			}
		} else {
			boolean result=file.mkdirs();
			if(!result) {
				throw new CreateDirFailException("["+path+"]创建失败，file.mkdirs()返回false");
			}
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午4:12:27
	 * @version 1.0
	 * @description 创建一个文件 
	 * @param file 需要创建的文件
	 * @param overWrite 当文件已经存在时是否删除后重新进行创建
	 * @return true：创建成功；false：创建失败（失败原因参考{@linkplain java.io.File#createNewFile()}
	 * 		当文件已存在但是其是一个目录时抛出FileIsNotFileException
	 * 		当文件已存在并且其时一个文件时，如果overWrite=false则返回true，如果overWrite=true则删除文件
	 * 		当file.ParentFile存在并且是一个目录时则创建新文件
	 * 		当file.ParentFile不存在则创建父目录，如果已经有重名的文件则抛出异常，否则将创建父目录，然后创建该文件
	 */
	public static void mkFile(File file,boolean overWrite) {
		if(file.exists() && file.isDirectory()) {
			throw new IllegalArgumentException("["+file.getName()+"]已存在，并且不是一个文件");
		}
		if(file.exists() && file.isFile()) {
			if(overWrite) {
				file.delete();
			}else {
				throw new IllegalArgumentException("["+file.getName()+"]已存在，并且您不允许覆盖该文件");
			}
		}
		////文件不存在 or 因为需要overWrite导致已经被删除
		
		//上层目录存在
		if (file.getParentFile().exists() && file.getParentFile().isDirectory()) {
			boolean result = false;
			try {
				result = file.createNewFile();
			} catch (IOException e) {
				throw new CreateFileFailException("["+file.getName()+"]文件创建失败", e);
			}
			if(!result) {
				throw new CreateFileFailException("["+file.getName()+"]文件创建失败，file.createNewFile()返回false");
			}
		}
	
		//上层目录不存在
		String parentPath = file.getParent();
		
		try {
			mkDir(parentPath);
		} catch (Exception e) {
			throw new CreateDirFailException("创建文件的上层目录失败，失败原因为："+e.getMessage(), e);
		}
		boolean result;
		try {
			result = file.createNewFile();
		} catch (IOException e) {
			throw new CreateFileFailException("["+file.getName()+"]文件创建失败", e);
		}
		if(!result) {
			throw new CreateFileFailException("["+file.getName()+"]文件创建失败，file.createNewFile()返回false");
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午4:17:07
	 * @version 1.0
	 * @description 创建一个文件 
	 * @param fileFullName 需要创建的文件名
	 * @param overWrite 当文件已经存在时是否删除后重新进行创建
	 * @return true：创建成功；false：创建失败（失败原因参考{@linkplain java.io.File#createNewFile()}
	 * 		当文件已存在但是其是一个目录时抛出FileIsNotFileException
	 * 		当文件已存在并且其是一个文件时，如果overWrite=false则返回true，如果overWrite=true则删除文件
	 * 		当file.ParentFile存在并且是一个目录时则创建新文件
	 * 		当file.ParentFile不存在则创建父目录，如果已经有重名的文件则抛出异常，否则将创建父目录，然后创建该文件
	 */
	public static void mkFile(String fileFullName, boolean overWrite) {
		File file = new File(fileFullName);
		mkFile(file, overWrite);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2019年4月2日 上午11:38:30
	 * @version 1.0
	 * @description 获取文件的后缀名 
	 * @param fileName 原始文件名称
	 * @param containPoint 是否包含点
	 * @return
	 */
	public static String getFileNameSuffix(String fileName,boolean containPoint) {
		int index=fileName.lastIndexOf(".");
		if(!containPoint) {
			index+=1;
		}
		String suffix = fileName.substring(index);
		return suffix;
	}
}
