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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.PlanTypes;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
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
	private ThresholdsState thresholdsState = ThresholdsState.getInstance();
	
	/* Stores information about the current world state, such as 
	 * shooting direction, ball location, etc. */
	private WorldState worldState = WorldState.getInstance();
	
	private final Strategy strat;
	
	/* The main frame holding the Control GUI. */
	private JFrame frame;
	
	/* Kick and drive buttons - for M1 but can change function as neccessary*/
	private JButton kickButton;
	private JButton driveButton;
	
	
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
	private JPanel thresholdingPanel;
	
	private JButton milestone4Button;
	
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
	public ControlGUI(Strategy s) {
		
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
    
        thresholdingPanel = new JPanel();
        thresholdingPanel.setLayout(new BoxLayout(thresholdingPanel, BoxLayout.Y_AXIS));
        
        
        /* The main (default) tab */
        setUpMainPanel(); 
        
        /* The Tresholding tab */
        
        setUpThresholdingPanel();
        
        
        
        tabPane.addTab("World Information", defaultPanel); 
        tabPane.addTab("Thresholding", thresholdingPanel);
    
        tabPane.addChangeListener(this);
        
        frame.add(tabPane);
       
        frame.pack();
        frame.setVisible(true);
        
        /* Fires off an initial pass through the ChangeListener method,
         * to initialise all of the default values. */
        this.stateChanged(null);
        
		
	}
	/**
	 * Sets up the Thresholding tab, where sliders for the thresholding are employed 
	 * to get more accurate thresholding in RGB 
	 */
	private void setUpThresholdingPanel() {
		
		JPanel ball = new JPanel();
		JLabel ball_label = new JLabel("Ball  R :");
		
		ball.add(ball_label);
		
		JSlider ball_R = new JSlider(JSlider.HORIZONTAL,
                0, 255, 130);
		ball_R.setMajorTickSpacing(40);
		ball_R.setMinorTickSpacing(20);
		ball_R.setPaintTicks(true);
		ball_R.setPaintLabels(true);
		ball_R.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBall_r(value);
				}				
			}
		});
		ball.add(ball_R);
		
		JLabel ball_label1 = new JLabel("  Ball  G :");
		ball.add(ball_label1);
		
		JSlider ball_G = new JSlider(JSlider.HORIZONTAL,
                0, 255, 90);
		ball_G.setMajorTickSpacing(40);
		ball_G.setMinorTickSpacing(20);
		ball_G.setPaintTicks(true);
		ball_G.setPaintLabels(true);
		ball_G.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBall_g(value);
				}				
			}
		});
		ball.add(ball_G);
		
		JLabel ball_label2 = new JLabel("  Ball  B :");
		ball.add(ball_label2);
		
		JSlider ball_B = new JSlider(JSlider.HORIZONTAL,
                0, 255, 90);
		ball_B.setMajorTickSpacing(40);
		ball_B.setMinorTickSpacing(20);
		ball_B.setPaintTicks(true);
		ball_B.setPaintLabels(true);
		ball_B.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBall_b(value);
				}				
			}
		});
		ball.add(ball_B);
		
		thresholdingPanel.add(ball);
		
		
		JPanel blue = new JPanel();
		JLabel blue_label = new JLabel("  Blue  R :");
		
		blue.add(blue_label);
		
		JSlider blue_R = new JSlider(JSlider.HORIZONTAL,
                0, 255, 120);
		blue_R.setMajorTickSpacing(40);
		blue_R.setMinorTickSpacing(20);
		blue_R.setPaintTicks(true);
		blue_R.setPaintLabels(true);
		blue_R.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBlue_r(value);
				}				
			}
		});
		
		blue.add(blue_R);
		
		JLabel blue_label1 = new JLabel("  Blue G :");
		
		blue.add(blue_label1);
		
		JSlider blue_G = new JSlider(JSlider.HORIZONTAL,
                0, 255, 170);
		blue_G.setMajorTickSpacing(40);
		blue_G.setMinorTickSpacing(20);
		blue_G.setPaintTicks(true);
		blue_G.setPaintLabels(true);
		blue_G.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBlue_g(value);
				}				
			}
		});
		
		blue.add(blue_G);
		
		JLabel blue_label2 = new JLabel("  Blue B :");
		
		blue.add(blue_label2);
		
		JSlider blue_B = new JSlider(JSlider.HORIZONTAL,
                0, 255, 90);
		blue_B.setMajorTickSpacing(40);
		blue_B.setMinorTickSpacing(20);
		blue_B.setPaintTicks(true);
		blue_B.setPaintLabels(true);
		blue_B.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setBlue_b(value);
				}				
			}
		});
		
		blue.add(blue_B);
		
		thresholdingPanel.add(blue);

		
		JPanel greenPlate = new JPanel();
		JLabel greenPlate_label = new JLabel("  greenPlate G : ");
		
		greenPlate.add(greenPlate_label);
		
		
		JSlider greenPlate_G = new JSlider(JSlider.HORIZONTAL,
                0, 255, 120);
		greenPlate_G.setMajorTickSpacing(40);
		greenPlate_G.setMinorTickSpacing(20);
		greenPlate_G.setPaintTicks(true);
		greenPlate_G.setPaintLabels(true);
		greenPlate_G.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setGreen_g(value);
				}				
			}
		});
		greenPlate.add(greenPlate_G);
		
		JLabel greenLabel1  = new JLabel("  G-R :");
		greenPlate.add(greenLabel1);
		
		JSlider greenPlate_RG = new JSlider(JSlider.HORIZONTAL,
                0, 80, 50);
		greenPlate_RG.setMajorTickSpacing(20);
		greenPlate_RG.setMinorTickSpacing(5);
		greenPlate_RG.setPaintTicks(true);
		greenPlate_RG.setPaintLabels(true);
		greenPlate_RG.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setGreen_RG(value);
				}				
			}
		});
		greenPlate.add(greenPlate_RG);
		
		JLabel greenLabel2  = new JLabel("  G-B :");
		greenPlate.add(greenLabel2);
		
		JSlider greenPlate_GB = new JSlider(JSlider.HORIZONTAL,
                0, 80, 50);
		greenPlate_GB.setMajorTickSpacing(20);
		greenPlate_GB.setMinorTickSpacing(5);
		greenPlate_GB.setPaintTicks(true);
		greenPlate_GB.setPaintLabels(true);
		greenPlate_GB.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int value = 0;
				if (!source.getValueIsAdjusting()) {
					value  = (int) source.getValue();
					thresholdsState.setGreen_GB(value);
				}				
			}
		});
		greenPlate.add(greenPlate_GB);
		
		thresholdingPanel.add(greenPlate);
		
		
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
		direction_left = new JButton("Left");
		direction_right = new JButton("Right");
		direction_choice.add(direction_left);
		direction_panel.add(direction_left);
		direction_choice.add(direction_right);
		direction_panel.add(direction_right);
		
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
		
		milestone4Button = new JButton("Milestone 4");
		
		startStopPanel.add(milestone4Button);
		
		startButton = new JButton("Start Match");
		
		startStopPanel.add(startButton);
		
		milestone4Button.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.startPlanningThread(PlanTypes.PlanType.MILESTONE_4.ordinal());
		    }
		});
		
		startButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.startPlanningThread(PlanTypes.PlanType.FREE_PLAY.ordinal());
		       
		        
		    }
		});
		
		stopButton = new JButton("Stop Match");
		
		startStopPanel.add(stopButton);
		
		stopButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.stopPlanningThread();
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
		        strat.startPlanningThread(PlanTypes.PlanType.PENALTY_OFFENCE.ordinal());
		    }
		});
		
		penaltyDefendButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	strat.startPlanningThread(PlanTypes.PlanType.PENALTY_DEFENCE.ordinal());
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
		        strat.getControlInterface().kick();
		    }
		});
		
		driveButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to control saying to drive
		    	strat.getControlInterface().drive();
		    }
		});
		
		defaultPanel.add(misc);
	}
	


}
