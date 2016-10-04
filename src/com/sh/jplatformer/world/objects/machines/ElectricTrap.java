package com.sh.jplatformer.world.objects.machines;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class ElectricTrap extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ENABLED  = 0;
	private static final int MODE_DISABLED = 1;
	
	// Audio
	//======
	private long audioTimer;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_machine_electricTrap1,
	                                                        Resources.WORLD.sound_machine_electricTrap2,
	                                                        Resources.WORLD.sound_machine_electricTrap3 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public ElectricTrap( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Electric Trap";
		movementSpeed    = 0f;
		bounds.width     = 60f;
		bounds.height    = 54f;
		jumpHeight       = 64f;
		isBlockingSpace  = false;
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
		// Init first energy
		//==================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + 4000L;
		}
		
		// Play sounds randomly
		//=====================
		if ( isPowerOn == true && routineMode == MODE_ENABLED )
		{
			if ( audioTimer < System.currentTimeMillis() )
			{
				audioTimer = System.currentTimeMillis() + Randomizer.getLong( 2000l, 8000L );
				
				WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ] , this );
			}
		}
		
		// Switch routine mode
		//====================
		if ( isPowerOn == true && routineTimer < System.currentTimeMillis() )
		{
			// Reset timer
			//============
			routineTimer = System.currentTimeMillis() + 4000L;
			
			// Set routine mode
			//=================
			if ( routineMode == MODE_ENABLED  )
			{
				routineMode = MODE_DISABLED;
			}
			else
			{
				routineMode = MODE_ENABLED;
			}
		}
		
		// Turn off
		//=========
		if ( isPowerOn == false )
		{
			routineMode = MODE_DISABLED;
		}
	}
	
	// onPlayerCollision
	//==================
	@Override
	public void onPlayerCollision()
	{
		if ( routineMode == MODE_ENABLED )
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
		if ( routineMode == MODE_ENABLED )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50L;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 1;
			}
		}
		
		// When off
		//=========
		if ( routineMode == MODE_DISABLED )
		{
			currentFrame = 0;
		}
	}
}