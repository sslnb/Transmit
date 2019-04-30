package com.arshiner.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 获取所有的各个文件路径 存量文件路径 已上传存量文件路径 增量文件路径 已上传增量文件路径 日志解析生成路径 日志文件路径 dll文件路径
 * 
 * @author 士林
 *
 */
public class FilePathName {

	public static final String ROOT = System.getProperty("user.dir")+System.getProperty("file.separator");// 根文件路径
	public static final String FileSepeartor = System.getProperty("file.separator");// 根文件路径
	public static final String RZJXWJPath =  "RZJXWJPath";
	public static final String CLStanbyPath = "CLStanbyPath";
	public static final String CLDIDPath =  "CLDIDPath";
	public static final String ZLStanbyPath =  "ZLStanbyPath";
	public static final String ZLDIDPath =  "ZLDIDPath";
	public static final String CACHE =  "CACHE";

//	public static void main(String[] args) {
//		System.out.println(ROOT);
//		System.out.println(ROOT.lastIndexOf(FileSepeartor));
//		System.out.println(getfileName());
//		System.out.println(CLStanbyPath);
//		try {
//			FilePathName.copyDir(FilePathName.getfileName()+"capture", FilePathName.getfileName()+"capture");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("创建测试");
//	}

	// dll文件的父目录
	public static String getfileName() {
		int lastindexOf = System.getProperty("user.dir").lastIndexOf(FileSepeartor);
		String lastindex = System.getProperty("user.dir").substring(0, lastindexOf + 1);
		return lastindex;
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourcePath
	 * @param newPath
	 * @throws IOException
	 */
	public static void copyDir(String sourcePath, String newPath) throws IOException {
		File file = new File(sourcePath);
		if (!file.exists()) {
			return ;
		}
		String[] filePath = file.list();
		if (!(new File(newPath)).exists()) {
			(new File(newPath)).mkdir();
		}

		for (int i = 0; i < filePath.length; i++) {
			if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
				copyDir(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
			}

			if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
				copyFile(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
			}
		}
	}
	/**
	 * 复制文件夹
	 * 
	 * @param sourcePath
	 * @param newPath
	 * @throws IOException
	 */
	public static void mvDir(String sourcePath, String newPath) throws IOException {
		File file = new File(sourcePath);
		if (!file.exists()) {
			return ;
		}
		String[] filePath = file.list();
		if (!(new File(newPath)).exists()) {
			(new File(newPath)).mkdir();
		}
		
		for (int i = 0; i < filePath.length; i++) {
			if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
				copyDir(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
				File   file1 = new File(sourcePath + File.separator + filePath[i]);
				file1.delete();
			}
			
			if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
				copyFile(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param oldPath
	 * @param newPath
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String newPath) throws IOException {
		File oldFile = new File(oldPath);
		File file = new File(newPath);
		FileInputStream in = new FileInputStream(oldFile);
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int readByte = 0;
		while ((readByte = in.read(buffer)) != -1) {
			out.write(buffer, 0, readByte);
		}
		in.close();
		out.close();
	}

	/**
	 * 删除单个文件
	 *
	 * @param fileName
	 *            要删除的文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}
}
