package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Switch extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Animation
	//==========
	private static final int ANIMATION_NONE     = 0;
	private static final int ANIMATION_INCREASE = 1;
	private static final int ANIMATION_DECREASE = 2;
	private int animationState;
	private int maxFrame;
	private int minFrame;

	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Switch( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Switch";
		bounds.width     = 42f;
		bounds.height    = 64f;
		jumpHeight       = 64f;
		isBlockingSpace  = false;
		isPowerSupported = true;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// use
	//====
	@Override
	public boolean use()
	{
		// Switch related power states
		//============================
		for ( MapObject o : worldController.getMapObjects() )
		{
			if ( o.getPowerId() == this.getPowerId() )
			{
				o.setPowerOn( !o.isPowerOn() );
			}
		}
		
		// Popup message
		//==============
		if ( isPowerOn == true )
		{
			WorldAudio.addSound( Resources.WORLD.sound_machine_switch_on, this );
			
			worldController.addPopup( Lang.txt( "game_on" ),
			                          bounds.x + bounds.width  / 2f,
			                          bounds.y + bounds.height / 2f );
		}
		else
		{
			WorldAudio.addSound( Resources.WORLD.sound_machine_switch_off, this );
			
			worldController.addPopup( Lang.txt( "game_off" ),
			                          bounds.x + bounds.width  / 2f,
			                          bounds.y + bounds.height / 2f );			
		}
		return ( true );
	}
	
	// updateFrame
	//============
	@Override
	protected void updateFrame()
	{
		// Increase glow
		//==============
		if ( animationState == ANIMATION_INCREASE )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50;
				currentFrame++;
				
				// Max / min frames
				//=================
				if ( isPowerOn == false )
				{
					maxFrame = 3;
					minFrame = 0;
				}
				else
				{
					maxFrame = 7;
					minFrame = 4;
				}
				
				// Limit frames
				//=============
				if ( currentFrame < minFrame ) currentFrame = minFrame;
				if ( currentFrame > maxFrame )
				{
					currentFrame = maxFrame;
					animationState = ANIMATION_DECREASE;
				}
			}
		}

		// Decrease glow
		//==============
		if ( animationState == ANIMATION_DECREASE )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 50;
				currentFrame--;
				
				// Max / min frames
				//=================
				if ( isPowerOn == false )
				{
					maxFrame = 3;
					minFrame = 0;
				}
				else
				{
					maxFrame = 7;
					minFrame = 4;
				}
				
				// Limit frames
				//=============
				if ( currentFrame > maxFrame ) currentFrame = maxFrame;
				if ( currentFrame < minFrame )
				{
					currentFrame = minFrame;
					frameTimer = System.currentTimeMillis() + 1000;
					animationState = ANIMATION_NONE;
				}
			}
		}
		
		// Initiate glow animation
		//========================
		if ( animationState == ANIMATION_NONE )
		{
			// Set frames
			//===========
			if ( isPowerOn == false ) currentFrame = 0;
			if ( isPowerOn == true )  currentFrame = 4;
			
			// Init glow
			//==========
			if ( frameTimer < System.currentTimeMillis() )
			{
				animationState = ANIMATION_INCREASE;
			}
		}
	}
}