package com.sh.jplatformer.world.objects.characters;

import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.util.Randomizer;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} character.
 * @author Stefan Hösemann
 */

public class Spikehead extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Audio
	//======
	private long audioTimer;
	private static transient Sound[] sounds = new Sound[] { Resources.WORLD.sound_character_spikehead1,
	                                                        Resources.WORLD.sound_character_spikehead2,
	                                                        Resources.WORLD.sound_character_spikehead3,
	                                                        Resources.WORLD.sound_character_spikehead4 };
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 */
	public Spikehead( WorldController newWorldController )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Spikehead";
		movementSpeed   = 10f;
		bounds.width    = 48f;
		bounds.height   = 54f;
		jumpHeight      = 96f;
		isBlockingSpace = false;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Play sounds randomly
		//=====================
		if ( audioTimer < System.currentTimeMillis() )
		{
			audioTimer = System.currentTimeMillis() + Randomizer.getLong( 5000, 10000 );
			
			WorldAudio.addSound( sounds[ Randomizer.getInt( 0, sounds.length - 1 ) ] , this );
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
		if ( frameTimer < System.currentTimeMillis() )
		{
			frameTimer = System.currentTimeMillis() + 75;
			currentFrame++;
			
			if ( currentFrame > 7 )
			{
				currentFrame = 0;
			}
		}
	}
}
