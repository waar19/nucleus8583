package org.nucleus8583.oim.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.nucleus8583.oim.field.Alignment;

import rk.commons.util.IOUtils;

public class BytesPadder {

	public static int hex2int(char ichar) {
		switch (ichar) {
		case '0':
			break;
		case '1':
			// 0001
			return 1;
		case '2':
			// 0010
			return 2;
		case '3':
			// 0011
			return 3;
		case '4':
			// 0100
			return 4;
		case '5':
			// 0101
			return 5;
		case '6':
			// 0110
			return 6;
		case '7':
			// 0111
			return 7;
		case '8':
			// 1000
			return 8;
		case '9':
			// 1001
			return 9;
		case 'A':
			// 1010
			return 10;
		case 'B':
			// 1011
			return 11;
		case 'C':
			// 1100
			return 12;
		case 'D':
			// 1101
			return 13;
		case 'E':
			// 1110
			return 14;
		case 'F':
			// 1111
			return 15;
		}

		return 0;
	}

	private byte padWith;

	private Alignment align;

	private int length;

	private byte[] padder;

	private byte[] emptyValue;
	
	public BytesPadder() {
		// do nothing
	}
	
	public BytesPadder(BytesPadder o) {
		padWith = o.padWith;
		align = o.align;
		
		length = o.length;
		
		padder = o.padder;
		emptyValue = o.emptyValue;
	}

	public void setPadWith(byte padWith) {
		this.padWith = padWith;
	}

	public void setAlign(Alignment align) {
		this.align = align;
	}

	public Alignment getAlign() {
		return align;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setEmptyValue(byte[] emptyValue) {
		this.emptyValue = emptyValue;
	}

	public byte[] getEmptyValue() {
	    return emptyValue;
	}

	public void initialize() {
		padder = new byte[length];
		Arrays.fill(padder, padWith);
	}

	public void pad(OutputStream out, byte[] value, int off, int valueLength)
			throws IOException {
		if (valueLength == 0) {
			write(out, padder, 0, length);
		} else if (valueLength == length) {
			write(out, value, 0, valueLength);
		} else {
			switch (align) {
			case TRIMMED_LEFT:
            case UNTRIMMED_LEFT:
				write(out, value, off, valueLength);
				write(out, padder, 0, length - valueLength);

				break;
			case TRIMMED_RIGHT:
            case UNTRIMMED_RIGHT:
				write(out, padder, 0, length - valueLength);
				write(out, value, off, valueLength);

				break;
			default: // NONE
				write(out, value, off, valueLength);
				write(out, padder, 0, length - valueLength);

				break;
			}
		}
	}

	public byte[] unpad(byte[] value, int length) throws IOException {
		byte[] result;
		int resultLength;

		switch (align) {
		case TRIMMED_LEFT:
			resultLength = 0;

			for (int i = length - 1; i >= 0; --i) {
				if (value[i] != padWith) {
					resultLength = i + 1;
					break;
				}
			}

			if (resultLength == 0) {
				result = emptyValue;
			} else if (resultLength == length) {
				result = value;
			} else {
				result = new byte[resultLength];
				System.arraycopy(value, 0, result, 0, resultLength);
			}

			break;
		case TRIMMED_RIGHT:
			int padLength = length;

			for (int i = 0; i < length; ++i) {
				if (value[i] != padWith) {
					padLength = i;
					break;
				}
			}

			if (padLength == 0) {
				result = value;
			} else if (padLength == length) {
				result = emptyValue;
			} else {
				resultLength = length - padLength;

				result = new byte[resultLength];
				System.arraycopy(value, padLength, result, 0, resultLength);
			}

			break;
		default: // NONE, UNTRIMMED_LEFT, UNTRIMMED_RIGHT
			result = value;
			break;
		}

		return result;
	}

	public int unpad(InputStream in, byte[] result, int off, int length) throws IOException {
		byte[] value = new byte[length];
		read(in, value, 0, length);

		int resultLength = length;

		switch (align) {
		case TRIMMED_LEFT:
			resultLength = 0;

			for (int i = length - 1; i >= 0; --i) {
				if (value[i] != padWith) {
					resultLength = i + 1;
					break;
				}
			}

			if (resultLength == 0) {
				System.arraycopy(emptyValue, 0, result, off, length);
			} else if (resultLength == length) {
				System.arraycopy(value, 0, result, off, length);
			} else {
				System.arraycopy(value, 0, result, off, resultLength);
			}

			break;
		case TRIMMED_RIGHT:
			int padLength = length;

			for (int i = 0; i < length; ++i) {
				if (value[i] != padWith) {
					padLength = i;
					break;
				}
			}

			if (padLength == 0) {
				System.arraycopy(value, 0, result, off, length);
			} else if (padLength == length) {
				System.arraycopy(emptyValue, 0, result, off, length);
			} else {
				resultLength = length - padLength;
				System.arraycopy(value, padLength, result, off, resultLength);
			}

			break;
		default: // NONE, UNTRIMMED_LEFT, UNTRIMMED_RIGHT
			System.arraycopy(value, 0, result, off, length);
			break;
		}

		return resultLength;
	}

	/**
	 * read N bytes from input stream and store it to <code>value</code>
	 * starting from offset <code>off</code>.
	 *
	 * @param in
	 * @param value
	 * @param off
	 * @param vlen
	 * @throws IOException
	 */
	public void read(InputStream in, byte[] value, int off, int vlen)
			throws IOException {
		IOUtils.readFully(in, value, off, vlen);
	}

	/**
	 * write N bytes of value to output stream.
	 *
	 * @param out
	 * @param value
	 * @param off
	 * @param vlen
	 * @throws IOException
	 */
	public void write(OutputStream out, byte[] value, int off, int vlen)
			throws IOException {
		out.write(value, off, vlen);
	}
}