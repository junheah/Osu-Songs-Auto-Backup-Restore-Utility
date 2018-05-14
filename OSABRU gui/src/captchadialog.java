import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class captchadialog extends Dialog {

	protected Object result;
	protected Shell shlBloodcatcomCaptcha;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public captchadialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	private String cookie="";
	private Text text;
	
	
	private String sync="";
	private String hash="";
	Display display = null;
	public String open() {
		createContents();
		shlBloodcatcomCaptcha.open();
		shlBloodcatcomCaptcha.layout();
		display = getParent().getDisplay();
		while (!shlBloodcatcomCaptcha.isDisposed()) {
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
		
		
		shlBloodcatcomCaptcha = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlBloodcatcomCaptcha.setSize(450, 236);
		shlBloodcatcomCaptcha.setText("bloodcat.com Captcha");
		shlBloodcatcomCaptcha.setLayout(null);
		
		Label lblCaptchaImage = new Label(shlBloodcatcomCaptcha, SWT.NONE);
		lblCaptchaImage.setBounds(10, 10, 313, 121);
		lblCaptchaImage.setText("captcha image");
		
		Button btnNewButton = new Button(shlBloodcatcomCaptcha, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//submit button
				shlBloodcatcomCaptcha.setEnabled(false);
				String cinput = text.getText();
				cookie = osabru.bloodcatcaptchacheck(cinput,sync,hash);
				if(cookie.length()>0) {
					//success
					MessageBox dialog = new MessageBox(shlBloodcatcomCaptcha, SWT.ICON_INFORMATION | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Success!");
					dialog.open();
					shlBloodcatcomCaptcha.dispose();
					
				}else{
					//fail
					MessageBox dialog = new MessageBox(shlBloodcatcomCaptcha, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("alert");
					dialog.setMessage("Failed. Try again");
					dialog.open();
					String[] tmp = osabru.bloodcatcaptcha();
					sync = tmp[0];
					hash = tmp[1];
					Image image = new Image(display,"captcha.png");
					lblCaptchaImage.setImage (image);
					text.setText("");
					shlBloodcatcomCaptcha.setEnabled(true);
				}
			}
		});
		btnNewButton.setBounds(329, 142, 93, 30);
		btnNewButton.setText("Submit");
		
		text = new Text(shlBloodcatcomCaptcha, SWT.BORDER);
		text.setBounds(10, 144, 313, 26);
		
		
		
		
		
		
		Button btnRefresh = new Button(shlBloodcatcomCaptcha, SWT.NONE);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//refresh button
				String[] tmp = osabru.bloodcatcaptcha();
				sync = tmp[0];
				hash = tmp[1];
				Image image = new Image(display,"captcha.png");
				lblCaptchaImage.setImage (image);
				
			}
		});
		btnRefresh.setBounds(329, 10, 93, 30);
		btnRefresh.setText("refresh");
		
		
		//load image from bloodcat captcha
		String[] tmp = osabru.bloodcatcaptcha();
		sync = tmp[0];
		hash = tmp[1];
		
		Image image = new Image(display,"captcha.png");
		lblCaptchaImage.setImage (image);

        
		

	}
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
	    if (bufferedImage.getColorModel() instanceof DirectColorModel) {
	        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	        PaletteData palette = new PaletteData(
	            colorModel.getRedMask(),
	            colorModel.getGreenMask(),
	            colorModel.getBlueMask()
	        );
	        ImageData data = new ImageData(
	            bufferedImage.getWidth(),
	            bufferedImage.getHeight(), colorModel.getPixelSize(),
	            palette
	        );
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[3];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                int pixel = palette.getPixel(
	                    new RGB(pixelArray[0], pixelArray[1], pixelArray[2])
	                );
	                data.setPixel(x, y, pixel);
	            }
	        }
	        return data;
	    } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
	        IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
	        int size = colorModel.getMapSize();
	        byte[] reds = new byte[size];
	        byte[] greens = new byte[size];
	        byte[] blues = new byte[size];
	        colorModel.getReds(reds);
	        colorModel.getGreens(greens);
	        colorModel.getBlues(blues);
	        RGB[] rgbs = new RGB[size];
	        for (int i = 0; i < rgbs.length; i++) {
	            rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	        }
	        PaletteData palette = new PaletteData(rgbs);
	        ImageData data = new ImageData(
	            bufferedImage.getWidth(),
	            bufferedImage.getHeight(),
	            colorModel.getPixelSize(),
	            palette
	        );
	        data.transparentPixel = colorModel.getTransparentPixel();
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[1];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                data.setPixel(x, y, pixelArray[0]);
	            }
	        }
	        return data;
	    }
	    return null;
	}
	

}
