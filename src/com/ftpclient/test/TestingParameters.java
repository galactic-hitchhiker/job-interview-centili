package com.ftpclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ftpclient.bll.configuration.FileConfigurationReader;
import com.ftpclient.bll.configuration.ICongfigurationReader;
import com.ftpclient.bll.configuration.InvalidConfigurationException;
import com.ftpclient.om.AppConfiguration;

/**
 * Test za proveru parametara
 * 
 * @author corax
 *
 */
public class TestingParameters {

	private String file1 = "test   1.txt";
	private String file2 = "test2 .txt";
	private String file3 = "test 3.txt";
	private String file4 = "test_4.txt";
	private String file5 = "test5.txt";
	private String file6 = "test6.txt";
	
	private String validConf = "validConf.txt";
	
	private String invalidConf1 = "invalidConf1.txt";
	private String invalidConf2 = "invalidConf2.txt";
	private String invalidConf3 = "invalidConf3.txt";
	private String invalidConf4 = "invalidConf4.txt";
	
	private String minimumValidConf = "minValidConf.txt";
	
	
	private File validConfFile;
	private File invalidConfFile1;
	private File invalidConfFile2;
	private File invalidConfFile3;
	private File invalidConfFile4;
	private File minValidConfFile;
	
	@Before
	public void setUp() throws IOException {
		new File(file1).createNewFile();
		new File(file2).createNewFile();
		new File(file3).createNewFile();
		new File(file4).createNewFile();
		new File(file5).createNewFile();
		new File(file6).createNewFile();
		
		
		validConfFile = new File(validConf);
		validConfFile.createNewFile();
		try(FileWriter fw = new FileWriter(validConfFile)) {
			fw.write("username:pera\r\n");
			fw.write("password:peric\r\n");
			fw.write("server:localhost\r\n");
			fw.write("files:"+file1+";"+file2+"\r\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		invalidConfFile1 = new File(invalidConf1);
		invalidConfFile1.createNewFile();
		try(FileWriter fw = new FileWriter(invalidConfFile1)) {
			fw.write("username:pera\r\n");
			fw.write("password:\r\n"); 														// pass is missing!
			fw.write("server:localhost\r\n");
			fw.write("files:"+file1+";"+file2+";"+file3+";"+file4+";"+file5+"\r\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		invalidConfFile2 = new File(invalidConf2);
		invalidConfFile2.createNewFile();
		try(FileWriter fw = new FileWriter(invalidConfFile2)) {
			fw.write("username:pera\r\n");
			fw.write("password:peric\r\n");
			fw.write("server:localhost\r\n");
			fw.write("files:"+file1+";"+file2+";"+file3+";"+file4+";"+file5+";"+file6+"\r\n"); // more than 5 files
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		invalidConfFile3 = new File(invalidConf3);
		invalidConfFile3.createNewFile();
		try(FileWriter fw = new FileWriter(invalidConfFile3)) {
			fw.write("username:pera\r\n");											
			fw.write("password:peric\r\n");
			fw.write("server:localhost\r\n");
			// required parameter "files" is missing
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		invalidConfFile4 = new File(invalidConf4);
		invalidConfFile4.createNewFile();
		try(FileWriter fw = new FileWriter(invalidConfFile4)) {
			fw.write("username:pera\r\n");											
			fw.write("password:peric\r\n");
			fw.write("server:localhost\r\n");
			fw.write("files:"+file1+";"+file2+";"+file3+";"+file4+";"+file5+"\r\n");
			fw.write("invalid:parameter\r\n"); // invalid parameter
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		minValidConfFile = new File(minimumValidConf);
		minValidConfFile.createNewFile();
		try(FileWriter fw = new FileWriter(minValidConfFile)) {
			fw.write("files:"+file1+";"+file2+";"+file3+";"+file4+";"+file5+"\r\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	@After
	public void setDown() throws IOException {
		new File(file1).delete();
		new File(file2).delete();
		new File(file3).delete();
		new File(file4).delete();
		new File(file5).delete();
		new File(file6).delete();
		
		new File(invalidConf1).delete();
		new File(invalidConf2).delete();
		new File(invalidConf3).delete();
		new File(invalidConf4).delete();
		new File(validConf).delete();
		new File(minimumValidConf).delete();
	}
	
	
	@Test
	public void validParameterValidationTest() throws InvalidConfigurationException  {
		
		ICongfigurationReader configurationReader = new FileConfigurationReader(validConfFile);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		assertTrue(appConfiguration.getUsername().equals("pera"));
		assertTrue(appConfiguration.getPassword().equals("peric"));
		assertTrue(appConfiguration.getServer().equals("localhost"));
		
		assertTrue(appConfiguration.getFilesForTransfer().get(0).getAbsolutePath().equals(new File(file1).getAbsolutePath()));
		assertTrue(appConfiguration.getFilesForTransfer().get(1).getAbsolutePath().equals(new File(file2).getAbsolutePath()));
 	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void missingParameterFailTest() throws InvalidConfigurationException {
		
		ICongfigurationReader configurationReader = new FileConfigurationReader(invalidConfFile1);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		fail();
 	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void missingRequiredParametarTest() throws InvalidConfigurationException {
		
		ICongfigurationReader configurationReader = new FileConfigurationReader(invalidConfFile3);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		fail();

 	}
	
	@Test
	public void minimumValidArgumentValidationTest() throws InvalidConfigurationException {
		ICongfigurationReader configurationReader = new FileConfigurationReader(minValidConfFile);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
 	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void unknownArgumentValidationTest() throws InvalidConfigurationException {

		ICongfigurationReader configurationReader = new FileConfigurationReader(invalidConfFile4);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		fail();
 	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void moreThan5FilesValidationTest() throws InvalidConfigurationException {
		ICongfigurationReader configurationReader = new FileConfigurationReader(invalidConfFile2);
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		fail();
 	}
	
	@Test(expected = InvalidConfigurationException.class)
	public void missingCfgFile() throws InvalidConfigurationException {
		ICongfigurationReader configurationReader = new FileConfigurationReader(new File("unknown.file"));
		AppConfiguration appConfiguration = configurationReader.readConfiguration();
		
		fail();
 	}
}
