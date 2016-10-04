package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Snail extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private int curDir;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Snail( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Snail";
		movementSpeed   = 10f;
		bounds.width    = 62f;
		bounds.height   = 54f;
		jumpHeight      = 96f;
		isBlockingSpace = false;
		
		// Routine
		//========
		curDir = MapObject.DIR_EAST;
		
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
		
		// Move on platform
		//=================
		if ( isOnGround == true )
		{
			// Move east
			//==========
			if ( curDir == DIR_EAST )
			{
				if ( !map.isBlocked( bounds.x + bounds.width, bounds.y - Map.CELL_SIZE / 2 ) ||
				     this.moveEast() == false )
				{
					curDir = DIR_WEST;
				}
			}
			
			// Move west
			//==========
			else if ( curDir == DIR_WEST )
			{
				if ( !map.isBlocked( bounds.x, bounds.y - Map.CELL_SIZE / 2 ) ||
				     this.moveWest() == false )
				{
					curDir = DIR_EAST;
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
		// Update frames
		//==============
		if ( direction == DIR_EAST ) currentFrame = 0;
		if ( direction == DIR_WEST ) currentFrame = 1;
	}
}
