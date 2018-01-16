package io.cybermiles.cosmos.sdk.store.multiKVStoreReciever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MultiKVStoreCompare {
	private static int compare(File f1, File f2, File out) throws FileNotFoundException {
		Scanner file1 = new Scanner(f1);
		Scanner file2 = new Scanner(f2);
		
		List<String> write = new ArrayList<String>();
		List<String> read = new ArrayList<String>();
		
		while(file1.hasNext()) {
			write.add(file1.nextLine());
		}

		while(file2.hasNext()) {
			read.add(file2.nextLine());
		}
		
		PrintStream output = new PrintStream(new FileOutputStream(out));
		System.setOut(output);
		
		int line = 0;
		int differences = 0;
		
		while(line < write.size() || line < read.size()) {
			String writeLine = null;
			String readLine = null;
			
			if(line < write.size()) {
				writeLine = write.get(line);
			}
			if(line < read.size()) {
				readLine = read.get(line);
			}
			
			if(writeLine == null) {
				System.out.println("Line " + line + ": excess out\n");
				differences++;
			}
			else if(readLine == null) {
				System.out.println("Line " + line + ": excess in\n");
				differences++;
			}
			else if(writeLine.compareTo(readLine) != 0) {
				System.out.println("Line " + line + ":");
				System.out.println("\tout: [" + writeLine + "]");
				System.out.println("\tin: [" + readLine + "]\n");
				differences++;
			}
			
			line++;
		}
		
		return differences;
	}
	
	public static void main(String[] args) throws IOException {
		File multiOut = new File("./src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/multi.out");
		File multiIn = new File("./src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/multi.in");
		File out = new File("./src/io/cybermiles/cosmos/sdk/store/multiKVStoreReciever/multi.diff");
		
		if(!out.exists()) {
			out.createNewFile();
		}
		
		PrintStream stdout = System.out;
		
		int diff = compare(multiOut, multiIn, out);
		
		System.out.println("Total Differences: " + diff);
		
		System.setOut(stdout);
		
		if(diff != 0) {
			System.out.println("--Compare Test Failed: (Check .out, .in, and .diff files)--");
		}
		else {
			System.out.println("--Compare Test Passed--");
		}
	}
}
