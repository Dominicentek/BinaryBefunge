import java.io.*;
import java.util.*;

public class BefungeToBinBefunge {
	public static void main(String[] args) throws Exception {
		String orig = new String(readAllBytes(new FileInputStream(new File(combine(args)))));
		String binary = "";
		String[] rows = orig.split("\n");
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < rows[i].length(); j++) {
				if (rows[i].charAt(j) == '\n') binary += "00100000";
				else binary += String.format("%8s", Integer.toBinaryString((int)rows[i].charAt(j) & 0xFF)).replace(' ', '0');
			}
			for (int j = rows[i].length(); j < 80; j++) {
				binary += "00100000";
			}
			if (i + 1 < 25) binary += "\n";
		}
		for (int i = rows.length; i < 25; i++) {
			for (int j = 0; j < 80; j++) {
				binary += "00100000";
			}
			if (i + 1 < 25) binary += "\n";
		}
		OutputStream out = new FileOutputStream(new File(combine(args).replaceAll(".bf", ".binbf")));
		out.write(binary.getBytes());
		out.close();
	}
	public static String combine(String[] args) {
		String result = "";
		for (int i = 0; i < args.length; i++) {
			result += args[i];
			if (i + 1 < args.length) result += " ";
		}
		return result;
	}
	public static byte[] readAllBytes(InputStream inputStream) throws IOException {
		final int bufLen = 1024;
		byte[] buf = new byte[bufLen];
		int readLen = 0;
		IOException exception = null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
				outputStream.write(buf, 0, readLen);
			}
			return outputStream.toByteArray();
		}
		catch (IOException e) {
			exception = e;
			throw e;
		}
		finally {
			if (exception == null) inputStream.close();
			else try {
				inputStream.close();
			}
			catch (IOException e) {
				exception.addSuppressed(e);
			}
		}
	}
}