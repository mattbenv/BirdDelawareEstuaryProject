package game;
/*Authors: Miguel Zavala, Derek Baum, Matt Benvenuto, Jake Wise
 * 
 */

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*Class: RedKnotGameState
 * -extends the abstract class GameState (Model) 
 * -keeps track of the gamestate data of the RedKnot minigame
 */
public class RedKnotGameState extends GameState {
	private RedKnot RK;
	private int score;
	static final String SCORE_TEXT = "Score: ";
	static final int SCORE_FONT_SIZE = 40;
	
	static boolean debug_mode;
	

	//Enemy clouds
	private ArrayList<Cloud> clouds; 
	private final int AMOUNT_OF_CLOUDS = 5;
	
	//FlockBirds
	private ArrayList<FlockBird> flock; 
	//The range which controls how low/high of a chance for a FlockBird to appear
	private final int FB_CHANCE_MAX = 1000; 
	private final int FB_CHANCE_LOW = 0;
	private final int FB_THRESHOLD = 10;
	

	
	public RedKnotGameState(Controller controller){
		super(controller);
		this.score=0; //Sets the initial score 
		this.RK = new RedKnot();
		this.flock = new ArrayList<>();
		this.clouds = new ArrayList<>();
		this.addGameObject(new GameObject(new Position(5,5), new Size(30,30), RedKnotAsset.BACKGROUND));
		debug_mode = false; //initially turns off debug mode
		
	}
	
	public void collectBird() {
		this.flock.add(FlockBird.spawnFlockBird(RK));
	}
	
	public void lostBird() {
		if(this.flock.size()>0) {
			this.flock.remove(0);
		}
	}
	

	@Override
	public void ontick() {
		updateClouds();
		moveBird();
		updateFlockBirds();
	
	}
	
	public void moveBird(){
		switch(RK.getFlyState()) {
		case 1:RK.FlyUp();break;
		case -1:RK.FlyDown();break;
		}
	}
	
	/*Created by Miguel:
	 * -Takes in no arguments, returns nothing
	 * -Using an iterator, iterates through the 'clouds' ArrayList
	 * -Handles any cloud related actions in order to keep it O(n) and
	 * to keep game logic more organized
	 */
	public void updateClouds(){
		addClouds(); //adds the clouds intially and readds clouds
		
		//Added iterator to remove clouds once they reach the end
		Iterator<Cloud> cloud_iter = clouds.iterator();
		
		while(cloud_iter.hasNext()) {
			Cloud c = cloud_iter.next();
			c.move();
			
			//If the cloud goes out of bounds (exits the left side of screen)
			//it then gets removed from 'clouds' and a new 'Cloud'
			//instance is created
			boolean outofbounce = c.checkIfOutOfBounds(Cloud.LEFT_MOST);
			//System.out.println(outofbounce);
			if(outofbounce){
				//System.out.println("REMOVING");
				cloud_iter.remove();
			}
		}
	}
	
	/*Created by Derek:
	 * -Takes in no arguments, returns nothing
	 * -Uses MVC design to pass over GameObjects to the Controller
	 * which then passes them to the RedKnotView in order to draw
	 * them onto the screen
	 */
	public ArrayList<GameObject> getUpdateableGameObjects(){
//		RK flock clouds
		ArrayList<GameObject> output = new ArrayList<>();
		output.add(RK);//it is important that we insert the redknot first.
		output.addAll(clouds);
		output.addAll(flock);
		return output;
		
	}
	
	/*Created by Miguel:
	 * -Takes in no arguments, returns nothing
	 * -Adds 'Cloud' instances into 'clouds' ArrayList
	 * -Currently re-adds 'Cloud' instances when they go off-screen
	 */
	public void addClouds() {
//		System.out.println("READDING CLOUDS");
//		System.out.println("CLOUDS SIZE:"+clouds.size());
		
		//Checks if there are not enough clouds and adds the amount needed
		if(clouds.size()<this.AMOUNT_OF_CLOUDS) {
			for(int i=0;i<this.AMOUNT_OF_CLOUDS-clouds.size();i++) {
				int screen_width = this.controller.getScreen().getX();
				int screen_height = this.controller.getScreen().getY();
				this.clouds.add(Cloud.spawnCloud(GameScreen.PLAY_SCREEN_WIDTH, Cloud.Y_MARGIN, GameScreen.PLAY_SCREEN_HEIGHT-Cloud.Y_MARGIN));
			}
		}
	}
	
	public void updateFlockBirds() {
		addRandomFlockBirds();
		
		//Added iterator to remove clouds once they reach the end
		Iterator<FlockBird> fb_iter = flock.iterator();
		
		while(fb_iter.hasNext()) {
			FlockBird FB = fb_iter.next();
			FB.move();
			
			//If the cloud goes out of bounds (exits the left side of screen)
			//it then gets removed from 'clouds' and a new 'Cloud'
			//instance is created
			boolean outofbounce = FB.checkIfOutOfBounds(FlockBird.LEFT_MOST);
			System.out.println(outofbounce);
			if(outofbounce){
				//System.out.println("REMOVING");
				fb_iter.remove();
			}
		}
	}
	
	public void addRandomFlockBirds() {
		int chance = Utility.randRangeInt(this.FB_CHANCE_LOW, this.FB_CHANCE_MAX);
		int threshold = this.FB_THRESHOLD;
		
		if(chance<threshold) {
			this.flock.add(FlockBird.spawnRandomFlockBird(GameScreen.PLAY_SCREEN_WIDTH+FlockBird.X_MARGIN, 0+FlockBird.Y_MARGIN, GameScreen.PLAY_SCREEN_HEIGHT-FlockBird.Y_MARGIN));
		}
	}
	
	/*Created by Miguel:
	 * -Takes in no arguments, returns nothing
	 * -Switches the 'debug_mode' boolean from true -> false
	 * and false -> true when called
	 * -When true, this allows the RedKnotView to draw the hitBoxes
	 * and any future possible debug mode for bug testing
	 */
	public void switchDebugMode() {
		if(RedKnotGameState.debug_mode) {
			RedKnotGameState.debug_mode = false;
		}
		else {
			RedKnotGameState.debug_mode = true;
		}
	}

	
	
	
	@Override
	public void addGameObject(GameObject o) {
		
	}
	
	/*Getters*/
	
	public ArrayList<Cloud> getClouds(){
		return this.clouds;
	}
	
	public int countBirds() {
		return flock.size();
	}
	
	public RedKnot getRK() {
		return RK;
	}

	public ArrayList<FlockBird> getFlock() {
		return flock;
	}
	
	public int getScore(){
		return score;
	}
	public void setScore(int x){
		score=x;
	}
	public void incrementScore(int x){
		score+=x;
	}

	public int getAMOUNT_OF_CLOUDS() {
		return AMOUNT_OF_CLOUDS;
	}

	
}
