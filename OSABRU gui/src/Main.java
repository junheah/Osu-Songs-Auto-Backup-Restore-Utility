import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Combo;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class Main {

	protected static Shell shlOsuSongsAuto;
	private static Text text;
	private static ProgressBar progressBar;
	private static Label lblCounter;
	Thread downloader;
	static Button btnBrowse, btnRestore, btnBackup;
	static Combo combo;

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
		shlOsuSongsAuto.setSize(492, 213);
		shlOsuSongsAuto.setText("OSABRU v0.1 by junheah");
		shlOsuSongsAuto.setLayout(new FormLayout());
		
		combo = new Combo(shlOsuSongsAuto, SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -10);
		fd_combo.top = new FormAttachment(0, 71);
		combo.setLayoutData(fd_combo);
		combo.setTextDirection(0);
		combo.setItems(new String[] {"osu.ppy.sh", "bloodcat.com", "bloodcat.com >> osu.ppy.sh"});
		combo.select(0);
		
		Label lblNewLabel = new Label(shlOsuSongsAuto, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 13);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Songs Dir :");
		
		text = new Text(shlOsuSongsAuto, SWT.BORDER);
		fd_lblNewLabel.right = new FormAttachment(text, -6);
		FormData fd_text = new FormData();
		fd_text.left = new FormAttachment(0, 99);
		text.setLayoutData(fd_text);
		
		//set text to osu dir
		location = osabru.getOsuDir();
		text.setText(location);
		
		
		btnBackup = new Button(shlOsuSongsAuto, SWT.NONE);
		FormData fd_btnBackup = new FormData();
		fd_btnBackup.left = new FormAttachment(0, 10);
		fd_btnBackup.top = new FormAttachment(text, 33);
		btnBackup.setLayoutData(fd_btnBackup);
		btnBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//when backup button pressed
				if(text.getText().length()>0) {
					FileDialog dialog = new FileDialog(shlOsuSongsAuto, SWT.SAVE);
					String[] filterExt = { ".osubak" };
			        dialog.setFilterExtensions(filterExt);
			        String target = dialog.open();
			        
			        
			        //get song id list to array list
					List<Integer> songs = osabru.getSongs(location);
					File output = new File(target);
					//write to file
					BufferedWriter outputWriter = null;
					  
					try {
						outputWriter = new BufferedWriter(new FileWriter(output));
						for(int i=0; i<songs.size(); i++) {
							if(i==songs.size()-1) outputWriter.write(songs.get(i)+"");
							else outputWriter.write(songs.get(i)+",");
						}			
						outputWriter.flush();  
						outputWriter.close(); 
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					MessageBox dialog2 = new MessageBox(shlOsuSongsAuto, SWT.ICON_INFORMATION | SWT.OK);
					dialog2.setText("alert");
					dialog2.setMessage("Successfully backed up "+(songs.size())+" beatmapsets");
					dialog2.open();
			        
			        
				}else {
					MessageBox dialog = new MessageBox(shlOsuSongsAuto, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Please select osu! songs directory");
					dialog.open();
				}
				
			}
		});
		btnBackup.setText("Backup");
		
		btnRestore = new Button(shlOsuSongsAuto, SWT.NONE);
		fd_btnBackup.right = new FormAttachment(btnRestore, -6);
		fd_combo.left = new FormAttachment(0, 208);
		FormData fd_btnRestore = new FormData();
		fd_btnRestore.top = new FormAttachment(combo, -2, SWT.TOP);
		fd_btnRestore.right = new FormAttachment(combo, -6);
		fd_btnRestore.left = new FormAttachment(0, 109);
		btnRestore.setLayoutData(fd_btnRestore);
		btnRestore.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(text.getText().length()>0) {
				//when restore button pressed

					 FileDialog fd = new FileDialog(shlOsuSongsAuto, SWT.OPEN);
				     String[] filterExt = { "*.osubak", "*.*" };
				     fd.setFilterExtensions(filterExt);
				     String selected = fd.open();
				     //System.out.println(selected);
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
				        messageBox.setMessage("Downloading "+(listtmp.length) +" beatmapsets. Continue?");
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
				        		//System.out.println(logincookie);
				        	}
				        	if(dlmode == 1 || dlmode == 2) {
				        		//bloodcat.com captcha
				        		captchadialog c = new captchadialog(shlOsuSongsAuto,SWT.OPEN);
				        		captchacookie = c.open();
				        		if(captchacookie.length()<=0) shlOsuSongsAuto.dispose();
				        		//System.out.println(captchacookie);
				        	}
				        	//disable shit
				        	text.setEnabled(false);
							btnBrowse.setEnabled(false);
							btnRestore.setEnabled(false);
							btnBackup.setEnabled(false);
							combo.setEnabled(false);
				        	//download shit
				        	location = text.getText();
				        	downloader = new Thread(new downloader(captchacookie, logincookie, location, dlmode, listtmp));
				        	downloader.start();
	
			        	}
			     	}
				}else {
					MessageBox dialog = new MessageBox(shlOsuSongsAuto, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Please select osu! songs directory");
					dialog.open();
				}
			}
		});
		btnRestore.setText("Restore");
		
		progressBar = new ProgressBar(shlOsuSongsAuto, SWT.SMOOTH);
		FormData fd_progressBar = new FormData();
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		fd_progressBar.bottom = new FormAttachment(100, -10);
		progressBar.setLayoutData(fd_progressBar);
		
		Label lblDownloadFrom = new Label(shlOsuSongsAuto, SWT.NONE);
		fd_text.bottom = new FormAttachment(lblDownloadFrom, -9);
		FormData fd_lblDownloadFrom = new FormData();
		fd_lblDownloadFrom.bottom = new FormAttachment(combo, -6);
		fd_lblDownloadFrom.left = new FormAttachment(0, 208);
		fd_lblDownloadFrom.right = new FormAttachment(100, -10);
		lblDownloadFrom.setLayoutData(fd_lblDownloadFrom);
		lblDownloadFrom.setText("Download from :");
		
		lblCounter = new Label(shlOsuSongsAuto, SWT.NONE);
		FormData fd_lblCounter = new FormData();
		fd_lblCounter.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_lblCounter.top = new FormAttachment(0, 105);
		fd_lblCounter.left = new FormAttachment(0, 10);
		lblCounter.setLayoutData(fd_lblCounter);
		
		btnBrowse = new Button(shlOsuSongsAuto, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			//when browse button pressed
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shlOsuSongsAuto);
			    //dialog.setFilterPath("c:\\"); // Windows specific
			    try{
			    	location = dialog.open();
			    	text.setText(location);
			    }catch(Exception r) {
			    	
			    }
			    
			}
		});
		fd_text.right = new FormAttachment(btnBrowse, -6);
		FormData fd_btnBrowse = new FormData();
		fd_btnBrowse.top = new FormAttachment(0, 8);
		fd_btnBrowse.right = new FormAttachment(100, -10);
		btnBrowse.setLayoutData(fd_btnBrowse);
		btnBrowse.setText("Browse");
		
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
	public static void dlfinished(int lmax, int skipped) {
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	//alert
		    	MessageBox dialog = new MessageBox(shlOsuSongsAuto, SWT.ICON_INFORMATION | SWT.OK);
				dialog.setText("alert");
				dialog.setMessage("Downloaded "+(lmax-skipped)+" out of "+(lmax) + " beatmapsets.");
				dialog.open();
				//enable ui's
				text.setEnabled(true);
				btnBrowse.setEnabled(true);
				btnRestore.setEnabled(true);
				btnBackup.setEnabled(true);
				combo.setEnabled(true);
		    }
		  });
		
	}
}
