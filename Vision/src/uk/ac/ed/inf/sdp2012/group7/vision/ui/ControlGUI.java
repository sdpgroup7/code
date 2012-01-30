package uk.ac.ed.inf.sdp2012.group7.vision.ui;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.vision.PitchConstants;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
import uk.ac.ed.inf.sdp2012.group7.vision.WorldState;

/**
 * Creates and maintains the swing-based Control GUI, which 
 * provides both control manipulation (pitch choice, direction,
 * etc) and threshold setting. Also allows the saving/loading of
 * threshold values to a file.
 * 
 * @author s0840449
 */
public class ControlGUI implements ChangeListener {
	
	/* A PitchConstants class used to load/save constants
	 * for the pitch. */
	private PitchConstants pitchConstants;
	
	/* The thresholds state class stores the current state 
 	 * of the thresholds. */
	private ThresholdsState thresholdsState;
	
	/* Stores information about the current world state, such as 
	 * shooting direction, ball location, etc. */
	private WorldState worldState;
	
	private final Strategy strat;
	
	/* The main frame holding the Control GUI. */
	private JFrame frame;
	
	/* Load/Save buttons. */
	private JButton saveButton;
	private JButton loadButton;
	
	/* Kick and drive buttons - for M1 but can change function as neccessary*/
	private JButton kickButton;
	private JButton driveButton;
	
	/*Locate Button*/
	private JButton locateButton;
	
	/* Start and Stop Buttons */
	private JButton startButton;
	private JButton stopButton;
	
	/*Penalty mode buttons */
	private JButton penaltyAttackButton;
	private JButton penaltyDefendButton;
	private JCheckBox returnToGame;
	
	/* Tabs. */
	private JTabbedPane tabPane;
	private JPanel defaultPanel;
	private JPanel ballPanel;
	private JPanel bluePanel;
	private JPanel yellowPanel;
	private JPanel greyPanel;
	private JPanel greenPanel;
	
	/* Radio buttons */
	JRadioButton pitch_0;
	JRadioButton pitch_1;
	JRadioButton colour_yellow;
	JRadioButton colour_blue;
	JRadioButton direction_right;
	JRadioButton direction_left;
	
	/* Ball sliders. */
	private RangeSlider ball_r;
	private RangeSlider ball_g;
	private RangeSlider ball_b;
	
	/* Blue robot sliders. */
	private RangeSlider blue_r;
	private RangeSlider blue_g;
	private RangeSlider blue_b;
	
	/* Yellow robot sliders. */
	private RangeSlider yellow_r;
	private RangeSlider yellow_g;
	private RangeSlider yellow_b;
	
	/* Grey circle sliders. */
	private RangeSlider grey_r;
	private RangeSlider grey_g;
	private RangeSlider grey_b;
	
	/* Green circle sliders. */
	private RangeSlider green_r;
	private RangeSlider green_g;
	private RangeSlider green_b;

	public final int THRESHOLD = 25;
	
    private boolean penaltyToGame = false;

	public void setBlueValues(Color c){
		setBlueValues(c.getRed(),c.getGreen(),c.getBlue());
	}

	public void setYellowValues(Color c){
		setYellowValues(c.getRed(),c.getGreen(),c.getBlue());
	}

	public void setGreyValues(Color c){
		setGreyValues(c.getRed(),c.getGreen(),c.getBlue());
	}

	public void setGreenValues(Color c){
		setGreenValues(c.getRed(),c.getGreen(),c.getBlue());
	}

	public void setBallValues(Color c){
		setBallValues(c.getRed(),c.getGreen(),c.getBlue());
	}



	public void setBlueValues(int r, int g, int b){

		int rLower = r-THRESHOLD;
		int rUpper = r+THRESHOLD;
		int gLower = g-THRESHOLD;
		int gUpper = g+THRESHOLD;
		int bLower = b-THRESHOLD;
		int bUpper = b+THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;

		blue_r.setValue(rLower);
		blue_r.setUpperValue(rUpper);
		blue_g.setValue(gLower);
		blue_g.setUpperValue(gUpper);
		blue_b.setValue(bLower);
		blue_b.setUpperValue(bUpper);

	}

	public void setYellowValues(int r, int g, int b){
		int rLower = r-THRESHOLD;
		int rUpper = r+THRESHOLD;
		int gLower = g-THRESHOLD;
		int gUpper = g+THRESHOLD;
		int bLower = b-THRESHOLD;
		int bUpper = b+THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;

		yellow_r.setValue(rLower);
		yellow_r.setUpperValue(rUpper);
		yellow_g.setValue(gLower);
		yellow_g.setUpperValue(gUpper);
		yellow_b.setValue(bLower);
		yellow_b.setUpperValue(bUpper);
	}

	public void setGreyValues(int r, int g, int b){
		int rLower = r-THRESHOLD;
		int rUpper = r+THRESHOLD;
		int gLower = g-THRESHOLD;
		int gUpper = g+THRESHOLD;
		int bLower = b-THRESHOLD;
		int bUpper = b+THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;

		grey_r.setValue(rLower);
		grey_r.setUpperValue(rUpper);
		grey_g.setValue(gLower);
		grey_g.setUpperValue(gUpper);
		grey_b.setValue(bLower);
		grey_b.setUpperValue(bUpper);
	}

	public void setGreenValues(int r, int g, int b){
		int rLower = r-THRESHOLD;
		int rUpper = r+THRESHOLD;
		int gLower = g-THRESHOLD;
		int gUpper = g+THRESHOLD;
		int bLower = b-THRESHOLD;
		int bUpper = b+THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;

		green_r.setValue(rLower);
		green_r.setUpperValue(rUpper);
		green_g.setValue(gLower);
		green_g.setUpperValue(gUpper);
		green_b.setValue(bLower);
		green_b.setUpperValue(bUpper);
	}

	public void setBallValues(int r, int g, int b){
		int rLower = r-THRESHOLD;
		int rUpper = r+THRESHOLD;
		int gLower = g-THRESHOLD;
		int gUpper = g+THRESHOLD;
		int bLower = b-THRESHOLD;
		int bUpper = b+THRESHOLD;

		if(rLower < 0) rLower = 0;
		if(gLower < 0) gLower = 0;
		if(bLower < 0) bLower = 0;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;
		if(rUpper > 255) rUpper = 255;

		ball_r.setValue(rLower);
		ball_r.setUpperValue(rUpper);
		ball_g.setValue(gLower);
		ball_g.setUpperValue(gUpper);
		ball_b.setValue(bLower);
		ball_b.setUpperValue(bUpper);
	}
	
	/**
	 * Default constructor. 
	 * 
	 * @param thresholdsState	A ThresholdsState object to update the threshold slider
	 * 							values.			
	 * @param worldState		A WorldState object to update the pitch choice, shooting
	 * 							direction, etc.
	 * @param pitchConstants	A PitchConstants object to allow saving/loading of data.
	 */
	public ControlGUI(ThresholdsState thresholdsState, WorldState worldState, PitchConstants pitchConstants) {
		
		/* All three state objects must not be null. */
		assert (thresholdsState != null);
		assert (worldState != null);
		assert (pitchConstants != null);
		
		this.thresholdsState = thresholdsState;
		this.worldState = worldState;
		this.pitchConstants = pitchConstants;
		strat = new Strategy(worldState);
	}
	
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
        
        ballPanel = new JPanel();
        ballPanel.setLayout(new BoxLayout(ballPanel, BoxLayout.Y_AXIS));
        
        bluePanel = new JPanel();
        bluePanel.setLayout(new BoxLayout(bluePanel, BoxLayout.Y_AXIS));
        
        yellowPanel = new JPanel();
        yellowPanel.setLayout(new BoxLayout(yellowPanel, BoxLayout.Y_AXIS));
        
        greyPanel = new JPanel();
        greyPanel.setLayout(new BoxLayout(greyPanel, BoxLayout.Y_AXIS));
        
        greenPanel = new JPanel();
        greenPanel.setLayout(new BoxLayout(greenPanel, BoxLayout.Y_AXIS));
                
        /* The main (default) tab */
        setUpMainPanel();
        
        /* The five threshold tabs. */
        setUpBallSliders();
        setUpBlueSliders();
        setUpYellowSliders();
        setUpGreySliders();
        setUpGreenSliders();
        
        tabPane.addTab("World Information", defaultPanel);
        tabPane.addTab("Ball", ballPanel);
        tabPane.addTab("Blue Robot", bluePanel);
        tabPane.addTab("Yellow Robot", yellowPanel);
        tabPane.addTab("Grey Circles", greyPanel);
        tabPane.addTab("Green Plates", greenPanel);
        
        tabPane.addChangeListener(this);
        
        frame.add(tabPane);
       
        frame.pack();
        frame.setVisible(true);
        
        /* Fires off an initial pass through the ChangeListener method,
         * to initialise all of the default values. */
        this.stateChanged(null);
		
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
		pitch_0 = new JRadioButton("Main");
		pitch_1 = new JRadioButton("Side Room");
		pitch_choice.add(pitch_0);
		pitch_panel.add(pitch_0);
		pitch_choice.add(pitch_1);
		pitch_panel.add(pitch_1);
		
		pitch_0.setSelected(true);
		
		pitch_0.addChangeListener(this);
		pitch_1.addChangeListener(this);
		
		defaultPanel.add(pitch_panel);
		
		/* Colour choice */
		JPanel colour_panel = new JPanel();
		JLabel colour_label = new JLabel("Our colour:");
		colour_panel.add(colour_label);
		
		ButtonGroup colour_choice = new ButtonGroup();
		colour_yellow = new JRadioButton("Yellow");
		colour_blue = new JRadioButton("Blue");
		colour_choice.add(colour_yellow);
		colour_panel.add(colour_yellow);
		colour_choice.add(colour_blue);
		colour_panel.add(colour_blue);
		
		colour_yellow.setSelected(true);
		
		colour_yellow.addChangeListener(this);
		colour_blue.addChangeListener(this);
		
		defaultPanel.add(colour_panel);
		
		/* Direction choice */
		JPanel direction_panel = new JPanel();
		JLabel direction_label = new JLabel("Our shooting direction:");
		direction_panel.add(direction_label);
		
		ButtonGroup direction_choice = new ButtonGroup();
		direction_right = new JRadioButton("Right");
		direction_left = new JRadioButton("Left");
		direction_choice.add(direction_right);
		direction_panel.add(direction_right);
		direction_choice.add(direction_left);
		direction_panel.add(direction_left);
		
		direction_right.setSelected(true);
		
		direction_right.addChangeListener(this);
		direction_left.addChangeListener(this);
		
		defaultPanel.add(direction_panel);
		
		/* Save/load buttons */
		JPanel saveLoadPanel = new JPanel();
		
		saveButton = new JButton("Save Thresholds");
		saveButton.addActionListener(new ActionListener() {
			
			/* Attempt to write all of the current thresholds to a file with a name 
			 * based on the currently selected pitch. */
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(frame, 
								"Are you sure you want to save current constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				try {
					FileWriter writer = new FileWriter(new File("constants/pitch" + pitchNum));
					
					/* Ball */
					writer.write(String.valueOf(ball_r.getValue()) + "\n");
					writer.write(String.valueOf(ball_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_g.getValue()) + "\n");
					writer.write(String.valueOf(ball_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(ball_b.getValue()) + "\n");
					writer.write(String.valueOf(ball_b.getUpperValue()) + "\n");
					
					/* Blue */
					writer.write(String.valueOf(blue_r.getValue()) + "\n");
					writer.write(String.valueOf(blue_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_g.getValue()) + "\n");
					writer.write(String.valueOf(blue_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(blue_b.getValue()) + "\n");
					writer.write(String.valueOf(blue_b.getUpperValue()) + "\n");
					
					/* Yellow */
					writer.write(String.valueOf(yellow_r.getValue()) + "\n");
					writer.write(String.valueOf(yellow_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_g.getValue()) + "\n");
					writer.write(String.valueOf(yellow_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(yellow_b.getValue()) + "\n");
					writer.write(String.valueOf(yellow_b.getUpperValue()) + "\n");;
					
					/* Grey */
					writer.write(String.valueOf(grey_r.getValue()) + "\n");
					writer.write(String.valueOf(grey_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_g.getValue()) + "\n");
					writer.write(String.valueOf(grey_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(grey_b.getValue()) + "\n");
					writer.write(String.valueOf(grey_b.getUpperValue()) + "\n");
					
					/* Green */
					writer.write(String.valueOf(green_r.getValue()) + "\n");
					writer.write(String.valueOf(green_r.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_g.getValue()) + "\n");
					writer.write(String.valueOf(green_g.getUpperValue()) + "\n");
					writer.write(String.valueOf(green_b.getValue()) + "\n");
					writer.write(String.valueOf(green_b.getUpperValue()) + "\n");
					
					/* We need to re-write the pitch dimensions. 
					 * TODO: This currently means that cross-saving values
					 * is basically unsupported as they will overwrite the
					 * pitch dimensions incorrectly.*/
					writer.write(String.valueOf(pitchConstants.getTopBuffer()) + "\n");
					writer.write(String.valueOf(pitchConstants.getBottomBuffer()) + "\n");
					writer.write(String.valueOf(pitchConstants.getLeftBuffer()) + "\n");
					writer.write(String.valueOf(pitchConstants.getRightBuffer()) + "\n");
					
					writer.flush();
					writer.close();
					
					System.out.println("Wrote successfully!");
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		saveLoadPanel.add(saveButton);
		
		loadButton = new JButton("Load Thresholds");
		loadButton.addActionListener(new ActionListener() {
			
			/* Override the current threshold settings from those set in
			 * the correct constants file for the current pitch. */
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int pitchNum = (pitch_0.isSelected()) ? 0 : 1;
				
				int result = JOptionPane.showConfirmDialog(frame, 
								"Are you sure you want to load pre-saved constants for pitch " + pitchNum + "?");
				
				if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) return;
				
				pitchConstants.setPitchNum(pitchNum);
				reloadSliderDefaults();
				
			}
		});
		
		saveLoadPanel.add(loadButton);
		
		defaultPanel.add(saveLoadPanel);
		
		/*
		The locate button is pressed to start the locating of the objects
		and pitch boundaries
		*/
		
		JPanel locatePanel = new JPanel();
		
		locateButton = new JButton("Locate Objects");
		
		locatePanel.add(locateButton);
		locateButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //Call the methods to find and set the thresholds of the objects and locate the corners
		        //Call to vision
		        System.err.println("Run Location");
		    }
		});
		    
		
		defaultPanel.add(locatePanel);
		
		/*
		Buttons for starting and stopping the match, before starting you have
		to have done locate.  Stop will be used for breaks in play etc.
		*/
		
		JPanel startStopPanel = new JPanel();
		
		startButton = new JButton("Start Match");
		stopButton = new JButton("Stop Match");
		
		startStopPanel.add(startButton);
		startStopPanel.add(stopButton);
		
		startButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to strategy system for there start of match code
		        System.err.println("Start Strategy");
		    }
		});
		
		stopButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to strategy to get them to stop what they are doing
		        System.err.println("Stop the strategy");
		    }
		});
		
		defaultPanel.add(startStopPanel);
		
		/*
		Penalty Mode buttons.  Stop button must be pressed first
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
		        System.err.println("Penalty Attack");
		    }
		});
		
		penaltyDefendButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to strategy to let them know to prepare to save
		        System.err.println("Goalie Mode");
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
		        //call to strategy letting them know that bot is taking a penalty
		        strat.mileStone1(true);
		    }
		});
		
		driveButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        //call to strategy letting them know that bot is taking a penalty
		    	strat.mileStone1(false);
		    }
		});
		
		defaultPanel.add(misc);
	}
	
	/**
	 * Sets up the sliders for the thresholding of the ball.
	 */
	private void setUpBallSliders() {
				
		 /* Red. */
		JPanel ball_r_panel = new JPanel();
        JLabel ball_r_label = new JLabel("Red:");
		ball_r = setUpSlider(0, 255, pitchConstants.ball_r_low, pitchConstants.ball_r_high, 10, 50);
		ball_r_panel.add(ball_r_label);
		ball_r_panel.add(ball_r);
		ballPanel.add(ball_r_panel);
        
        /* Green. */
		JPanel ball_g_panel = new JPanel();
        JLabel ball_g_label = new JLabel("Green:");
        ball_g = setUpSlider( 0, 255, pitchConstants.ball_g_low, pitchConstants.ball_g_high, 10, 50);
        ball_g_panel.add(ball_g_label);
		ball_g_panel.add(ball_g);
		ballPanel.add(ball_g_panel);
        
        /* Blue. */
		JPanel ball_b_panel = new JPanel();
        JLabel ball_b_label = new JLabel("Blue:");
        ball_b = setUpSlider( 0, 255, pitchConstants.ball_b_low, pitchConstants.ball_b_high, 10, 50);
        ball_b_panel.add(ball_b_label);
		ball_b_panel.add(ball_b);
		ballPanel.add(ball_b_panel);
                
        ball_r.addChangeListener(this);
        ball_g.addChangeListener(this);
        ball_b.addChangeListener(this);

		
	}
	
	/**
	 * Sets up the sliders for the thresholding of the blue robot.
	 */
	private void setUpBlueSliders() {

		/* Red. */
		JPanel blue_r_panel = new JPanel();
		JLabel blue_r_label = new JLabel("Red:");
		blue_r = setUpSlider(0, 255, pitchConstants.blue_r_low, pitchConstants.blue_r_high, 10, 50);
		blue_r_panel.add(blue_r_label);
		blue_r_panel.add(blue_r);
		bluePanel.add(blue_r_panel);

		/* Green. */
		JPanel blue_g_panel = new JPanel();
		JLabel blue_g_label = new JLabel("Green:");
		blue_g = setUpSlider( 0, 255, pitchConstants.blue_g_low, pitchConstants.blue_g_high, 10, 50);
		blue_g_panel.add(blue_g_label);
		blue_g_panel.add(blue_g);
		bluePanel.add(blue_g_panel);

		/* Blue. */
		JPanel blue_b_panel = new JPanel();
		JLabel blue_b_label = new JLabel("Blue:");
		blue_b = setUpSlider( 0, 255, pitchConstants.blue_b_low, pitchConstants.blue_b_high, 10, 50);
		blue_b_panel.add(blue_b_label);
		blue_b_panel.add(blue_b);
		bluePanel.add(blue_b_panel);

		blue_r.addChangeListener(this);
		blue_g.addChangeListener(this);
		blue_b.addChangeListener(this);

	}
	
	/**
	 * Sets up the sliders for the thresholding of the yellow robot.
	 */
	private void setUpYellowSliders() {
		
		/* Red. */
		JPanel yellow_r_panel = new JPanel();
		JLabel yellow_r_label = new JLabel("Red:");
		yellow_r = setUpSlider(0, 255, pitchConstants.yellow_r_low, pitchConstants.yellow_r_high, 10, 50);
		yellow_r_panel.add(yellow_r_label);
		yellow_r_panel.add(yellow_r);
		yellowPanel.add(yellow_r_panel);

		/* Green. */
		JPanel yellow_g_panel = new JPanel();
		JLabel yellow_g_label = new JLabel("Green:");
		yellow_g = setUpSlider( 0, 255, pitchConstants.yellow_g_low, pitchConstants.yellow_g_high, 10, 50);
		yellow_g_panel.add(yellow_g_label);
		yellow_g_panel.add(yellow_g);
		yellowPanel.add(yellow_g_panel);

		/* Blue. */
		JPanel yellow_b_panel = new JPanel();
		JLabel yellow_b_label = new JLabel("Blue:");
		yellow_b = setUpSlider( 0, 255, pitchConstants.yellow_b_low, pitchConstants.yellow_b_high, 10, 50);
		yellow_b_panel.add(yellow_b_label);
		yellow_b_panel.add(yellow_b);
		yellowPanel.add(yellow_b_panel);

		yellow_r.addChangeListener(this);
		yellow_g.addChangeListener(this);
		yellow_b.addChangeListener(this);;

	}
	
	/**
	 * Sets up the sliders for the thresholding of the grey robot.
	 */
	private void setUpGreySliders() {
		
		/* Red. */
		JPanel grey_r_panel = new JPanel();
		JLabel grey_r_label = new JLabel("Red:");
		grey_r = setUpSlider(0, 255, pitchConstants.grey_r_low, pitchConstants.grey_r_high, 10, 50);
		grey_r_panel.add(grey_r_label);
		grey_r_panel.add(grey_r);
		greyPanel.add(grey_r_panel);

		/* Green. */
		JPanel grey_g_panel = new JPanel();
		JLabel grey_g_label = new JLabel("Green:");
		grey_g = setUpSlider( 0, 255, pitchConstants.grey_g_low, pitchConstants.grey_g_high, 10, 50);
		grey_g_panel.add(grey_g_label);
		grey_g_panel.add(grey_g);
		greyPanel.add(grey_g_panel);

		/* Blue. */
		JPanel grey_b_panel = new JPanel();
		JLabel grey_b_label = new JLabel("Blue:");
		grey_b = setUpSlider( 0, 255, pitchConstants.grey_b_low, pitchConstants.grey_b_high, 10, 50);
		grey_b_panel.add(grey_b_label);
		grey_b_panel.add(grey_b);
		greyPanel.add(grey_b_panel);

		grey_r.addChangeListener(this);
		grey_g.addChangeListener(this);
		grey_b.addChangeListener(this);

	}
	
	/**
	 * Sets up the sliders for the thresholding of the green robot.
	 */
	private void setUpGreenSliders() {
		
		/* Red. */
		JPanel green_r_panel = new JPanel();
		JLabel green_r_label = new JLabel("Red:");
		green_r = setUpSlider(0, 255, pitchConstants.green_r_low, pitchConstants.green_r_high, 10, 50);
		green_r_panel.add(green_r_label);
		green_r_panel.add(green_r);
		greenPanel.add(green_r_panel);

		/* Green. */
		JPanel green_g_panel = new JPanel();
		JLabel green_g_label = new JLabel("Green:");
		green_g = setUpSlider( 0, 255, pitchConstants.green_g_low, pitchConstants.green_g_high, 10, 50);
		green_g_panel.add(green_g_label);
		green_g_panel.add(green_g);
		greenPanel.add(green_g_panel);

		/* Blue. */
		JPanel green_b_panel = new JPanel();
		JLabel green_b_label = new JLabel("Blue:");
		green_b = setUpSlider( 0, 255, pitchConstants.green_b_low, pitchConstants.green_b_high, 10, 50);
		green_b_panel.add(green_b_label);
		green_b_panel.add(green_b);
		greenPanel.add(green_b_panel);

		green_r.addChangeListener(this);
		green_g.addChangeListener(this);
		green_b.addChangeListener(this);

	}

	/**
	 * Creates and returns a new RangeSlider from a number of parameters.
	 * 
	 * @param minVal		The minimum value the slider can have.
	 * @param maxVal		The maximum value the slider can have.
	 * @param lowerVal		The initial value for the lower end of the range.
	 * @param upperVal		The initial value for the higher end of the range.
	 * @param minorTick		The minor tick distance.
	 * @param majorTick		The major tick distance.
	 * 
	 * @return				A new RangeSlider created from the given parameters.
	 */
	private RangeSlider setUpSlider(int minVal, int maxVal, int lowerVal, int upperVal, int minorTick, int majorTick) {
		
        RangeSlider slider = new RangeSlider(minVal, maxVal);
        
        setSliderVals(slider, lowerVal, upperVal);
        
        slider.setMinorTickSpacing(minorTick);
        slider.setMajorTickSpacing(majorTick);
        
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        return slider;
		
	}

	/**
	 * A Change listener for various components on the GUI. When a component is
	 * changed all information is updated.
	 * 
	 *  @param e		The event that was created for the change.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		
		/* Update the world state. */
		if (pitch_0.isSelected()) {
			worldState.setPitch(0);
		} else {
			worldState.setPitch(1);
		}
		if(colour_yellow.isSelected()) {
			worldState.setColour(0);
		} else {
			worldState.setColour(1);
		}
		if(direction_right.isSelected()) {
			worldState.setDirection(0);
		} else {
			worldState.setDirection(1);
		}
		
		/* Update the ThresholdsState object. */
		
		int index = tabPane.getSelectedIndex();
		
		switch(index) {
		case(0):
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(false);
			break;
		case(1):
			thresholdsState.setBall_debug(true);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(false);
			break;
		case(2):
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(true);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(false);
			break;
		case(3):
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(true);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(false);
			break;
		case(4):
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(true);
			thresholdsState.setGreen_debug(false);
			break;
		case(5):
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(true);
			break;
		default:
			thresholdsState.setBall_debug(false);
			thresholdsState.setBlue_debug(false);
			thresholdsState.setYellow_debug(false);
			thresholdsState.setGrey_debug(false);
			thresholdsState.setGreen_debug(false);
			break;
		}
		
		/* Ball. */
		thresholdsState.setBall_r_low(ball_r.getValue());
		thresholdsState.setBall_r_high(ball_r.getUpperValue());

		thresholdsState.setBall_g_low(ball_g.getValue());
		thresholdsState.setBall_g_high(ball_g.getUpperValue());
		
		thresholdsState.setBall_b_low(ball_b.getValue());
		thresholdsState.setBall_b_high(ball_b.getUpperValue());


		/* Blue Robot. */
		thresholdsState.setBlue_r_low(blue_r.getValue());
		thresholdsState.setBlue_r_high(blue_r.getUpperValue());

		thresholdsState.setBlue_g_low(blue_g.getValue());
		thresholdsState.setBlue_g_high(blue_g.getUpperValue());
		
		thresholdsState.setBlue_b_low(blue_b.getValue());
		thresholdsState.setBlue_b_high(blue_b.getUpperValue());
		
		/* Yellow Robot. */
		thresholdsState.setYellow_r_low(yellow_r.getValue());
		thresholdsState.setYellow_r_high(yellow_r.getUpperValue());

		thresholdsState.setYellow_g_low(yellow_g.getValue());
		thresholdsState.setYellow_g_high(yellow_g.getUpperValue());
		
		thresholdsState.setYellow_b_low(yellow_b.getValue());
		thresholdsState.setYellow_b_high(yellow_b.getUpperValue());
		
		/* Grey Circles. */
		thresholdsState.setGrey_r_low(grey_r.getValue());
		thresholdsState.setGrey_r_high(grey_r.getUpperValue());

		thresholdsState.setGrey_g_low(grey_g.getValue());
		thresholdsState.setGrey_g_high(grey_g.getUpperValue());
		
		thresholdsState.setGrey_b_low(grey_b.getValue());
		thresholdsState.setGrey_b_high(grey_b.getUpperValue());
		
		
		/* Green Circles. */
		thresholdsState.setGreen_r_low(green_r.getValue());
		thresholdsState.setGreen_r_high(green_r.getUpperValue());

		thresholdsState.setGreen_g_low(green_g.getValue());
		thresholdsState.setGreen_g_high(green_g.getUpperValue());
		
		thresholdsState.setGreen_b_low(green_b.getValue());
		thresholdsState.setGreen_b_high(green_b.getUpperValue());
		
		
	}
	
	/**
	 * Reloads the default values for the sliders from the PitchConstants file.
	 */
	public void reloadSliderDefaults() {
		
		/* Ball slider */
		setSliderVals(ball_r, pitchConstants.ball_r_low, pitchConstants.ball_r_high);
		setSliderVals(ball_g, pitchConstants.ball_g_low, pitchConstants.ball_g_high);
		setSliderVals(ball_b, pitchConstants.ball_b_low, pitchConstants.ball_b_high);
		
		/* Blue slider */
		setSliderVals(blue_r, pitchConstants.blue_r_low, pitchConstants.blue_r_high);
		setSliderVals(blue_g, pitchConstants.blue_g_low, pitchConstants.blue_g_high);
		setSliderVals(blue_b, pitchConstants.blue_b_low, pitchConstants.blue_b_high);
		
		/* Yellow slider */
		setSliderVals(yellow_r, pitchConstants.yellow_r_low, pitchConstants.yellow_r_high);
		setSliderVals(yellow_g, pitchConstants.yellow_g_low, pitchConstants.yellow_g_high);
		setSliderVals(yellow_b, pitchConstants.yellow_b_low, pitchConstants.yellow_b_high);
		
		/* Grey slider */
		setSliderVals(grey_r, pitchConstants.grey_r_low, pitchConstants.grey_r_high);
		setSliderVals(grey_g, pitchConstants.grey_g_low, pitchConstants.grey_g_high);
		setSliderVals(grey_b, pitchConstants.grey_b_low, pitchConstants.grey_b_high);
		
		/* Green slider */
		setSliderVals(green_r, pitchConstants.green_r_low, pitchConstants.green_r_high);
		setSliderVals(green_g, pitchConstants.green_g_low, pitchConstants.green_g_high);
		setSliderVals(green_b, pitchConstants.green_b_low, pitchConstants.green_b_high);
		
	}

	/**
	 * Set the the values of a range slider.
	 * 
	 * @param rangeSlider		The range slider to set the values for.
	 * @param low				The lower end of the range.
	 * @param high				The higher end of the range.
	 */
	private void setSliderVals(RangeSlider rangeSlider, int low, int high) {
		/* If try to set lower val > current higher val nothing will happen (and vice-versa),
         * so we set the lower val twice in case of this situation. */
		rangeSlider.setValue(low);
		rangeSlider.setUpperValue(high);
		rangeSlider.setValue(low);
	}

    public ThresholdsState getThresholdsState(){
        return this.thresholdsState;
    }

    public WorldState getWorldState(){
        return this.worldState;
    }

}
