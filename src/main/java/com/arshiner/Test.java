package com.arshiner;

import org.apache.tomcat.jni.File;

import com.arshiner.common.FilePathName;

public class Test {
	public static void main(String[] args) {
		String str ="1"+FilePathName.FileSepeartor+"2"+FilePathName.FileSepeartor;
		System.out.println(str.indexOf("1"));
	}
}
