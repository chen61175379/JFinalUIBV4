package com.platform.tools.security.md;

import java.security.MessageDigest;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import com.jfinal.log.Log;

/**
 * MD系列加密组件<br>
 * Tiger、Whirlpool和GOST3411共3种算法
 */
public abstract class ToolMD {

	@SuppressWarnings("unused")
	private static final Log log = Log.getLog(ToolMD.class);

	/**
	 * Tiger加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static byte[] encodeTiger(byte[] data) throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Tiger");

		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * TigerHex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static String encodeTigerHex(byte[] data) throws Exception {

		// 执行消息摘要
		byte[] b = encodeTiger(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * Whirlpool加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static byte[] encodeWhirlpool(byte[] data) throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Whirlpool");

		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * WhirlpoolHex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static String encodeWhirlpoolHex(byte[] data) throws Exception {

		// 执行消息摘要
		byte[] b = encodeWhirlpool(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * GOST3411加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static byte[] encodeGOST3411(byte[] data) throws Exception {

		// 加入BouncyCastleProvider支持
		Security.addProvider(new BouncyCastleProvider());

		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("GOST3411");

		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * GOST3411Hex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * 
	 * @throws Exception
	 */
	public static String encodeGOST3411Hex(byte[] data) throws Exception {

		// 执行消息摘要
		byte[] b = encodeGOST3411(data);

		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}
	
}
