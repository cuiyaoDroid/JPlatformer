package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Skater extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ROAMING = 0;
	private static final int MODE_RESTING = 1;
	private int curDir;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Skater( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Skater";
		movementSpeed   = 10f;
		bounds.width    = 48f;
		bounds.height   = 64f;
		jumpHeight      = 64f;
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
		// Move on platform
		//=================
		if ( routineMode == MODE_ROAMING )
		{
			// Get map
			//========
			Map map = worldController.getMap();
			
			// Check timer + update mode
			//==========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_RESTING;
				routineTimer = System.currentTimeMillis() + 4000;
			}
			
			// Move when on ground
			//====================
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
		else
		{
			// Check timer + update rest mode
			//===============================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_ROAMING;
				routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4000, 6000 );
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
		if ( state == STATE_RUNNING )
		{
			// When moving
			//============
			if ( direction == DIR_EAST ) currentFrame = 0;
			if ( direction == DIR_WEST ) currentFrame = 1;
		}
		else
		{
			// When resting
			//=============
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 3, 5 );
				frameTimer = System.currentTimeMillis() + 300;
			}
		}
	}
}
