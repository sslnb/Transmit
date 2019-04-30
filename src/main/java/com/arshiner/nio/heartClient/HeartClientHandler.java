package com.arshiner.nio.heartClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.arshiner.StartAgent;
import com.arshiner.common.ConfigManager;
import com.arshiner.common.Heart;
import com.arshiner.common.SystemInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
public class HeartClientHandler extends ChannelInboundHandlerAdapter {
	ConfigManager config = ConfigManager.getInstance();
	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> jm = StartAgent.flashpeizhi();
		List<Object> list = new ArrayList<>();
		if (jm == null || jm.isEmpty()) {
			ctx.writeAndFlush(list);
		}
		for (Iterator<Entry<String, Object>> it = jm.entrySet().iterator(); it.hasNext();) {
			Entry<String, Object> entry = it.next();
			Heart heart = new Heart();
			HashMap<String, Object> heartinfo = SystemInfo.getHeartInfo();
			heart.setCp(new Double(heartinfo.get("dev").toString()));
			heart.setCpu(new Double(heartinfo.get("cpu").toString()));
			heart.setNc(new Double(heartinfo.get("mem").toString()));
			heart.setLogo(ConfigManager.properties.getProperty("kip"));
			heart.setJgxtlb(entry.getKey());
			list.add(heart);
		}
		ctx.writeAndFlush(list);
	}
	public Timestamp getTime() {
		Date utilDate = new Date();// util.Date
		Timestamp sqlDate = new Timestamp(utilDate.getTime());// util.Date转sql.Date
		return sqlDate;
	}

	/**
	 * 这里有多少个Handler就会执行多少次
	 */

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
