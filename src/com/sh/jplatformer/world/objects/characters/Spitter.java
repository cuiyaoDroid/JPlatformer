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

public class Spitter extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ROAMING  = 0;
	private static final int MODE_SPITTING = 1;
	private int curDir;
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_spitter1,
	                                                        Resources.WORLD.sound_character_spitter2,
	                                                        Resources.WORLD.sound_character_spitter3 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Spitter( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Spitter";
		movementSpeed   = 5f;
		bounds.width    = 60f;
		bounds.height   = 60f;
		jumpHeight      = 0f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
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
		
		// Init first jump
		//================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getInt( 3500, 5000 );
		}
		
		// Move underneath platform
		//=========================
		if ( map.isBlocked( bounds.x + bounds.width / 2,
		                    bounds.y + bounds.height + 1f ) == true )
		{
			// Check timer + update mode
			//==========================
			if ( routineTimer < System.currentTimeMillis() )
			{
				// Turn to spitting mode
				//======================
				if ( routineMode == MODE_ROAMING )
				{
					// Spit acid
					//==========
					routineMode  = MODE_SPITTING;
					routineTimer = System.currentTimeMillis() + 200;
					worldController.addMapObject( new AcidDrop( worldController ),
					                              bounds.x + bounds.width / 2,
					                              bounds.y + bounds.height / 2,
					                              true );
					// Play sound
					//===========
					WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ], this );
				}
				
				// Turn to roaming mode
				//=====================
				else if ( routineMode == MODE_SPITTING )
				{
					routineMode  = MODE_ROAMING;
					routineTimer = System.currentTimeMillis() + Randomizer.getInt( 3500, 5000 );
				}				
			}
			
			// Move east
			//==========
			if ( curDir == DIR_EAST )
			{
				if ( !map.isBlocked( bounds.x + bounds.width, bounds.y + bounds.height + Map.CELL_SIZE / 2 ) ||
				     this.moveEast() == false )
				{
					curDir = DIR_WEST;
				}
			}
			
			// Move west
			//==========
			else if ( curDir == DIR_WEST )
			{
				if ( !map.isBlocked( bounds.x, bounds.y + bounds.height + Map.CELL_SIZE / 2 ) ||
				     this.moveWest() == false )
				{
					curDir = DIR_EAST;
				}
			}
		}
		
		// Move up
		//========
		this.jump( 15f, true );
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
		if ( direction   == DIR_EAST )      currentFrame = 0;
		if ( direction   == DIR_WEST )      currentFrame = 1;
		if ( routineMode == MODE_SPITTING ) currentFrame = 2;
	}
}
