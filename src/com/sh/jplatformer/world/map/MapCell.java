package com.sh.jplatformer.world.map;

import java.io.Serializable;

/**
 * The {@code MapCell} class provides a data model for {@code Map} cells.
 * @author Stefan Hösemann
 */

public class MapCell implements Serializable
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	public int tileSetId;
	public int tileId;
	public int x;
	public int y;
	
	// Constructor
	//============
	/**
	 * Constructs a new empty {@code MapCell}.
	 */
	public MapCell()
	{
		this.clear();
	}
	
	// clear
	//======
	/**
	 * Sets all values {@code 0}.
	 */
	public void clear()
	{
		tileSetId = -1;
		tileId = 0;
		x = 0;
		y = 0;
	}
}