package com.sh.jplatformer.world.map;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code MapObject} class is a base class for all objects located on and interacting with
 * a specified {@code Map}. A {@code MapObject} can be specified by a variety of attributes.
 * @author Stefan Hösemann
 */

public abstract class MapObject implements Serializable
{
	// Object management
	//==================
	private static final long serialVersionUID = 1L;
	public static int idCount;
	protected int id;
	
	// States + direction constants
	//=============================	
	public static final int STATE_STANDING = 0;
	public static final int STATE_RUNNING  = 1;
	public static final int STATE_JUMPING  = 2;
	public static final int STATE_FALLING  = 3;
	public static final int STATE_HANGING  = 4;
	public static final int STATE_USING    = 5;
	
	public static final int DIR_NONE = 0;
	public static final int DIR_EAST = 1;
	public static final int DIR_WEST = 2;
	
	public static final int ALIGN_RIGHT  = 0;
	public static final int ALIGN_LEFT   = 1;
	public static final int ALIGN_TOP    = 2;
	public static final int ALIGN_BOTTOM = 3;
	public static final int ALIGN_CENTER = 4;
	
	// States 
	//=======
	protected String name;
	protected boolean isAlive;
	protected int state;
	protected int score;
	
	// World + objects
	//================
	protected WorldController worldController;
	protected ArrayList<MapObject> surroundingObjects;
	
	// Energy
	//=======
	protected int powerId;
	protected boolean isPowerSupported;
	protected boolean isPowerOn;	
	
	// Movement
	//=========
	protected int direction;
	protected boolean isBlockingSpace;
	protected boolean isOnGround;
	protected boolean ignoreGravity;
	protected float jumpHeight;
	protected float jumpTargetY;
	protected float horizontalForce;
	protected float verticalForce;
	protected float movementSpeed;
	protected Vector2 deltaMovementSpeed;
	protected Rectangle bounds;
	protected Rectangle scanArea;
	
	// Orientation
	//============
	protected int verticalAlignment;
	protected int horizontalAlignment;
	
	// Routine
	//========
	protected int routineMode;
	protected long routineTimer;
	
	// Rendering
	//==========
	protected transient Sprite[] frames;
	protected long frameTimer;
	protected int currentFrame;
	protected int frameSize;
	protected float alpha;
	
	// Audio
	//======
	protected Sound soundFile;
	protected long soundId;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public MapObject( WorldController newWorldController )
	{
		// General
		//========
		if ( newWorldController != null )
		{
			// ID counting
			//============
			idCount ++;
			id = idCount;
			
			// Initial properties
			//===================
			worldController    = newWorldController;
			surroundingObjects = new ArrayList<MapObject>();
			isAlive            = true;
		}
		
		// Movement
		//=========
		direction          = MapObject.DIR_EAST;
		deltaMovementSpeed = new Vector2();
		isBlockingSpace    = true;
		
		// Rendering
		//==========
		frameSize = Map.CELL_SIZE;
		alpha = 0f;
		
		// Collision
		//==========
		bounds              = new Rectangle( 0f, 0f, frameSize, frameSize );
		scanArea            = new Rectangle( bounds );
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment   = ALIGN_BOTTOM;
	}
	
	// createFromClass
	//================
	/**
	 * Creates an instance of a {@code MapObject} at runtime.
	 * @param className the class name of the {@code MapObject} to be created.
	 * @param worldController the {@code WorldController} to interact with.
	 * @return the new {@code MapObject}. This method returns {@code null} if an error occurred.
	 */
	public static MapObject createFromClassName( String className, WorldController worldController )
	{
		try
		{
			// Get constructor and create
			//===========================
			Class<?> objectClass = Class.forName( className );
			Constructor<?> ctor  = objectClass.getConstructor( WorldController.class );
			Object object        = ctor.newInstance( new Object[] { worldController } );
			
			return ( ( MapObject ) object );
		}
		catch ( Exception e )
		{
			// Failed
			//=======
			System.err.println( "Error creating map object: " + className + "!" );
			return ( null );
		}
	}
	
	// draw
	//=====
	/**
	 * Draws the {@code currentFrame} of this {@code MapObject}. 
	 * @param batch the {@code SpriteBatch} to render.
	 */
	public void draw( SpriteBatch batch )
	{
		// Update alpha
		//=============
		updateAlpha();
		
		// Draw frames
		//============
		frames[ currentFrame ].setPosition( bounds.x + bounds.width / 2f - frameSize / 2f, bounds.y );
		frames[ currentFrame ].draw( batch, alpha  );
	}
	
	// update
	//=======
	/**
	 * Updates this {@code MapObject}.
	 */
	public void update()
	{
		// Updates
		//========
		this.updateSpeed();
		this.updateState();
		this.applyHorizontalForce();
		this.applyVerticalForce();
		this.act();
		this.updateFrame();
		
		// Perform player collision
		//=========================
		if ( worldController.isLive() )
		{
			if ( worldController.getPlayer() != null )
			{
				if ( worldController.getPlayer().getBounds().overlaps( bounds ) )
				{
					this.onPlayerCollision();
				}
			}
		}
	}
	
	// updateSpeed
	//============
	private void updateSpeed()
	{
		deltaMovementSpeed.x = movementSpeed * WorldController.worldDelta * 10f;
	}
	
	// updateState
	//============
	private void updateState()
	{
		if ( isOnGround == true )
		{
			if ( direction == DIR_NONE ) state = STATE_STANDING;
			if ( direction == DIR_EAST ) state = STATE_RUNNING;
			if ( direction == DIR_WEST ) state = STATE_RUNNING;
		}
		else
		{
			if ( verticalForce > 0 ) state = STATE_JUMPING;
			if ( verticalForce < 0 ) state = STATE_FALLING;
		}
	}
	
	// updateFrame
	//============
	protected void updateFrame()
	{
	}
	
	// updateAlpha
	//============
	protected void updateAlpha()
	{
		// Update alpha
		//=============
		if ( isAlive == true )
		{
			// Increase
			//=========
			alpha += WorldController.worldDelta * 7f;
			if ( alpha > 1f ) alpha = 1f;
		}
		else
		{
			// Decrease
			//=========
			alpha -= WorldController.worldDelta * 7f;
			if ( alpha < 0f ) alpha = 0f;
		}
	}
	
	// initFrames
	//===========
	/**
	 * Initializes the {@code frames} array by searching {@code Resources.WORLD.objectsSprites} for
	 * a sprite by the key of the {@code name} of this {@code MapObject} and reads the frames based
	 * on {@code MAX_SIZE}. 
	 */
	public void initFrames()
	{
		// Read frames
		//============
		try
		{
			// Values
			//=======
			Sprite sprite             = Resources.WORLD.objectsSprites.get( name );
			TextureRegion[][] regions = sprite.split( frameSize, frameSize );
			frames                    = new Sprite[regions[0].length];
	
			// Read sprites
			//=============
			for ( int i = 0; i < frames.length; i++ )
			{
				frames[i] = new Sprite( regions[0][i] );
			}
		}
		catch ( Exception e )
		{
			System.err.println( "Error initializing frames for map object: " + name + "!" );
		}
	}
	
	// onPlayerCollision
	//==================
	/**
	 * This method is called if the {@code WorldController} is live and if the bounds of this
	 * {@code MapObject} overlap the bounds of the player object (if existent). 
	 */
	public void onPlayerCollision()
	{
	}
	
	// act
	//====
	/**
	 * Performs the individual routine of this {@code MapObject}
	 */
	public void act()
	{	
	}
	
	// use
	//====
	/**
	 * This method is performed when the {@code MapObject} is used.
	 */
	public boolean use()
	{	
		return ( true );
	}
	
	// moveEast
	//=========
	public boolean moveEast()
	{
		return ( this.move( deltaMovementSpeed.x, 0 ) );
	}
	
	// moveWest
	//=========
	public boolean moveWest()
	{
		return ( this.move( -deltaMovementSpeed.x, 0 ) );
	}
	
	// jump
	//=====
	public void jump( float jumpHeight, boolean forceJump )
	{
		if ( forceJump == true || isOnGround == true )
		{
			jumpTargetY = bounds.y + jumpHeight;
		}
	}
	
	// jump
	//=====
	public void jump()
	{
		this.jump( jumpHeight, false );
	}
	
	// move
	//=====
	/**
	 * Moves this {@code MapObject} by the specified velocity on the {@code Map}. If the destination
	 * cell is blocked, no movement will be performed.
	 * @param speedX the horizontal speed.
	 * @param speedY the vertical speed.
	 * @return {@code true} if the movement was successful, {@code false} if the movement was
	 * blocked in any direction.
	 */
	public boolean move( float speedX, float speedY )
	{
		// Resets / Updates
		//=================
		Map map = worldController.getMap();
		direction = DIR_NONE;
		
		// Limit speed
		//============
		if ( speedX > +Map.CELL_SIZE / 2f ) speedX = +Map.CELL_SIZE / 2f;
		if ( speedX < -Map.CELL_SIZE / 2f ) speedX = -Map.CELL_SIZE / 2f;
		if ( speedY > +Map.CELL_SIZE / 2f ) speedY = +Map.CELL_SIZE / 2f;
		if ( speedY < -Map.CELL_SIZE / 2f ) speedY = -Map.CELL_SIZE / 2f;
		
		// Temporary values
		//=================
		boolean isBlockedX = false;
		boolean isBlockedY = false;
		Vector2 detection1 = new Vector2( 0f, 0f );
		Vector2 detection2 = new Vector2( 0f, 0f );
		Vector2 detection3 = new Vector2( 0f, 0f );
		Rectangle detection4 = new Rectangle();
		
		// Move up
		//========
		if ( speedY > 0f )
		{
			// Set collision vectors
			//======================
			detection1.set( bounds.x,                     bounds.y + bounds.height + speedY );
			detection2.set( bounds.x + bounds.width - 1f, bounds.y + bounds.height + speedY );
			detection3.set( bounds.x + bounds.width / 2f, bounds.y + bounds.height + speedY );
			detection4.set( bounds.x, bounds.y + speedY , bounds.width, bounds.height );
			
			// Check tiles
			//============
			if ( !map.isBlocked( detection1 ) &&
			     !map.isBlocked( detection2 ) &&
			     !map.isBlocked( detection3 ) )
			{
				// Check objects
				//==============
				for ( int i = 0; i < surroundingObjects.size(); i++ )
				{
					if ( surroundingObjects.get( i ) != this &&
					     surroundingObjects.get( i ).isBlockingSpace == true &&
					     surroundingObjects.get( i ).getBounds().overlaps( detection4 ) )
					{
						// Reset position
						//===============
						bounds.y = surroundingObjects.get( i ).getBounds().y - bounds.height;
						isBlockedY = true;
						this.setVerticalForce( 0f );
						break;
					}
				}
				
				// Perform movement
				//=================
				if ( isBlockedY == false )
				{
					bounds.y += speedY;
					isOnGround = false;
				}
			}
			else
			{
				bounds.y = Map.CELL_SIZE * ( map.getCellAt( detection1 ).y ) - bounds.height;
				isBlockedY = true;
				this.setVerticalForce( 0f );
			}
		}
		
		// Move down
		//==========
		if ( speedY < 0f )
		{
			// Set collision vectors
			//======================
			detection1.set( bounds.x,                     bounds.y + speedY );
			detection2.set( bounds.x + bounds.width - 1f, bounds.y + speedY );
			detection3.set( bounds.x + bounds.width / 2f, bounds.y + speedY );
			detection4.set( bounds.x, bounds.y + speedY , bounds.width, bounds.height );
			
			// Check tiles
			//============
			if ( !map.isBlocked( detection1 ) &&
			     !map.isBlocked( detection2 ) &&
			     !map.isBlocked( detection3 ) )
			{
				// Check objects
				//==============
				for ( int i = 0; i < surroundingObjects.size(); i++ )
				{
					if ( surroundingObjects.get( i ) != this &&
					     surroundingObjects.get( i ).isBlockingSpace == true &&
					     surroundingObjects.get( i ).getBounds().overlaps( detection4 ) )
					{
						// Reset position
						//===============
						bounds.y = surroundingObjects.get( i ).getBounds().y + surroundingObjects.get( i ).getBounds().height;
						isBlockedY = true;
						isOnGround = true;
						this.setVerticalForce( 0f );
						break;
					}
				}
				
				// Perform movement
				//=================
				if ( isBlockedY == false )
				{
					bounds.y += speedY;
					isOnGround = false;
				}
			}
			else
			{
				bounds.y = Map.CELL_SIZE * ( map.getCellAt( detection1 ).y + 1f );
				isBlockedY = true;
				isOnGround = true;
				this.setVerticalForce( 0f );
			}
		}

		// Move east
		//==========
		if ( speedX > 0f )
		{
			// Set collision vectors
			//======================
			detection1.set( bounds.x + bounds.width + speedX, bounds.y );
			detection2.set( bounds.x + bounds.width + speedX, bounds.y + bounds.height - 1f );
			detection3.set( bounds.x + bounds.width + speedX, bounds.y + bounds.height / 2f );
			detection4.set( bounds.x + speedX, bounds.y, bounds.width, bounds.height );
			
			// Check tiles
			//============
			if ( !map.isBlocked( detection1 ) &&
			     !map.isBlocked( detection2 ) &&
			     !map.isBlocked( detection3 ) )
			{
				// Check objects
				//==============
				for ( int i = 0; i < surroundingObjects.size(); i++ )
				{
					if ( surroundingObjects.get( i ) != this &&
					     surroundingObjects.get( i ).isBlockingSpace == true &&
					     surroundingObjects.get( i ).getBounds().overlaps( detection4 ) )
					{
						// Reset position
						//===============
						bounds.x = surroundingObjects.get( i ).getBounds().x - bounds.width;
						isBlockedX = true;
						break;
					}
				}
				
				// Perform movement
				//=================
				if ( isBlockedX == false )
				{
					bounds.x += speedX;
					direction = DIR_EAST;
				}
			}
			else
			{
				bounds.x = Map.CELL_SIZE * ( map.getCellAt( detection1 ).x ) - bounds.width;
				isBlockedX = true;
			}
		}
		
		// Move west
		//==========
		if ( speedX < 0f )
		{
			// Set collision vectors
			//======================
			detection1.set( bounds.x + speedX, bounds.y );
			detection2.set( bounds.x + speedX, bounds.y + bounds.height - 1f );
			detection3.set( bounds.x + speedX, bounds.y + bounds.height / 2f );
			detection4.set( bounds.x + speedX, bounds.y, bounds.width, bounds.height );
			
			// Check tiles
			//============
			if ( !map.isBlocked( detection1 ) &&
			     !map.isBlocked( detection2 ) &&
			     !map.isBlocked( detection3 ) )
			{
				// Check objects
				//==============
				for ( int i = 0; i < surroundingObjects.size(); i++ )
				{
					if ( surroundingObjects.get( i ) != this &&
					     surroundingObjects.get( i ).isBlockingSpace == true &&
					     surroundingObjects.get( i ).getBounds().overlaps( detection4 ) )
					{
						// Reset position
						//===============
						bounds.x = surroundingObjects.get( i ).getBounds().x + surroundingObjects.get( i ).getBounds().width;
						isBlockedX = true;
						break;
					}
				}
				
				// Perform movement
				//=================
				if ( isBlockedX == false )
				{
					bounds.x += speedX;
					direction = DIR_WEST;
				}
			}
			else
			{
				bounds.x = Map.CELL_SIZE * ( map.getCellAt( detection1 ).x + 1f );
				isBlockedX = true;
			}
		}
		
		// Kill when in water
		//===================
		if ( bounds.y + bounds.height < map.getWaterHeight() )
		{
			this.setAlive( false );
		}
		
		// Limit western border
		//=====================
		if ( bounds.x < 0 )
		{
			bounds.x = 0;
			isBlockedX = true;
		}
				
		// Limit eastern border
		//=====================
		if ( bounds.x > worldController.getMapBounds().width - bounds.width )
		{
			bounds.x = worldController.getMapBounds().width - bounds.width;
			isBlockedX = true;
		}
		
		// Limit bottom border
		//====================
		if ( bounds.y < -Map.CELL_SIZE * 2f )
		{
			bounds.y = -Map.CELL_SIZE * 2f;
			isBlockedY = true;
			this.setAlive( false );
		}
		
		// Limit top border
		//=================
		if ( bounds.y > worldController.getMapBounds().height + Map.CELL_SIZE )
		{
			this.setAlive( false );
		}
		return ( !isBlockedX && !isBlockedY );
	}
	
	// getFrames
	//==========
	public Sprite[] getFrames()
	{
		return ( frames );
	}
	
	// getAlpha
	//=========
	public float getAlpha()
	{
		return ( alpha );
	}
	
	// setWorldController
	//===================
	public void setWorldController( WorldController newWorldController )
	{
		worldController = newWorldController;
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}
	
	// setId
	//======
	public void setId( int newId )
	{
		id = newId;
	}
	
	// getId
	//======
	public int getId()
	{
		return ( id );
	}
	
	// setAlive
	//=========
	public void setAlive( boolean value )
	{
		isAlive = value;
	}
	
	// isAlive
	//========
	public boolean isAlive()
	{
		return ( isAlive );
	}
	
	// setName
	//========
	public void setName( String newName )
	{
		name = newName;
	}	
	
	// getName
	//========
	public String getName()
	{
		return ( name );
	}
	
	// setScore
	//=========
	public void setScore( int value )
	{
		score = value;
	}	
	
	// getScore
	//=========
	public int getScore()
	{
		return ( score );
	}
	
	// isPowerSupported
	//=================
	public boolean isPowerSupported()
	{
		return ( isPowerSupported );
	}
	
	// setPowerId
	//===========
	public void setPowerId( int newId )
	{
		powerId = newId;
	}
	
	// getPowerId
	//===========
	public int getPowerId()
	{
		return ( powerId );
	}
	
	// setPowerOn
	//===========
	public void setPowerOn( boolean value )
	{
		isPowerOn = value;
	}
	
	// isPowerOn
	//==========
	public boolean isPowerOn()
	{
		return ( isPowerOn );
	}
	
	// setState
	//=========
	public void setState( int newState )
	{
		state = newState;
	}
	
	// getState
	//=========
	public int getState()
	{
		return ( state );
	}
	
	// setBlockingSpace
	//====================
	public void setBlockingSpace( boolean value )
	{
		isBlockingSpace = value;
	}
	
	// isBlockingSpace
	//===================
	public boolean isBlockingSpace()
	{
		return ( isBlockingSpace );
	}
	
	// setRoutineTimer
	//================
	public void setRoutineTimer( long newTime )
	{
		routineTimer = newTime;
	}
	
	
	// resetRoutineTimer
	//==================
	/**
	 * Resets the routine timer in order to synchronize all objects routines.
	 */
	public void resetRoutineTimer()
	{
		routineTimer = 0;
	}
	
	// getRoutineTimer
	//================
	public long getRoutineTimer()
	{
		return ( routineTimer );
	}
	
	// setSurroundingObjects
	//======================
	/**
	 * Sets an array of {@code MapObjects} which is used for collision detection and scanned for
	 * usable objects.
	 * @param mapObjects an array of {@code MapObjects}.
	 */
	public void setSurroundingObjects( ArrayList<MapObject> mapObjects )
	{
		surroundingObjects = mapObjects;
	}
	
	// getSurroundingObjects
	//======================
	public ArrayList<MapObject> getSurroundingObjects()
	{
		return ( surroundingObjects );
	}
	
	// setMovementSpeed
	//=================
	public void setMovementSpeed( float newMovementSpeed )
	{
		movementSpeed = newMovementSpeed;
	}	
	
	// getMovementSpeed
	//=================
	public float getMovementSpeed()
	{
		return ( movementSpeed );
	}
	
	// getMovementVector
	//==================
	public Vector2 getDeltaMovementSpeed()
	{
		return ( deltaMovementSpeed );
	}
	
	// getJumpHeight
	//==============
	public float getJumpHeight()
	{
		return ( jumpHeight );
	}
	
	// isOnGround
	//===========
	public boolean isOnGround()
	{
		return ( isOnGround );
	}
	
	// setHorizontalForce
	//===================
	public void setHorizontalForce( float force )
	{
		horizontalForce = force;
	}
	
	// setVerticalForce
	//=================
	public void setVerticalForce( float force )
	{
		verticalForce = force;
		jumpTargetY = -999f;
	}
	
	// applyVerticalForce
	//===================
	private void applyVerticalForce()
	{
		// When no gravity
		//================
		if ( ignoreGravity == true )
		{
			return;
		}
		
		// When jumping
		//=============
		if ( jumpTargetY > bounds.y )
		{
			// Apply force
			//============
			verticalForce = ( bounds.y - jumpTargetY ) * -8f;
			
			if ( verticalForce < 150f ) verticalForce = 150f;
			if ( verticalForce > 450f ) verticalForce = 450f;
			
			this.move( 0, verticalForce * WorldController.worldDelta );
			
			// Reset at reverse point
			//=======================
			if ( bounds.y >= jumpTargetY )
			{
				this.setVerticalForce( 0f );
			}
		}
		
		// When falling
		//=============
		else
		{
			// Apply force
			//============
			verticalForce -= ( 1100f * WorldController.worldDelta );
			if ( verticalForce < -600 ) verticalForce = -600;
			
			this.move( 0f, verticalForce * WorldController.worldDelta );
		}
		
		// Initialize fall
		//================
		if ( verticalForce == 0.0f )
		{
			verticalForce = -0.00001f;
		}
	}
	
	// applyHorizontalForce
	//=====================
	private void applyHorizontalForce()
	{
		// Move object
		//============
		this.move( horizontalForce * WorldController.worldDelta, 0f );
		
		// Decrease force
		//===============
		if ( horizontalForce > 0 )
		{
			// When positive
			//==============
			horizontalForce -= 120f * WorldController.worldDelta;
			
			if ( horizontalForce <= 0f )
			{
				horizontalForce = 0f;
			}
		}
		else
		{
			// When negative
			//==============
			horizontalForce += 120f * WorldController.worldDelta;
			
			if ( horizontalForce >= 0f )
			{
				horizontalForce = 0f;
			}
		}

	}
	
	// setPosition
	//============
	/**
	 * @param newPosition the new position of this {@code MapObject} on the {@code Map} in units.
	 * @param center if this value is {@code true}, the object will be centered at the given point.
	 */
	public void setPosition( Vector2 newPosition, boolean center )
	{
		this.setPosition( newPosition.x,  newPosition.y, center );
	}
	
	// setPosition
	//============
	/**
	 * @param x the x-position on the {@code Map} in units.
	 * @param y the y-position on the {@code Map} in units.
	 * @param center if this value is {@code true}, the object will be centered at the given point.
	 */
	public void setPosition( float x, float y, boolean center )
	{
		// Set position
		//=============
		if ( center == true )
		{
			bounds.x = x - bounds.width  / 2f;
			bounds.y = y - frameSize / 2f + 1;
		}
		else
		{
			bounds.x = x;
			bounds.y = y;
		}
		
		// Reset vertical force
		//=====================
		this.setVerticalForce( 0f );
	}
	
	// getFrameSize
	//=============
	/**
	 * @return the side length of the frame square. 
	 */
	public int getFrameSize()
	{
		return ( frameSize );
	}
	// getDirection
	//=============
	/**
	 * @return the current horizontal direction in which this {@code MapObject} is moving.
	 */
	public int getDirection()
	{
		return ( direction );
	}
	
	// getBounds
	//==========
	/**
	 * @return the bounding rectangle of this {@code MapObject}.
	 */
	public Rectangle getBounds()
	{
		return ( bounds );
	}
	
	// getCenter
	//==========
	/**
	 * @return the center of this {@code MapObject}.
	 */
	public Vector2 getCenter()
	{
		return ( new Vector2( bounds.x + bounds.width / 2,
		                      bounds.y + bounds.height / 2 ) );
	}
	
	// getScanArea
	//============
	/**
	 * @return the rectangular area around this {@code MapObject} to scan for other objects.
	 */
	public Rectangle getScanArea()
	{
		int size = Map.CELL_SIZE;
		
		scanArea.x      = bounds.x      - size / 2;
		scanArea.y      = bounds.y      - size / 2;
		scanArea.width  = bounds.width  + size;
		scanArea.height = bounds.height + size;
		
		return ( scanArea );
	}
	
	// getHorizontalAlignment
	//=======================
	public int getHorizontalAlignment()
	{
		return ( horizontalAlignment );
	}
	
	// getVerticalAlignment
	//=====================
	public int getVerticalAlignment()
	{
		return ( verticalAlignment );
	}
}
