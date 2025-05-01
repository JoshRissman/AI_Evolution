import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JFrame;

public class AI_Evolution extends Canvas
{
	private static final long serialVersionUID = 3L;
	
	/** The width of {@link #frame}. */
	final int WIDTH = 500;
	/** The usable width of {@link #frame} (determined when {@link #init(String, int, int)} is run).*/
	int USABLE_WIDTH = WIDTH;
	/** The height of {@link #frame}. */
	final int HEIGHT = 500;
	/** The usable height of {@link #frame} (determined when {@link #init(String, int, int)} is run).*/
	int USABLE_HEIGHT = HEIGHT;
	/** How far the AI travel per step in pixels. */
	final int STEP_SIZE = 5;
	/** The number of steps the AI will have to take on their descent. */
	int STEPS = USABLE_HEIGHT / STEP_SIZE;
	/** The number of AI per generation. */
	final int AI_COUNT = 1000;
	/** The number of generations trained before showing the results */
	final int VIEW_RATE = 100;
	/** The wait after the BufferStrategy is created in milliseconds. Prevents flickering. */
	final int WAIT_AFTER_BUFFERSTRATEGY_CREATION = 50;
	/** The wait between each generation being drawn in milliseconds. */
	final int WAIT_BETWEEN_GENERATIONS = 500;
	/** The wait between each step being drawn in milliseconds. Only effects the program when {@link #SILENT_SIMULATE} is 
	 * false.
	 */
	final int WAIT_BETWEEN_STEPS = 10;
	/** Keeps track of the number of generations that have been simulated. */
	int COUNT = 0;
	/** The title of {@link #frame}. */
	final String TITLE = "Evolve!";
	/** The color for each step of the best AI. */
	final Color BEST_AI_COLOR = Color.red;
	/** The color for each step of any AI that isn't the best. */
	final Color DEFAULT_AI_COLOR = Color.black;
	/** The color the background is drawn as */
	final Color BACKGROUND_COLOR = Color.white;
	/** Determines whether or not only the best AI will be shown. Setting this value to true will hide other AI, while 
	 * false will show them. Note that it will render all {@link #VIEW_RATE} number of simulations, then show the best AI
	 * found, as it would when set to true.
	 */
	final boolean SILENT_SIMULATE = true;
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
		new AI_Evolution();
		
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
		
		setBufferStrategy();
		
		//sets the default color for any AI that isn't the best
		AIEv.default_color = DEFAULT_AI_COLOR;
		
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
		
		//set the default close operation, and turn off resizing
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		//set the location to the center of the screen
		frame.setLocationRelativeTo(null);
		
		//add this canvas to the frame and make it visible
		frame.add(this);
		frame.setVisible(true);	
		
		//determine the usable width and height in frame which makes it so that the steps are all visible
		Insets insets = frame.getInsets();
		USABLE_HEIGHT = HEIGHT - insets.top - insets.bottom;
		USABLE_WIDTH = WIDTH - insets.left - insets.left;
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
		COUNT = 0;
		//set the frame's title to the current generation
		frame.setTitle(String.format("Generation %d", COUNT));
		//plot the current best AI, which is the ancestor for all future AI
		plotBest(bestAI);
		COUNT++;
		
		//use a while loop instead of a do-while just in case the current best AI is already perfect
		while(!isPerfect(bestAI))
		{
			// NOTE still an issue where the generation numbers are off
			frame.setTitle(String.format("Generation %d", COUNT));
			bestAI = simulate(bestAI, VIEW_RATE, WIDTH, silent);
			COUNT += VIEW_RATE;
			if(!silent)
			{
				frame.setTitle("Current Best AI");
				
			}
			plotBest(bestAI);
		}
		//the best will not have been shown without 
		plotBest(bestAI);
		//show the user that the AI have found the center, and how many generations it took for them to do so.
		frame.setTitle(String.format("Finished at Generation %d!", COUNT));
	}
	
	public AIEv simulate(AIEv bestAI, int count, int tolerance, boolean silent)
	{
		if(count == 0)
		{
			return bestAI;
		}
		ArrayList<AIEv> arr = new ArrayList<AIEv>();
		for(int a = 1; a < AI_COUNT; a++)
		{
			arr.add(new AIEv(bestAI.steps, WIDTH, tolerance));
		}
		arr.add(bestAI);
		
		if(!silent)
		{
			frame.setTitle(String.format("Generation %d", COUNT + VIEW_RATE - count));
			int step = 0;
			while(step < STEPS)
			{
				render(step, arr);
				step++;
			}
			sleep(WAIT_BETWEEN_GENERATIONS);
		}
		
		int indexOfBest = 0;
		double scoreOfBest = Double.MAX_VALUE;
		for(int a = 0; a < arr.size(); a++)
		{
			double score = 0;
			for(int x: arr.get(a).steps)
			{
				score += Math.abs(WIDTH / 2.0 - x);
			}
			if(scoreOfBest > score)
			{
				indexOfBest = a;
				scoreOfBest = score;
			}
		}
		arr.get(indexOfBest).color = Color.red;
		arr.get(indexOfBest).score = scoreOfBest;
		return simulate(arr.get(indexOfBest), count - 1, tolerance, silent);
	}
	
	public void render(int step, ArrayList<AIEv> arr)
	{
		//draw the background
		draw.setColor(BACKGROUND_COLOR);
		draw.fillRect(0, 0, WIDTH, HEIGHT);
		
		for(AIEv ai: arr)
		{
			//set color for the AI
			draw.setColor(ai.color);
			//the x coordinate is determined by the AI, every x coordinate has a unique y coordinate in ascending order 
			//	(note that here, an ascending y value means going DOWN the screen), the final two parameters are width
			//	 and height, which are the same since every step is a square
			draw.fillRect(ai.get(step), step * STEP_SIZE, STEP_SIZE, STEP_SIZE);
		}
		this.getBufferStrategy().show();
		sleep(WAIT_BETWEEN_STEPS);
	}
	
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
	
	public void plotBest(AIEv bestAI) throws NullPointerException
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
			draw.fillRect(curstep, step * STEP_SIZE, STEP_SIZE, STEP_SIZE);
			//increase the y value
			step++;
		}
		this.getBufferStrategy().show();
		sleep(WAIT_BETWEEN_GENERATIONS);
	}
	
	public void sleep(int milliseconds)
	{
		long time = System.currentTimeMillis() + milliseconds;
		while(time > System.currentTimeMillis()) {}
	}
	
	/**
	 * Sets the BufferStrategy for this canvas and instatiates {@link #draw}
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

class AIEv
{
	static Color default_color = Color.black;
	Color color = default_color;
	double score = 0;
	double rateOfMutation = 1.0/95.0;
	ArrayList<Integer> steps = new ArrayList<Integer>();
	public AIEv(ArrayList<Integer> steps, int screenWidth, int tolerance)
	{
		for(int num: steps)
		{
			if((int)(Math.random() / rateOfMutation) == 0)
			{
				int temp = (int) (num + Math.round(Math.random() * tolerance * 2 - tolerance));
				if(temp > screenWidth)
				{
					temp = screenWidth - 10;//idk just keeps it from going off screen
				}
				else if(temp < 0)
				{
					temp = 0;
				}
				this.steps.add(temp);
			}
			else
			{
				this.steps.add(num);
			}
		}
	}
	public AIEv(int stepsAllowed, int screenWidth)
	{
		steps = new ArrayList<Integer>();
		for(int a = 0; a < stepsAllowed; a++)
		{
			int step = (int)Math.round(Math.random() * screenWidth);
			this.steps.add(step);
		}
	}
	public int get(int step)
	{
		return steps.get(step);
	}
}
