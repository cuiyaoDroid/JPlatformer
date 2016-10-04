package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class SpikesDown extends MapObject
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
	public SpikesDown( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Spikes (down)";
		bounds.width    = 50f;
		bounds.height   = 31f;
		jumpHeight      = 96f;
		isBlockingSpace = true;
		
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
		// Move up
		//========
		this.jump( 15f, true );
		
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
}