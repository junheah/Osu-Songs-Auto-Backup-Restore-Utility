import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class logindialog extends Dialog {

	protected Object result;
	protected Shell shlOsuppyshLogin;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public logindialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	private String cookie="";
	public String open() {
		createContents();
		shlOsuppyshLogin.open();
		shlOsuppyshLogin.layout();
		Display display = getParent().getDisplay();
		while (!shlOsuppyshLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return cookie;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlOsuppyshLogin = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlOsuppyshLogin.setSize(450, 205);
		shlOsuppyshLogin.setText("osu.ppy.sh Login");
		
		text = new Text(shlOsuppyshLogin, SWT.BORDER);
		text.setBounds(118, 10, 304, 26);
		
		text_1 = new Text(shlOsuppyshLogin, SWT.BORDER | SWT.PASSWORD);
		text_1.setBounds(118, 42, 304, 26);
		
		Label lblNewLabel = new Label(shlOsuppyshLogin, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(10, 16, 102, 20);
		lblNewLabel.setText("id :");
		
		Label lblNewLabel_1 = new Label(shlOsuppyshLogin, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(10, 48, 102, 20);
		lblNewLabel_1.setText("password :");
		
		Button btnLogIn = new Button(shlOsuppyshLogin, SWT.NONE);
		btnLogIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String id = text.getText();
				String pw = text_1.getText();
				text.setEnabled(false);
				text_1.setEnabled(false);
				btnLogIn.setEnabled(false);
				
				//login attempt
				cookie = osabru.login(id,pw);
				if(cookie.length()>0) {
					//success
					MessageBox dialog = new MessageBox(shlOsuppyshLogin, SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Login success!");
					dialog.open();
					shlOsuppyshLogin.dispose();
				}else {
					//fail
					MessageBox dialog = new MessageBox(shlOsuppyshLogin, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Login failed. Try again");
					dialog.open();
					text.setEnabled(true);
					text_1.setEnabled(true);
					btnLogIn.setEnabled(true);
					
				}
			}
		});
		btnLogIn.setBounds(166, 106, 93, 30);
		btnLogIn.setText("Log in");

	}

}
