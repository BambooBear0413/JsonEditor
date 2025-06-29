package io.bamboobear.json_editor;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ErrorReport {
	private static File reportsDir = new File("reports");
	
	static void init() {
		if(!reportsDir.isDirectory() && !reportsDir.mkdir()) {
			showDirectoryCreationFailureDialog();
		}
	}
	
	private static void showDirectoryCreationFailureDialog() {
		JOptionPane.showMessageDialog(Main.getMainWindow(), "Failed to create the directory \"%s\"".formatted(reportsDir.getPath()),
				"Failed to Create the Directory", JOptionPane.WARNING_MESSAGE);
	}
	
	private ErrorReport() {}
	
	public static void output(Throwable throwable)                     { output(Thread.currentThread(), throwable); }
	public static void output(Throwable throwable, boolean showDialog) { output(Thread.currentThread(), throwable, showDialog); }
	public static void output(Thread thread, Throwable throwable)      { output(thread, throwable, true); }
	
	public static synchronized void output(Thread thread, Throwable throwable, boolean showDialog) {
		throwable.printStackTrace();
		
		if(!reportsDir.isDirectory() && !reportsDir.mkdir()) { // Failed to create directory
			if(showDialog) {
				showDialog(thread, throwable);
			} else {
				showDirectoryCreationFailureDialog();
			}
			return;
		}
		
		String className = throwable.getClass().getCanonicalName();
		String dateTime = getDateTime();
		File outputFile = new File(reportsDir, String.format("%s-%s.txt", dateTime, className));

		for(int i = 1; outputFile.exists(); i++) {
			outputFile = new File(reportsDir, "%s-%s-%d".formatted(dateTime, className, i));
		}

		try (FileWriter fw = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
			fw.write(createReportContent(thread, throwable));
		} catch (IOException e) {
			String s = String.format("An error %s was thrown when generating the error log: %s", e.getClass().getCanonicalName(), outputFile.getName());
			System.err.println(s);
			e.printStackTrace();
			
			if(showDialog) {
				showDialog(Thread.currentThread(), e, s);
				
				String message = String.format("""
					An error occurred: %s. (%s)
					See error log at: %s
					
					WARNING:
					There are another error occurred when generating the error log: %s. (%s)
					Therefore, The contant of the error log may be imcomplete or the file may not exist.\
					""", className, throwable.getMessage(), outputFile.getAbsolutePath(), e.getClass().getCanonicalName(), e.getMessage());
				showDialog(thread, throwable, message);
			}
			
			return;
		}
			
		if(showDialog) {
			String message = String.format("An error occurred: %s. (%s)\nSee error log at: %s", className, throwable.getMessage(), outputFile.getAbsolutePath());
			JOptionPane.showMessageDialog(Main.getMainWindow(), message, className, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void showDialog(Thread thread, Throwable throwable) {
		String className = throwable.getClass().getCanonicalName();
		showDialog(thread, throwable, "An error occurred: %s. (%s)".formatted(className, throwable.getMessage()));
	}
	
	private static void showDialog(Thread thread, Throwable throwable, String message) {
		String className = throwable.getClass().getCanonicalName();
		
		JDialog dialog = new JDialog(Main.getMainWindow(), className, true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JButton button = new JButton("OK");
		button.addActionListener(e -> dialog.dispose());
		
		JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {button});
		dialog.add(optionPane, BorderLayout.NORTH);
		
		button.setFont(optionPane.getFont());
		
		JTextArea textArea = new JTextArea(createReportContent(thread, throwable));
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		dialog.add(scrollPane, BorderLayout.CENTER);
		
		dialog.pack();
		dialog.setSize(Math.min(1000, dialog.getWidth()), Math.min(700, dialog.getHeight()));
		dialog.setLocationRelativeTo(dialog.getParent());
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	private static String getDateTime() { return new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(new Date()); }
	
	private static String createReportContent(Thread thread, Throwable throwable) {
		StringBuilder sb = new StringBuilder();
		sb.append("Exception in thread \"%s\" ".formatted(thread.getName()));
		sb.append(createErrorContent(throwable));
		return sb.toString();
	}
	
	private static String createErrorContent(Throwable throwable) {
		StringBuilder sb = new StringBuilder();
		String message = throwable.getMessage();
		sb.append(throwable.getClass().getCanonicalName() + ((message == null || message.isBlank()) ? "" : (": " + message)) + "\n");
		for(StackTraceElement element : throwable.getStackTrace()) {
			sb.append("\tat " + element.toString() + "\n");
		}
		
		Throwable cause = throwable.getCause();
		if(cause != null) {
			sb.append("Caused by: ");
			sb.append(createErrorContent(cause));
		}
		
		return sb.toString();
	}
}
