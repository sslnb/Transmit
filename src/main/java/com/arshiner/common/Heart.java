package com.arshiner.common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 心跳
 * @author MSI-PC
 *
 */
public class Heart implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double cpu;
	private double nc;
	private double cp;
	private String xlh;//心跳序列号
	private String logo;
	private Timestamp time ;
	private String jgxtlb;
	
	public String getJgxtlb() {
		return jgxtlb;
	}

	public void setJgxtlb(String jgxtlb) {
		this.jgxtlb = jgxtlb;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public  Heart (){
		
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getXlh() {
		return xlh;
	}
	public void setXlh(String xlh) {
		this.xlh = xlh;
	}
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	public double getNc() {
		return nc;
	}
	public void setNc(double nc) {
		this.nc = nc;
	}
	public double getCp() {
		return cp;
	}
	public void setCp(double cp) {
		this.cp = cp;
	}
	
}
