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

public class Gland extends MapObject
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
	
	// Audio
	//======
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_gland1,
	                                                        Resources.WORLD.sound_character_gland2,
	                                                        Resources.WORLD.sound_character_gland3,
	                                                        Resources.WORLD.sound_character_gland4 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Gland( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Gland";
		movementSpeed   = 5f;
		bounds.width    = 32f;
		bounds.height   = 31f;
		jumpHeight      = 0f;
		isBlockingSpace = false;
		
		// Alignment
		//==========
		horizontalAlignment = ALIGN_CENTER;
		verticalAlignment = ALIGN_TOP;
		
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
		
		// Init first acid spit
		//=====================
		if ( routineTimer == 0L )
		{
			routineTimer = System.currentTimeMillis() + Randomizer.getInt( 4000, 5500 );
		}
		
		// Check timer + spit acid
		//========================
		if ( map.isBlocked( bounds.x + bounds.width / 2,
		                    bounds.y + bounds.height + 1f ) == true &&
		                    routineTimer < System.currentTimeMillis() )
		{
			// Add acid drop
			//==============
			animationState = ANIMATION_CONTRACT;
			routineTimer   = System.currentTimeMillis() + Randomizer.getInt( 4000, 5500 );
			worldController.addMapObject( new AcidDrop( worldController ),
			                              bounds.x + bounds.width / 2,
			                              bounds.y + bounds.height / 2,
			                              true );
			
			// Play sound
			//===========
			WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ], this );
		}
		
		// Move up when not docked
		//========================
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
		// When contracting
		//=================
		if ( animationState == ANIMATION_CONTRACT )
		{
			if ( frameTimer < System.currentTimeMillis() )
			{
				// Update timer + frame
				//=====================
				frameTimer = System.currentTimeMillis() + 20;
				currentFrame++;
				
				// Limit frames
				//=============
				if ( currentFrame < 0 ) currentFrame = 0;
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
				frameTimer = System.currentTimeMillis() + 20;
				currentFrame--;
				
				// Limit frames
				//=============
				if ( currentFrame > 7 ) currentFrame = 7;
				if ( currentFrame < 0 ) animationState = ANIMATION_NONE;
			}
		}
		
		// When moving on platform
		//========================
		if ( animationState == ANIMATION_NONE )
		{
			currentFrame = 0;
		}
	}
}
