package com.sh.jplatformer.world.objects.machines;

import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class Springboard extends MapObject
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
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Springboard( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name             = "Springboard";
		bounds.width     = 64f;
		bounds.height    = 64f;
		jumpHeight       = 64f;
		isBlockingSpace  = false;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
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
						o.jump( o.getJumpHeight() * 2.5f, true );
						animationState = ANIMATION_CONTRACT;
						
						// Play sound
						//===========
						WorldAudio.addSound( Resources.WORLD.sound_machine_springboard, this );
					}
				}
			}
		}
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
				frameTimer = System.currentTimeMillis() + 14;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame < 0 ) currentFrame = 0;
				if ( currentFrame < 1 ) animationState = ANIMATION_NONE;
			}
		}
	}
}