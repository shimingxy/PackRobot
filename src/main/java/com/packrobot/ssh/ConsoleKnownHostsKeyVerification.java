package com.packrobot.ssh;

import java.io.IOException;
import java.io.File;  

import com.sshtools.j2ssh.transport.AbstractKnownHostsKeyVerification;  
import com.sshtools.j2ssh.transport.InvalidHostFileException;  
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;  
  
public class ConsoleKnownHostsKeyVerification extends  AbstractKnownHostsKeyVerification {  

    public ConsoleKnownHostsKeyVerification(String host,String username) throws InvalidHostFileException {  
    	super(new File(System.getProperty("user.home"), ".ssh4java" + File.separator   +username+"@"+host+ "_hosts.key").getAbsolutePath());  
    	System.out.println("known_hosts file is : "   + new File(System.getProperty("user.home"), ".ssh4java" + File.separator   +username+"@"+host+ "_hosts.key").getAbsolutePath()); 
    } 
    
    public ConsoleKnownHostsKeyVerification(String knownhosts) throws InvalidHostFileException {  
        super(knownhosts);  
    }  
    
    public void onHostKeyMismatch(String host, SshPublicKey pk,  SshPublicKey actual) {  
        try {  
            System.out.println("The host key supplied by " + host + " is: "   + actual.getFingerprint());  
            System.out.println("The current allowed key for " + host + " is: "   + pk.getFingerprint());  
            getResponse(host, pk);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void onUnknownHost(String host, SshPublicKey pk) {  
        try {  
            System.out.println("The host " + host  + " is currently unknown to the system");  
            System.out.println("The host key fingerprint is: "   + pk.getFingerprint());  
            getResponse(host, pk);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * ��ȡ�û��������Ϣ���ж��Ƿ������������ 
     * <p> 
     * �޸ģ�xxx ��ȥ�������л�ȡ��Ϣ��ֱ�ӽ��ܹ��ף�ע�͵��Ĵ���ΪԴ�� 
     */  
    private void getResponse(String host, SshPublicKey pk)  throws InvalidHostFileException, IOException {  
        if (isHostFileWriteable()) {  
            allowHost(host, pk, true);  
        }  
    }  
}  

