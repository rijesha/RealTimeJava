import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

import org.apache.commons.cli.*;
import java.util.concurrent.Semaphore;

import fr.dgac.ivy.*;

public class RealTimeFFT {
	private static Semaphore calendarLock = new Semaphore(1);
	
	private static FftPlotter realFftPlotter; // = new RealFftPlotter(32);
	
	private static int SAMPLINGFREQ = 125;
	private static String regexString = "^DATA(.*)";

	private static int data_index = 2;

	
	public static void main(String[] args) {
		parseCLI(args);
		
		realFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, false, calendarLock);
		
		IvyCallBack ivyCB = new IvyCallBack(realFftPlotter, data_index);
		try {
			System.out.println("Making ivyhandler");
			IvyHandler ivyHandler = new IvyHandler("RealTimeFFtPlotter", ivyCB, regexString);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		Thread fftreal = new Thread(realFftPlotter);
		fftreal.start();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		newFrame();
	}
	
	private static void parseCLI(String[] args) {
		Options options = new Options();

		Option index = new Option("i", "data_ind", true, "Which data index from regex" );
		options.addOption(index);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
			cmd = parser.parse(options, args);
			if (cmd.hasOption("data_ind")){
				data_index = Integer.parseInt(cmd.getOptionValue("data_ind"));
			}
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Real Time FFT Grapher", options);

            System.exit(1);
            return;
        }

	}
	
	private static void newFrame(){
		JFrame frame = new JFrame("Fancy Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
		
        frame.setSize(50, 75);
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.ipady = 25;      //make this component tall
		c.ipadx = 25;      //make this component tall
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1;

		c.gridx = 0;
		c.gridy = 0;
		pane.add(realFftPlotter.getGraph(), c);

		frame.pack();
		frame.setVisible(true);
	}
	

}

class IvyCallBack implements IvyMessageListener{
	
	private FftPlotter plotter;
	private int downsample = 0;
	private int data_index = 1;

	public IvyCallBack(FftPlotter plotter, int data_index){
		this.plotter = plotter;
		this.data_index = data_index;		
			System.out.println("Made ivyCB");
	}

	
	public void receive(IvyClient client, String[] args) {
		String[] splited = args[0].split(" ");

		try{
			plotter.addDataPoint(Double.parseDouble(splited[data_index]));
		
		}
			catch (Exception e){
		}
		
		
	}

}
