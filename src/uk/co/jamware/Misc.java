package uk.co.jamware;

public class Misc {
	public static void println(String l) {
		System.out.println(l);
	}
	
	public static void print(String l) {
		System.out.print(l);
	}
	
	public static void print_err(String l) {
		System.err.println(l);
	}
	public static String longToName(long name) {
		try {
			if (name <= 0L || name >= 0x5b5b57f8a98a5dd1L) {
				return "invalid_name";
			}
			if (name % 37L == 0L) {
				return "invalid_name";
			}
			int i = 0;
			char ac[] = new char[12];
			while (name != 0L) {
				long l1 = name;
				name /= 37L;
				ac[11 - i++] = Misc.validChars[(int) (l1 - name * 37L)];
			}
			return new String(ac, 12 - i, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static final char[] validChars = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
}
