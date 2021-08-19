package com.projectweb.projectwebmvc;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TesteCrypt {

	public static void main(String[] args) {
		
		
		BCryptPasswordEncoder ebc = new BCryptPasswordEncoder();
		String senha = ebc.encode("1234");
		System.out.println(senha);
	}

}
