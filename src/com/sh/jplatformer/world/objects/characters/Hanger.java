package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Hanger extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ON_GROUND = 0;
	private static final int MODE_HANGING   = 1;
	private static final int MODE_FALLING   = 2;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_cartonGuy1,
	                                                        Resources.WORLD.sound_character_cartonGuy2,
	                                                        Resources.WORLD.sound_character_cartonGuy3,
	                                                        Resources.WORLD.sound_character_cartonGuy4 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Hanger( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Hanger";
		movementSpeed   = 10f;
		bounds.width    = 58f;
		bounds.height   = 64f;
		jumpHeight      = 148f;
		isBlockingSpace = true;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Init first jump
		//================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4500, 6000 );
		}
		
		// Toggle blocking behavior
		//=========================
		if ( isOnGround || routineMode == MODE_HANGING )
		{
			// Block when no movement
			//=======================
			isBlockingSpace = true;
		}
		else if ( worldController.getPlayer() != null )  
		{
			// Set player
			//===========
			MapObject p = worldController.getPlayer();
			
			// Unblock if x overlaps
			//======================
			if ( p.getBounds().overlaps( this.scanArea ) )
			{
				if ( p.getBounds().x + p.getBounds().width <= bounds.x ||
				     p.getBounds().x >= bounds.x + bounds.width )
				{
					isBlockingSpace = true;
				}
				else
				{
					isBlockingSpace = false;
				}
			}
		}
		
		// While on ground / jumping
		//==========================
		if ( routineMode == MODE_ON_GROUND )
		{
			// Init jump when ready
			//=====================
			if ( routineTimer < System.currentTimeMillis() )
			{
				// Jump
				//=====
				this.jump();
				routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4500, 6000 );
				
				// Play sound
				//===========
				WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ], this );
			}
			
			// Check roof collision
			//=====================
			if ( bounds.y >= jumpTargetY-15f )
			{
				if ( map.isBlocked( bounds.x + bounds.width / 2, bounds.y + bounds.height + 1f ) &&
				     isOnGround == false )
				{
					routineMode = MODE_HANGING;
					routineTimer = System.currentTimeMillis() + 5000;
				}
			}
		}
		
		// While hanging
		//==============
		else
		{
			// Check timer
			//============
			if ( routineTimer < System.currentTimeMillis() )
			{
				// Turn to fall
				//=============
				if ( routineMode == MODE_HANGING )
				{
					routineMode = MODE_FALLING;
					
					WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length-1  ) ], this );
				}
				
				// Return to MODE_ON_GROUND
				//=========================
				if ( isOnGround == true )
				{	
					routineMode  = MODE_ON_GROUND;
					routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4500, 6000 );
				}
			}
			else
			{
				// Check roof tile
				//================
				if ( map.isBlocked( bounds.x + bounds.width / 2,
				                    bounds.y + bounds.height + 1f ) == false )
				{
					routineTimer = 0;
					setVerticalForce( 0f );
				}
				else
				{
					// Stick to roof tile
					//===================
					this.jump( 15f, true );
					state = STATE_USING;
				}
			}
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		worldController.getPlayer().setAlive( false );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// While on ground
		//================
		if ( state == STATE_STANDING )
		{
			// Looking around
			//===============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 0, 2 );
				frameTimer = System.currentTimeMillis() + 500;
			}
			
			// Before jumping
			//===============
			if ( routineTimer < System.currentTimeMillis() + 1000 )
			{
				currentFrame = 3;
			}
		}
		
		// While hanging
		//==============
		if ( state == STATE_USING )
		{
			// Looking around
			//===============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = 4;
			}
			
			// Before falling
			//===============
			if ( routineTimer < System.currentTimeMillis() + 1000 )
			{
				currentFrame = 5;
			}
		}
		
		// While not on ground
		//====================
		if ( state == STATE_JUMPING ) { currentFrame = 6; frameTimer = 0; };
		if ( state == STATE_FALLING ) { currentFrame = 7; frameTimer = 0; };
	}
}
