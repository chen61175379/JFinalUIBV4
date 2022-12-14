package com.junit.platform.tool.security;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import com.platform.tools.security.ToolDSA;

/**
 * DSA签名校验
 */
public class TestDSA {

	/**
	 * 公钥
	 */
	private byte[] publicKey;

	/**
	 * 私钥
	 */
	private byte[] privateKey;

	/**
	 * 初始化密钥
	 * 
	 * @throws Exception
	 */
	@Before
	public void initKey() throws Exception {
		Map<String, Object> keyMap = ToolDSA.initKey();

		publicKey = ToolDSA.getPublicKey(keyMap);

		privateKey = ToolDSA.getPrivateKey(keyMap);

		System.err.println("公钥: \n" + Base64.encodeBase64String(publicKey));
		System.err.println("私钥： \n" + Base64.encodeBase64String(privateKey));
	}

	/**
	 * 校验签名
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		String inputStr = "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作"
				+ "录入密码，检测录入的密码和数据库密码是否是123456，如果是，强制弹出密码修改框改密码，修改完后重新录入新密码继续登录操作123456";
		byte[] data = inputStr.getBytes();

		// 产生签名
		byte[] sign = ToolDSA.sign(data, privateKey);
		System.err.println("签名:\r" + Hex.encodeHexString(sign));

		// 验证签名
		boolean status = ToolDSA.verify(data, publicKey, sign);
		System.err.println("状态:\r" + status);
		assertTrue(status);

	}

}
