package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
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

public class Ghost extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private int curDir;
	
	// Audio
	//======
	private long audioTimer;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_ghost1,
	                                                        Resources.WORLD.sound_character_ghost2,
	                                                        Resources.WORLD.sound_character_ghost3 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Ghost( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Ghost";
		movementSpeed   = 10f;
		bounds.width    = 56f;
		bounds.height   = 60f;
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
		
		// Play sounds randomly
		//=====================
		if ( audioTimer < System.currentTimeMillis() )
		{
			audioTimer = System.currentTimeMillis() + Randomizer.getLong( 7500, 15000 );
			
			WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ] , this );
		}
		
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
