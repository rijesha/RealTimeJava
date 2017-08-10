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
	private static FftPlotter complexFftPlotter;
	
	private static int SAMPLINGFREQ = 125;
	private static String regexString = "^DATA(.*)";

	
	public static void main(String[] args) {
		parseCLI(args);
		
		realFftPlotter = new FftPlotter("Real FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, false, calendarLock);
		complexFftPlotter = new FftPlotter("Complex FFT", "power", "Frequency (Hz)", SAMPLINGFREQ, 4, true, calendarLock);
		
		IvyCallBack ivyCB = new IvyCallBack(realFftPlotter);
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

        Option input = new Option("c", "disable_gui", false, "disable the gui");
        options.addOption(input);

        Option output = new Option("l", "start_logging", false, "enabling logging on startup");
        options.addOption(output);

        Option serialport = new Option("d", "serial_device_port", false, "location of serial device port");
        options.addOption(serialport);

		Option piping = new Option("p", "enable_pipe", false, "Pipe information from System.in");
        options.addOption(piping);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
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
	public IvyCallBack(FftPlotter plotter){
		this.plotter = plotter;
		
			System.out.println("Made ivyCB");
	}

	public void receive(IvyClient client, String[] args) {
		String[] splited = args[0].split(" ");

		try{
			plotter.addDataPoint(Double.parseDouble(splited[1]));
		}
		catch (Exception e){

		}

	}
}
