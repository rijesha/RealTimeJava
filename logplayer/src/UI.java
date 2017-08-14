import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.OutputStreamWriter;

import fr.dgac.ivy.*;


/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class UI extends JPanel implements ChangeListener, ActionListener, Runnable {
    static final int FPS_MAX = 30;
    static final int FPS_INIT = 1;    //initial frames per second
	
	private JButton startButton = new JButton("Start");
	private JButton stopButton = new JButton("Stop");
	
	private ArrayList<DataPoint> dataMap = new ArrayList<DataPoint>();
	
	private JSlider slider;
	private JLabel sliderLabel;
	
	private IvyHandler ivyHandler;

    public UI(ArrayList<DataPoint> dataMap) {

		try {
			ivyHandler = new IvyHandler("Log File Player", new IvyCallBack(), "");
		}
		catch  (Exception ie) {
			ie.printStackTrace();
		}

		this.dataMap =  dataMap;
		
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Create the label.
        sliderLabel = new JLabel("Time: ", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        slider = new JSlider(JSlider.HORIZONTAL, 0, dataMap.size(), FPS_INIT);

        slider.addChangeListener(this);

        //Turn on labels at major tick marks.
        slider.setMajorTickSpacing(7680);
        slider.setMinorTickSpacing(1280);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        add(sliderLabel);
        add(slider);
        startButton.addActionListener(this);
		add(startButton);
		stopButton.addActionListener(this);
		add(stopButton);
		
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
    
 
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
			sliderLabel.setText("Time: " + dataMap.get(source.getValue()).time/1000);
        }
    }

	public void sendHTTPRequest(double lat, double lon, double alt) {
		try {
			URL url = new URL("http", "127.0.0.1",1919, "null");
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(
    		httpCon.getOutputStream());
			String msg = "{\"fillcolor\": \"orange\", \"status\": 0, \"linecolor\": \"yellow\", \"id\": 22, \"shape\": 0, \"msgname\": \"SHAPE\", \"msgclass\": \"ground\", \"radius\": 10, \"lonarr\": [" + (int) (lon*10000000) + ","+ (int) (lon*10000000) +"], \"opacity\": 2, \"latarr\": [" + (int) (lat*10000000) + ","+ (int) (lat*10000000) +"], \"text\": "  + alt + "} ";
			out.write(msg);
			out.close();
			httpCon.getInputStream();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "Start" && slider.isEnabled()) {
			slider.setEnabled(false);
		} else if (command == "Stop"){
			slider.setEnabled(true);
		}
	}

	@Override
	public void run() {
		while (true) {

	    	try {
				Thread.sleep(125);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			int i = slider.getValue();
			while(i < dataMap.size() && !slider.isEnabled()) {
				
		         DataPoint dp = dataMap.get(i);
				 slider.setValue(i);
				 sliderLabel.setText("Time: " + dp.time/1000);
				  ivyHandler.send(dp.data);
		         try {
				 	Thread.sleep((int) 8);
				 } catch (InterruptedException e) {
				 	e.printStackTrace();
				 }
					
				i++;
		    }
		}	
	}
}

class IvyCallBack implements IvyMessageListener{

	public IvyCallBack(){
		
	}
	public void receive(IvyClient client, String[] args) {
		//System.out.println(args[0]);
	}
}