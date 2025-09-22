package fr.abes.utils;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BufferedRWTest {
	
	File origin = null;
	File destination = null;
	String destinationName = null;
	


	@Before
	public void setUp() throws Exception {
		
		origin = File.createTempFile("test", null);		
		Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(origin), "UTF-8"));
		for (int i = 0; i < 1024; i++) {			
			w.write(i);			
		}
		w.flush();
		w.close();
		
		destinationName = "newfile"+System.currentTimeMillis()+".test";
		destination = new File(destinationName);

	}

	@After
	public void tearDown() throws Exception {
		
		origin.delete();
		destination.delete();
	}

	@Test
	public void testExtension() {
		
		
		String test1 = "toto.txt";
		String test2 = "toto.txt.titi";
		String test3 = "tata";
		
		
		assertTrue("txt".equals(BufferedRW.extension(test1)));
		assertTrue("titi".equals(BufferedRW.extension(test2)));
		assertTrue(null == BufferedRW.extension(test3));
		
	}
	
	@Test
	public void testCopy() throws FileNotFoundException, UnsupportedEncodingException {
		
		long originLength = origin.length();
		System.out.println("taille fichier origine="+originLength);
		System.out.println("prêt à copier vers "+destinationName);		
		boolean success = BufferedRW.copy(origin, destination,BufferedRW.UTF8);
		long destinationLength = destination.length();
		System.out.println("taille fichier destination="+destinationLength);
		assertTrue("la copie a échouée ",success&&destinationLength==originLength);
		
	}
	
	@Test
	public void testRenameTo() throws IOException {
		
		long originLength = origin.length();
		System.out.println("taille fichier origine="+originLength);
		System.out.println("prêt à renommer vers "+destinationName);
		//boolean success = origin.renameTo(destination);
		Files.move(origin.toPath(), destination.toPath());
		long destinationLength = destination.length();
		System.out.println("taille fichier destination="+destinationLength);
		assertTrue("le rename a échoué ",
				//success&&
				destinationLength==originLength);
		
	}
	
//	@Test
//	public void testMoveNFSProof() throws FileNotFoundException, UnsupportedEncodingException {
//		
//		File nfsFile = null;
//		String nfsFileName = null;
//		
//		//TODO TMX find a NFS mock
//		nfsFileName = "/home/batch/exportslibreservice/docBase/extracted/newfileNFS"+System.currentTimeMillis()+".test";
//		nfsFile = new File(nfsFileName);
//		
//		long originLength = origin.length();
//		System.out.println("taille fichier origine="+originLength);
//		System.out.println("prêt à renommer NFS proof vers "+nfsFileName);
//		
//		boolean success = BufferedRW.moveNFSProof(origin, nfsFile, BufferedRW.UTF8);
//		long nfsFileLength = nfsFile.length();
//		System.out.println("taille fichier NFS="+nfsFileLength);
//		assertTrue("le rename NFS proof a échoué ",success&&nfsFileLength==originLength);
//		
//		nfsFile.delete();
//
//		
//	}
	
	
	
	

}
