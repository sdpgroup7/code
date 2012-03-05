package uk.ac.ed.inf.sdp2012.group7.ui;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import com.googlecode.javacv.cpp.ARToolKitPlus.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.StrategyOld;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
import uk.ac.ed.inf.sdp2012.group7.vision.Vision;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * Creates and maintains the swing-based Control GUI, which 
 * provides both control manipulation (pitch choice, direction,
 * etc) and threshold setting. Also allows the saving/loading of
 * threshold values to a file.
 * 
 * @author s0840449
 */
public class ControlGUI implements ChangeListener {
	
	/* The thresholds state class stores the current state 
 	 * of the thresholds. */
	private ThresholdsState thresholdsState;
	
	/* Stores information about the current world state, such as 
	 * shooting direction, ball location, etc. */
	private WorldState worldState = WorldState.getInstance();
	
	private final StrategyOld strat;
	
	/* The main frame holding the Control GUI. */
	private JFrame frame;
	
	/* Kick and drive buttons - for M1 but can change function as neccessary*/
	private JButton kickButton;
	private JButton driveButton;
	
	/*Locate Button*/
	private JButton locateButton;
	
	/* Start/Stop Button */
	private JButton startButton;
	private JButton stopButton;
	
	/*Penalty mode buttons */
	private JButton penaltyAttackButton;
	private JButton penaltyDefendButton;
	private JCheckBox returnToGame;
	
	private JButton overlayButton;
	private JButton barrelButton;
	
	
	/* Tabs. */
	private JTabbedPane tabPane;
	private JPanel defaultPanel;
	
	/* Radio buttons */
	JButton pitch_0;
	JButton pitch_1;
	JButton colour_yellow;
	JButton colour_blue;
	JButton direction_right;
	JButton direction_left;
	
    private boolean penaltyToGame = false;


	/**
	 * Default constructor. 
	 * 
	 * @param thresholdsState	A ThresholdsState object to update the threshold slider
	 * 							values.			
	 * @param worldState		A WorldState object to update the pitch choice, shooting
	 * 							direction, etc.
	 */
	public ControlGUI(StrategyOld s) {
		
		/* All three state objects must not be null. */
		assert (thresholdsState != null);
		assert (worldState != null);
		
		strat = s;
	}
	
	public void stateChanged(ChangeEvent e) {}
	
	/**
	 * Initialise the GUI, setting up all of the components and adding the required
	 * listeners.
	 */
	public void initGUI() {
		
		frame = new JFrame("Control GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setLayout(new FlowLayout());
        
        /* Create panels for each of the tabs */
        tabPane = new JTabbedPane();
        
        defaultPanel = new JPanel();
        defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));
          
        /* The main (default) tab */
        setUpMainPanel();
        
        
        tabPane.addTab("World Information", defaultPanel);
        
        tabPane.addChangeListener(this);
        
        frame.add(tabPane);
       
        frame.pack();
        frame.setVisible(true);
        
		
	}
	
	/**
	 * Sets up the main tab, adding in the pitch choice, the direction
	 * choice, the robot-colour choice and save/load buttons.
	 */
	private void setUpMainPanel() {
		
		/* Pitch choice */
		JPanel pitch_panel = new JPanel();
		JLabel pitch_label = new JLabel("Pitch:");
		pitch_panel.add(pitch_label);
		
		ButtonGroup pitch_choice = new ButtonGroup();
		pitch_0 = new JButton("Main");
		pitch_1 = new JButton("Side Room");
		pitch_choice.add(pitch_0);
		pitch_panel.add(pitch_0);
		pitch_choice.add(pitch_1);
		pitch_panel.add(pitch_1);
		
		
		pitch_0.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				
				worldState.setRoom(0);
				System.err.println(worldState.getRoom());
			}
		});
		pitch_1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				
				worldState.setRoom(1);
				System.err.println(worldState.getRoom());
			}
		});
		
		defaultPanel.add(pitch_panel);
		
		/* Colour choice */
		JPanel colour_panel = new JPanel();
		JLabel colour_label = new JLabel("Our colour:");
		colour_panel.add(colour_label);
		
		ButtonGroup colour_choice = new ButtonGroup();
		colour_yellow = new JButton("Yellow");
		colour_blue = new JButton("Blue");
		
		colour_yellow.setActionCommand("Yellow");
		colour_blue.setActionCommand("Blue");
		
		colour_choice.add(colour_yellow);
		colour_panel.add(colour_yellow);
		colour_choice.add(colour_blue);
		colour_panel.add(colour_blue);
		
		//colour_yellow.setSelected(true);
		
		colour_yellow.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				
				worldState.setColor(Color.yellow);
				System.err.println(worldState.getColor());
			}
		});
		colour_blue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				worldState.setColor(Color.blue);
				System.err.println(worldState.getColor());
		
		}
		
			
		});
			
		defaultPanel.add(colour_panel);
		
	

		
		/* Direction choice */
		JPanel direction_panel = new JPanel();
		JLabel direction_label = new JLabel("Our shooting direction:");
		direction_panel.add(direction_label);
		
		ButtonGroup direction_choice = new ButtonGroup();
		direction_right = new JButton("Right");
		direction_left = new JButton("Left");
		direction_choice.add(direction_right);
		direction_panel.add(direction_right);
		direction_choice.add(direction_left);
		direction_panel.add(direction_left);
		
		direction_right.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				worldState.setShootingDirection(1);
				System.err.println(Integer.toString(worldState.getShootingDirection()));
			}
		});
		direction_left.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e){
				worldState.setShootingDirection(-1);
				System.err.println(Integer.toString(worldState.getShootingDirection()));
			}
		});
		
		defaultPanel.add(direction_panel);
		
		
		JPanel startStopPanel = new JPanel();
		
		startButton = new JButton("Start Match");
		
		startStopPanel.add(startButton);
		
		startButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.mileStone3NavigateOn();
		    }
		});
		
		stopButton = new JButton("Stop Match");
		
		startStopPanel.add(stopButton);
		
		stopButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	strat.mileStone3NavigateOff();
		    }
		});
		
		
		defaultPanel.add(startStopPanel);
		
		JPanel overlayPanel = new JPanel();
		
		overlayButton = new JButton("Toggle Overlay");
		barrelButton = new JButton("Toggle Barrel Fix");
		
		overlayPanel.add(overlayButton);
		overlayPanel.add(barrelButton);
		
		overlayButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        worldState.setGenerateOverlay(!worldState.getGenerateOverlay());
		    }
		});
		
		barrelButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        worldState.setBarrelFix(!worldState.getBarrelFix());
		    }
		});
		
		defaultPanel.add(overlayPanel);
		
		/*
		Penalty Mode buttons. 
		*/
		
		JPanel penaltyPanel = new JPanel();
		
		penaltyAttackButton = new JButton("Penalty Shoot");
		penaltyDefendButton = new JButton("Penalty Goalie");
		returnToGame = new JCheckBox("Return to Game");
		
		penaltyPanel.add(returnToGame);
		penaltyPanel.add(penaltyAttackButton);
		penaltyPanel.add(penaltyDefendButton);
		
		returnToGame.addActionListener(new ActionListener() {
		    
		    @Override
		    //Boolean value to be sent with function calls to strategy for penalties
		    //letting them know whether they should continue playing or not after taking or saving penalty
		    public void actionPerformed(ActionEvent e) {
		        penaltyToGame = !(penaltyToGame);
		        if (penaltyToGame) {
		            System.err.println("Checked");
		        } else {
		            System.err.println("Unchecked");
		        }
		    }
		});
		
		penaltyAttackButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to strategy letting them know that bot is taking a penalty
		        //strat.startPlanningThread(PlanTypes.PlanType.PENALTY_OFFENCE.ordinal());
		    	Vision.logger.info("Penalty not in Milestone3.");
		    }
		});
		
		penaltyDefendButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//strat.startPlanningThread(PlanTypes.PlanType.PENALTY_DEFENCE.ordinal());
		    	Vision.logger.info("Penalty not in Milestone3.");
		    }
		});
		
		defaultPanel.add(penaltyPanel);
		
		/*Currently Milestone 1 stuff - WILL CHANGE */
		
		JPanel misc = new JPanel();
		
		kickButton = new JButton("Kick");
		driveButton = new JButton("Drive");
		
		misc.add(kickButton);
		misc.add(driveButton);
		
		kickButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to control saying kick
		        strat.mileStone1(true);
		    }
		});
		
		driveButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to control saying to drive
		    	strat.mileStone1(false);
		    }
		});
		
		defaultPanel.add(misc);
	}
	


}
