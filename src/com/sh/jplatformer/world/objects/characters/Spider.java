package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Spider extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_MOVING_UP    = 0;
	private static final int MODE_HANGING_ROOF = 1;
	private static final int MODE_FALLING      = 2;
	private static final int MODE_HANGING_DOWN = 3;
	private float fallOriginY;
	private float fallHeight;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Spider( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Spider";
		movementSpeed   = 10f;
		bounds.width    = 42f;
		bounds.height   = 60f;
		jumpHeight      = 0f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
		// Routine
		//========
		fallHeight = Map.CELL_SIZE * 3;
		
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
		
		// While moving up
		//================
		if ( routineMode == MODE_MOVING_UP )
		{
			// Check roof collision
			//=====================
			if ( map.isBlocked( bounds.x + bounds.width / 2, bounds.y + bounds.height + 1f ) )
			{
				routineTimer = System.currentTimeMillis() + 5000;
				routineMode = MODE_HANGING_ROOF;
			}
			
			// Move up when not docked
			//========================
			ignoreGravity = false;
			this.jump( 15f, true );
		}
		
		// While hanging on roof tile
		//===========================
		if ( routineMode == MODE_HANGING_ROOF )
		{
			// Dock to roof
			//=============
			ignoreGravity = true;
			
			// Initiate fall
			//==============
			if ( routineTimer < System.currentTimeMillis() )
			{
				fallOriginY   = bounds.y;
				routineMode   = MODE_FALLING;
				ignoreGravity = false;
				
				WorldAudio.addSound( Resources.WORLD.sound_character_spider, this );
			}
		}
		
		// While falling
		//==============
		if ( routineMode == MODE_FALLING )
		{
			// Limit fall + Switch state
			//==========================
			if ( bounds.y <= fallOriginY - fallHeight || isOnGround )
			{
				// Update routine
				//===============
				routineTimer  = System.currentTimeMillis() + 5000;
				routineMode   = MODE_HANGING_DOWN;
				
				// Update position
				//================
				if ( bounds.y <= fallOriginY - fallHeight )
				{
					bounds.y = fallOriginY - fallHeight;
				}
			}
		}
		
		// While hanging
		//==============
		if ( routineMode == MODE_HANGING_DOWN )
		{
			// Defy gravity
			//=============
			ignoreGravity = true;
			
			// Initiate moving up
			//===================
			if ( routineTimer < System.currentTimeMillis() )
			{
				routineMode = MODE_MOVING_UP;
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
		// While on moving up
		//===================
		if ( routineMode == MODE_MOVING_UP || routineMode == MODE_HANGING_ROOF )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				frameTimer = System.currentTimeMillis() + 80;
				
				currentFrame++;
				if ( currentFrame > 4 )
				{
					currentFrame = 0;
				}
			}
		}
		
		// While hanging
		//==============
		else
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				currentFrame = Randomizer.getInt( 5, 7 );
				frameTimer = System.currentTimeMillis() + 300;
			}
		}
	}
}
