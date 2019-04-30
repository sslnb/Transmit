/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arshiner.common;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William
 */
public class SysResource {

    public static String gs_os;
    public static String captureName;
	public static InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            //System.out.println("unknown host!");  
        }
        return null;
    }

    public static String getHostIp(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); //get the ip address  
        return ip;
    }

    public static String getHostName(InetAddress netAddress) {
        //System.out.println("ip is: " + netAddress);
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getHostName(); //get the host address  
        return name;
    }


    //保存文本文件
    public static boolean saveToFile(String s_file, String s_txt) {
        //保存配置
        File foption = new File(s_file);
        try {
            if (!foption.exists())//如果文件不存在,则新建.  
            {
                File parentDir = new File(foption.getParent());
                if (!parentDir.exists())//如果所在目录不存在,则新建.  
                {
                    parentDir.mkdirs();
                }
                foption.createNewFile();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(s_txt);
            System.out.println(sb.toString());
            PrintWriter pw = new PrintWriter(new FileWriter(foption), true);
            pw.println(sb.toString());
            pw.close();
            return true;
        } catch (IOException ex) {
            //ex.printStackTrace();
            return false;
        }
    }



    //删除目录下所有文件
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File ftmp = new File(dir, children[i]);
                if (false == ftmp.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

    
    //启动os进程 返回值s_ret为PID
    //s_exec必须带全路径
    public static String startProcess(String s_path, String s_exec) {
        String s_ret = "";
        if (gs_os.startsWith("windows")) {
            s_ret = startWinProcess(s_path, s_exec);
        } else if (gs_os.startsWith("linux")) {
            String[] cmd = {"/bin/sh", "-c", s_exec + " >/dev/null &"};
            File dir = new File(s_path);
            try {
                Process proc = Runtime.getRuntime().exec(cmd, null, dir);
                if (null != proc) {
                    s_ret = getPidByName(s_exec);
                } else {
                    s_ret = "";
                }
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
        } else if (gs_os.startsWith("aix")) {
            String[] cmd = {"/bin/ksh", "-c", s_exec + " >/dev/null &"};
            File dir = new File(s_path);
            try {
                Process proc = Runtime.getRuntime().exec(cmd, null, dir);
                if (null != proc) {
                    s_ret = getPidByName(s_exec);
                } else {
                    s_ret = "";
                }
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                LineNumberReader line = new LineNumberReader(reader);
                String str;
                while ((str = line.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
        }else if (gs_os.startsWith("sunos")) {
        	//加入对solaris系统的支持
            String[] cmd = {"/bin/sh", "-c", s_exec + " >/dev/null &"};
            File dir = new File(s_path);
            try {
                Process proc = Runtime.getRuntime().exec(cmd, null, dir);
                if (null != proc) {
                    s_ret = getPidByName(s_exec);
                } else {
                    s_ret = "";
                }
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                LineNumberReader line = new LineNumberReader(reader);
                String str;
                while ((str = line.readLine()) != null) {
                    System.out.println(str);
                }
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
        } else {
            return "";
        }
        return s_ret;
    }
  
    //检查进程是否存在tasklist /FI "PID eq 7360"
    public static boolean checkProcess(String s_pid) {
        if (("".equals(s_pid)) || ("0".equals(s_pid))) {
            return false;
        }
        BufferedReader bufferedReader = null;
        try {
            Process proc = null;
            String[] cmd = {"", "", ""};
            if (gs_os.startsWith("windows")) {
                //根据进程pid查询
                return checkWinProcess(1, s_pid);
            } else if (gs_os.startsWith("linux")) {
                cmd[0] = "/bin/sh";
                cmd[1] = "-c";
                cmd[2] = "pwdx " + s_pid;
            } else if (gs_os.startsWith("aix")) {
                cmd[0] = "/bin/ksh";
                cmd[1] = "-c";
                //为了pid完全匹配
                cmd[2] = "ps -ef |awk '{print \" \"$2\" \"}'|grep \" \"" + s_pid + "\" \"";
            } else if (gs_os.startsWith("sunos")) {//支持solaris系统
                cmd[0] = "/sbin/sh";
                cmd[1] = "-c";
                //为了pid完全匹配
                cmd[2] = "ps -ef |awk '{print \" \"$2\" \"}'|grep \" \"" + s_pid + "\" \"";
            } else {
                return false;
            }
            proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if ((line.contains(s_pid)) && (!line.contains("No such process"))) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            //ex.printStackTrace();
            return false;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    //停止os进程
    public static boolean stopProcess(String s_pid) {
        if (("".equals(s_pid)) || ("0".equals(s_pid))) {
            return true;
        }
        Process process = null;
        if (gs_os.startsWith("windows")) {
            String cmd = "cmd /c taskkill /pid " + s_pid + " /T /F";
            try {
                process = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else if (gs_os.startsWith("linux")) {
            String[] cmd = {"/bin/sh", "-c", "kill -9 " + s_pid};
            try {
                process = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else if (gs_os.startsWith("aix")) {
            String[] cmd = {"/bin/ksh", "-c", "kill -9 " + s_pid};
            try {
                process = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }else if (gs_os.startsWith("sunos")) {
            String[] cmd = {"/sbin/sh", "-c", "kill -9 " + s_pid};
            try {
                process = Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
        try {
            process.waitFor();
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(SysResource.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    //windows下检查进程
    public static boolean checkWinProcess(int i_type, String s_in) {
        if (("".equals(s_in)) || ("0".equals(s_in))) {
            return false;
        }
        Kernel32 kernel32 = Kernel32.INSTANCE;
        //Kernel32 kernel32 = (Kernel32) Native.loadLibrary(Kernel32.class, W32APIOptions.UNICODE_OPTIONS);
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
        WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        try {
            String s_pid = "";
            while (kernel32.Process32Next(snapshot, processEntry)) {
                //根据进程pid查询
                if (1 == i_type) {
                    s_pid = processEntry.th32ProcessID.toString();
                    //ll_pid = (long)processEntry.th32ProcessID;
                    if (s_in.equals(s_pid)) {
                        //System.out.println(processEntry.th32ProcessID + "\t" + Native.toString(processEntry.szExeFile));
                        return true;
                    }
                } else {  //根据进程名查询
                    if ((s_in.toLowerCase()).equals(Native.toString(processEntry.szExeFile).toLowerCase())) {
                        //System.out.println(processEntry.th32ProcessID + "\t" + Native.toString(processEntry.szExeFile));
                        return true;
                    }
                }
                //System.out.println(processEntry.th32ProcessID + "\t" + Native.toString(processEntry.szExeFile));
            }
        } finally {
            kernel32.CloseHandle(snapshot);
        }
        return false;
    }

    //windows下启动进程 
    public static String startWinProcess(String s_path, String s_cmdline) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        SECURITY_ATTRIBUTES procSecAttr = new SECURITY_ATTRIBUTES();
        SECURITY_ATTRIBUTES threadSecAttr = new SECURITY_ATTRIBUTES();
        WinBase.PROCESS_INFORMATION.ByReference byRef = new WinBase.PROCESS_INFORMATION.ByReference();
        WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
        //正常运行，隐藏界面
        startupInfo.dwFlags = 1;
        startupInfo.wShowWindow = new WinDef.WORD(0);
        boolean success = kernel32.CreateProcess(null, s_cmdline, procSecAttr,
                threadSecAttr, false, new DWORD(0x00000010), null, s_path,
                startupInfo, byRef);
        if (!success) {
            return "0";
        } else {
            //System.out.println(byRef.dwProcessId);
            return byRef.dwProcessId.toString();
        }
    }

    //根据进程名，获取pid
    public static String getPidByName(String s_name) {
        if (("".equals(s_name)) || ("0".equals(s_name))) {
            return "";
        }
        String[] cmd = {"", "", ""};
        if (gs_os.startsWith("linux")) {
            cmd[0] = "/bin/sh";
            cmd[1] = "-c";
            cmd[2] = "ps -ef|grep " + s_name + "|grep -v grep|awk '{print $2}'";
        } else if (gs_os.startsWith("aix")) {
            cmd[0] = "/bin/ksh";
            cmd[1] = "-c";
            cmd[2] = "ps -ef|grep " + s_name + "|grep -v grep|awk '{print $2}'";
        }else if (gs_os.startsWith("sunos")) {//支持solaris系统
            cmd[0] = "/sbin/sh";
            cmd[1] = "-c";
            cmd[2] = "ps -ef|grep " + s_name + "|grep -v grep|awk '{print $2}'";
        }  else {
            return "";
        }
        BufferedReader bufferedReader = null;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            proc.waitFor();
            bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if (!"".equals(line)) {
                    return line.trim();
                }
            }
            return "";
        } catch (Exception ex) {
            //ex.printStackTrace();
            return "";
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static String updateHostID() {
        String s_mac = "";
        String s_ret = "";
        String s_title = "";
        Process process = null;
        BufferedReader br = null;
        try {
            if (gs_os.startsWith("windows")) {
                s_title = "ADB20WIN";
                process = Runtime.getRuntime().exec("wmic nicconfig where IPEnabled=TRUE get MACAddress");
            } else if (gs_os.startsWith("linux")) {
                String[] cmd = {"/bin/sh", "-c", "ifconfig |grep HWaddr|awk '{print $5}'"};
                s_title = "ADB20LUX";
                process = Runtime.getRuntime().exec(cmd);
            } else if (gs_os.startsWith("aix")) {
                String[] cmd = {"/bin/ksh", "-c", "netstat -v |grep Hardware|awk '{print $3}'"};
                s_title = "ADB20AIX";
                process = Runtime.getRuntime().exec(cmd);
            } else if (gs_os.startsWith("sunos")) {//支持solaris系统
                String[] cmd = {"/sbin/sh", "-c", "netstat -p |grep `hostname`|awk '{print $5}'"};
                s_title = "ADB20SUN";
                process = Runtime.getRuntime().exec(cmd);
            }else {
                return "";
            }
            //没有下面这句，在win2000下执行会导致服务器端主线程挂起，现服务器端已经改为独立线程来发命令
            process.getOutputStream().close();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                //System.out.println(line.trim());
                line = line.trim();
                if (!"MACAddress".equals(line)) {
                    s_ret = s_ret + line.replaceAll(":", "");
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return "";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    //e.printStackTrace();
                    return "";
                }
            }
        }
        s_ret = (s_ret.toUpperCase() + "000000000000000000000000").substring(0, 24);
        return s_title + s_ret;
    }

	public static String getGs_os() {
		return gs_os;
	}

	public static void setGs_os(String gs_os) {
		if (gs_os.toLowerCase().startsWith("windows")) {
			SysResource.gs_os = "windows";
			captureName="capture.exe";
		}
		else if (gs_os.toLowerCase().startsWith("aix")) {
			SysResource.gs_os = "aix";
			captureName="capture";
		}
		else if (gs_os.toLowerCase().startsWith("sunos")) {
			SysResource.gs_os = "sunos";
			captureName="capture";
		}
		else if (gs_os.toLowerCase().startsWith("linux")) {
			SysResource.gs_os = "linux";
			captureName="capture";
		}else{
			SysResource.gs_os = "linux";
			captureName="capture";
		}
	}



}
