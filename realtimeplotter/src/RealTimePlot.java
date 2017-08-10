import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

import org.apache.commons.cli.*;
import java.util.concurrent.Semaphore;

import fr.dgac.ivy.*;

public class RealTimePlot {
	private static Semaphore calendarLock = new Semaphore(1);
	private static RealTimeGraph chart1;	

	
	private static int SAMPLINGFREQ = 125;
	private static String regexString = "^DATA(.*)";

	
	public static void main(String[] args) {
		parseCLI(args);
		chart1 = new RealTimeGraph("In Phase Voltage Signal", "Voltage", "", calendarLock);
		
		IvyCallBack ivyCB = new IvyCallBack(chart1);
		try {
			System.out.println("Making ivyhandler");
			IvyHandler ivyHandler = new IvyHandler("RealTimeFFtPlotter", ivyCB, regexString);
		}
		catch (Exception e) {
			e.printStackTrace();
		}


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
		pane.add(chart1, c);

		frame.pack();
		frame.setVisible(true);
	}
	

}

class IvyCallBack implements IvyMessageListener{
	
	private RealTimeGraph plotter;
	public IvyCallBack(RealTimeGraph plotter){
		this.plotter = plotter;
		
			System.out.println("Made ivyCB");
	}

	public void receive(IvyClient client, String[] args) {
		String[] splited = args[0].split(" ");

		try{
			plotter.update((float) Double.parseDouble(splited[1]));
		}
		catch (Exception e){

		}

	}
}