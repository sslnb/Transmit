package com.arshiner.nio.transmitServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.arshiner.common.ClientDTO;
import com.arshiner.common.FilePathName;
import com.arshiner.common.ServerDTO;

/**
 * 传输文件处理
 * 
 * @author 士林
 *
 */
public class FileUploadServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = Logger.getLogger(FileUploadServerHandler.class);
	private int byteRead;
	private volatile long start = 0;
	private static int count = 0;
	private static int size = 0;
	private volatile boolean status = false;
	private String file_dir = FilePathName.ROOT;
	public static RandomAccessFile randomAccessFile;
	private final ServerDTO serDto = new ServerDTO();// 可能线程不安全
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-ss");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
	}

	String type = "log";

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ClientDTO) {
			ClientDTO ef = (ClientDTO) msg;
			if (ef.getFilestatus().equals("Modified")) {
				byteRead = ef.getEndPos();
				byte[] bytes = ef.getBytes();
				String path = "";
				if (ef.getFile_md5().equals("redo")) {
					type = "redo";
				}
				path = file_dir + ef.getFileClientName();
				File file1 = new File(path);
				// 如果此文件存再 删除
				if (!type.equals("redo")) {
					if (file1.exists() && count == 0) {
						file1.delete();
					}
				}
				count++;
				File file = new File(path);
				// 判断目标文件所在的目录是否存在
				if (!file.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					if (!file.getParentFile().mkdirs()) {
					}
				}
				// 创建文件后如果数据长度为0
				if (bytes.length == 0) {
					ctx.channel().close();
				}
				status = false;
				start = ef.getStarPos();
				randomAccessFile = new RandomAccessFile(file, "rw");// 第一次初始化
				randomAccessFile.seek(start);
				randomAccessFile.write(bytes);
				start = start + byteRead;
				serDto.setCurrentFilenName(ef.getFileClientName());
				InetAddress addr;
				String host = "";
				try {
					addr = InetAddress.getLocalHost();
					host = addr.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				serDto.setServerLogo(host);
				serDto.setStarPos(start);
				serDto.setStatus("传输中");
				if (start < (ef.getLength())) {
					status = true;
					randomAccessFile.close();
					ctx.writeAndFlush(serDto);
				} else {
					status = true;
					randomAccessFile.close();
					count = 0;
					// 修改
					ctx.channel().close();
					// 调用文件拷贝方法
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
