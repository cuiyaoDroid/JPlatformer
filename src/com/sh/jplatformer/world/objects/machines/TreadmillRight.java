package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan H�semann
 */

public class TreadmillRight extends MapObject
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
	public TreadmillRight( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Treadmill (right)";
		bounds.width     = 64f;
		bounds.height    = 64f;
		jumpHeight       = 64f;
		isBlockingSpace  = true;
		isPowerSupported = true;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Move objects on top
		//====================
		if ( isPowerOn == true )
		{
			for ( MapObject o : surroundingObjects )
			{
				// Check bounds
				//=============
				if ( o.isOnGround() == true && o.getBounds().y == bounds.y + bounds.height )
				{
					o.setHorizontalForce( +120f );
				}
			}
		}
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
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// When on
		//========
		if ( isPowerOn == true )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 2;
			}
		}
		
		// When off
		//=========
		else
		{
			currentFrame = 1;
		}
	}
}