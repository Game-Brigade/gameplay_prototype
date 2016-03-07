/*
 * Tether.java
 *
 * A tether object. A player enters it's radius and begins orbitting
 *
 * Author: Dashiell Brown
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics.fish;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class PlayerFishModel extends BoxObstacle {

  /** Width of the player, used for collision detection */
  private static final int PLAYER_FISH_WIDTH = 40;
  
  /** Height of the player, used for collision detection */
  private static final int PLAYER_FISH_HEIGHT = 40;
  
  /** Fist move and need to be affected by forces */
  private static final BodyDef.BodyType PLAYER_FISH_BODY_TYPE = BodyDef.BodyType.DynamicBody;

  /** The density of the player */
  private static final float PLAYER_DEFAULT_DENSITY  =  1.0f;
  /** The friction of the player */
  private static final float PLAYER_DEFAULT_FRICTION = 0.1f;
  /** The restitution of the player */
  private static final float PLAYER_DEFAULT_RESTITUTION = 0.4f;
  /** The thrust factor to convert player input into thrust */
  private static final float PLAYER_DEFAULT_THRUST = 30.0f;

  private int health;

  private Vector2 force;

  /** Create a new player at x,y. */
  public PlayerFishModel(float x, float y) {
    super(x, y, PLAYER_FISH_WIDTH, PLAYER_FISH_HEIGHT);
    setBodyType(PLAYER_FISH_BODY_TYPE);
    setDensity(PLAYER_DEFAULT_DENSITY);
    setDensity(PLAYER_DEFAULT_DENSITY);
    setFriction(PLAYER_DEFAULT_FRICTION);
    setRestitution(PLAYER_DEFAULT_RESTITUTION);
    setName("player");
    force = new Vector2();
    health = 1;
  }

  public boolean isAlive() {
    return health > 0;
  }

  public void applyTetherForce(Vector2 tetherForce) {
    body.applyForceToCenter(tetherForce, true);
  }

  public void applyTetherForce(TetherModel tether) {
    applyTetherForce(tether.calculateAttractiveForce(this));
  }

}