package com.arshiner.nio.heartClient;


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
 
public class HeartClient {
    public  void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                		ch.pipeline().addLast(new ObjectEncoder());
                		ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                		ch.pipeline().addLast(new HeartClientHandler());
                		ch.pipeline().addLast(new StringDecoder());
                		ch.pipeline().addLast(new StringEncoder());
                	
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            if (!f.isSuccess()) {
				return ;
			}
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
  public static void main(String[] args) throws Exception {
  new HeartClient().connect(9091, "127.0.0.1");
}


}

