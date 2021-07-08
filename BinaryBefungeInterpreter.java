import java.util.*;
import java.io.*;

public class BinaryBefungeInterpreter {
	public static void main(String[] args) throws Exception {
		int direction = 0;
		int x = 0;
		int y = 0;
		char[][] playfield = new char[25][80];
		String rawData = new String(readAllBytes(new FileInputStream(new File(combine(args)))));
		String[] rows = rawData.split("\n");
		if (rows.length != 25) {
			System.out.println("Your program does not have 25 rows.");
			System.exit(0);
		}
		boolean error = false;
		for (int i = 0; i < rows.length; i++) {
			if (rows[i].length() != 640) {
				error = true;
				System.out.println("Row " + (i + 1) + " does not have 640 characters. (80 binary digits)");
			}
		}
		if (error) System.exit(0);
		for (int i = 0; i < rows.length; i++) {
			String[] binary = rows[i].split("(?<=\\G........)");
			for (int j = 0; j < binary.length; j++) {
				playfield[i][j] = (char)Integer.parseInt(binary[j], 2);
			}
		}
		ArrayList<Integer> stack = new ArrayList<Integer>();
		boolean stringMode = false;
		while (true) {
			char character = playfield[y][x];
			// System.out.println("EXECUTE: " + x + " " + y + " " + character);
			if (stringMode) {
				if (character == '"') stringMode = false;
				else stack.add((int)character);
				if (direction == 0) x++;
				if (direction == 1) x--;
				if (direction == 2) y--;
				if (direction == 3) y++;
				if (x == -1) x = 79;
				if (x == 80) x = 0;
				if (y == -1) y = 24;
				if (y == 25) y = 0;
				continue;
			}
			if (character == '+') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b + a);
			}
			if (character == '-') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b - a);
			}
			if (character == '*') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b * a);
			}
			if (character == '/') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b / a);
			}
			if (character == '%') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b % a);
			}
			if (character == '!') {
				int a = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1);
				if (a == 0) stack.add(1);
				else stack.add(0);
			}
			if (character == '`') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(b > a ? 1 : 0);
			}
			if (character == '>') direction = 0;
			if (character == '<') direction = 1;
			if (character == '^') direction = 2;
			if (character == 'v') direction = 3;
			if (character == '?') direction = (int)Math.floor(Math.random() * 4);
			if (character == '_') {
				int a = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1);
				if (a == 0) direction = 0;
				else direction = 1;
			}
			if (character == '|') {
				int a = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1);
				if (a == 0) direction = 3;
				else direction = 2;
			}
			if (character == '"') stringMode = true;
			if (character == ':') stack.add(stack.get(stack.size() - 1));
			if (character == '\\') {
				int a = stack.get(stack.size() - 1);
				int b = stack.get(stack.size() - 2);
				stack.remove(stack.size() - 1);
				stack.remove(stack.size() - 1);
				stack.add(a);
				stack.add(b);
			}
			if (character == '$') stack.remove(stack.size() - 1);
			if (character == '.') {
				int value = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1);
				System.out.print(value);
			}
			if (character == ',') {
				char ascii = (char)stack.get(stack.size() - 1).intValue();
				stack.remove(stack.size() - 1);
				System.out.print(ascii);
			}
			if (character == '#') {
				if (direction == 0) x++;
				if (direction == 1) x--;
				if (direction == 2) y--;
				if (direction == 3) y++;
				if (x == -1) x = 79;
				if (x == 80) x = 0;
				if (y == -1) y = 24;
				if (y == 25) y = 0;
			}
			if (character == 'g') {
				int xCoord = stack.get(stack.size() - 1);
				int yCoord = stack.get(stack.size() - 2);
				if (xCoord < 0 || xCoord >= 80 || yCoord < 0 || yCoord >= 25) stack.add(0);
				else stack.add((int)playfield[yCoord][xCoord]);
			}
			if (character == 'p') {
				int xCoord = stack.get(stack.size() - 1);
				int yCoord = stack.get(stack.size() - 2);
				int value = stack.get(stack.size() - 3);
				if (xCoord >= 0 && xCoord < 80 && yCoord >= 0 && yCoord < 25) playfield[yCoord][xCoord] = (char)value;
			}
			if (character == '&') {
				boolean valid = false;
				while (!valid) {
					valid = true;
					String value = new Scanner(System.in).nextLine();
					int integer = 0;
					try {
						integer = Integer.parseInt(value);
					}
					catch (NumberFormatException e) {
						System.out.println("This is not a valid number.");
						valid = false;
					}
					if (valid) {
						stack.add(integer);
					}
				}
			}
			if (character == '~') {
				boolean allowedToContinue = false;
				while (!allowedToContinue) {
					String input = new Scanner(System.in).nextLine();
					if (input.length() > 0) {
						stack.add((int)input.charAt(0));
						allowedToContinue = true;
					}
					else System.out.println("Cannot parse empty string.");
				}
			}
			if (character == '@') System.exit(0);
			if (character == '0') stack.add(0);
			if (character == '1') stack.add(1);
			if (character == '2') stack.add(2);
			if (character == '3') stack.add(3);
			if (character == '4') stack.add(4);
			if (character == '5') stack.add(5);
			if (character == '6') stack.add(6);
			if (character == '7') stack.add(7);
			if (character == '8') stack.add(8);
			if (character == '9') stack.add(9);
			if (direction == 0) x++;
			if (direction == 1) x--;
			if (direction == 2) y--;
			if (direction == 3) y++;
			if (x == -1) x = 79;
			if (x == 80) x = 0;
			if (y == -1) y = 24;
			if (y == 25) y = 0;
		}
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