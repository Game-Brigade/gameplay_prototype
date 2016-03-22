/*
 * FishController.java
 *
 * Author: Walker M. White && Dashiell Brown
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.downstream;

import java.util.ArrayList;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;

import edu.cornell.gdiac.util.*;
import edu.cornell.gdiac.downstream.*;
import edu.cornell.gdiac.downstream.obstacle.*;
import edu.cornell.gdiac.downstream.models.*;

/**
 * Gameplay specific controller for Downstream.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class DownstreamController extends WorldController implements ContactListener {
	/** Reference to the fish texture */
	private static final String KOI_TEXTURE = "koi/koi.png";
	/** The reference for the tether textures  */
	private static final String LILY_TEXTURE = "tethers/lilypad.png";
	/** Reference to the enemy image assets */
	private static final String ENEMY_TEXTURE = "enemy/enemy.png";
	/** Reference to the Lantern asset image*/
	private static final String LANTERN_TEXTURE = "tethers/notlit.png";
	/** Reference to the Lightin Texture image */
	private static final String LIGHTING_TEXTURE = "tethers/aura.png";
	/** Reference to the 4-sided land texture */
	private static final String LAND_4SIDE_TEXTURE = "terrain/land.png";
	/** Reference to the left land texture */
	private static final String LEFT_LAND_TEXTURE = "terrain/LEFT.png";
	/** Reference to the right land texture */
	private static final String RIGHT_LAND_TEXTURE = "terrain/RIGHT.png";
	/** Reference to the top land texture */
	private static final String TOP_LAND_TEXTURE = "terrain/TOP.png";
	/** Reference to the bottom land texture */
	private static final String BOTTOM_LAND_TEXTURE = "terrain/bottom.png";

	/** The asset for the collision sound */
	//private static final String  COLLISION_SOUND = "fish/bump.mp3";


	/** Texture assets for the koi */
	private TextureRegion koiTexture;
	/** Texture assets for the lilypads */
	private TextureRegion lilyTexture;
	/** Texture assets for the enemy fish */
	private TextureRegion enemyTexture;
	/** Texture assets for lantern */
	private TextureRegion lanternTexture;
	/** Texture assets for light */
	private TextureRegion lightingTexture;
	/** Texture assets for the land */
	private TextureRegion land4Texture;
	private TextureRegion leftLandTexture;
	private TextureRegion rightLandTexture;
	private TextureRegion topLandTexture;
	private TextureRegion bottomLandTexture;

	/** Texture filmstrip for the main afterburner */
	//private FilmStrip mainTexture;


	/** Track asset loading from all instances and subclasses */
	private AssetState fishAssetState = AssetState.EMPTY;

	private boolean tethered;

	private float PLAYER_LINEAR_VELOCITY = 4f;
	private float CAMERA_MAX_LINEAR_VELOCITY = 8f;
	private float CAMERA_CURRENT_LINEAR_VELOCITY = 0f;
	private float CAMERA_ACCELERATION = 0.1f;

	private boolean enableSlow = false;
	private boolean enableLeadingLine = false;
	private boolean enableTetherRadius = true;

	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (fishAssetState != AssetState.EMPTY) {
			return;
		}

		fishAssetState = AssetState.LOADING;


		manager.load(ENEMY_TEXTURE, Texture.class);
		assets.add(ENEMY_TEXTURE);

		// Ship textures
		manager.load(KOI_TEXTURE, Texture.class);
		assets.add(KOI_TEXTURE);

		manager.load(LILY_TEXTURE, Texture.class);
		assets.add(LILY_TEXTURE);

		manager.load(LANTERN_TEXTURE, Texture.class);
		assets.add(LANTERN_TEXTURE);

		manager.load(LIGHTING_TEXTURE, Texture.class);
		assets.add(LIGHTING_TEXTURE);

		manager.load(LAND_4SIDE_TEXTURE, Texture.class);
		assets.add(LAND_4SIDE_TEXTURE);

		manager.load(LEFT_LAND_TEXTURE, Texture.class);
		assets.add(LEFT_LAND_TEXTURE);

		manager.load(RIGHT_LAND_TEXTURE, Texture.class);
		assets.add(RIGHT_LAND_TEXTURE);

		manager.load(TOP_LAND_TEXTURE, Texture.class);
		assets.add(TOP_LAND_TEXTURE);

		manager.load(BOTTOM_LAND_TEXTURE, Texture.class);
		assets.add(BOTTOM_LAND_TEXTURE);


		//sounds
		//manager.load(MAIN_FIRE_SOUND, Sound.class);
		//assets.add(MAIN_FIRE_SOUND);


		super.preLoadContent(manager);
	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {
		if (fishAssetState != AssetState.LOADING) {
			return;
		}

		enemyTexture = createTexture(manager,ENEMY_TEXTURE,false);
		koiTexture = createTexture(manager,KOI_TEXTURE,false);
		lilyTexture = createTexture(manager,LILY_TEXTURE,false);
		lanternTexture = createTexture(manager, LANTERN_TEXTURE, false);
		lightingTexture = createTexture(manager, LIGHTING_TEXTURE, false);
		land4Texture = createTexture(manager,LAND_4SIDE_TEXTURE,false);
		leftLandTexture = createTexture(manager,LEFT_LAND_TEXTURE,false);
		rightLandTexture = createTexture(manager,RIGHT_LAND_TEXTURE,false);
		topLandTexture = createTexture(manager,TOP_LAND_TEXTURE,false);
		bottomLandTexture = createTexture(manager,BOTTOM_LAND_TEXTURE,false);

		SoundController sounds = SoundController.getInstance();
		//sounds.allocate(manager,MAIN_FIRE_SOUND);


		super.loadContent(manager);
		fishAssetState = AssetState.COMPLETE;
	}

	// Physics constants for initialization
	/** Density of non-enemy objects */
	private static final float BASIC_DENSITY   = 0.0f;
	/** Density of the enemy objects */
	private static final float ENEMY_DENSITY   = 1.0f;
	/** Friction of non-enemy objects */
	private static final float BASIC_FRICTION  = 0.1f;
	/** Friction of the enemy objects */
	private static final float ENEMY_FRICTION  = 0.3f;
	/** Collision restitution for all objects */
	private static final float BASIC_RESTITUTION = 0.1f;
	/** Threshold for generating sound on collision */
	private static final float SOUND_THRESHOLD = 1.0f;

	private static final float TETHER_DENSITY = ENEMY_DENSITY;
	private static final float TETHER_FRICTION = ENEMY_FRICTION;
	private static final float TETHER_RESTITUTION = BASIC_RESTITUTION;

	// Since these appear only once, we do not care about the magic numbers.
	// In an actual game, this information would go in a data file.
	// Wall vertices
	private static final float[][] LAND = {{}};

	private static final float[] WALL1 = { 0.0f, 18.0f, 16.0f, 18.0f, 16.0f, 17.0f,
			8.0f, 15.0f,  1.0f, 17.0f,  2.0f,  7.0f,
			3.0f,  5.0f,  3.0f,  1.0f, 16.0f,  1.0f,
			16.0f,  0.0f,  0.0f,  0.0f};




	private ArrayList<TetherModel> tethers = new ArrayList<TetherModel>();
	private ArrayList<TetherModel> lanterns = new ArrayList<TetherModel>();
	private ArrayList<EnemyModel> enemies = new ArrayList<EnemyModel>();

	// Other game objects
	/** The initial koi position */
	private static Vector2 KOI_POS = new Vector2(-2, 6);
	/** The goal door position */
	private static Vector2 GOAL_POS = new Vector2( 6, 12);

	// Physics objects for the game
	/** Reference to the goalDoor (for collision detection) */
	private BoxObstacle goalDoor;
	/** Reference to the player avatar */
	private PlayerModel koi;

	private EnemyModel eFish;

	/**
	 * Creates and initialize a new instance of Downstream
	 *
	 * The game has no  gravity and deafault settings
	 */
	public DownstreamController() {
		setDebug(false);
		setComplete(false);
		setFailure(false);
		world.setContactListener(this);
		tethered = false;
	}

	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public void reset() {
		Vector2 gravity = new Vector2(world.getGravity() );

		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		enemies.clear();
		lanterns.clear();
		tethers.clear();
		objects.clear();
		addQueue.clear();
		world.dispose();
		tethered = true;

		world = new World(gravity,false);
		world.setContactListener(this);
		setComplete(false);
		setFailure(false);
		populateLevel();
		canvas.setCameraPosition(koi.getPosition().cpy().scl(scale));
	}

	/**
	 * Lays out the game geography.
	 */
	private void populateLevel() {
		// Add level goal
		float dwidth;
		float dheight;
		float rad = lilyTexture.getRegionWidth()/scale.x/2;

		boolean sensorTethers = true;
<<<<<<< HEAD
		
		
		BoxObstacle land = new BoxObstacle(15,25,topLandTexture.getRegionWidth()/scale.x,topLandTexture.getRegionHeight()/scale.y);
=======

		/*
		BoxObstacle land = new BoxObstacle(20,20,topLandTexture.getRegionWidth()/scale.x,topLandTexture.getRegionHeight()/scale.y);
>>>>>>> refs/remotes/origin/master
		land.setBodyType(BodyDef.BodyType.StaticBody);
		land.setName("land");
		land.setDensity(TETHER_DENSITY);
		land.setFriction(TETHER_FRICTION);
		land.setRestitution(TETHER_RESTITUTION);
		land.setDrawScale(scale);
		land.setTexture(topLandTexture);
		addObject(land);
<<<<<<< HEAD
		
		
		TetherModel lantern = new TetherModel(5, 5, dwidth, dheight, true);
=======
		 */

		TetherModel lily = new TetherModel(0, 6, rad);
		lily.setBodyType(BodyDef.BodyType.StaticBody);
		lily.setName("lily"+ 1);
		lily.setDensity(TETHER_DENSITY);
		lily.setFriction(TETHER_FRICTION);
		lily.setRestitution(TETHER_RESTITUTION);
		lily.setSensor(sensorTethers);
		lily.setDrawScale(scale);
		lily.setTexture(lilyTexture);
		addObject(lily);
		tethers.add(lily);

		TetherModel lantern = new TetherModel(15, 4, rad, true);
>>>>>>> refs/remotes/origin/master
		lantern.setBodyType(BodyDef.BodyType.StaticBody);
		lantern.setName("lantern"+ 1);
		lantern.setDensity(TETHER_DENSITY);
		lantern.setFriction(TETHER_FRICTION);
		lantern.setRestitution(TETHER_RESTITUTION);
		lantern.setSensor(sensorTethers);
		lantern.setDrawScale(scale);
		lantern.setTexture(lanternTexture);
		lantern.setlightingTexture(lightingTexture);
		lantern.setRotation(0);
		addObject(lantern);
		tethers.add(lantern);
		lanterns.add(lantern);

		lily = new TetherModel(30, 4, rad);
		lily.setBodyType(BodyDef.BodyType.StaticBody);
		lily.setName("lily"+ 3);
		lily.setDensity(TETHER_DENSITY);
		lily.setFriction(TETHER_FRICTION);
		lily.setRestitution(TETHER_RESTITUTION);
		lily.setSensor(sensorTethers);
		lily.setDrawScale(scale);
		lily.setTexture(lilyTexture);
		addObject(lily);
		tethers.add(lily);


		lily = new TetherModel(30, 14, rad);
		lily.setBodyType(BodyDef.BodyType.StaticBody);
		lily.setName("lily"+ 4);
		lily.setDensity(TETHER_DENSITY);
		lily.setFriction(TETHER_FRICTION);
		lily.setRestitution(TETHER_RESTITUTION);
		lily.setSensor(sensorTethers);
		lily.setDrawScale(scale);
		lily.setTexture(lilyTexture);
		addObject(lily);
		tethers.add(lily);


		lily = new TetherModel(50, 7, rad);
		lily.setBodyType(BodyDef.BodyType.StaticBody);
		lily.setName("lily"+ 3);
		lily.setDensity(TETHER_DENSITY);
		lily.setFriction(TETHER_FRICTION);
		lily.setRestitution(TETHER_RESTITUTION);
		lily.setSensor(sensorTethers);
		lily.setDrawScale(scale);
		lily.setTexture(lilyTexture);
		addObject(lily);
		tethers.add(lily);


		lantern = new TetherModel(70, 7, rad, true);
		lantern.setBodyType(BodyDef.BodyType.StaticBody);
		lantern.setName("lantern"+ 1);
		lantern.setDensity(TETHER_DENSITY);
		lantern.setFriction(TETHER_FRICTION);
		lantern.setRestitution(TETHER_RESTITUTION);
		lantern.setSensor(sensorTethers);
		lantern.setDrawScale(scale);
		lantern.setTexture(lanternTexture);
		lantern.setlightingTexture(lightingTexture);
		lantern.setRotation(0);
		addObject(lantern);
		tethers.add(lantern);
		lanterns.add(lantern);

		TextureRegion etexture = enemyTexture;
		dwidth  = etexture.getRegionWidth()/scale.x;
		dheight = etexture.getRegionHeight()/scale.y;
		eFish = new EnemyModel(20, 0, dwidth, dheight);
		eFish.setDensity(ENEMY_DENSITY);
		eFish.setFriction(ENEMY_FRICTION);
		eFish.setRestitution(BASIC_RESTITUTION);
		eFish.setName("enemy");
		eFish.setDrawScale(scale);
		eFish.setTexture(etexture);
		eFish.setAngle((float) (Math.PI/2));
		eFish.setBodyType(BodyDef.BodyType.StaticBody);
		eFish.setGoal(0, 0);
		addObject(eFish);
		enemies.add(eFish);

		eFish = new EnemyModel(61, 18, dwidth, dheight);
		eFish.setDensity(ENEMY_DENSITY);
		eFish.setFriction(ENEMY_FRICTION);
		eFish.setRestitution(BASIC_RESTITUTION);
		eFish.setName("enemy");
		eFish.setDrawScale(scale);
		eFish.setTexture(etexture);
		eFish.setAngle((float) (Math.PI/2));
		eFish.setBodyType(BodyDef.BodyType.StaticBody);
		eFish.setGoal(0, 0);
		addObject(eFish);
		enemies.add(eFish);

		eFish = new EnemyModel(76, 0, dwidth, dheight);
		eFish.setDensity(ENEMY_DENSITY);
		eFish.setFriction(ENEMY_FRICTION);
		eFish.setRestitution(BASIC_RESTITUTION);
		eFish.setName("enemy");
		eFish.setDrawScale(scale);
		eFish.setTexture(etexture);
		eFish.setAngle((float) (Math.PI/2));
		eFish.setBodyType(BodyDef.BodyType.StaticBody);
		eFish.setGoal(0, 0);
		addObject(eFish);
		enemies.add(eFish);



		// Create the fish avatar
		dwidth  = koiTexture.getRegionWidth()/scale.x;
		dheight = koiTexture.getRegionHeight()/scale.y;
		koi = new PlayerModel(KOI_POS.x, KOI_POS.y, dwidth, dheight);
		koi.setDrawScale(scale);
		koi.setName("koi");
		koi.setTexture(koiTexture);
		koi.setTethered(false);

		addObject(koi);

	}

	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void update(float dt) {

		System.out.println(CAMERA_CURRENT_LINEAR_VELOCITY);

		float thrust = koi.getThrust();
		InputController input = InputController.getInstance();
		koi.setFX(thrust * input.getHorizontal());
		koi.setFY(thrust * input.getVertical());
		koi.applyForce();
		//		koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY));

		// unused. was testing using "s" to slow down
		//		if (enableSlow && input.slow) koi.setLinearVelocity(koi.getLinearVelocity().setLength(4));

		if (input.didTether()) {
			tethered = !tethered; 
			koi.setTethered(false);
			//			CAMERA_CURRENT_LINEAR_VELOCITY = CAMERA_MAX_LINEAR_VELOCITY/2;
			CAMERA_CURRENT_LINEAR_VELOCITY = 2;
		}
		if (!tethered) {
			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*2));
		}
		if (tethered) {
			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*1.5f));
		}
		//		if (input.space) tethered = true; else tethered = false;
		CAMERA_CURRENT_LINEAR_VELOCITY = Math.min(CAMERA_CURRENT_LINEAR_VELOCITY+CAMERA_ACCELERATION, CAMERA_MAX_LINEAR_VELOCITY);
		TetherModel closestTether = getClosestTether();


		//check to see if closest tether is just attached or has been previously attached
		if (tethered & closestTether.getEntry().x == 0f & closestTether.isLantern()){
			//if just attached, define it as such
			Vector2 ent = new Vector2(closestTether.getX(), closestTether.getY());
			closestTether.setEntry(ent);

		}
		//checks to see if the fish is within reasonable circulating distance. It will pass the if statment many times
		if (tethered){
			//this is because the fish moves to quickly to get an exact range, so we must find it within .5 distance
			if ((closestTether.getEntry().x + .5 > koi.getPosition().x) && (closestTether.getEntry().x -.5 < koi.getPosition().x && closestTether.isLantern())){
				//because of the range, we only want the first instance, so we only check if it has not been previously checked in the last frame. 
				if (closestTether.set == false){
					//System.out.println(closestTether.getRotations());
					closestTether.updateRotations();
				}
				closestTether.set = true;
			}
			else{
				closestTether.set = false;
			}
		}
		else {
			closestTether.set = false;
		}

		boolean camera_zoom = true;

		// laggy catch up with space
		// if tethered, move slowly to tether; 
		// else if pressing space move quickly to fish, 
		// else slowly to fish

		if (koi.isTethered() || tethered && 
				koi.getPosition().sub(koi.getInitialTangentPoint(closestTether.getPosition())).len2() < .01) {
			if (!koi.isTethered()) {
				System.out.println("PENIS");
				koi.refreshTetherForce(closestTether.getPosition(), closestTether.getOrbitRadius());
			}
			koi.applyTetherForce(closestTether.getPosition(), closestTether.getOrbitRadius());
			canvas.moveCameraTowards(closestTether.getPosition().cpy().scl(scale), CAMERA_CURRENT_LINEAR_VELOCITY/2);
			if (camera_zoom) canvas.zoomOut();
			koi.setTethered(true);
			//			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY));
		} else {
			if (tethered) canvas.moveCameraTowards(koi.getPosition().cpy().scl(scale), CAMERA_CURRENT_LINEAR_VELOCITY);
			else 			 canvas.moveCameraTowards(koi.getPosition().cpy().scl(scale), CAMERA_CURRENT_LINEAR_VELOCITY/2);
			if (camera_zoom) canvas.zoomIn();
			//			koi.setLinearVelocity(koi.getLinearVelocity().setLength(PLAYER_LINEAR_VELOCITY*2));
		}


		koi.resolveDirection();



		float angV = 3f;
		float radius = closestTether.getPosition().dst(koi.getPosition());
		float tetherSpeed = angV*radius;

		float MAX_SPEED = 7f;
		float MIN_SPEED = 6f;

		int motionType = 0;

		if (lanterns.get(0).getRotations() <= 1.5){
			enemies.get(0).moveTowardsGoal();
			enemies.get(0).patrol(20, 0, 20, 18);
		}
		else{
			enemies.get(0).setGoal(1000, 1000);
			enemies.get(0).moveTowardsGoal();
		}

		if (lanterns.get(1).getRotations() <= 1.5){
			enemies.get(1).moveTowardsGoal();
			enemies.get(1).patrol(61, 0, 61, 18);
		}
		else{
			enemies.get(1).setGoal(1000, 1000);
			enemies.get(1).moveTowardsGoal();
		}
		if (lanterns.get(1).getRotations() <= 1.5){
			enemies.get(2).moveTowardsGoal();
			enemies.get(2).patrol(76, 0, 76, 18);
		}
		else{
			enemies.get(2).setGoal(1000, 1000);
			enemies.get(2).moveTowardsGoal();
		}


		SoundController.getInstance().update();
	}

	private TetherModel getClosestTether() {
		TetherModel closestTether = tethers.get(0);
		float closestDistance = tethers.get(0).getPosition().sub(koi.getPosition()).len2();
		for (TetherModel tether : tethers) {
			float newDistance = tether.getPosition().sub(koi.getPosition()).len2();
			if (newDistance < closestDistance) {
				closestDistance = newDistance;
				closestTether = tether;
			}
		}
		return closestTether;
	}

	public void draw(float delta) {
		super.draw(delta);

		if (enableLeadingLine) {
			Vector2 farOff = koi.getPosition().cpy();
			farOff.add(koi.getLinearVelocity().cpy().scl(1000));
			canvas.drawLeadingLine(koi.getPosition().cpy(), farOff);
		}
		if (enableTetherRadius) {
			Vector2 closestTether = getClosestTether().getPosition().cpy().scl(scale);
			Vector2 initialTangent = koi.getInitialTangentPoint(getClosestTether().getPosition()).scl(scale);
			float radius = closestTether.dst(initialTangent);
			canvas.drawTetherCircle(closestTether, TetherModel.TETHER_DEFAULT_ORBIT*scale.len());
			//canvas.drawTetherCircle(koi.cent.cpy().scl(scale), koi.pull.len()/2*scale.len());
		}

	}

	/// CONTACT LISTENER METHODS
	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when we first get a collision between two objects.  We use 
	 * this method to test if it is the "right" kind of collision.  In particular, we
	 * use it to test if we made it to the win door.
	 *
	 * @param contact The two bodies that collided
	 */
	public void beginContact(Contact contact) {
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		String s1 = ((Obstacle)body1.getUserData()).getName();
		String s2 = ((Obstacle)body2.getUserData()).getName();

		if( (body1.getUserData() == koi && body2.getUserData() == eFish) 
				|| (body2.getUserData() == koi && body1.getUserData() == eFish))  {
			setFailure(true);
		}

		if( (body2.getUserData() == koi && (s1.startsWith("lily") || s1.startsWith("lantern")))) {
			TetherModel t = (TetherModel) body1.getUserData();
		}

	}

	/**
	 * Callback method for the start of a collision
	 *
	 * This method is called when two objects cease to touch.  We do not use it.
	 */ 
	public void endContact(Contact contact) {}

	private Vector2 cache = new Vector2();

	/** Unused ContactListener method */
	public void postSolve(Contact contact, ContactImpulse impulse) {}

	/**
	 * Handles any modifications necessary before collision resolution
	 *
	 * This method is called just before Box2D resolves a collision.  We use this method
	 * to implement sound on contact, using the algorithms outlined similar to those in
	 * Ian Parberry's "Introduction to Game Physics with Box2D".  
	 * 
	 * However, we cannot use the proper algorithms, because LibGDX does not implement 
	 * b2GetPointStates from Box2D.  The danger with our approximation is that we may
	 * get a collision over multiple frames (instead of detecting the first frame), and
	 * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
	 * prevents this from happening.
	 *
	 * @param  contact  	The two bodies that collided
	 * @param  oldManfold  	The collision manifold before contact
	 */

	public void preSolve(Contact contact, Manifold oldManifold) {
		float speed = 0;

		// Use Ian Parberry's method to compute a speed threshold
		Body body1 = contact.getFixtureA().getBody();
		Body body2 = contact.getFixtureB().getBody();
		WorldManifold worldManifold = contact.getWorldManifold();
		Vector2 wp = worldManifold.getPoints()[0];
		cache.set(body1.getLinearVelocityFromWorldPoint(wp));
		cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
		speed = cache.dot(worldManifold.getNormal());

		/*
		// Play a sound if above threshold
		if (speed > SOUND_THRESHOLD) {
			String s1 = ((Obstacle)body1.getUserData()).getName();
			String s2 = ((Obstacle)body2.getUserData()).getName();
			if (s1.equals("koi") || s1.startsWith("enemy") || s1.startsWith("tether")) {
				SoundController.getInstance().play(s1, COLLISION_SOUND, false, 0.5f);
			}
			if (s2.equals("koi") || s2.startsWith("enemy") || s2.startsWith("tether")) {
				SoundController.getInstance().play(s2, COLLISION_SOUND, false, 0.5f);
			}
		}
		 */
	}


}