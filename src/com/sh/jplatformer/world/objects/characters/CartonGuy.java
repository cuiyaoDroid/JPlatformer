package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class CartonGuy extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
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
	public CartonGuy( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Carton Guy";
		movementSpeed   = 10f;
		bounds.width    = 54f;
		bounds.height   = 63f;
		jumpHeight      = 132f;
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
		// Init first jump
		//================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4500, 6000 );
		}
		
		// Jump from time to time
		//=======================
		if ( routineTimer < System.currentTimeMillis() && isOnGround == true )
		{
			// Jump
			//=====
			this.jump();
			routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4500, 6000 );
			
			// Play sound
			//===========
			WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ], this );
		}
		
		// Toggle blocking behavior
		//=========================
		if ( worldController.getPlayer() != null )
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
				frameTimer = System.currentTimeMillis() + 400;
			}
			
			// Before jumping
			//===============
			if ( routineTimer < System.currentTimeMillis() + 1000 )
			{
				currentFrame = 3;
			}
		}
		
		// While not on ground
		//====================
		if ( state == STATE_JUMPING ) { currentFrame = 4; frameTimer = 0; };
		if ( state == STATE_FALLING ) { currentFrame = 5; frameTimer = 0; };
	}
}
