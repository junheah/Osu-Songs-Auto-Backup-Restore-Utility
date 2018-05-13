import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class Main {

	protected static Shell shlOsuSongsAuto;
	private Text text;
	private static ProgressBar progressBar;
	private static Label lblCounter;
	Thread downloader;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlOsuSongsAuto.open();
		shlOsuSongsAuto.layout();
		while (!shlOsuSongsAuto.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	String location;
	
	protected void createContents() {
		shlOsuSongsAuto = new Shell();
		shlOsuSongsAuto.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				System.exit(0);
			}
		});
		shlOsuSongsAuto.setSize(507, 209);
		shlOsuSongsAuto.setText("OSABRU by junheah");
		
		Combo combo = new Combo(shlOsuSongsAuto, SWT.READ_ONLY);
		combo.setTextDirection(0);
		combo.setItems(new String[] {"osu.ppy.sh", "bloodcat.com", "bloodcat.com >> osu.ppy.sh"});
		combo.setBounds(208, 71, 277, 28);
		combo.select(0);
		
		Label lblNewLabel = new Label(shlOsuSongsAuto, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 83, 20);
		lblNewLabel.setText("Songs Dir :");
		
		text = new Text(shlOsuSongsAuto, SWT.BORDER);
		text.setBounds(99, 10, 386, 26);
		
		//set text to osu dir
		location = osabru.getOsuDir();
		if(location.length()>0) text.setText(location);
		else text.setText("Osu Songs folder not found! Please enter manually");
		
		
		Button btnBackup = new Button(shlOsuSongsAuto, SWT.NONE);
		btnBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//when backup button pressed
				FileDialog dialog = new FileDialog(shlOsuSongsAuto, SWT.SAVE);
				String[] filterExt = { ".osubak", "*.*" };
		        dialog.setFilterExtensions(filterExt);
		        String selected = dialog.open();
				
			}
		});
		btnBackup.setBounds(10, 61, 93, 30);
		btnBackup.setText("Backup");
		
		Button btnRestore = new Button(shlOsuSongsAuto, SWT.NONE);
		btnRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//when restore button pressed

				 FileDialog fd = new FileDialog(shlOsuSongsAuto, SWT.OPEN);
			     String[] filterExt = { "*.osubak", "*.*" };
			     fd.setFilterExtensions(filterExt);
			     String selected = fd.open();
			     System.out.println(selected);
			     if(selected!=null) {	 
			    	//read backupfile
			        BufferedReader br;
			        String[] listtmp = {};
			        try {
			        	br = new BufferedReader(new FileReader(selected));
			            StringBuilder sb = new StringBuilder();
			            String line = br.readLine();

			            while (line != null) {
			                sb.append(line);
			                sb.append(System.lineSeparator());
			                line = br.readLine();
			            }
			            listtmp = sb.toString().split(",");
			            br.close();
			        } catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        //asks user for confirmation
			        MessageBox messageBox = new MessageBox(shlOsuSongsAuto, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			        messageBox.setMessage("Downloading "+(listtmp.length+1) +" beatmapsets. Continue?");
			        messageBox.setText("Confirm action");
			        int response = messageBox.open();
			        if (response == SWT.YES) {
			        	String logincookie = "";
			        	String captchacookie = "";
			        	//captcha and login stuff
			        	int dlmode = combo.getSelectionIndex();
			        	if(dlmode == 0 || dlmode == 2) {
			        		//osu.ppy.sh login
			        		logindialog l = new logindialog(shlOsuSongsAuto,SWT.OPEN);
			        		logincookie = l.open();
			        		if(logincookie.length()<=0) shlOsuSongsAuto.dispose();
			        		System.out.println(logincookie);
			        	}
			        	if(dlmode == 1 || dlmode == 2) {
			        		//bloodcat.com captcha
			        		captchadialog c = new captchadialog(shlOsuSongsAuto,SWT.OPEN);
			        		captchacookie = c.open();
			        		if(captchacookie.length()<=0) shlOsuSongsAuto.dispose();
			        		System.out.println(captchacookie);
			        	}
			        	
			        	//download shit
			        	location = text.getText();
			        	downloader = new Thread(new downloader(captchacookie, logincookie, location, dlmode, listtmp));
			        	downloader.start();

			            	
			        }
			    }
			}
		});
		btnRestore.setBounds(109, 61, 93, 30);
		btnRestore.setText("Restore");
		
		progressBar = new ProgressBar(shlOsuSongsAuto, SWT.SMOOTH);
		progressBar.setBounds(10, 131, 475, 21);
		
		Label lblDownloadFrom = new Label(shlOsuSongsAuto, SWT.NONE);
		lblDownloadFrom.setBounds(208, 45, 157, 20);
		lblDownloadFrom.setText("Download from :");
		
		lblCounter = new Label(shlOsuSongsAuto, SWT.NONE);
		lblCounter.setBounds(10, 105, 192, 20);
		
	}

	public static void changeprogress(int pro) {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	progressBar.setSelection(pro);
		    }
		});

	}
	public static void changecounter(String count) {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	lblCounter.setText(count);
		    }
		  });
	}
}
