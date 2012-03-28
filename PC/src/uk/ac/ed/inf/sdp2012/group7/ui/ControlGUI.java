package uk.ac.ed.inf.sdp2012.group7.ui;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;
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
	private ControlInterface ci = ControlInterface.getInstance();
	
	private final Strategy strat;
	
	/* The main frame holding the Control GUI. */
	private JFrame frame;
	
	/* Kick and drive buttons - for M1 but can change function as neccessary*/
	private JButton kickButton;
	private JButton driveButton;
	
	
	/* Start/Stop Button */
	private JButton startPlanButton;
	private JButton stopPlanButton;
	private JButton startMovingButton;
	private JButton stopMovingButton;
	
	/*Logging buttons*/
	private JButton traceButton;
	private JButton infoButton;
	private JButton debugButton;
	private JButton errorButton;
	private JButton fatalButton;
	private JButton offButton;

	
	
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
	private JPanel commandTestingPanel;
	
	
	/* Radio buttons */
	JButton pitch_0;
	JButton pitch_1;
	JButton colour_yellow;
	JButton colour_blue;
	JButton direction_right;
	JButton direction_left;
	
	/* Buttons for command testing panel */
	private JButton forwards;
	private JButton backwards;
	private JButton forwardsDistance;
	private JButton backwardsDistance;
	private JButton forwardsFast;
	private JButton backwardsFast;
	private JButton forwardsSlow;
	private JButton backwardsSlow;
	private JButton rotateLeft;
	private JButton rotateRight;
	private JButton rotateLeft90;
	private JButton rotateRight90;
	private JButton arcLeft;
	private JButton arcRight;
	private JButton kick;
	private JButton stop;
	
    private boolean penaltyToGame = false;

    
	/**
	 * Default constructor. 
	 * 
	 * @param thresholdsState	A ThresholdsState object to update the threshold slider
	 * 							values.			
	 * @param worldState		A WorldState object to update the pitch choice, shooting
	 * 							direction, etc.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public ControlGUI(Strategy s) throws IOException, ClassNotFoundException {
		
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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();
                try {
    				FileOutputStream saveFile = new FileOutputStream("saveFile.sav");
    				ObjectOutputStream save =new ObjectOutputStream(saveFile);
    				save.writeObject(thresholdsState);
    				save.close();
    				
    			} catch (FileNotFoundException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
             		
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }

		
        });

        
        frame.setLayout(new FlowLayout());
        
        /* Create panels for each of the tabs */
        tabPane = new JTabbedPane();
        
        defaultPanel = new JPanel();
        defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));
    
        thresholdingPanel = new JPanel();
        thresholdingPanel.setLayout(new BoxLayout(thresholdingPanel, BoxLayout.Y_AXIS));
        
        commandTestingPanel = new JPanel();
        commandTestingPanel.setLayout(new BoxLayout(commandTestingPanel, BoxLayout.Y_AXIS));
        
        /* The main (default) tab */
        setUpMainPanel(); 
        
        /* The Tresholding tab */
        setUpThresholdingPanel();
        
        setUpCommandTestingPanel();
        
        
        
        tabPane.addTab("World Information", defaultPanel); 
        tabPane.addTab("Thresholding", thresholdingPanel);
        tabPane.addTab("Command Testing", commandTestingPanel);
    
        tabPane.addChangeListener(this);
        
        frame.add(tabPane);
       
        frame.pack();
        frame.setVisible(true);
        
        /* Fires off an initial pass through the ChangeListener method,
         * to initialise all of the default values. */
        this.stateChanged(null);
        
		
	}
	private void setUpCommandTestingPanel() {
		JPanel nonBlocking = new JPanel();
		JLabel nonBlockingLabel = new JLabel("Non-blocking:");
		nonBlocking.add(nonBlockingLabel);
		
		forwards = new JButton("Forwards");
		backwards = new JButton("Backwards");
		forwardsFast = new JButton("Forwards fast");
		backwardsFast = new JButton("Backwards fast");
		forwardsSlow = new JButton("Forwards slow");
		backwardsSlow = new JButton("Backwards slow");
		
		forwards.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.drive();
		    }
		});
				
		backwards.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveBackwards();
		    }
		});
		
		forwardsFast.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveForwardsSpeed(600);
		    }
		});
		
		backwardsFast.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveBackwardsSpeed(600);
		    }
		});
		
		forwardsSlow.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveForwardsSpeed(100);
		    }
		});
		
		backwardsSlow.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveBackwardsSpeed(100);
		    }
		});
		
		nonBlocking.add(forwards);
		nonBlocking.add(backwards);
		nonBlocking.add(forwardsFast);
		nonBlocking.add(backwardsFast);
		nonBlocking.add(forwardsSlow);
		nonBlocking.add(backwardsSlow);
		commandTestingPanel.add(nonBlocking);
		
		
		JPanel blocking = new JPanel();
		JLabel blockingLabel = new JLabel("Blocking:");
		blocking.add(blockingLabel);
		
		forwardsDistance = new JButton("Forwards 30cm");
		backwardsDistance = new JButton("Backwards 30cm");
		
		forwardsDistance.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveForwardsDistance(30);
		    }
		});
		
		backwardsDistance.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.moveBackwardsDistance(30);
		    }
		});
		
		blocking.add(forwardsDistance);
		blocking.add(backwardsDistance);		
		commandTestingPanel.add(blocking);
		
		JPanel rotate = new JPanel();
		JLabel rotateLabel = new JLabel("Rotation:");
		rotate.add(rotateLabel);
		
		rotateRight = new JButton("Rotate right non-blocking");
		rotateLeft = new JButton("Rotate left non-blocking");
		rotateRight90 = new JButton("Rotate right 90");
		rotateLeft90 = new JButton("Rotate left 90");
		
		rotateRight.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.rotateRight();
		    }
		});
		
		rotateLeft.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.rotateLeft();
		    }
		});
		
		rotateRight90.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.rotateBy(Math.PI/2,true);
		    }
		});
		
		rotateLeft90.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.rotateBy(Math.PI/2,false);
		    }
		});
		
		rotate.add(rotateRight);
		rotate.add(rotateLeft);
		rotate.add(rotateRight90);
		rotate.add(rotateLeft90);
		commandTestingPanel.add(rotate);
		
		JPanel arcing = new JPanel();
		JLabel arcingLabel = new JLabel("Arcing:");
		arcing.add(arcingLabel);
		
		arcLeft = new JButton("Arc left");
		arcRight = new JButton("Arc right");
		
		arcLeft.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.arc(20, true);
		    }
		});
		
		arcRight.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.arc(20,false);
		    }
		});
		
		
		arcing.add(arcLeft);
		arcing.add(arcRight);		
		commandTestingPanel.add(arcing);
		
		arcing.add(arcLeft);
		arcing.add(arcRight);		
		commandTestingPanel.add(arcing);
		
		JPanel kickPanel = new JPanel();
		kick = new JButton("Kick");

		kick.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.kick();
		    }
		});
		kickPanel.add(kick);
		commandTestingPanel.add(kickPanel);
		
		JPanel stopPanel = new JPanel();
		stop = new JButton("STOP");

		stop.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        ci.stop();
		    }
		});
		stopPanel.add(stop);
		commandTestingPanel.add(stopPanel);
		
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
                0, 255, thresholdsState.getBall_r());
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
                0, 255, thresholdsState.getBall_g());
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
                0, 255, thresholdsState.getBall_b());
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
                0, 255, thresholdsState.getBlue_r());
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
                0, 255, thresholdsState.getBlue_g());
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
                0, 255, thresholdsState.getBlue_b());
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
                0, 255, thresholdsState.getGreen_g());
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
                0, 80, thresholdsState.getGreen_RG());
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
                0, 80, thresholdsState.getGreen_GB());
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
		
		
		JPanel startStopPlanPanel = new JPanel();
		
		
		startPlanButton = new JButton("Start Planning");
		
		startStopPlanPanel.add(startPlanButton);
		
		startPlanButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.startPlanningThread(PlanTypes.PlanType.FREE_PLAY.ordinal());
		       
		        
		    }
		});
		
		stopPlanButton = new JButton("Stop Planning");
		
		startStopPlanPanel.add(stopPlanButton);
		
		stopPlanButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        strat.stopPlanningThread();
		        WorldState.getInstance().canMove = false;
		    }
		});
		
		
		JPanel startStopMovingPanel = new JPanel();
		
		startMovingButton = new JButton("Start Moving");
		
		startStopMovingPanel.add(startMovingButton);
		
		startPlanButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        WorldState.getInstance().canMove = true;
		    }
		});
		
		stopMovingButton = new JButton("Stop Moving");
		
		startStopMovingPanel.add(stopMovingButton);
		
		stopMovingButton.addActionListener(new ActionListener() {
		    
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	WorldState.getInstance().canMove = false;
		    }
		});
		
		
		
		defaultPanel.add(startStopPlanPanel);
		defaultPanel.add(startStopMovingPanel);
		
		
		JPanel loggerPanel = new JPanel();
		
		traceButton = new JButton("Trace");
		loggerPanel.add(traceButton);
		traceButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.TRACE);
		    }
		});
		
		infoButton = new JButton("Info");
		loggerPanel.add(infoButton);
		infoButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.INFO);
		    }
		});
		
		debugButton = new JButton("Debug");
		loggerPanel.add(debugButton);
		debugButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.DEBUG);
		    }
		});
		
		errorButton = new JButton("Error");
		loggerPanel.add(errorButton);
		errorButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.ERROR);
		    }
		});
		
		fatalButton = new JButton("Fatal");
		loggerPanel.add(fatalButton);
		fatalButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.FATAL);
		    }
		});
		
		offButton = new JButton("Off");
		loggerPanel.add(offButton);
		offButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Logger.getRootLogger().setLevel(Level.OFF);
		    }
		});
		
		
		
		defaultPanel.add(loggerPanel);
		
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
