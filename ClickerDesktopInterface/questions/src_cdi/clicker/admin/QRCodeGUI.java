package clicker.admin;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;




public class QRCodeGUI extends JPanel
{
	private CommunicationHub hub;
	private ByteMatrix byteMatrix;
	
	public QRCodeGUI()
	{
		super();
		hub = CommunicationHub.getInstance();
		
		JTextField connectionString = new JTextField(hub.getConnectionString());
		connectionString.setEditable(false);
		add(connectionString);
		
		createQrCode(hub.getConnectionString(), 500);
		
		repaint();
		
	}
	
	public void createQrCode(String content, int qrCodeSize)
	{
		try
		{
			// Create the ByteMatrix for the QR-Code that encodes the given String.
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
		    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		 
		    QRCodeWriter qrCodeWriter = new QRCodeWriter();
		    byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
	 
	    }
		catch (Exception ex){}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
	    Graphics2D graphics = (Graphics2D) g;
	    graphics.setColor(Color.WHITE);
	    graphics.fillRect(0, 0, byteMatrix.getWidth(), byteMatrix.getWidth());
	 
	    graphics.setColor(Color.BLACK);
	 
	    for (int i = 0; i < byteMatrix.getWidth(); i++)
	    {
	    	for (int j = 0; j < byteMatrix.getWidth(); j++)
	    	{
	    		if (byteMatrix.get(i, j) == 0)
	    		{
	    			graphics.fillRect(i, j, 1, 1);
	    		}
	    	}
	    }
	}
}
