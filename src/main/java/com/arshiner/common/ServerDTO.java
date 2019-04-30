package com.arshiner.common;

import java.io.Serializable;

/**
 * 服务端向客户端传的DTO
 * 
 * @author ssl
 *
 */
public class ServerDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7474224282499816391L;
	private long starPos; // 下次传输的位置
	private String currentFilenName; // 当前传输文件名
	private String serverLogo; // 当前链路的服务端ip标识
	private String filestatus; // 操作状态,类型和客户端相同
	private String status; // 传输状态,和客户端相同
	private String file_md5;

	public String getFile_md5() {
		return file_md5;
	}

	public void setFile_md5(String file_md5) {
		this.file_md5 = file_md5;
	}

	public synchronized long getStarPos() {
		return starPos;
	}

	public synchronized void setStarPos(long starPos) {
		this.starPos = starPos;
	}

	public synchronized String getCurrentFilenName() {
		return currentFilenName;
	}

	public synchronized void setCurrentFilenName(String currentFilenName) {
		this.currentFilenName = currentFilenName;
	}

	public synchronized String getServerLogo() {
		return serverLogo;
	}

	public synchronized void setServerLogo(String serverLogo) {
		this.serverLogo = serverLogo;
	}

	public synchronized String getFilestatus() {
		return filestatus;
	}

	public synchronized void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
	}

	public synchronized String getStatus() {
		return status;
	}

	public synchronized void setStatus(String status) {
		this.status = status;
	}

	public synchronized static long getSerialversionuid() {
		return serialVersionUID;
	}

}
