package sneer;

import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Boot {
	
	private static final String TITLE = "Sneer Friend-to-Friend Installation";
	
	private static Socket _socket;
	private static ObjectInputStream _objectIn;

	
	public static void main(String[] ignored) {
		try {
			boot();
		} catch (Throwable t) {			
			showError(t);
		}
	}

	private static void boot() throws Exception {
		if (!mainAppInstalled()) installMainAppFromPeer();
		executeMainApp();
	}

	private static boolean mainAppInstalled() {
		return mainAppFile() != null;
	}

	private static File mainAppFile() {
		return null;
	}

//	private static File lastValidAppJarFile() {
//		while (true) {
//			File candidate = lastAppJarFile();
//			if (candidate == null) return null;
//			if (isValidSignature(candidate)) return candidate;
//			deleteSignedFile(candidate);
//		}
//	}


	private static void installMainAppFromPeer() throws Exception {
		welcome();
		try{
			openConnectionToPeer();
			receiveMainApp();
		} finally {
			closeConnectionToPeer();
		}
	}


	private static void welcome() {
		String message =
			" Do you have a sovereign friend to help you\n" +
			" install Sneer and guide your first steps in\n" +
			" sovereign computing?";
		int hasFriend = JOptionPane.showConfirmDialog(null, message, TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (hasFriend == JOptionPane.YES_OPTION) return;
		
		showMessage(" You will need one.  :)", JOptionPane.INFORMATION_MESSAGE, "Close");
		System.exit(0);
	}

	private static void showMessage(String message, int type, String okButton) {
		JOptionPane.showOptionDialog(null, message, TITLE, 0, type, null, new Object[]{okButton}, okButton);
	}

//	private static void compileMainApp() throws Exception {
//		delete(tempDirectory());
//		extractMainAppSource();
//		compileMainAppSource();
//	}
//
//	private static void compileMainAppSource() throws Exception {
//		File dest = new File(tempDirectory(), "classes");
//		dest.mkdir();
//
//		execute(compilerJar(), 
//			"-source", "1.6",
//			"-target", "1.6",
//			"-d", dest.getAbsolutePath(),
//			sourceDirectory().getAbsolutePath()
//		);
//
//		
// //FileOutputStream os = new FileOutputStream(jar);
// //JarOutputStream jos = new JarOutputStream(os, manifest());
// //addJarEntries(jar, jos);
// //jos.close();		
//
//	}



	private static void execute(File jar, String... args) throws Exception {
		executeClass(jar, mainClass(jar), args);
	}

	private static String mainClass(File jar) throws IOException {
		return new JarFile(jar).getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
	}

//	private static void delete(File file) throws IOException {
//		if (file.isDirectory()) {
//			for (File subFile : file.listFiles()) delete(subFile);
//			return;
//		}
//		if (!file.delete()) throw new IOException("Unable to delete file " + file);
//	}

//	private static void extractMainAppSource() throws Exception {
//		ZipFile sources = new ZipFile(mainAppSourceFile());
//		Enumeration<? extends ZipEntry> entries = sources.entries();
//		while (entries.hasMoreElements()) {
//			extractMainAppSourceEntry(sources, entries.nextElement());
//		}
//	}

//	private static void extractMainAppSourceEntry(ZipFile sources, ZipEntry entry) throws IOException {
//		if (entry.isDirectory()) return;
//		
//		InputStream inputStream = sources.getInputStream(entry);
//		byte[] buffer = new byte[1024*4];
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		int read;
//		while (-1 != (read = inputStream.read(buffer))) {
//			bos.write(buffer, 0, read);
//		}
//		inputStream.close();
//		
//		File sourceFile = new File(sourceDirectory(), entry.getName());
//		save(sourceFile, bos.toByteArray());
//	}

//	private static File tempDirectory() {
//		File result = new File(sneerDirectory(), "temp");
//		result.mkdir();
//		return result;
//	}

	private static void receiveMainApp() throws Exception {
		byte[] jarContents = receiveByteArray();
		byte[] signature = receiveByteArray();
	
		//saveSignedFile(firstAppJar(), jarContents, signature);
	}

//	private static File firstAppJar() {
//		return new File(appDirectory(), "0000000000.jar");
//	}



//	private static void saveSignedFile(File file, byte[] contents, byte[] signature) throws IOException {
//		File signatureFile = new File(file.getAbsolutePath() + ".signature");
//		save(signatureFile, signature);
//		save(file, contents);
//	}

	private static void checkHash(byte[] jarContents, byte[] signature) {
		System.err.println("SHA-512");
	}

//	private static File mainAppSourceFile() {
//		return new File(sneerDirectory(), "MainApplication.zip");
//	}
	
//	private static File lastAppJarFile() {
//		File[] versions = appDirectory().listFiles();
//
//		File result = null;
//		for (File version : versions) {
//			String name = version.getName();
//			if (!name.endsWith(".jar")) continue;
//			if (result == null) result = version;
//			if (name.compareTo(result.getName()) > 0) result = version;
//		}
//		return result;
//	}
	
	private static File appDirectory() {
		File result = new File(sneerDirectory(), "application");
		result.mkdir();
		return result;
	}

	private static void closeConnectionToPeer() throws IOException {
		if (_objectIn != null) _objectIn.close();
		if (_socket != null) _socket.close();
	}

//	private static void receiveCompiler() throws Exception {
//		receiveFileContents(compilerJar());
//	}

//	private static File compilerJar() {
//		return new File(sneerDirectory(), "compiler.jar");
//	}

//	private static void receiveFileContents(File file) throws Exception {
//		save(file, receiveByteArray());
//	}

	private static byte[] receiveByteArray() throws Exception {
		return (byte[])_objectIn.readObject();
	}

	private static File sneerDirectory() {
		File result = new File(System.getProperty("user.home"), ".sneer");
		result.mkdir();
		return result;
	}

	private static void openConnectionToPeer() throws Exception {
		String address = promptForHostnameAndPort();
		if (address == null) System.exit(0);
		_socket = new Socket(hostGiven(address), portGiven(address));
		_objectIn = new ObjectInputStream(_socket.getInputStream());
		
		ObjectOutputStream output = new ObjectOutputStream(_socket.getOutputStream());
		output.writeObject("Bootstrap");
	}


//	private static void addJarEntries(File dir, JarOutputStream jos){
//		File files[] = dir.listFiles();
//		
//		for (File file : files) {
//			if(file.isDirectory()){
//				addJarEntries(file, jos);
//			} else {
//				ZipEntry entry = new ZipEntry(resourceName(clazz));	
//				jos.putNextEntry(entry);
//				jos.write(readClassBytes(clazz));
//				jos.closeEntry();			
//			}
//		}		
//	}


	private static void executeMainApp() throws Exception {
		executeClass(mainAppFile(), "Main");
	}

	private static void executeClass(File jar, String className, String... args) throws ClassNotFoundException, MalformedURLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> clazz = new URLClassLoader(new URL[] { jar.toURI().toURL() }).loadClass(className);
		clazz.getMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { args });
	}
	
	private static String hostGiven(String s) {
		String[] addressParts = s.split(":");
		return addressParts[0];
	}
	
	private static int portGiven(String s) {
		String[] addressParts = s.split(":");
		return Integer.parseInt(addressParts[1]);
	}

	private static String promptForHostnameAndPort() {
		String message =
			"Ask your friend what you have to enter below and why.";
		return (String)JOptionPane.showInputDialog(null, message, TITLE, JOptionPane.INFORMATION_MESSAGE, null, null, "hostaddress:1234");
	}

//	private static void save(File file, byte[] contents) throws IOException {
//		FileOutputStream fos = new FileOutputStream(file);
//		try {
//			fos.write(contents);
//		} finally {
//			fos.close();
//		}
//	}

	private static void showError(Throwable t) {
		String message = "There was an error:\n" +
			t + "\n\n" +
			"The Sneer installation will now exit.";
		showMessage(message, JOptionPane.ERROR_MESSAGE, "Whatever");
	}
}