package goldendelicios.lite2edit;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Lite2Edit {
	private static final String LITEMATIC_PREFIX = ".litematic";
	private static File dir = new File(System.getProperty("user.dir"));
	private static PrintStream errorFile;
	private static ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);

	public static void main(String[] args) {
		try {
			Files.deleteIfExists(Paths.get("errors.log"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (args.length != 0) {
			File[] files = new File[args.length];
			for (int i = 0; i < args.length; i++) {
				String filename = args[i];
				File file = new File(filename);
				if (!filename.endsWith(LITEMATIC_PREFIX) || !file.isFile()) {
					System.err.println("Error: '" + filename + "' is not a valid file");
					return;
				}
				files[i] = file;
			}
			convert(files);
		}
		else {
			System.err.println("Invalid arguments.");
			System.err.println("Correct usage: `java -jar Lite2Edit.jar [Path to file 1] [Path to file 2]...`");
		}
	}
	

	
	private static void convert(File[] inputs) {
		for (int i = 0; i < inputs.length; i++) {
			String working = "Working... (" + i + "/" + inputs.length + " complete)";
			System.out.println(working);
			
			File input = inputs[i];
			try {
				File parent = input.getAbsoluteFile().getParentFile();
				List<File> outputs = Converter.litematicToWorldEdit(input, parent);
				
				if (outputs.isEmpty()) {
					System.out.print(input.getName() + " is not a valid litematic file\n");
				}
				else {
					for (File output : outputs) {
						System.out.print("Exported to " + output.getName() + "\n");
					}
				}
			} catch (Throwable e) {
				System.out.print("Error while converting " + input.getName() + ":\n" + e + "\n");
				handleException(e);
			}
		}
	}
	
	private static void handleException(Throwable e) {
		e.printStackTrace();
		if (errorFile == null) {
			try {
				errorFile = new PrintStream("errors.log");
			} catch (Exception e2) {
				System.err.println("Failed to write to errors.log");
				e2.printStackTrace();
				return;
			}
		}
		e.printStackTrace(errorFile);
		errorFile.flush();
	}
}
