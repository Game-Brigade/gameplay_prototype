/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream.models;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.GameCanvas;
import edu.cornell.gdiac.downstream.obstacle.*;

public class TetherModel extends WheelObstacle {

	/** Radius of this tether, used for collision detection */
	private static final int TETHER_DEFAULT_RADIUS = 1;

	/** The radius at which the player orbits a tether */
	public static final float TETHER_DEFAULT_ORBIT = 2f;  

	/** The range at which the player can enter orbit around this tether */
	private static final int TETHER_DEFAULT_RANGE = 4;



	private static final BodyDef.BodyType TETHER_BODY_TYPE = BodyDef.BodyType.StaticBody;

	/** The type of this tether */
	private TetherType type;

	private float rotations;
	private float radius;

	private Vector2 entry = new Vector2();

	private float sparkSize = 0;

	public boolean set = false;

	private static TextureRegion lightingTexture;

	/** Tethers can be lilipads, lanterns, or lotus flowers */
	public enum TetherType {
		Lilypad,
		Lantern,
		Lotus
	};

	public TetherModel(float x, float y, TetherType type) {
		super(x,y,TETHER_DEFAULT_RANGE);
		setType(type);
		setBodyType(TETHER_BODY_TYPE);
	}

	public TetherModel(float x, float y, float r, boolean b){
		super(x,y, TETHER_DEFAULT_RANGE);
		setType(TetherType.Lantern);
		setBodyType(TETHER_BODY_TYPE);
	}

	public TetherModel(float x, float y, float r) {
		super(x,y,TETHER_DEFAULT_RANGE);
		setType(TetherType.Lilypad);
		setBodyType(TETHER_BODY_TYPE);
	}

	public void setType(TetherType newType) {
		type = newType;
	}

	public float getRadius() {
		return TETHER_DEFAULT_RADIUS;
	}

	public float getOrbitRadius() {
		return TETHER_DEFAULT_ORBIT;
	}

	public float getSensorRadius() {
		return TETHER_DEFAULT_RANGE;
	}

	public void setRotation(float i){
		rotations = i;
	}
	public void updateRotations(){
		rotations = rotations + .5f;
	}

	public float getRotations(){
		return rotations;
	} 

	public Vector2 getEntry(){
		return entry;
	}
	public void setEntry(Vector2 v){
		entry = v;
	}
	public boolean isLantern(){
		return type == TetherType.Lantern;
	}

	public void setlightingTexture(TextureRegion t){
		lightingTexture = t;
	}

	public void draw(GameCanvas canvas) {
		if (texture != null) {
			if (type == TetherType.Lilypad){
				canvas.draw(texture,Color.WHITE,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
			if (type == TetherType.Lantern){
				canvas.draw(texture,Color.WHITE,texture.getRegionHeight()/2,texture.getRegionWidth()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),.35f,.35f);
				if (sparkSize < rotations){
					sparkSize += .005f;
				}

			}
			if (rotations > 1.5){
				sparkSize = 2.0f;
			}


			//sparkSize = sparkSize / 5;
			canvas.draw(lightingTexture,Color.WHITE, lightingTexture.getRegionWidth()/2, lightingTexture.getRegionHeight()/2,getX()*drawScale.x,getY()*drawScale.x,getAngle(),sparkSize,sparkSize);
			//System.out.println(origin);
		}
	}
}