/*
 * BoxObject.java
 *
 * Given the name Box2D, this is your primary model class.  Most of the time,
 * unless it is a player controlled avatar, you do not even need to subclass
 * BoxObject.  Look through the code and see how many times we use this class.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.downstream.*;  // For GameCanvas
import edu.cornell.gdiac.downstream.obstacle.SimpleObstacle;
//Downstream-core refactor
/**
 * Box-shaped model to support collisions.
 *
 * Unless otherwise specified, the center of mass is as the center.
 */
public class EnemyModel extends SimpleObstacle {
	/** Shape information for this box */
	protected PolygonShape shape;
	/** The width and height of the box */
	private Vector2 dimension;
	/** A cache value for when the user wants to access the dimensions */
	private Vector2 sizeCache;
	/** A cache value for the fixture (for resizing) */
	private Fixture geometry;
	/** Cache of the polygon vertices (for resizing) */
	private float[] vertices;
	
	private Vector2 goal = new Vector2(0, 0);
	private ArrayList<Vector2> patrolPath = new ArrayList<Vector2>();
	
	/** Cache object for transforming the force according the object angle */
	public Affine2 affineCache = new Affine2();
	/** Cache object for left afterburner origin */
	public Vector2 leftOrigin = new Vector2();
	/** Cache object for right afterburner origin */
	public Vector2 rghtOrigin = new Vector2();
	
	private Vector2 lastGoal = new Vector2();
	
	private TetherModel tethered;
	
	private boolean flee = false;
	
	private float alpha = 1;
	
	private Color c = new Color(255, 255, 255, alpha);
	
	public boolean dead = false;
	
	
	
	/** 
	 * Returns the dimensions of this box
	 *
	 * This method does NOT return a reference to the dimension vector. Changes to this 
	 * vector will not affect the shape.  However, it returns the same vector each time
	 * its is called, and so cannot be used as an allocator.
	 *
	 * @return the dimensions of this box
	 */
	public Vector2 getDimension() {
		return sizeCache.set(dimension);
	}

	/** 
	 * Sets the dimensions of this box
	 *
	 * This method does not keep a reference to the parameter.
	 *
	 * @param value  the dimensions of this box
	 */
	public void setDimension(Vector2 value) {
		setDimension(value.x, value.y);
	}
	
	/** 
	 * Sets the dimensions of this box
	 *
	 * @param width   The width of this box
	 * @param height  The height of this box
	 */
	public void setDimension(float width, float height) {
		dimension.set(width, height);
		markDirty(true);
		resize(width, height);
	}
	
	/**
	 * Returns the box width
	 *
	 * @return the box width
	 */
	public float getWidth() {
		return dimension.x;
	}
	
	/**
	 * Sets the box width
	 *
	 * @param value  the box width
	 */
	public void setWidth(float value) {
		sizeCache.set(value,dimension.y);
		setDimension(sizeCache);
	}
	
	/**
	 * Returns the box height
	 *
	 * @return the box height
	 */
	public float getHeight() {
		return dimension.y;
	}
	
	/**
	 * Sets the box height
	 *
	 * @param value  the box height
	 */
	public void setHeight(float value) {
		sizeCache.set(dimension.x,value);
		setDimension(sizeCache);
	}
	
	/**
	 * Creates a new box at the origin.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 *
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public EnemyModel(float width, float height) {
		this(0, 0, width, height);
	}

	/**
	 * Creates a new box object.
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 *
	 * @param x  		Initial x position of the box center
	 * @param y  		Initial y position of the box center
	 * @param width		The object width in physics units
	 * @param height	The object width in physics units
	 */
	public EnemyModel(float x, float y, float width, float height) {
		super(x,y);
		dimension = new Vector2(width,height);
		sizeCache = new Vector2();
		shape = new PolygonShape();
		vertices = new float[8];
		geometry = null;
		lastGoal = new Vector2(x,y);
		
		// Initialize
		resize(width, height);	
	}
	
	public EnemyModel(float x, float y, float w, float h, ArrayList<Vector2> path) {
		this(x,y,w,h);
		patrolPath = path;
	}
	
	public EnemyModel(float x, float y, float w, float h, ArrayList<Vector2> path, TetherModel attached) {
		this(x,y,w,h);
		patrolPath = path;
		tethered = attached;
	}
	
	/**
	 * Reset the polygon vertices in the shape to match the dimension.
	 */
	private void resize(float width, float height) {
		// Make the box with the center in the center
		vertices[0] = -width/2.0f;
		vertices[1] = -height/2.0f;
		vertices[2] = -width/2.0f;
		vertices[3] =  height/2.0f;
		vertices[4] =  width/2.0f;
		vertices[5] =  height/2.0f;
		vertices[6] =  width/2.0f;
		vertices[7] = -height/2.0f;
		shape.set(vertices);
	}

	/**
	 * Create new fixtures for this body, defining the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void createFixtures() {
		if (body == null) {
			return;
		} 
		
	    releaseFixtures();

		// Create the fixture
		fixture.shape = shape;
		geometry = body.createFixture(fixture);
		markDirty(false);
	}
	
	/**
	 * Release the fixtures for this body, reseting the shape
	 *
	 * This is the primary method to override for custom physics objects
	 */
	protected void releaseFixtures() {
	    if (geometry != null) {
	        body.destroyFixture(geometry);
	        geometry = null;
	    }
	}

	
	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas Drawing context
	 */
	public void drawDebug(GameCanvas canvas) {
		canvas.drawPhysics(shape,Color.YELLOW,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
	}
	
	public Vector2 getGoal() {
		return this.goal;
	}
	
	/** 
	 * Sets the goal state to where the fish wants to go
	 * @param x
	 * @param y
	 */
	public void setGoal(float x, float y){
		this.goal.set(x, y);
	}
	
	/**
	 * Makes the goal state of the fish alternate between two points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	
	public void patrol(ArrayList<Vector2> goals){
		
		for(int i = 0; i < goals.size(); i++){
			//if (getX() > (goals.get(i).x - 1)
			
			if ( i != goals.size() - 1 && (getX() > (goals.get(i).x - .5) && getX() < (goals.get(i).x + .5)) && (getY() > (goals.get(i).y - .5) && getY() < (goals.get(i).y + .5)))
			{
				this.goal.set(goals.get(i+1).x, goals.get(i+1).y);
				setAngle(findA(this.getPosition(), this.goal));
				lastGoal = goals.get(i);
				
			}
			if ( i == goals.size() - 1 && (getX() > (goals.get(i).x - .5) && getX() < (goals.get(i).x + .5)) && (getY() > (goals.get(i).y - .5) && getY() < (goals.get(i).y + .5))){
				this.goal.set(goals.get(0).x, goals.get(0).y);
				setAngle(findA(this.getPosition(), this.goal));
				lastGoal = goals.get(i);
			}
			
		}
	}
	
	public void patrol() {
		patrol(patrolPath);
	}
	
	public void patrol(float x1, float y1, float x2, float y2){
		boolean turnAround = (getX() > (x1 - 1) && getX() < (x1 + 1)) && (getY() > (y1 - 1) && getY() < (y1 + 1));
		boolean turnAround2 = (getX() > (x2 - 1) && getX() < (x2 + 1)) && (getY() > (y2 - 1) && getY() < (y2 + 1));
		
		if (turnAround){
			this.goal.set(x2, y2);
			setAngle(findA(this.getPosition(), this.goal));
		}
		if (turnAround2){
			this.goal.set(x1, y1);
			setAngle(findA(this.getPosition(), this.goal));
		}
	}
	/**
	 * Call during update, will move the fish towards the goal state
	 */
	public void moveTowardsGoal(){
	

		//float distance = Vector2.dst(lastGoal.x, lastGoal.y, goal.x, goal.y);

		Vector2 direction = goal.cpy().sub(this.getPosition()).nor();

		setY(getY() + direction.y *.2f);
		setX(getX() + direction.x *.2f);
	}
	 public float findA(Vector2 target, Vector2 t2) {
		  	float angle = (float) Math.toDegrees(Math.atan2(target.y - t2.y, target.x - t2.x));
		  	angle = angle - 180;
		    return (float) Math.toRadians(angle);
		}
	 
	 public void fleeFind(){
		 if (tethered != null){
			 if (tethered.lit){
				 goal = new Vector2(999, 999);
				 setAngle(findA(this.getPosition(), this.goal));
			 }
		 }
	 }
	 
	 public void fleeFind(ArrayList<TetherModel> tethers){
		 for(TetherModel t: tethers){
			 if (t.lightCircle.contains(this.getPosition())){
				 flee = true;
			 };
		 }
	 }
	 
	 private void fade(){
		 if (flee){
			 alpha = alpha - .03f;
			 dead = true;
		 }
		 c.set(255, 255, 255, alpha);
	 }
	 
	 public void draw(GameCanvas canvas){
		 if (texture != null) {
			 fade();
			 canvas.draw(texture, c ,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
	 }
	/*public void applyForce() {
			if (!isActive()) {
				return;
			}
			
			// Orient the force with rotation.
			affineCache.setToRotationRad(getAngle());
			affineCache.applyTo(force);
			
			//#region INSERT CODE HERE
			// Apply force to the rocket BODY, not the rocket
			
			body.applyForceToCenter(force,true);
			
			//#endregion
		}*/

}

