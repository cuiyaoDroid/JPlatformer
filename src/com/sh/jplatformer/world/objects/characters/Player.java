package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Player extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	public  static final float MAX_JUMP_HEIGHT = 152f;
	public  static final float MIN_JUMP_HEIGHT =  62f;
	
	// Routine
	//========
	private static final int MODE_ALIVE      = 0;
	private static final int MODE_DEATH_INIT = 1;
	private static final int MODE_DEATH      = 2;
	private int lastState;
	private int lastDir;
	
	// Death animation
	//================
	private Vector2 deathAnimPos;
	private float deathAnimForce;
	private long deathAnimTimer;
	
	// Jump
	//=====
	private boolean jumpKeyDown;
	private float currentJumpHeight;
	
	// Audio
	//======
	private long audioTimer;
	private int lastIdx;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_player_step1,
	                                                        Resources.WORLD.sound_character_player_step2,
	                                                        Resources.WORLD.sound_character_player_step3,
	                                                        Resources.WORLD.sound_character_player_step4,
	                                                        Resources.WORLD.sound_character_player_step5,
	                                                        Resources.WORLD.sound_character_player_step6 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Player( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Player";
		movementSpeed   = 22f;
		bounds.width    = 42f;
		bounds.height   = 84f;
		frameSize       = 96;
		jumpHeight      = MAX_JUMP_HEIGHT;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_BOTTOM;
		
		// Init frames
		//============
		this.initFrames();
		
		// Routine values
		//===============
		lastState = MapObject.STATE_STANDING;
		lastDir   = MapObject.DIR_EAST;
	}
	
	// draw
	//=====
	/**
	 * Draws the current {@code cuurentFrame} of this {@code MapObject}. 
	 * @param batch the {@code SpriteBatch} to render.
	 */
	public void draw( SpriteBatch batch )
	{
		// Draw sprite
		//============
		if ( routineMode == MODE_DEATH )
		{
			// Draw at offset
			//===============
			frames[ currentFrame ].setPosition( deathAnimPos.x, deathAnimPos.y );
			frames[ currentFrame ].draw( batch );
		}
		else
		{
			// Draw at position
			//=================
			super.draw( batch );
		}
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Process routine mode
		//=====================
		if ( routineMode == MODE_DEATH_INIT ) initDeathSequence();
		if ( routineMode == MODE_DEATH )      processDeathSequence();
		if ( routineMode == MODE_ALIVE )      processPlayerInput();
		
		// Play step sound
		//================
		if ( ( state == STATE_RUNNING && audioTimer < System.currentTimeMillis() ) ||
		     ( state == STATE_FALLING && isOnGround == true ) )
		{
			// Reset timer
			//============
			audioTimer = System.currentTimeMillis() + 200L;
			
			// Pick audio file
			//================
			int newIdx = lastIdx;
			
			while ( newIdx == lastIdx )
			{
				newIdx = Randomizer.getInt( 0, sounds.length - 1 );
			}
			
			// Play step sound
			//================
			WorldAudio.addSound( sounds[newIdx] , this );
			lastIdx = newIdx;
		}
	}
	
	// initDeathSequence
	//==================
	private void initDeathSequence()
	{
		// Set values
		//===========
		deathAnimTimer = System.currentTimeMillis() + 1500L;
		ignoreGravity  = true;
		routineMode    = MODE_DEATH;
		
		// Animation position
		//===================
		deathAnimForce = 0f; 
		deathAnimPos   = new Vector2( frames[currentFrame].getX(),
			                          frames[currentFrame].getY() );
		
		// Play sound
		//===========
		Resources.UI.sound_game_lose.play();
	}
	
	// processDeathSequence
	//=====================
	private void processDeathSequence()
	{
		// Perform sequence
		//=================
		if ( deathAnimTimer <= System.currentTimeMillis() )
		{
			// Move sprite
			//============
			deathAnimForce += 10f * WorldController.worldDelta;
			deathAnimPos.y -= deathAnimForce;
			
			// Kill player
			//============
			if ( worldController.getWorldCamera().project( deathAnimPos ).y < -frameSize * 2f ||
			     deathAnimPos.y  < -frameSize * 2f )
			{
				super.setAlive( false );
			}
		}
	}
	
	// processPlayerInput
	//===================
	private void processPlayerInput()
	{
		// Move east / west
		//=================
		if ( Gdx.input.isKeyPressed( Keys.RIGHT ) )
		{
			this.moveEast();
		}
		else if ( Gdx.input.isKeyPressed( Keys.LEFT  ) )
		{
			this.moveWest();
		}
		
		// Use
		//====
		if ( Gdx.input.isKeyJustPressed( Keys.UP ) )
		{
			// Use objects within bounds
			//==========================
			for ( MapObject o : surroundingObjects )
			{
				if ( bounds.overlaps( o.getBounds() ) )
				{
					o.use();
				}
			}
		}
		
		// Jump
		//=====
		if ( Gdx.input.isKeyPressed( Keys.SPACE ) )
		{
			// Initiate minimum jump
			//======================
			if ( state != STATE_JUMPING )
			{
				jumpKeyDown = true;
				currentJumpHeight = Player.MIN_JUMP_HEIGHT;
				
				this.jump( Player.MIN_JUMP_HEIGHT, false );
			}
			
			// Extend jump
			//============
			if ( jumpKeyDown && currentJumpHeight <= Player.MAX_JUMP_HEIGHT )
			{
				// Increase value
				//===============
				float v = verticalForce * WorldController.worldDelta;
				
				// Limit value
				//============
				if ( currentJumpHeight + v > Player.MAX_JUMP_HEIGHT )
				{
					v = Player.MAX_JUMP_HEIGHT - currentJumpHeight;
				}
				
				// Extend jump
				//============
				jumpTargetY       += v;
				currentJumpHeight += v;
			}
		}
		
		// Release jump
		//=============
		if ( !Gdx.input.isKeyPressed( Keys.SPACE ) )
		{
			jumpKeyDown = false;
		}
	}
	
	// setAlive
	//=========
	/**
	 * Overrides the super method in order to initiate a death sequence if {@code isAlive} is set
	 * {@code false}.
	 */
	@Override
	public void setAlive( boolean value )
	{
		// Init dead sequence when live
		//=============================
		if ( worldController.isLive() && routineMode == MODE_ALIVE )
		{
			routineMode = MODE_DEATH_INIT;
			
			worldController.setWorldState( WorldController.STATE_PLAYER_DIES );
		}
		
		// Skip sequence in editor
		//========================
		else if ( !worldController.isLive() )
		{
			super.setAlive( value );
		}
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// When death
		//===========
		if ( routineMode == MODE_DEATH || routineMode == MODE_DEATH_INIT )
		{
			currentFrame = 3;
			return;
		}
		
		// When running
		//=============
		if ( state == STATE_RUNNING )
		{
			// Update timer + frame
			//=====================
			if ( frameTimer < System.currentTimeMillis() || lastState != state )
			{
				frameTimer = System.currentTimeMillis() + 30;
				
				currentFrame++;
			}
			
			// Limit frames: East
			//===================
			if ( direction == DIR_EAST )
			{
				if ( currentFrame > 21 || currentFrame < 8 )
				{
					currentFrame = 8;
				}
			}

			// Limit frames: East
			//===================
			if ( direction == DIR_WEST )
			{
				if ( currentFrame > 35 || currentFrame < 22 )
				{
					currentFrame = 22;
				}
			}
		}

		// When resting
		//=============
		if ( state == STATE_STANDING )
		{
			// Check current frame
			//====================
			if ( currentFrame > 2 )
			{
				currentFrame = 0;
			}
			
			// Update frame
			//=============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 0, 2 );
				frameTimer = System.currentTimeMillis() + Randomizer.getLong( 500L, 3000L );
			}
		}
		
		// When jumping
		//=============
		if ( state == STATE_JUMPING )
		{
			// Jump: East
			//===========
			if ( lastDir == DIR_EAST )
			{
				currentFrame = 4;
			}
			
			// Jump: West
			//===========
			if ( lastDir == DIR_WEST )
			{
				currentFrame = 6;
			}
		}
		
		// When falling
		//=============
		if ( state == STATE_FALLING )
		{
			// Fall: East
			//===========
			if ( lastDir == DIR_EAST )
			{
				currentFrame = 5;
			}
			
			// Fall: West
			//===========
			if ( lastDir == DIR_WEST )
			{
				currentFrame = 7;
			}
		}
		
		// Update last state / direction
		//==============================
		if ( direction != MapObject.DIR_NONE )
		{
			lastDir   = direction;
		}
		lastState = state;
	}
	
	// initFrames
	//===========
	@Override
	public void initFrames()
	{
		// Read frames
		//============
		try
		{
			// Values
			//=======
			Sprite sprite = Resources.WORLD.objectsSprites.get( name );

			int lenght    = (int) ( sprite.getWidth() / frameSize * sprite.getHeight() / frameSize );
			frames        = new Sprite[lenght];
			
			// Read sprites
			//=============
			for ( int i = 0; i < frames.length; i++ )
			{
				int x = (int) ( ( i * frameSize ) % sprite.getWidth() );
				int y = (int) ( ( i * frameSize ) / sprite.getWidth() ) * frameSize;

				frames[i] = new Sprite( sprite, x, y, 96, 96 );
			}
		}
		catch ( Exception e )
		{
			System.err.println( "Error initializing frames for map object: " + name + "!" );
		}
	}
}
