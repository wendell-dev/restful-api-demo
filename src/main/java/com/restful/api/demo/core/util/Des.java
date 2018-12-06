package com.restful.api.demo.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.restful.api.demo.core.exception.SystemException;

/**
 * DES加密解密
 * 
 * @author wendell
 */
public class Des {

	private Des() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 加密字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String encode(String data) {
		return new String(encode(data.getBytes()));
	}

	/**
	 * 解密字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String decode(String data) {
		return new String(decode(data.toCharArray()));
	}

	/**
	 * 编码byte[]
	 * 
	 * @param data
	 * @return
	 */
	public static char[] encode(byte[] data) {
		char[] out = new char[((data.length + 2) / 3) * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;

			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}

	/**
	 * 解码byte[]
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] decode(char[] data) {

		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ix++) {
			if ((data[ix] > 255) || codes[data[ix]] < 0) {
				--tempLen; // ignore non-valid chars and padding
			}
		}
		// calculate required length:
		// -- 3 bytes for every 4 valid base64 chars
		// -- plus 2 bytes if there are 3 extra base64 chars,
		// or plus 1 byte if there are 2 extra.

		int len = (tempLen / 4) * 3;
		if ((tempLen % 4) == 3) {
			len += 2;
		}
		if ((tempLen % 4) == 2) {
			len += 1;

		}
		byte[] out = new byte[len];

		int shift = 0; // # of excess bits stored in accum
		int accum = 0; // excess bits
		int index = 0;

		// we now go through the entire array (NOT using the 'tempLen' value)
		for (int ix = 0; ix < data.length; ix++) {
			int value = (data[ix] > 255) ? -1 : codes[data[ix]];

			if (value >= 0) { // skip over non-code
				accum <<= 6; // bits shift up by 6 each time thru
				shift += 6; // loop, with new bits being put in
				accum |= value; // at the bottom.
				if (shift >= 8) { // whenever there are 8 or more shifted in,
					shift -= 8; // write them out (from the top, leaving any
					out[index++] = // excess at the bottom for next iteration.
							(byte) ((accum >> shift) & 0xff);
				}
			}
		}

		// if there is STILL something wrong we just have to throw up now!
		if (index != out.length) {
			throw new SystemException("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
		}
		return out;
	}

	/**
	 * 加密文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void encode(File file) throws IOException {
		if (!file.exists()) {
			System.exit(0);
		} else {
			byte[] decoded = readBytes(file);
			char[] encoded = encode(decoded);
			writeChars(file, encoded);
		}
	}

	/**
	 * 解码文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void decode(File file) throws IOException {
		if (!file.exists()) {
			System.exit(0);
		} else {
			char[] encoded = readChars(file);
			byte[] decoded = decode(encoded);
			writeBytes(file, decoded);
		}
	}

	//
	// code characters for values 0..63
	//
	private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

	//
	// lookup table for converting base64 characters to value in range 0..63
	//
	private static byte[] codes = new byte[256];
	static {
		for (int i = 0; i < 256; i++) {
			codes[i] = -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			codes[i] = (byte) (i - 'A');
		}

		for (int i = 'a'; i <= 'z'; i++) {
			codes[i] = (byte) (26 + i - 'a');
		}
		for (int i = '0'; i <= '9'; i++) {
			codes[i] = (byte) (52 + i - '0');
		}
		codes['+'] = 62;
		codes['/'] = 63;
	}

	private static byte[] readBytes(File file) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream fis = new FileInputStream(file);
				InputStream is = new BufferedInputStream(fis)) {
			int count = 0;
			byte[] buf = new byte[16384];
			while ((count = is.read(buf)) != -1) {
				if (count > 0) {
					baos.write(buf, 0, count);
				}
			}
			return baos.toByteArray();
		}
	}

	private static char[] readChars(File file) throws IOException {
		try (CharArrayWriter caw = new CharArrayWriter();
				Reader fr = new FileReader(file);
				Reader in = new BufferedReader(fr)) {
			int count = 0;
			char[] buf = new char[16384];
			while ((count = in.read(buf)) != -1) {
				if (count > 0) {
					caw.write(buf, 0, count);
				}
			}
			return caw.toCharArray();
		}
	}

	private static void writeBytes(File file, byte[] data) throws IOException {
		try (OutputStream fos = new FileOutputStream(file); OutputStream os = new BufferedOutputStream(fos)) {
			os.write(data);
		}
	}

	private static void writeChars(File file, char[] data) throws IOException {
		try (Writer fos = new FileWriter(file); Writer os = new BufferedWriter(fos)) {
			os.write(data);
		}
	}

}
