package bg.tu_sofia.graduation.ui;

import java.awt.Dimension;

import javax.swing.JLabel;

public class StatusBar extends JLabel {
    
	private static final long serialVersionUID = 7310354040268680426L;

    public StatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 25));
        setMessage("Ready");
    }
    
    public void setMessage(String message) {
        setText(" "+message);        
    }        
}
