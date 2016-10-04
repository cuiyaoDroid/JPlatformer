package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class RocketGuy extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public RocketGuy( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Rocket Guy";
		movementSpeed   = 10f;
		bounds.width    = 48f;
		bounds.height   = 64f;
		jumpHeight      = 0f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		
		// Init frames
		//============
		this.initFrames();
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
		// Floating animation
		//===================
		if ( frameTimer < System.currentTimeMillis() )
		{
			frameTimer = System.currentTimeMillis() + 40;
			
			currentFrame++;
			if ( currentFrame > 7 )
			{
				currentFrame = 0;
			}
		}
	}
}
