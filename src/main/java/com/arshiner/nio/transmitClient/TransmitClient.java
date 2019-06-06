package com.arshiner.nio.transmitClient;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.File;

//import java.io.File;
//import java.io.FileInputStream;
//import org.apache.commons.codec.digest.DigestUtils;
import com.arshiner.common.ClientDTO;
 
public class TransmitClient {
	public  ChannelFuture f = null;
	public  EventLoopGroup group =null;
	public  Bootstrap b=null;

	public void connect(int port, String host, final ClientDTO fileUploadFile,final String ote) throws Exception {
         group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                		ch.pipeline().addLast(new ObjectEncoder());
                		ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                		ch.pipeline().addLast(new FileUploadClientHandler(fileUploadFile));
                		ch.pipeline().addLast(new StringDecoder());
                		ch.pipeline().addLast(new StringEncoder());
                }
            });
            f = b.connect(host, port).sync();
            System.out.println("Agent客户端启动成功");
            f.channel().closeFuture().sync();
        } finally {
        	if (null!=group) {
        		group.shutdownGracefully().sync();
        		group =null;
        		b =null;
        		f =null;
        		if (FileUploadClientHandler.randomAccessFile!=null) {
        			FileUploadClientHandler.randomAccessFile.close();
        			FileUploadClientHandler.randomAccessFile=null;
				}
			}
        }
    }
 
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 9090;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
       while (true) {
    	   ClientDTO uploadFile = new ClientDTO();
           File file = new File("G:/transmit.properties");
           uploadFile.setFile(file);
           uploadFile.setFile_md5("redo");
           uploadFile.setFileClientName(file.getName());
           uploadFile.setStarPos(110);// 文件开始位置
           uploadFile.setLength(file.length());
           uploadFile.setClientLogo("127.0.0.1");
           uploadFile.setFilestatus("Modified");
           TransmitClient tran = new TransmitClient();
//   		tran.connect(new Integer(port), fip, filestatus, "Modified");
//   		tran=null;
   		tran.connect(port, "127.0.0.1",uploadFile,"Modified");
   		tran=null;
	}
    }

}

