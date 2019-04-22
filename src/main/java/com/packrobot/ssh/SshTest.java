package com.packrobot.ssh;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;

public class SshTest {

	public SshTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		SshClient sshClient=new SshClient();
		
		PasswordAuthenticationClient pwd=new PasswordAuthenticationClient();
		pwd.setUsername("rwmsapp");
		pwd.setPassword("rwmsapp");
		ConsoleKnownHostsKeyVerification console=new ConsoleKnownHostsKeyVerification("10.200.8.158","rwmsapp");
		
		sshClient.connect("10.200.8.158",22,console);
		
		int connResult=sshClient.authenticate(pwd);
		
		if(connResult==AuthenticationProtocolState.COMPLETE) {
			System.out.println("COMPLETE");
			SessionChannelClient channel=sshClient.openSessionChannel();
			channel.executeCommand("touch /rwmsapp/bak/smhssh.txt");
		}else {
			System.out.println("FAIL");
		}
	}

}
