package de.hsrm.cs.wwwvs.hamster.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HamsterTestDataStore {

	private String pathToHamsterExe = "./tests/hamster";
	private String pathToHamsterServer = "./hamster_server";
	private String pathToHamsterFile = System.getProperty("user.dir");
	private String hamsterFileName = "hamsterfile.dat";
	private String testdatenPath = "testdaten/";

	private int port = 8088;
	
	private  static int sleepMin = 500;
	private  static int sleepMed = 2000;
	private  static int sleepMax = 5000;
	
	public int testcaseTimeoutms = 20000;

	/*
	 * 
	 * Testdaten
	 * 			besitzer	hamster		treats	price	revol
	 * td1:		otto		heinz		23		0
	 * 
	 * td2:		otto		heinz		0		23
	 * 
	 * td3:		diesnameee123456789012345678901		langerName	0		0		
	 * 
	 * td4:		diesnameee123456789012345678901		diesnameee123456789012345678902		0		0
	 * 
	 * td5 		otto		heinz		65535	0
	 * 
	 * td6		otto{1..50} heinz{1..50} {1..50} 0
	 * 
	 * td7		otto 		heinz 		23
	 * 			karl 		blondy 		42
	 * 
	 * td8		otto		heinz 		23
	 * 			otto		blondy 		42
	 * 
	 * td9		otto		heinz 		18
	 * 
	 * td10		otto		heinz		0 (after giving 50)
	 * 
	 * td11		otto		heinz		0		65535
	 * 
	 * td12		otto 		blondy 23
	 *			hans 		blondy 23
     * 			ernst 		foo 23
     * 
     * td13		otto		heinz		23
     * 			bernd		blondy		42
	 */
	
	
	public static void sleepMin() {
		try {
			Thread.sleep(sleepMin);
		} catch (InterruptedException e) {
			
		}
	}
	public static void sleepMid() {
		try {
			Thread.sleep(sleepMed);
		} catch (InterruptedException e) {
			
		}
	}
	public static void sleepMax() {
		try {
			Thread.sleep(sleepMax);
		} catch (InterruptedException e) {
			
		}
	}
	
	/**
	 * deletes the hamsterfile.dat
	 * @return
	 */
	public boolean wipeHamsterfile() {
		
		Path path = Paths.get(this.pathToHamsterFile, this.hamsterFileName);
		
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			showException(e);
			return false;
		}

		return true;
	}
	
	public boolean compareHamsterFileEqual(String hamsterTestFileName) throws IOException {
		
		String userDir = System.getProperty("user.dir");
		Path testFilePath = Paths.get(userDir, this.testdatenPath, hamsterTestFileName);
		
		
		byte[] sutFile = Files.readAllBytes(Paths.get(this.pathToHamsterFile, this.hamsterFileName));
		byte[] testFile = Files.readAllBytes(testFilePath);
		
		int countByteSUT = sutFile.length;
		int countByteTest = testFile.length;
		
		if (countByteSUT != countByteTest) {
			System.out.println("Hamsterfiles have different sizes");
			return false;
		}
		
		return true;		
	}

	public boolean copyTestHamsterfile(String hamsterTestFileName) {
			
		String userDir = System.getProperty("user.dir");
				
		Path srcFile = Paths.get(userDir, this.testdatenPath, hamsterTestFileName);		
		Path destFile = Paths.get(this.pathToHamsterFile, hamsterFileName);
		
		try {
			System.out.println(String.format("Copy %s to %s", srcFile, destFile));
			Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			showException(e);
			return false;
		}

		return true;
	}

	public void createTestdata1() throws IOException {

		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 23");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}
	}

	public void createTestdata2() throws IOException {

		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 0");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}
		for (int i = 18; i < 23; i++) {
			sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " state otto heinz");
			while (sut.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					showException(e);
				}
			}
		}
	}

	public void createTestdata5() throws IOException {

		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 65535");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}
		for (int i = 18; i < 50; i++) {
			sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " state otto heinz");
			while (sut.isAlive()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					showException(e);
				}
			}
		}
	}
	
	public void createTestdata11( ) throws IOException {
		
		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 0");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);				
			}
		}
		sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " feed otto heinz 16374");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);				
			}
		}
	}
	
	
	
	public void createTestdata8( ) throws IOException {
		
		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 23");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);	
			}
		}
		sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto blondy 42");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}

	}
	
	public void createTestdata13() throws IOException {
		Process sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add otto heinz 23");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}
		sut = Runtime.getRuntime().exec(this.pathToHamsterExe + " add bernd blondy 42");
		while (sut.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				showException(e);
			}
		}
		
	}

	public Process startHamsterServer(int port) {
		String sutPath = getPathToHamsterServer();

		System.out.println("Starting server on port " + port);
		Process sut = null;
		try {
			sut = Runtime.getRuntime().exec(sutPath + " -p " + port);
		} catch (IOException e) {
			e.printStackTrace();
			fail("Failed to start server.");
		}

		assertTrue("Server process is not running.", sut.isAlive());

		HamsterTestDataStore.sleepMax();
		return sut;
	}
	
	public String getPathToHamsterServer( ) {
		
		return this.pathToHamsterServer;
	}
	
	public String getPathToHamsterFile() {
		
		return this.pathToHamsterFile;
	}

	public void setPathToHamsterServer(String p) {
		this.pathToHamsterServer = p;
	}

	public void setPathToHamsterFile(String p) {
		this.pathToHamsterFile = p;
	}
	
	public void setPathToHamsterExe(String p) {
		this.pathToHamsterExe = p;
	}
	public String getPathToHamsterExe() {
		return this.pathToHamsterExe;
	}

	public String getHamsterFileName() { return this.hamsterFileName; }

	public void setHamsterFileName(String hamsterFileName) { this.hamsterFileName = hamsterFileName; }

	private static HamsterTestDataStore inst = null;

	public static HamsterTestDataStore getInstance() {

		if (inst == null) {
			inst = new HamsterTestDataStore();
		}

		return inst;
	}

	private HamsterTestDataStore() {
	}

	private void showException(Exception e) {
		System.out.println("Error during hamsterfile tampering: " + e.getMessage());
	}

	public int getPort() {
		return port++;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
