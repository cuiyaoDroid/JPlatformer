package com.sh.jplatformer.resources;

/**
 * The {@code Resources} class grants access to the particular external resources used in the game.
 * Resources are clustered in type-specific inner classes.
 * @author Stefan Hösemann
 */

public class Resources
{
	// Properties
	//===========
	public static final World WORLD = new World();
	public static final Ui UI = new Ui();
	
	// Constructor
	//============
	private Resources()
	{
	}
	
	// dispose
	//========
	/**
	 * Releases all resources.
	 */
	public static void dispose()
	{
		UI.dispose();
		WORLD.dispose();
	}
}