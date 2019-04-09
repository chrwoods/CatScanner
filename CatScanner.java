package catScannerWithGraphics;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class CatScanner {
	private static final String SPACER = "    ";
	private static String readableLoc;
	private static String filepathsLoc;
	private static String currentTime;
	
	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		currentTime = LocalDateTime.now().format(formatter);
		Scanner scan = new Scanner(System.in);
		System.out.println("Would you like to scan or search an existing scan?");
		String tempString = scan.nextLine();
		if(!tempString.contains("search") && tempString.contains("scan")) {
			if(!dscan(scan)) {
				System.out.println("Program was aborted.");
				scan.close();
				return;
			}
			System.out.println("Would you like to search for a file from this scan? (y/n):");
			tempString = scan.nextLine();
			if(!(tempString.contains("y") || tempString.contains("Y"))) {
				System.out.println("Goodbye!");
				scan.close();
				return;
			}
			
		} else if (!tempString.contains("search") && !tempString.contains("scan")) {
			System.out.println("Input not understood, aborting program.");
			scan.close();
			return;
		}

		if(readableLoc == null) {
			System.out.println("Enter the filepath for the readable .txt file:");
			readableLoc = scan.nextLine();
			System.out.println("Enter the filepath for the filepaths .txt file:");
			filepathsLoc = scan.nextLine();
		}
		
		File readableFile;
		File filepathsFile;
		boolean endProgram = false;
		int lineNum;
		ArrayList<Integer> foundLines = new ArrayList<Integer>();
		
		readableFile = new File(readableLoc);
		filepathsFile = new File(filepathsLoc);
		
		while (!endProgram) {
			System.out.println("Enter the filename you want to search for:");
			String searchFileString = scan.nextLine();
			Scanner readableScan;
			Scanner filepathsScan;
			lineNum = 0;
			try {
				readableScan = new Scanner(readableFile);
				filepathsScan = new Scanner(filepathsFile);
			} catch (Exception e) {
				System.out.println("Error: Readable or filepath file not found.");
				return;
			}
			System.out.println("Searching...");
			while(readableScan.hasNextLine()) {
				if(readableScan.nextLine().contains(searchFileString)) {
					foundLines.add(lineNum);
				}
				lineNum++;
			}
			readableScan.close();
			System.out.println(foundLines.size() + " matches found.");
			lineNum = 0;
			boolean writeMatches = false;
			System.out.println("Would you like to print the locations of the matches or write them to a file? (y/n):");
			tempString = scan.nextLine();
			PrintWriter matchWriter = null;
			if(tempString.contains("y") || tempString.contains("Y")) {
				writeMatches = true;
				System.out.println("Enter the directory where you would like to write the file:");
				tempString = scan.nextLine();
				if(!(tempString.endsWith("/") || tempString.endsWith("\\"))) tempString += "/";
				tempString += "MatchesForSearchFor" + searchFileString + currentTime + ".txt";
				try {		
					matchWriter = new PrintWriter(tempString);
					System.out.println("Writing matches file to " + tempString);
				} catch (Exception e) {
					System.out.println("Was not able to write file to given location, printing instead.");
					writeMatches = false;
				}
			}
			
			while(filepathsScan.hasNextLine()) {
				if(foundLines.get(0) == lineNum) {
					if(writeMatches) matchWriter.println(filepathsScan.nextLine());
					else System.out.println(filepathsScan.nextLine());
					foundLines.remove(0);
					if(foundLines.size() == 0) break;
				} else {
					filepathsScan.nextLine();
				}
				lineNum++;
			}
			System.out.println("Finished!");
			filepathsScan.close();
			if(writeMatches) matchWriter.close();
			System.out.println("Would you like to search again?");
			tempString = scan.nextLine();
			endProgram = !(tempString.contains("y") || tempString.contains("Y"));
		}
		scan.close();
		System.out.println("Goodbye!");
		
	}
	
	private static boolean dscan(Scanner scan) {
		System.out.println("Enter filepath of directory you want to scan (defaults to C:/ if nothing entered):");
		String scanLocation = scan.nextLine();
		if (scanLocation.isEmpty()) scanLocation = "C:/";
		File startDirectory;
		try {
			startDirectory = new File(scanLocation);
			startDirectory.getName();
		} catch (Exception e) {
			System.out.println("Directory not found.");
			return false;
		}
		//String writeLocation = "C:/Users/#####/Desktop/CatScannerScans";
		System.out.println("Enter filepath of directory where you want to store the two directory scan .txt files:");
		readableLoc = scan.nextLine();	
		readableLoc += "/ScanOf" + startDirectory.getName();
		readableLoc += currentTime;
		filepathsLoc = readableLoc;
		readableLoc += "Readable.txt";
		filepathsLoc += "Filepaths.txt";
		
		File writeFile = new File(readableLoc);
		//if(writeFile.mkdirs()) System.out.println("Created directory to store .txt files");
		
		PrintWriter readWriter;
		PrintWriter pathWriter;
		try {
			readWriter = new PrintWriter(writeFile);
			pathWriter = new PrintWriter(filepathsLoc);
			System.out.println("Writing readble file to " + readableLoc);
			System.out.println("Writing filepath file to " + filepathsLoc);
		} catch (Exception e) {
			System.out.println("Was not able to write files to given location.");
			return false;
		}
		
		System.out.println("Scanning...");
		int numErrors = writeDirectory(startDirectory, "", readWriter, pathWriter, 0);
		if (numErrors > 0) System.out.println(numErrors + " folders/files were unable to be accessed.");
		else System.out.println("All files were able to be scanned!");
		readWriter.close();
		pathWriter.close();
		System.out.println("Finished!");
		return true;
	}
	

	private static int writeDirectory(File file, String spacing, PrintWriter writer, PrintWriter searchWriter, int numFileDenied) {
		try {
			if (file.isFile()) {
				writer.println(spacing + file.getName());
				searchWriter.println(file);
				return numFileDenied;
			}
			writer.println(spacing + file.getName() + " {");
			File[] fileList = file.listFiles();
			searchWriter.println(file);
			for(int i = 0; i < fileList.length; i++) {
				numFileDenied = writeDirectory(fileList[i], spacing + SPACER, writer, searchWriter, numFileDenied);
			}
			writer.println(spacing + "}");
			searchWriter.println();
			return numFileDenied;
		} catch (Exception e) {
			writer.println(spacing + SPACER + "!!!ERROR: Access was denied to this directory.");
			writer.println(spacing + "}");
			searchWriter.println("Access denied to certain files.");
			searchWriter.println();
			return numFileDenied + 1;
		}
	}

}
