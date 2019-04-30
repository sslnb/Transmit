package com.arshiner.common;
import java.io.File;
import java.io.Serializable;
/**
 * 里面需要包含一下内容，
 * 1）当前文件名的绝对目录，
 * 2）文件
 * 3）起始位置
 * 4）终止位置
 * 5）要传数据
 * 6）客户端标识
 * 7)客户端系统版本（如果需要主机是Linux备机是window系统）
 * 8)文件传输状态： 这个文件是否传输成功服务端回写后删除此文件
 * @author 孙士林
 * ClientDTO 模式 数据传输对象，对原有要传输的原生数据进行封装
 */
public class ClientDTO implements Serializable {


    private static final long serialVersionUID = 1L;
    private File file;// 文件，这个并不会传过去
    private long  length;
    private String fileClientName;// 文件名
    private long starPos;// 开始位置
    private byte[] bytes;// 文件字节数组
    private int endPos;// 结尾位置
    private String clientLogo;//客户端 JGXTLB标识
    private boolean status ;//数据文件传输状态，成功，失败，正在传输
    private String ip;	//客户端系统版本
    private String filestatus;
    private String file_md5;
    
    public String getFile_md5() {
		return file_md5;
	}

	public void setFile_md5(String file_md5) {
		this.file_md5 = file_md5;
	}

	public synchronized  long getStarPos() {
        return starPos;
    }

    public synchronized String getClientLogo() {
		return clientLogo;
	}

	public synchronized void setClientLogo(String clientLogo) {
		this.clientLogo = clientLogo;
	}

	public synchronized void setStarPos(long starPos) {
        this.starPos = starPos;
    }

    public synchronized int getEndPos() {
        return endPos;
    }

    public synchronized void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public synchronized byte[] getBytes() {
        return bytes;
    }

    public synchronized void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public synchronized File getFile() {
        return file;
    }

    public synchronized void setFile(File file) {
        this.file = file;
    }

	public synchronized String getFileClientName() {
		return fileClientName;
	}

	public synchronized void setFileClientName(String fileClientName) {
		this.fileClientName = fileClientName;
	}

	public synchronized long getLength() {
		return length;
	}

	public synchronized void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}


	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getFilestatus() {
		return filestatus;
	}

	public void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
	}

}