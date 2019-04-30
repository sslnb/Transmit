package com.arshiner.common;

import org.springframework.stereotype.Component;

/**
 * 文件的信息
 * @author 士林
 *
 */
@Component
public class FileStatus {

	private long lastModifed;
	private String filename;
	private long   position=0L;
	private long  filesize =0L;
	
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public long getLastModifed() {
		return lastModifed;
	}
	public void setLastModifed(long l) {
		this.lastModifed = l;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getPosition() {
		return position;
	}
	public void setPosition(long position) {
		this.position = position;
	}
	
	
}
