package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Platform1 extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	private static final int MODE_ON = 0;
	private static final int MODE_TRANSITION = 1;
	private static final int MODE_OFF = 2;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Platform1( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Platform #1";
		bounds.width     = 64f;
		bounds.height    = 64f;
		jumpHeight       = 64f;
		ignoreGravity    = true;
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
		// Act when enabled
		//=================
		if ( isPowerOn == true )
		{
			// Update modes
			//=============
			if ( routineTimer < System.currentTimeMillis() )
			{
				switch ( routineMode )
				{
					case MODE_OFF:
					{
						// Turn to "on"
						//=============
						routineMode = MODE_ON;
						routineTimer = System.currentTimeMillis() + 5000L;
						break;
					}
					case MODE_ON:
					{
						// Turn to "transition"
						//=====================
						routineMode = MODE_TRANSITION;
						routineTimer = System.currentTimeMillis() + 3000L;
						break;
					}
					case MODE_TRANSITION:
					{
						// Turn to "off"
						//==============
						routineMode = MODE_OFF;
						routineTimer = System.currentTimeMillis() + 5000L;
						break;
					}
				}
			}
		}
		else
		{
			routineMode = MODE_OFF;
		}
		
		// Update space blocking
		//======================
		isBlockingSpace = !( routineMode == MODE_OFF );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateAlpha()
	{
		// When "on"
		//==========
		if ( routineMode == MODE_ON )
		{
			alpha += WorldController.worldDelta * 2f;
			if ( alpha > 1f ) alpha = 1f;
		}
		
		// When "transition"
		//==================
		if ( routineMode == MODE_TRANSITION )
		{
			alpha -= WorldController.worldDelta * 1f;
			if ( alpha < 0.75f ) alpha = 0.75f;
		}
		
		// When "off"
		//===========
		if ( routineMode == MODE_OFF )
		{
			alpha -= WorldController.worldDelta * 1f;
			if ( alpha < 0.25f ) alpha = 0.25f;
		}
		
		// When no power
		//==============
		if ( isPowerOn == false )
		{
			alpha = 0.04f;
		}
	}
}