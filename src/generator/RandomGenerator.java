package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class RandomGenerator {

	static DecimalFormat FORMAT = new DecimalFormat("#.##");
	
	public static void genNormal(int n, int d) {
		Random rand = new Random(0l);
		try {
			FileWriter fw = new FileWriter("synthetic-" + n + "-" + d + ".txt");
			for (int i = 0; i < n; i++) {
				StringBuilder builder = new StringBuilder();
				for (int j = 0; j < d; j++) {
					double num = Double.MAX_VALUE;
					while (num >= 5.0 || num <= -5.0) {
						num = rand.nextGaussian();
					}
					builder.append(FORMAT.format(num)).append(" ");
				}
				builder.deleteCharAt(builder.length() - 1).append("\n");
				fw.write(builder.toString());

				if ((i + 1) % 10000 == 0) {
					System.out.println((i + 1) + "/" + n + " finished.");
					fw.flush();
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void genNormal(int n, int d, long seed) {
		Random rand = new Random(seed);
		try {
			FileWriter fw = new FileWriter("synthetic-" + n + "-" + d + ".txt");
			for (int i = 0; i < n; i++) {
				StringBuilder builder = new StringBuilder();
				for (int j = 0; j < d; j++) {
					double num = Double.MAX_VALUE;
					while (num >= 5.0 || num <= -5.0) {
						num = rand.nextGaussian();
					}
					builder.append(FORMAT.format(num)).append(" ");
				}
				builder.deleteCharAt(builder.length() - 1).append("\n");
				fw.write(builder.toString());

				if ((i + 1) % 10000 == 0) {
					System.out.println((i + 1) + "/" + n + " finished.");
					fw.flush();
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
