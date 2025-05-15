import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.InputMismatchException;
import javax.swing.JFrame;

/**
 * This class, when constructed, runs AI simulations with either default or given parameters depending on the constructor
 * used.
 * @author Joshua Rissman
 *
 */
public class AI_Evolution extends Canvas
{
	private static final long serialVersionUID = 5L;
	
	/** The width of {@link #frame}. */
	int WIDTH = 500;
	/** The usable width of {@link #frame} (determined when {@link #init(String, int, int)} is run).*/
	int USABLE_WIDTH = WIDTH;
	/** The height of {@link #frame}. */
	int HEIGHT = 500;
	/** The usable height of {@link #frame} (determined when {@link #init(String, int, int)} is run).*/
	int USABLE_HEIGHT = HEIGHT;
	/** How far the AI travel per step in pixels. */
	int STEP_SIZE = 5;
	/** The number of steps the AI will have to take on their descent. */
	int STEPS = USABLE_HEIGHT / STEP_SIZE;
	/** The number of AI per generation. */
	int AI_COUNT = 1000;
	/** The size of the AI (represented as squares). **/
	int AI_SIZE = 5;
	/** The number of generations trained before showing the best AI. */
	int VIEW_RATE = 100;
	/** The wait after the BufferStrategy is created in milliseconds. Prevents flickering. */
	int WAIT_AFTER_BUFFERSTRATEGY_CREATION = 50;
	/** The wait between each generation being drawn in milliseconds. */
	int WAIT_BETWEEN_GENERATIONS = 500;
	/** 
	 * This is the wait after the best AI is drawn in milliseconds. Only effects the program when {@link 
	 * #SILENT_SIMULATE} is false. 
	 */
	int WAIT_AFTER_BEST = 2000;
	/** 
	 * The wait between each step being drawn in milliseconds. Only effects the program when {@link #SILENT_SIMULATE} is 
	 * false.
	 */
	int WAIT_BETWEEN_STEPS = 10;
	/** The horizontal distance the AI can travel each step (reassigned to be {@link #USABLE_WIDTH} when {@link #init
	 * (String, int, int)} is run). */
	int TOLERANCE = USABLE_WIDTH;
	/** Keeps track of the number of generations that have been simulated. */
	int GEN_COUNTER = 0;
	/** The rate at which mutation can occur. */
	double MUTATION_RATE = 1.0 / 95.0;
	/** The title of {@link #frame}. */
	String TITLE = "Evolve!";
	/** The color for each step of the best AI. */
	Color BEST_AI_COLOR = Color.red;
	/** The color for each step of any AI that isn't the best. */
	static Color DEFAULT_AI_COLOR = Color.black;
	/** The color the background is drawn as. */
	Color BACKGROUND_COLOR = Color.white;
	/** Determines whether or not only the best AI will be shown. Setting this value to true will hide other AI, while 
	 * false will show them. Note that it will render all {@link #VIEW_RATE} number of simulations, then show the best AI
	 * found, as it would when set to true.
	 */
	boolean SILENT_SIMULATE = true;
	/**
	 * The frame in which the simulation is shown. Will halt the program when closed. Will not close automatically when 
	 * the simulation is done.
	 */
	JFrame frame;
	/** This variable stores the Graphics for this canvas from the BufferStrategy */
	Graphics draw;

	public static void main(String[] args)
	{
		//allows for this file to be run to start the simulation
		
		//the default, equivalent to new AI_Evolution(500, 500, 5, 1000, 5, 100, 1000, 50, 500, 
		//	1.0 / 95.0, Color.black, Color.white);
		new AI_Evolution(); 
		
		//a user specified silent run, equivalent to new AI_Evolution(); 
		new AI_Evolution(500, 500, 5, 1000, 5, 100, 500, 50, 500, 1.0 / 95.0, Color.black, Color.white);
		
		//a user specified non-silent run, equivalent to new AI_Evolution(); where SILENT_SIMULATE = false; 
		new AI_Evolution(500, 500, 5, 1000, 5, 100, 500, 50, 500, 2000, 10, 1.0 / 95.0, Color.red, Color.white, 
				Color.black);
	}
	
	/**
	 * This constructor calls {@link #init(String, int, int)} to setup the JFrame object, {@link #setBufferStrategy()} to  
	 * setup this canvas' BufferStrategy and instantiate {@link #draw} and {@link #frame} then starts the simulation. It 
	 * also sets the default AI color for AIEv objects.
	 */
	public AI_Evolution()
	{
		//sets up the JFrame frame
		init(TITLE, WIDTH, HEIGHT);
		
		//redefines TOLERANCE based on the potentially updated USABLE_WIDTH in init()
		TOLERANCE = USABLE_WIDTH;
		
		setBufferStrategy();
		
		//starts the simulation
		run(SILENT_SIMULATE);
	}
	
	
	/**
	 * A constructor for the user to customize their own set of simulations that only show the best AI (see 
	 * {@link #Portfolio.AI_Evolution.AI_Evolution(int, int, int, int, int, int, int, int, int, int, int, double, Color, 
	 * Color, Color)} for runs that show all AI simulations. Warning: since the user provides their own 
	 * {@code width} and {@code height}, the element {@link #frame}{@code .title} may not be visible.
	 * @param width The width of {@link #frame}.
	 * @param height The height of {@link #frame}.
	 * @param step_size How far the AI travel per step in pixels.
	 * @param ai_count The number of AI per generation.
	 * @param ai_size The size of the AI (represented as squares).
	 * @param view_rate The number of generations trained before showing the best AI.
	 * @param tolerance The horizontal distance the AI can travel each step.
	 * @param wait_after_bufferstrategy_creation
	 * @param wait_between_generations The wait after the BufferStrategy is created in milliseconds. Meant to prevent 
	 * flickering.
	 * @param mutation_rate The rate at which mutation can occur.
	 * @param ai_color The color of the best AI (which is the only AI shown using this constructor).
	 * @param background_color The color the background is drawn as.
	 * @throws InputMismatchException Thrown in three cases. 
	 * <p>
	 * Case 1: {@code width}, {@code height},  {@code step_size}, {@code ai_count}, 
	 * {@code ai_size}, {@code view_rate},  {@code tolerance}, or {@code mutation_rate} are less than or equal to zero.
	 * </p>
	 * <p>
	 * Case 2: {@code wait_after_bufferstrategy_creation} or {@code wait_between_generations} are less than zero.
	 * </p>
	 * <p>
	 * Case 3: From the {@code width} and {@code height} provided, there is not enough usable space to run the simulation.
	 * </p>
	 */
	public AI_Evolution(int width, int height, int step_size, int ai_count, int ai_size, int view_rate, 
			int tolerance, int wait_after_bufferstrategy_creation, int wait_between_generations, double mutation_rate,
			Color ai_color, Color background_color) throws InputMismatchException
	{
		//global variable initializations
		SILENT_SIMULATE = true;
		WIDTH = width;
		HEIGHT = height;
		STEP_SIZE = step_size;
		AI_COUNT = ai_count;
		AI_SIZE = ai_size;
		VIEW_RATE = view_rate;
		TOLERANCE = tolerance;
		WAIT_AFTER_BUFFERSTRATEGY_CREATION = wait_after_bufferstrategy_creation;
		WAIT_BETWEEN_GENERATIONS = wait_between_generations;
		MUTATION_RATE = mutation_rate;
		BEST_AI_COLOR = ai_color;
		BACKGROUND_COLOR = background_color;
		
		//case 1 exceptions:
		if(WIDTH <= 0)
		{
			throw new InputMismatchException("Width must be greater than 0.");
		}
		if(HEIGHT <= 0)
		{
			throw new InputMismatchException("Height must be greater than 0.");
		}
		if(STEP_SIZE <= 0)
		{
			throw new InputMismatchException("The step size must be greater than 0.");
		}
		if(AI_COUNT <= 0)
		{
			throw new InputMismatchException("The AI count (number of AI) must be greater than 0.");
		}
		if(AI_SIZE <= 0)
		{
			throw new InputMismatchException("The AI size must be greater than 0.");
		}
		if(VIEW_RATE <= 0)
		{
			throw new InputMismatchException("The view rate must be greater than 0.");
		}
		if(TOLERANCE <= 0)
		{
			throw new InputMismatchException("The tolerance must be greater than 0.");
		}
		if(MUTATION_RATE <= 0)
		{
			throw new InputMismatchException("The mutation rate must be greater than 0.");
		}
		
		//case 2 exceptions:
		if(WAIT_AFTER_BUFFERSTRATEGY_CREATION < 0)
		{
			throw new InputMismatchException("The wait after BufferStrategy creation rate must not be less than 0.");
		}
		if(WAIT_BETWEEN_GENERATIONS < 0)
		{
			throw new InputMismatchException("The wait between generations must not be less than 0.");
		}
		
		//color overlap warning
		if(BACKGROUND_COLOR.equals(BEST_AI_COLOR))
		{
			System.out.println("WARNING: BACKGROUND COLOR AND BEST AI COLOR ARE THE SAME. THEREFORE THE BEST"
					+ " AI MAY NOT BE VISIBLE.");
		}
		
		//AI size and step size overlap warning and correction
		if(STEP_SIZE < AI_SIZE)
		{
			System.out.printf("The step size of the AI is smaller than the size of the AI itself, and will thus overlap."
					+ " Your selected AI size is %d and your selected AI step size is %d. Therefore, the step size"
					+ " will be updated to %d.\n", step_size, ai_size, ai_size);
			STEP_SIZE = AI_SIZE;
			
		}
		
		//sets up the JFrame frame (also determines USABLE_WIDTH and USABLE_HEIGHT)
		init(TITLE, WIDTH, HEIGHT);
		
		
		//excessive tolerance warning and correction
		if(USABLE_WIDTH < TOLERANCE)
		{
			System.out.printf("The usable width of the canvas area is smaller than the tolerance of the AI. Therefore,"
					+ " the tolerance will be updated to the usable width: %d.\n", USABLE_WIDTH);
			TOLERANCE = USABLE_WIDTH;
			
		}
		
		//case 3 exceptions:
		try
		{
			//USABLE_WIDTH already contains the - AI_SIZE check we would otherwise do
			if(USABLE_WIDTH < 0)
			{
				throw new InputMismatchException(String.format("There is not enough usable width for the given AI size. Please"
						+ " check to make sure that\n"
						+ "USABLE_WIDTH - AI_SIZE < 0\n"
						+ "%d - %d = %d", USABLE_WIDTH + AI_SIZE, AI_SIZE, USABLE_WIDTH));
			}
			
			if(USABLE_HEIGHT - AI_SIZE < 0)
			{
				throw new InputMismatchException(String.format("There is not enough usable height for the given AI size. "
						+ "Please check to make sure that\n"
						+ "USABLE_HEIGHT - AI_SIZE < 0 \n"
						+ "%d - %d = %d", USABLE_HEIGHT, AI_SIZE, USABLE_HEIGHT - AI_SIZE));
			}
		}
		catch(InputMismatchException ex) 
		{
			frame.setTitle("Error! Check console!");
			throw ex;
		}
		
		
		setBufferStrategy();
		
		//starts the simulation
		run(SILENT_SIMULATE);
	}
	
	/**
	 * A constructor for the user to customize their own set of simulations that show all AI simulations (see 
	 * {@link #Portfolio.AI_Evolution.AI_Evolution(int, int, int, int, int, int, int, int, int, double, Color, Color)} 
	 * for runs that show only the best AI simulations. Warning: since the user provides their own 
	 * {@code width} and {@code height}, the element {@link #frame}{@code .title} may not be visible.
	 * @param width The width of {@link #frame}.
	 * @param height The height of {@link #frame}.
	 * @param step_size How far the AI travel per step in pixels.
	 * @param ai_count The number of AI per generation.
	 * @param ai_size The size of the AI (represented as squares).
	 * @param view_rate The number of generations trained before showing the best AI.
	 * @param tolerance The horizontal distance the AI can travel each step.
	 * @param wait_after_bufferstrategy_creation
	 * @param wait_between_generations The wait after the BufferStrategy is created in milliseconds. Meant to prevent 
	 * flickering.
	 * @param wait_after_best This is the wait after the best AI is drawn in milliseconds.
	 * @param wait_between_steps The wait between each step being drawn in milliseconds.
	 * @param mutation_rate The rate at which mutation can occur.
	 * @param best_ai_color The color of the best AI (which is the only AI shown using this constructor).
	 * @param background_color The color the background is drawn as.
	 * @param default_ai_color The color for each step of any AI that isn't the best.
	 * @throws InputMismatchException Thrown in three cases. 
	 * <p>
	 * Case 1: {@code width}, {@code height},  {@code step_size}, {@code ai_count}, 
	 * {@code ai_size}, {@code view_rate},  {@code tolerance}, or {@code mutation_rate} are less than or equal to zero.
	 * </p>
	 * <p>
	 * Case 2: {@code wait_after_bufferstrategy_creation}, {@code wait_between_generations}, {@code wait_after_best}, 
	 * or {@code wait_between_steps} are less than zero.
	 * </p>
	 * <p>
	 * Case 3: From the {@code width} and {@code height} provided, there is not enough usable space to run the simulation.
	 * </p>
	 */
	public AI_Evolution(int width, int height, int step_size, int ai_count, int ai_size, int view_rate, 
			int tolerance, int wait_after_bufferstrategy_creation, int wait_between_generations, int wait_after_best,
			int wait_between_steps, double mutation_rate, Color best_ai_color, Color background_color, 
			Color default_ai_color) throws InputMismatchException
	{
		//global variable initializations
		SILENT_SIMULATE = false;
		WIDTH = width;
		HEIGHT = height;
		STEP_SIZE = step_size;
		AI_COUNT = ai_count;
		AI_SIZE = ai_size;
		VIEW_RATE = view_rate;
		TOLERANCE = tolerance;
		WAIT_AFTER_BUFFERSTRATEGY_CREATION = wait_after_bufferstrategy_creation;
		WAIT_BETWEEN_GENERATIONS = wait_between_generations;
		WAIT_AFTER_BEST = wait_after_best;
		WAIT_BETWEEN_STEPS = wait_between_steps;
		MUTATION_RATE = mutation_rate;
		BEST_AI_COLOR = best_ai_color;
		BACKGROUND_COLOR = background_color;
		DEFAULT_AI_COLOR = default_ai_color;
		
		//case 1 exceptions:
		if(WIDTH <= 0)
		{
			throw new InputMismatchException("Width must be greater than 0.");
		}
		if(HEIGHT <= 0)
		{
			throw new InputMismatchException("Height must be greater than 0.");
		}
		if(STEP_SIZE <= 0)
		{
			throw new InputMismatchException("The step size must be greater than 0.");
		}
		if(AI_COUNT <= 0)
		{
			throw new InputMismatchException("The AI count (number of AI) must be greater than 0.");
		}
		if(AI_SIZE <= 0)
		{
			throw new InputMismatchException("The AI size must be greater than 0.");
		}
		if(VIEW_RATE <= 0)
		{
			throw new InputMismatchException("The view rate must be greater than 0.");
		}
		if(TOLERANCE <= 0)
		{
			throw new InputMismatchException("The tolerance must be greater than 0.");
		}
		if(MUTATION_RATE <= 0)
		{
			throw new InputMismatchException("The mutation rate must be greater than 0.");
		}
		
		//case 2 exceptions:
		if(WAIT_AFTER_BUFFERSTRATEGY_CREATION < 0)
		{
			throw new InputMismatchException("The wait after BufferStrategy creation rate must not be less than 0.");
		}
		if(WAIT_BETWEEN_GENERATIONS < 0)
		{
			throw new InputMismatchException("The wait between generations must not be less than 0.");
		}
		if(WAIT_AFTER_BEST < 0)
		{
			throw new InputMismatchException("The wait after the best AI is shown must not be less than 0.");
		}
		if(WAIT_BETWEEN_STEPS < 0)
		{
			throw new InputMismatchException("The wait between the steps the AI takes must not be less than 0.");
		}
		
		//color overlap warnings
		if(BACKGROUND_COLOR.equals(BEST_AI_COLOR))
		{
			System.out.println("WARNING: BACKGROUND COLOR AND BEST AI COLOR ARE THE SAME. THEREFORE THE BEST"
					+ " AI MAY NOT BE VISIBLE.");
		}
		if(BACKGROUND_COLOR.equals(DEFAULT_AI_COLOR))
		{
			System.out.println("WARNING: BACKGROUND COLOR AND DEFAULT AI COLOR ARE THE SAME. THEREFORE THE DEFAULT"
					+ " AI MAY NOT BE VISIBLE.");
		}
		if(BEST_AI_COLOR.equals(DEFAULT_AI_COLOR))
		{
			System.out.println("WARNING: BEST AI COLOR AND DEFAULT AI COLOR ARE THE SAME. THEREFORE THE BEST"
					+ " AI AND DEFAULT AI MAY NOT BE DISTINGUISHABLE.");
		}
		
		//AI size and step size overlap warning and correction
		if(STEP_SIZE < AI_SIZE)
		{
			System.out.printf("The step size of the AI is smaller than the size of the AI itself, and will thus overlap."
					+ " Your selected AI size is %d and your selected AI step size is %d. Therefore, the step size"
					+ " will be updated to %d.\n", step_size, ai_size, ai_size);
			STEP_SIZE = AI_SIZE;
			
		}
		
		//sets up the JFrame frame (also determines USABLE_WIDTH and USABLE_HEIGHT)
		init(TITLE, WIDTH, HEIGHT);
		
		
		//excessive tolerance warning and correction
		if(USABLE_WIDTH < TOLERANCE)
		{
			System.out.printf("The usable width of the canvas area is smaller than the tolerance of the AI. Therefore,"
					+ " the tolerance will be updated to the usable width: %d.\n", USABLE_WIDTH);
			TOLERANCE = USABLE_WIDTH;
			
		}
		
		//case 3 exceptions:
		try
		{
			//USABLE_WIDTH already contains the - AI_SIZE check we would otherwise do
			if(USABLE_WIDTH < 0)
			{
				throw new InputMismatchException(String.format("There is not enough usable width for the given AI size. Please"
						+ " check to make sure that\n"
						+ "USABLE_WIDTH - AI_SIZE < 0\n"
						+ "%d - %d = %d", USABLE_WIDTH + AI_SIZE, AI_SIZE, USABLE_WIDTH));
			}
			
			if(USABLE_HEIGHT - AI_SIZE < 0)
			{
				throw new InputMismatchException(String.format("There is not enough usable height for the given AI size. "
						+ "Please check to make sure that\n"
						+ "USABLE_HEIGHT - AI_SIZE < 0 \n"
						+ "%d - %d = %d", USABLE_HEIGHT, AI_SIZE, USABLE_HEIGHT - AI_SIZE));
			}
		}
		catch(InputMismatchException ex) 
		{
			frame.setTitle("Error! Check console!");
			throw ex;
		}
		
		
		setBufferStrategy();
		
		//starts the simulation
		run(SILENT_SIMULATE);
	}
	
	/**
	 * Sets up {@link #frame} using the given title, width, and height.
	 * @param title 	A String that is the title to be set for {@link #frame}.
	 * @param width 	An int that is to be the width for {@link #frame}.
	 * @param height 	An int that is to be the height for ({@link #frame}.
	 */
	public void init(String title, int width, int height)
	{
		//create a frame with the given title and dimensions
		frame = new JFrame(title);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		frame.setSize(new Dimension(width, height));
		
		//set the default close operation, and turn off resizing
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		//set the location to the center of the screen
		frame.setLocationRelativeTo(null);
		
		//add this canvas to the frame and make it visible
		frame.add(this);
		frame.setVisible(true);	
		
		//fixes WIDTH so that it matches the actual frame
		if(frame.getWidth() != WIDTH)
		{
			int frameWidth = frame.getWidth();
			System.out.printf("The width given (%d) and the actual width (%d) do not match! The program will accomodate"
					+ " this, but the user should know that in your case %d seems to be the minimum width.\n",
					WIDTH, frameWidth, frameWidth);
			WIDTH = frameWidth;
		}
		//fixes HEIGHT so that it matches the actual frame
		if(frame.getHeight() != HEIGHT)
		{
			int frameHeight = frame.getHeight();
			System.out.printf("The height given (%d) and the actual height (%d) do not match! The program will accomodate"
					+ " this, but the user should know that in your case %d seems to be the minimum height.\n",
					WIDTH, frameHeight, frameHeight);
			WIDTH = frameHeight;
		}
		
		//determine the usable width and height in frame which makes it so that the steps are all visible
		Insets insets = frame.getInsets();
		USABLE_HEIGHT = HEIGHT - insets.top - insets.bottom;
		USABLE_WIDTH = WIDTH - insets.left - insets.left - AI_SIZE;
		STEPS = USABLE_HEIGHT / STEP_SIZE;
	}

	/**
	 * This method starts a simulation and runs until the simulation has produced a perfect AI (the AI that follows the
	 * given criteria the best).
	 * @param silent A boolean that determines whether or not this simulation will show the 
	 */
	public void run(boolean silent)
	{
		//the current best AI. "best" meaning the closest to making a straight line down the middle
		AIEv bestAI = new AIEv(STEPS, USABLE_WIDTH);
		//reset COUNT for a new simulation
		GEN_COUNTER = 0;
		//set the frame's title to the current generation
		frame.setTitle(String.format("Generation %d", GEN_COUNTER));
		//plot the current best AI, which is the ancestor for all future AI
		plotBest(bestAI);
		
		//use a while loop instead of a do-while just in case the current best AI is already perfect
		while(!isPerfect(bestAI))
		{
			bestAI = simulate(bestAI, VIEW_RATE, TOLERANCE, silent);
			frame.setTitle(String.format("Generation %d", GEN_COUNTER));
			if(!silent)
			{
				frame.setTitle("Current Best AI");
			}
			plotBest(bestAI);
		}
		
		//the best will not have been shown without 
		plotBest(bestAI);
		//show the user that the AI have found the center, and how many generations it took for them to do so.
		frame.setTitle(String.format("Finished at Generation %d!", GEN_COUNTER));
	}
	
	/**
	 * Runs {@code count} simulations based on {@code bestAI}.
	 * @param bestAI The best AI found thus far. This will be used as a basis for the future generations.
	 * @param count How many generations should pass before returning the best AI found.
	 * @param tolerance How far the AI can step horizontally.
	 * @param silent Whether or not this is a silent run. <b>{@code true}</b> if it is silent, <b>{@code false}</b>
	 * if it is not.
	 * @return the best AI found within {@code count} simulations.
	 */
	public AIEv simulate(AIEv bestAI, int count, int tolerance, boolean silent)
	{
		//count is the variable that counts down from VIEW_RATE so that an update on the best AI will be given at regular
		//	intervals. Therefore, when count reaches zero, return the best AI found thus far.
		if(count == 0)
		{
			return bestAI;
		}
		
		//increase the label for this generation
		GEN_COUNTER++;
		
		//make an ArrayList to hold all the AI for this generation
		ArrayList<AIEv> arr = new ArrayList<AIEv>();
		
		//add new AI based on the best from the previous generation. Since that best AI will be included, start from 1.
		for(int a = 1; a < AI_COUNT; a++)
		{
			arr.add(new AIEv(bestAI.steps, USABLE_WIDTH, tolerance, MUTATION_RATE));
		}
		//add the best AI to the ArrayList
		arr.add(bestAI);
		
		//If this is a non-silent run, then the following code displays all the steps in between the VIEW_RATE number of
		//	generations.
		if(!silent)
		{
			frame.setTitle(String.format("Generation %d", GEN_COUNTER));
			int step = 0;
			while(step < STEPS)
			{
				render(step, arr);
				step++;
			}
			sleep(WAIT_BETWEEN_GENERATIONS);
		}
		
		//arbitrarily say 0th AI is the worst, but will immediately check that assumption
		int indexOfBest = 0;
		double scoreOfBest = Double.MAX_VALUE; //lowest possible score (explained below)
		
		//determines the best AI
		for(int a = 0; a < arr.size(); a++)
		{
			//fitness function. Lower scores are better, and is determined by closeness to the center on average
			double score = 0;
			for(int x: arr.get(a).steps)
			{
				score += Math.abs(WIDTH / 2.0 - x);
			}
			
			//code to set the score of the AI. Currently has no function, but functionality may be added in a future
			//	update
//			arr.get(a).score = score;
			
			//determines if this AI is better than the best or not according to the fitness function, and redefines which
			//	is the best if necessary.
			if(scoreOfBest > score)
			{
				indexOfBest = a;
				scoreOfBest = score;
			}
		}
		
		//color the best AI
		arr.get(indexOfBest).color = BEST_AI_COLOR;
		
		//call this method again until count is zero. Once count is zero, the best AI found within the given VIEW_RATE
		//	will have been found.
		return simulate(arr.get(indexOfBest), count - 1, tolerance, silent);
	}
	
	/**
	 * Draws all given AI for a single given step for visual comparison. This will only be used in non-silent runs.
	 * @param step The step to view.
	 * @param arr An ArrayList of AI of which all will have this step drawn.
	 */
	public void render(int step, ArrayList<AIEv> arr)
	{
		//draw the background
		draw.setColor(BACKGROUND_COLOR);
		draw.fillRect(0, 0, WIDTH, HEIGHT);
		
		//draw all AI in arr but only their current step
		for(AIEv ai: arr)
		{
			//set color for the AI
			draw.setColor(ai.color);
			//the x coordinate is determined by the AI, every x coordinate has a unique y coordinate in ascending order 
			//	(note that here, an ascending y value means going DOWN the screen), the final two parameters are width
			//	 and height, which are the same since every step is a square
			draw.fillRect(ai.get(step), step * STEP_SIZE, AI_SIZE, AI_SIZE);
		}
		this.getBufferStrategy().show();
		
		//a user determined pause to allow for greater or lesser visibility of each individual step
		sleep(WAIT_BETWEEN_STEPS);
	}
	
	/**
	 * A method that checks if an AI is "perfect" determined by if every step falls in the center (integer division). 
	 * @param ai 
	 * @return a boolean. <b>{@code true}</b> if every step of the AI lands in the center of the canvas. Otherwise, 
	 * <b>{@code false}</b>.
	 */
	public boolean isPerfect(AIEv ai)
	{
		int center = WIDTH / 2;
		for(int x: ai.steps)
		{
			if(x != center)
				return false;
		}
		return true;
	}
	
	/**
	 * Given an AI, shows all the steps it has taken and colors it {@link #BEST_AI_COLOR}.
	 * @param bestAI The AI to be drawn.
	 */
	public void plotBest(AIEv bestAI)
	{
		
		//draw the background
		draw.setColor(BACKGROUND_COLOR);
		draw.fillRect(0, 0, WIDTH, HEIGHT);
		
		//set color for the best AI
		draw.setColor(BEST_AI_COLOR);
		//contains the current step in the y direction, starting at 0
		int step = 0;
		for(int curstep: bestAI.steps)
		{
			//the x coordinate is determined by the AI, every x coordinate has a unique y coordinate in ascending order 
			//	(note that here, an ascending y value means going DOWN the screen), the final two parameters are width
			//	 and height, which are the same since every step is a square
			draw.fillRect(curstep, step * STEP_SIZE, AI_SIZE, AI_SIZE);
			//increase the y value
			step++;
		}
		
		this.getBufferStrategy().show();
		
		if(SILENT_SIMULATE)
			sleep(WAIT_BETWEEN_GENERATIONS);
		else
			sleep(WAIT_AFTER_BEST);
	}
	
	/**
	 * A sleep method meant to halt the program until the set amount of time has passed. This method is primarily used 
	 * for waiting between drawing to the screen for the user to see what has been drawn.
	 * @param milliseconds An int representing the number of milliseconds to be awaited.
	 */
	public void sleep(int milliseconds)
	{
		long time = System.currentTimeMillis() + milliseconds;
		while(time > System.currentTimeMillis()) {}
	}
	
	/**
	 * Sets the BufferStrategy for this canvas and instantiates {@link #draw}
	 */
	public void setBufferStrategy()
	{
		this.createBufferStrategy(1);
		//wait for the the BufferStrategy to be set before continuing, seems to need time to process to prevent 
		//	flickering and loss of content.
		sleep(WAIT_AFTER_BUFFERSTRATEGY_CREATION);
		
		//instantiate draw, so that objects may be drawn to the canvas
		draw = this.getBufferStrategy().getDrawGraphics();
	}
}

/**
 * This class is the actual AI to be used by AI_Evolution
 * @author Joshua Rissman
 *
 */
class AIEv
{
	/** The color value AI will take by default upon creation */
	Color color = AI_Evolution.DEFAULT_AI_COLOR;
	/** 
	 * This does not yet do anything. I have left it in case I decide to expand on the functionality and show scores or 
	 * something to that effect.
	 */
	double score = 0;
	/** Holds the integer representation for the horizontal (x-values) of the steps that make up this AI. */
	ArrayList<Integer> steps = new ArrayList<Integer>();
	
	/**
	 * A constructor that builds an AI based on a given AI that it will randomly deviate from depending on mutation rate
	 * and step size ({@code tolerance})
	 * @param steps An ArrayList of the steps of a parent AI for this AI to be based on.
	 * @param frameWidth The width of the frame this AI is drawn in. Determines the limits of how far to the left and 
	 * right it can go. 
	 * @param tolerance The distance horizontally that the AI can move in one step.
	 * @param rateOfMutation The mutation rate of this AI. In other words, how likely it is that one of its parents steps
	 * are to be different from its own.
	 */
	public AIEv(ArrayList<Integer> steps, int frameWidth, int tolerance, double rateOfMutation)
	{
		for(int num: steps)
		{
			//Checks for mutation by seeing if a random number is less than or equal to it
			//	fun fact: this if-statement could be rewritten as "if((int)(Math.random() / rateOfMutation) == 0)"
			if(Math.random() <= rateOfMutation)
			{
				//"num +" makes the new step related to its predecessor (the original AI's step). 
				//	Math.round(Math.random() * tolerance * 2 - tolerance) makes it so that there is an equal chance of 
				//	stepping left or right (the limit of newStep as Math.random() approaches 0 is num - tolerance, whereas
				//	the of newStep limit as Math.random() approaches 1 is num + tolerance).
				int newStep = (int) (num + Math.round(Math.random() * tolerance * 2 - tolerance));
				
				//prevent the newStep from exceeding the width of the frame by rounding it to either edge
				if(newStep >= frameWidth)
				{
					newStep = frameWidth;
				}
				else if(newStep < 0)
				{
					newStep = 0;
				}
				this.steps.add(newStep);
			}
			else
			{
				this.steps.add(num);
			}
		}
	}
	
	/**
	 * A constructor that builds and AI from scratch. This AI has no limit on how far it can step horizontally so that it 
	 * may start off purely random.
	 * @param steps An ArrayList of the steps of a parent AI for this AI to be based on.
	 * @param frameWidth The width of the frame this AI is drawn in. Determines the limits of how far to the left and 
	 * right it can go.
	 */
	public AIEv(int stepsAllowed, int frameWidth)
	{
		steps = new ArrayList<Integer>();
		for(int a = 0; a < stepsAllowed; a++)
		{
			int step = (int)Math.round(Math.random() * frameWidth);
			this.steps.add(step);
		}
	}
	
	/**
	 * A standard "getter" method so outside classes can access the steps without modifying them.
	 * @param step The index of the step the user wants to retrieve
	 * @return an int which represents the horizontal placement (x-value) of this particular step.
	 */
	public int get(int step)
	{
		return steps.get(step);
	}
}
