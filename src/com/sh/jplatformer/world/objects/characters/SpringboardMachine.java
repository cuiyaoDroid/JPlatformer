package com.sh.jplatformer.world.objects.characters;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class SpringboardMachine extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Animation
	//==========
	private static final int ANIMATION_NONE     = 0;
	private static final int ANIMATION_CONTRACT = 1;
	private static final int ANIMATION_EXPAND   = 2;
	private int animationState;
	
	// Routine
	//========
	private int curDir;
		
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public SpringboardMachine( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Springboard Machine";
		movementSpeed   = 6f;
		bounds.width    = 64;
		bounds.height   = 64f;
		jumpHeight      = 16f;
		isBlockingSpace = false;
		
		// Routine
		//========
		curDir = MapObject.DIR_EAST;
		animationState = ANIMATION_NONE;
		
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
		
		// Invoke jumps
		//=============
		for ( MapObject o : surroundingObjects )
		{
			// Check y-position
			//=================
			if ( o.getBounds().y >= bounds.y + bounds.height - frameSize / 2 &&
			     o.getBounds().y <= bounds.y + bounds.height + 4 )
			{
				// Check x-position
				//=================
				if ( o.getBounds().x + o.getBounds().width > bounds.x  &&
				     o.getBounds().x < bounds.x + bounds.width )
				{
					// Check state
					//============
					if ( o.getState() == STATE_FALLING )
					{
						// Invoke jump
						//============
						o.getBounds().y = bounds.y + bounds.height;
						o.jump( o.getJumpHeight() * 1.5f, true );
						animationState = ANIMATION_CONTRACT;
						
						// Play sound
						//===========
						WorldAudio.addSound( Resources.WORLD.sound_character_springboard, this );
					}
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
		// When contracting
		//=================
		if ( animationState == ANIMATION_CONTRACT )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 14;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame < 3 ) currentFrame = 3;
				if ( currentFrame > 7 )
				{
					currentFrame = 7;
					animationState = ANIMATION_EXPAND;
				}
			}
		}

		// When expanding
		//===============
		if ( animationState == ANIMATION_EXPAND )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 17;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 7;
				if ( currentFrame < 3 ) animationState = ANIMATION_NONE;
			}
		}
		
		// When moving on platform
		//========================
		if ( animationState == ANIMATION_NONE )
		{
			if ( direction == DIR_EAST )  currentFrame = 2;
			if ( direction == DIR_WEST )  currentFrame = 1;
			if ( state == STATE_FALLING ) currentFrame = 0;
		}
	}
}
