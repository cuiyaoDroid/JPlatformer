package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class AngryBall extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ROLLING = 0;
	private static final int MODE_JUMPING = 1;
	private int curDir;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public AngryBall( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Angry Ball";
		movementSpeed   = 20f;
		bounds.width    = 52f;
		bounds.height   = 52f;
		jumpHeight      = 160f;
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
		if ( routineMode == MODE_ROLLING )
		{
			// Get map
			//========
			Map map = worldController.getMap();
			
			// Update timer + update mode
			//===========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_JUMPING;
				routineTimer = System.currentTimeMillis() + 4000L;
			}
			
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
			if ( curDir == DIR_WEST )
			{
				if ( !map.isBlocked( bounds.x, bounds.y - Map.CELL_SIZE / 2 ) ||
				     this.moveWest() == false )
				{
					curDir = DIR_EAST;
				}
			}
		}
		
		// When jumping
		//=============
		if ( routineMode == MODE_JUMPING )
		{
			// Update timer + update mode
			//===========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_ROLLING;
				routineTimer = System.currentTimeMillis() + Randomizer.getLong( 3000, 5000 );
			}
			
			// Perform jump
			//=============
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
		// When moving
		//============
		if ( state == STATE_RUNNING )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 20;
				
				if ( direction == DIR_EAST ) currentFrame++;
				if ( direction == DIR_WEST ) currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame < 2 ) currentFrame = 7;
				if ( currentFrame > 7 ) currentFrame = 2;
			}
		}

		// When expanding
		//===============
		if ( state == STATE_JUMPING ) currentFrame = 0;
		if ( state == STATE_FALLING ) currentFrame = 1;
	}
}
