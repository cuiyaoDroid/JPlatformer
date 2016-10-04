package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonDown extends MapObject
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
	public CannonDown( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Cannon (down)";
		bounds.width     = 38f;
		bounds.height    = 40f;
		jumpHeight       = 0f;
		isBlockingSpace  = true;
		ignoreGravity    = true;
		isPowerSupported = true;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Fire cannon ball
		//=================
		if ( isPowerOn == true && routineTimer < System.currentTimeMillis() )
		{
			// Reset timer
			//============
			routineTimer = System.currentTimeMillis() + 4000L;
			
			// Create cannon ball
			//===================
			CannonBall cannonBall = new CannonBall( worldController, CannonBall.MODE_DOWN );
			worldController.addMapObject( cannonBall,
			                              bounds.x + bounds.width / 2f,
			                              bounds.y - cannonBall.getBounds().height / 2f,
			                              true );
		}
	}
}