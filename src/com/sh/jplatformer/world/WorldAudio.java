package com.sh.jplatformer.world;

import java.util.ArrayList;
import com.badlogic.gdx.audio.Sound;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code WorldAudio} class represents the sound output of the game world. It offers a variety
 * of methods to play environment and objects sounds depending on their location.
 * @author Stefan Hösemann
 */

public class WorldAudio
{
	// Properties
	//===========
	private static ArrayList<WorldSound> objectSounds  = new ArrayList<WorldSound>();
	private static boolean playCollectSound;
	
	// play
	//=====
	/**
	 * Plays all world sounds.
	 * @param worldController the world to set sound to.
	 */
	public static void play( WorldController worldController )
	{
		playEnvironmentSound( worldController );
		playWaterSound( worldController );
		playObjectSounds();
	}
	
	// playEnvironmentSound
	//=====================
	private static void playEnvironmentSound( WorldController worldController )
	{
		// Load environment sound
		//=======================
		Resources.WORLD.loadEnvironmentSound( worldController.getMap().getEnvironmentSoundFile() );
		
		// Play environment sound
		//=======================
		if ( Resources.WORLD.sound_environment != null )
		{
			if ( Resources.WORLD.sound_environment.isPlaying() == false )
			{
				Resources.WORLD.sound_environment.play();
			}
		}
	}
	
	// playWaterSound
	//===============
	private static void playWaterSound( WorldController worldController )
	{
		// Temporary values
		//=================
		WorldCamera camera = worldController.getWorldCamera();
		float waterHeight  = worldController.getMap().getWaterHeight();
		float cameraY      = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
		float volume       = ( 100f - ( cameraY - waterHeight ) ) / 100f;
		
		// Limit volume
		//=============
		if ( volume      > 1.0f ) volume = 1.0f;
		if ( volume      < 0.0f ) volume = 0.0f;
		if ( waterHeight < 1.0f ) volume = 0.0f;
		
		// Set volume
		//===========
		Resources.WORLD.sound_water.setVolume( volume );
		
		// Play water sound
		//=================
		if ( Resources.WORLD.sound_water.isPlaying() == false  )
		{
			Resources.WORLD.sound_water.play();
		}
	}
	
	// playObjectSounds
	//=================
	private static void playObjectSounds()
	{
		// Play all added sounds
		//======================
		for ( WorldSound s : objectSounds )
		{
			// Calculate values
			//=================
			float p = calculatePan( s.mapObject );
			float v = calculateVolume( s.mapObject );
			
			// Play sound
			//===========
			if ( v > 0f )
			{
				long id = s.sound.play();
				
				s.sound.setPan( id, p, v );
			}
		}
		
		// Clear sound array
		//==================
		objectSounds.clear();
		
		// Play collect sound
		//===================
		if ( playCollectSound )
		{
			Resources.WORLD.sound_item_collect.play();
			playCollectSound = false;
		}
	}
	
	// pause
	//======
	/**
	 * Pauses all sounds.
	 */
	public static void pause()
	{
		// Pause environment sound
		//========================
		if ( Resources.WORLD.sound_environment != null )
		{
			Resources.WORLD.sound_environment.pause();
		}
		
		// Pause water sound
		//==================
		Resources.WORLD.sound_water.pause();
	}
	
	// addSound
	//=========
	/**
	 * @param soundFile the sound to play.
	 * @param source the {@code MapObject} that is the sound source. It must have a valid
	 * {@code WorldController}.
	 */
	public static void addSound( Sound soundFile, MapObject source )
	{
		objectSounds.add( new WorldSound( soundFile, source ) );
	}
	
	// addCollectSound
	//================
	/**
	 * Initiates a single "collect" sound playback for the current frame.
	 */
	public static void addCollectSound()
	{
		playCollectSound = true;
	}
	
	// calculateVolume
	//================
	private static float calculateVolume( MapObject source )
	{
		// Temporary values
		//=================
		float x            = source.getBounds().x + source.getBounds().width  / 2f;
		float y            = source.getBounds().y + source.getBounds().height / 2f;
		WorldCamera camera = source.getWorldController().getWorldCamera();
		
		// Calculate volumes
		//==================
		float vX = 1 - Math.abs( ( camera.position.x - x ) / ( camera.viewportWidth  / 2.0f * camera.zoom ) );
		float vY = 1 - Math.abs( ( camera.position.y - y ) / ( camera.viewportHeight / 1.5f * camera.zoom ) );
		
		if ( vX < 0f ) vX = 0f;
		if ( vY < 0f ) vY = 0f;
		
		// Mix and return volume
		//======================
		float volume = vX * vY * 2f * ( 1f / ( camera.zoom * camera.zoom ) );
		
		if ( volume > 1f ) volume = 1f;
		if ( volume < 0f ) volume = 0f;

		return ( volume );
	}
	
	// calculatePan
	//=============
	private static float calculatePan( MapObject source )
	{
		// Temporary values
		//=================
		float x            = source.getBounds().x + source.getBounds().width / 2f;
		WorldCamera camera = source.getWorldController().getWorldCamera();
		
		// calculatePan
		//=============
		return ( ( camera.position.x - x ) / ( camera.viewportWidth / 2f * camera.zoom ) * -1 );
	}
}

class WorldSound
{
	// Properties
	//===========
	public Sound sound;
	public MapObject mapObject;
	
	// Constructor
	//============
	public WorldSound( Sound soundFile, MapObject source )
	{
		sound = soundFile;
		mapObject = source;
	}
}
