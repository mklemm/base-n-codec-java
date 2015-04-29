package net.codesup.utilities.basen;

import java.math.BigInteger;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class AnyBaseEncoderTest {
	public static final AnyBaseEncoder BASE_36_ENCODER = new AnyBaseEncoder(36);

	@Test
	public void testEncode() {
		//printAll();
		printMinMax(128);
		printMinMax(64);
		printMinMax(32);
		printMinMax(16);
		printMinMax(8);
		final UUID guid = UUID.randomUUID();
		System.out.println("GUID: " + guid.toString());
		final String encoded = AnyBaseEncoderTest.BASE_36_ENCODER.encode(guid);
		System.out.println("GUID enc: " + encoded);
		final UUID decoded = AnyBaseEncoderTest.BASE_36_ENCODER.decodeUuid(encoded);
		System.out.println("GUID dec: " + decoded);
		Assert.assertEquals(guid, decoded);
	}

	@Test
	public void testCanonical() {
		//printAll();
//		printMinMax(128);
//		printMinMax(64);
//		printMinMax(32);
//		printMinMax(16);
//		printMinMax(8);
		final UUID guid = UUID.fromString("eab02684-03a7-4d99-bd10-edd7bf2445ae");
		System.out.println("GUID: " + guid.toString());
		final String encoded = AnyBaseEncoderTest.BASE_36_ENCODER.encode(guid);
		System.out.println("GUID enc: " + encoded);
		final UUID decoded = AnyBaseEncoderTest.BASE_36_ENCODER.decodeUuid(encoded);
		Assert.assertEquals(guid, decoded);
		Assert.assertEquals("2ZGFQE37T37MMRY3M4QZ1IU8", encoded);
	}

	@Test
	public void testPrintAll() {
		final BigInteger min128 = BigInteger.ONE.shiftLeft(127).negate();
		final BigInteger max128 = min128.negate().subtract(BigInteger.ONE);
		final long min64 = Long.MIN_VALUE;
		final long max64 = Long.MAX_VALUE;
		final int min32 = Integer.MIN_VALUE;
		final int max32 = Integer.MAX_VALUE;
		printInt("min128", min128);
		printInt("max128", max128);
		printInt("min64", min64);
		printInt("max64", max64);
		printInt("min32", min32);
		printInt("max32", max32);
		printInt("zero", 0);
	}

	public void printMinMax(final int numBits) {
		final BigInteger min = BigInteger.ONE.shiftLeft(numBits - 1).negate();
		final BigInteger max = min.negate().subtract(BigInteger.ONE);
		final BigInteger umax = BigInteger.ONE.shiftLeft(numBits).subtract(BigInteger.ONE);
		printInt("min" + numBits, min);
		printInt("max" + numBits, max);
		printInt("umax" + numBits, umax);
	}

	public void printInt(final String name, final long integer) {
		printInt(name, BigInteger.valueOf(integer));
	}

	public void printInt(final String name, final BigInteger bigInteger) {
		System.out.printf("%s(16): %x\n", name, bigInteger);
		System.out.printf("%s(10): %d\n", name, bigInteger);
		final String encoded = AnyBaseEncoderTest.BASE_36_ENCODER.encode(bigInteger);
		System.out.printf("enc %s(36): %s\n", name, encoded);
		System.out.printf("Encoded version takes %d chars.\n", encoded.length());
		final BigInteger decoded = AnyBaseEncoderTest.BASE_36_ENCODER.decode(encoded);
		System.out.printf("dec %s(16): %x\n", name, decoded);
		System.out.printf("dec %s(10): %d\n", name, decoded);
	}

	@Test
	public void testUuidFromBytes() {
		final UUID uuid = UUID.randomUUID();
		final byte[] bytes = AnyBaseEncoder.uuidToBytes(uuid);
		final UUID decodedUuid = AnyBaseEncoder.uuidFromBytes(bytes);
		Assert.assertEquals(uuid, decodedUuid);
	}

	@Test
	public void testOften() {
		BigInteger bigInteger = new BigInteger("1234567890123456789012345678901234567890");
		final int iterationCount = 100000;
		final String[] resultBuffer = new String[iterationCount];
		final long encodeStartTime = System.currentTimeMillis();
		for(int i = 0; i < iterationCount; i++) {
			resultBuffer[i] = BASE_36_ENCODER.encode(bigInteger);
		}
		final long encodeEndTime = System.currentTimeMillis();
		Assert.assertEquals(iterationCount, resultBuffer.length);
		final String encoded = "2ZGFQE37T37MMRY3M4QZ1IU8";
		final BigInteger[] intBuffer = new BigInteger[iterationCount];
		final long decodeStartTime = System.currentTimeMillis();
		for(int i = 0; i < iterationCount; i++) {
			intBuffer[i] = BASE_36_ENCODER.decode(encoded);
		}
		final long decodeEndTime = System.currentTimeMillis();
		Assert.assertEquals(iterationCount, intBuffer.length);
		System.out.println("Encoding time: " + (encodeEndTime - encodeStartTime));
		System.out.println("Decoding time: " + (decodeEndTime - decodeStartTime));
	}
}

