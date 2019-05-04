package com.arshiner.nio.transmitClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import com.arshiner.common.ClientDTO;
import com.arshiner.common.ServerDTO;
public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
	private int byteRead;
	private long start = 0;
	private int lastLength = 0;
	public RandomAccessFile randomAccessFile;
	private ClientDTO fileUploadFile;// 可能线程不安全
	
	public FileUploadClientHandler(ClientDTO ef) {
		if (ef.getFile().exists()) {
			if (!ef.getFile().isFile()) {
				return;
			}
		}
		this.fileUploadFile = ef;
	}

	// 客户端连接成功就会出发此函数
	/**
	 * 如果服务端发送策略，客户端接受策略，如果不发送策略
	 * 
	 */
	public void channelActive(ChannelHandlerContext ctx) throws IOException {
		try {
			// start = fileUploadFile.getStarPos();
			start = 0l;
			randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
			if (randomAccessFile.length() < (50*1024*1024)) {
				lastLength = (int) randomAccessFile.length();
			} else {
				lastLength = 50*1024*1024;
			}
			// start大于文件的长度 重传
			start = 0l;
			randomAccessFile.seek(start);
			byte[] bytes = new byte[lastLength];
			/**
			 * 这里获取配置文件中start的位置，
			 */
			randomAccessFile.seek(start);
			// 如果start小于文件的长度
			if ((byteRead = randomAccessFile.read(bytes)) != -1) {
				fileUploadFile.setEndPos(byteRead);
				fileUploadFile.setBytes(bytes);
				fileUploadFile.setStarPos(start);
				ctx.writeAndFlush(fileUploadFile);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			} else {
				ctx.channel().close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException i) {
		}
	}


	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ServerDTO) {
			ServerDTO server = (ServerDTO) msg;
			start = server.getStarPos();
			if (start != -1) {
				randomAccessFile.seek(start);
				int a = (int) (randomAccessFile.length() - start);
				int b = (int) (byteRead);
				if (a < b) {
					lastLength = a;
				}
				byte[] bytes = new byte[lastLength];
				if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
					fileUploadFile.setEndPos(byteRead);
					fileUploadFile.setBytes(bytes);
					fileUploadFile.setStarPos(start);
					try {
						ctx.writeAndFlush(fileUploadFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				} else {
					randomAccessFile.close();
					// 这里需要进行监控
					randomAccessFile.close();
					ctx.close();
				}
			} // 如果不是对象，那么可能是有问题如果是true，则是错的将此文件重传
		} else if ((boolean) msg) {
			randomAccessFile.close();
			ctx.close();
		}
	}

	/**
	 * 这里有多少个Handler就会执行多少次
	 * 
	 */

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
