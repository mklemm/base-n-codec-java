package net.codesup.utilities.basen;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

/**
 * Encodes a BigInteger, Guid or byte array against an arbitrary radix number system. This class is intended to encode short (up to 64 bytes) byte sequences as if they represent a single binary
 * number. For example, it is well suited to encode IPV6 addresses, UUIDs, GUIDs, Hashes (SHA or MD5) etc. It isn't very efficient for long byte sequences, like binary files, images etc. Also, it
 * doesn't insert any output formatting, marker, or fill characters. If you need something like this, you should use an ordinary Base64 etc. encoding.
 */
public class AnyBaseEncoder {
	public static final String WHITESPACE_ALPHABET = " \t\n\r\u000B\u0085\u00A0\u2000\u2001\u2002\u2004\u2005\u2006\u2007\u2008\u2009\u200A"; // Funny: only whitespace...
	public static final String BASE_32_HEX_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
	public static final String BASE_32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
	public static final String BASE_36_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String BASE_52_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static final String BASE_62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static final String BASE_64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	public static final String BASE_85_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~";
	public static final String Z_85_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-:+=^!/*?&<>()[]{}@%$#";
	public static final String BASE_91_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"";
	public static final String BASE_94_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"-\\'";
	public static final String BASE_98_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\"-\\' \t\n\r";
	/**
	 * Works in the spirit of ordinary hex numbers, but with 32 different digits instead of 16
	 */
	public static final AnyBaseEncoder BASE_32_HEX = new AnyBaseEncoder(AnyBaseEncoder.BASE_32_HEX_ALPHABET);
	/**
	 * As opposed to Base32Hex, lower significance is represented by letters A-Z, and higher by numbers 2-7
	 */
	public static final AnyBaseEncoder BASE_32 = new AnyBaseEncoder(AnyBaseEncoder.BASE_32_ALPHABET);
	/**
	 * Base36 is suitable for generating file names on case-insensitive filesystems, like e.g. on MS Windows. An 128bit integer value (e.g. MD5 or GUID) will require 25 characters
	 */
	public static final AnyBaseEncoder BASE_36 = new AnyBaseEncoder(AnyBaseEncoder.BASE_36_ALPHABET);
	/**
	 * Base52 just uses upper- and lowercase alphabetical characters. This is well-suited for use in XML as anything that requires to be of the XML "NCName" type, like IDs, element or attribute names.
	 * An 128bit integer value (e.g. MD5 or GUID) will require 23 characters
	 */
	public static final AnyBaseEncoder BASE_52 = new AnyBaseEncoder(AnyBaseEncoder.BASE_52_ALPHABET);
	/**
	 * Base62 uses digits and upper- and lowercase letters.
	 */
	public static final AnyBaseEncoder BASE_62 = new AnyBaseEncoder(AnyBaseEncoder.BASE_62_ALPHABET);
	/**
	 * base64 is the typical alphabet used in email encoding of binary data.
	 */
	public static final AnyBaseEncoder BASE_64 = new AnyBaseEncoder(AnyBaseEncoder.BASE_64_ALPHABET);
	/**
	 * Base85 uses characters typically allowed in case-sensitive filesystems as file names
	 */
	public static final AnyBaseEncoder BASE_85 = new AnyBaseEncoder(AnyBaseEncoder.BASE_85_ALPHABET);
	/**
	 * Base91 uses all printable characters except dash -, backslash \, and apostrophe '
	 */
	public static final AnyBaseEncoder BASE_91 = new AnyBaseEncoder(AnyBaseEncoder.BASE_91_ALPHABET);
	/**
	 * Base94 uses all printable ASCII characters except whitespace
	 */
	public static final AnyBaseEncoder BASE_94 = new AnyBaseEncoder(AnyBaseEncoder.BASE_94_ALPHABET);
	/**
	 * Base98 uses all printable ASCII characters including newline and whitespace
	 */
	public static final AnyBaseEncoder BASE_98 = new AnyBaseEncoder(AnyBaseEncoder.BASE_98_ALPHABET);
	private static final String[] ALPHABETS = {AnyBaseEncoder.BASE_32_HEX_ALPHABET, AnyBaseEncoder.BASE_36_ALPHABET, AnyBaseEncoder.BASE_52_ALPHABET, AnyBaseEncoder.BASE_62_ALPHABET,
			AnyBaseEncoder.BASE_64_ALPHABET, AnyBaseEncoder.BASE_85_ALPHABET, AnyBaseEncoder.BASE_91_ALPHABET, AnyBaseEncoder.BASE_94_ALPHABET};
	private static final BigInteger MIN_128_INV = BigInteger.ONE.shiftLeft(127);
	private final char[] alphabet;
	private final BigInteger targetBase;

	public AnyBaseEncoder(final String alphabet) {
		this(alphabet.toCharArray());
	}

	public AnyBaseEncoder(final char[] alphabet) {
		this.alphabet = alphabet;
		this.targetBase = BigInteger.valueOf(alphabet.length);
	}

	public AnyBaseEncoder(final int radix) {
		this(findAlphabet(radix));
	}

	private static String findAlphabet(final int radix) {
		for (int i = 0; i < AnyBaseEncoder.ALPHABETS.length; i++) {
			if (AnyBaseEncoder.ALPHABETS[i].length() >= radix) {
				return AnyBaseEncoder.ALPHABETS[i].substring(0, radix);
			}
		}
		return AnyBaseEncoder.BASE_94_ALPHABET;
	}

	public String encode(final UUID guid) {
		final byte[] guidBytes = uuidToBytes(guid);
		return encode(guidBytes);
	}

	public String encode(final byte[] bytes) {
		final BigInteger guidInt = new BigInteger(bytes);
		return encode(guidInt);
	}

	public String encode(final BigInteger bigInt) {
		BigInteger value = bigInt.add(AnyBaseEncoder.MIN_128_INV);
		final StringBuilder stringBuilder = new StringBuilder();
		do {
			final BigInteger[] fracAndRemainder = value.divideAndRemainder(this.targetBase);
			stringBuilder.append(this.alphabet[fracAndRemainder[1].intValue()]);
			value = fracAndRemainder[0];
		}
		while (value.compareTo(BigInteger.ZERO) > 0);
		return stringBuilder.toString();
	}

	public BigInteger decode(final char[] encoded) {
		BigInteger sum = BigInteger.ZERO;
		final int charLen = encoded.length;
		for (int i = 0; i < charLen; i++) {
			sum = sum.add(this.targetBase.pow(i).multiply(BigInteger.valueOf(Arrays.binarySearch(this.alphabet, encoded[i]))));
		}
		return sum.subtract(AnyBaseEncoder.MIN_128_INV);
	}

	public BigInteger decode(final String encoded) {
		return decode(encoded.toCharArray());
	}

	public UUID decodeUuid(final String encoded) {
		final BigInteger bigInt = decode(encoded);
		return uuidFromBytes(bigInt.toByteArray());
	}

	public static UUID uuidFromBytes(final byte[] data) {
		long lsb = 0;
		long msb = 0;
		long highInt = 0;
		long midShort = 0;
		long lowShort = 0;
		for(int i = 3; i >= 0; i--) {
			highInt = highInt << 8 | data[i] & 0xff;
		}
		for(int i = 5; i >= 4; i--) {
			midShort = midShort << 8 | data[i] & 0xff;
		}
		for(int i = 7; i >= 6; i--) {
			lowShort = lowShort << 8 | data[i] & 0xff;
		}
		msb = highInt << 32 | midShort << 16 | lowShort;
 		for (int i = 8; i < 16; i++)
			lsb = lsb << 8 | data[i] & 0xff;
		return new UUID(msb, lsb);
	}

	public static byte[] uuidToBytes(final UUID uuid) {
		long lsb = uuid.getLeastSignificantBits();
		long msb = uuid.getMostSignificantBits();
		final byte[] bytes = new byte[16];
		bytes[7] = (byte)(msb >> 1 * 8 & 0xFF);
		bytes[6] = (byte)(msb >> 0 * 8 & 0xFF);
		bytes[5] = (byte)(msb >> 3 * 8 & 0xFF);
		bytes[4] = (byte)(msb >> 2 * 8 & 0xFF);
		bytes[3] = (byte)(msb >> 7 * 8 & 0xFF);
		bytes[2] = (byte)(msb >> 6 * 8 & 0xFF);
		bytes[1] = (byte)(msb >> 5 * 8 & 0xFF);
		bytes[0] = (byte)(msb >> 4 * 8 & 0xFF);
		for (int i = 0; i < 8; i++) {
			bytes[15 - i] = (byte)(lsb & 0xFF);
			lsb = lsb >> 8;
		}
		return bytes;
	}
}
