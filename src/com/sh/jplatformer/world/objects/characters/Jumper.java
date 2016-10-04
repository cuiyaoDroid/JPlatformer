package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Jumper extends MapObject
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
	public Jumper( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Jumper";
		movementSpeed   = 10f;
		bounds.width    = 50f;
		bounds.height   = 50f;
		jumpHeight      = 180f;
		isBlockingSpace = false;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{		
		// Jump
		//=====
		if ( isOnGround == true )
		{
			this.jump();
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
		// Update frames
		//==============
		if ( state == STATE_JUMPING ) currentFrame = 0;
		if ( state == STATE_FALLING ) currentFrame = 1;
	}
}
