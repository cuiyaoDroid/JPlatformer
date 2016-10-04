package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Box extends MapObject
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
	public Box( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Box";
		bounds.width    = 64f;
		bounds.height   = 64f;
		jumpHeight      = 96f;
		isBlockingSpace = true;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		if ( state == STATE_FALLING )
		{
			worldController.getPlayer().setAlive( false );
		}
	}
}