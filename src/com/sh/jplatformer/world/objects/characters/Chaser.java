package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.math.Rectangle;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Chaser extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ROAMING = 0;
	private static final int MODE_CHASING = 1;
	private static final int MODE_HALTING = 2;
	private int curDir;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Chaser( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Chaser";
		movementSpeed   = 6f;
		bounds.width    = 54f;
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
		
		// Follow player
		//==============
		for ( MapObject o : surroundingObjects )
		{
			// Find player
			//============
			if ( o == worldController.getPlayer() && o.isOnGround() )
			{	
				// Check y-position
				//=================
				if ( o.getBounds().y >= bounds.y &&
				     o.getBounds().y < bounds.y + Map.CELL_SIZE )
				{
					// Player is west
					//===============
					if ( o.getBounds().x + o.getBounds().width < bounds.x )
					{		
						if ( map.isBlocked( bounds.x, bounds.y - Map.CELL_SIZE / 2 ) )
						{
							routineMode = MODE_CHASING;
							this.moveWest();
						}
						return;
					}
					
					// Player is east
					//===============
					if ( o.getBounds().x > bounds.x + bounds.width )
					{
						if ( map.isBlocked( bounds.x + bounds.width, bounds.y - Map.CELL_SIZE / 2 ) )
						{
							routineMode = MODE_CHASING;
							this.moveEast();
						}
						return;
					}
				}
			}
		}
		
		// Halt when player lost
		//======================
		if ( routineMode == MODE_CHASING )
		{
			routineMode = MODE_HALTING;
			routineTimer = System.currentTimeMillis() + 2000;
		}

		// Return to roaming
		//==================
		if ( routineMode == MODE_HALTING )
		{
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_ROAMING;
			}
		}
		
		// Roam on platform
		//=================
		if ( routineMode == MODE_ROAMING && isOnGround == true )
		{
			// Move east
			//==========
			if ( curDir == DIR_EAST )
			{
				MapCell cell = map.getCellAt( bounds.x + bounds.width, bounds.y );
				
				if ( this.moveEast() == false || !map.isBlocked( cell.x, cell.y - 1 ) )
				{
					curDir = DIR_WEST;
				}
			}
			
			// Move west
			//==========
			if ( curDir == DIR_WEST )
			{
				MapCell cell = map.getCellAt( bounds.x, bounds.y );
				
				if ( this.moveWest() == false || !map.isBlocked( cell.x, cell.y - 1 ) )
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
		// Movement frames
		//================
		if ( routineMode == MODE_ROAMING )
		{
			// When roaming
			//=============
			if ( direction == DIR_EAST ) currentFrame = 0;
			if ( direction == DIR_WEST ) currentFrame = 1;
		}
		else
		{
			// When chasing
			//=============
			if ( direction == DIR_EAST ) currentFrame = 2;
			if ( direction == DIR_WEST ) currentFrame = 3;
		}
		
		// When falling
		//=============
		if ( state == STATE_FALLING ) currentFrame = 4;
	}
	
	// getScanArea
	//============
	/**
	 * @return the rectangular area around this {@code MapObject} to scan for other objects.
	 */
	public Rectangle getScanArea()
	{
		int size = Map.CELL_SIZE * 6;
		
		scanArea.x      = bounds.x      - size / 2;
		scanArea.y      = bounds.y      - size / 2;
		scanArea.width  = bounds.width  + size;
		scanArea.height = bounds.height + size;
		
		return ( scanArea );
	}
}
