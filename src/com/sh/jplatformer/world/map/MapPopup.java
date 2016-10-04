package com.sh.jplatformer.world.map;

import java.io.Serializable;
import com.badlogic.gdx.math.Vector2;

/**
 * The {@code MapPopup} class represents messages that pop up in the game world.
 * @author Stefan Hösemann
 */

public class MapPopup implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	
	// Properties
	//===========
	private String text;
	private Vector2 position;
	private float alpha;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapPopup}.
	 * @param newText the displayed text.
	 * @param x the x-position (center).
	 * @param y the y-position (center).
	 */
	public MapPopup( String newText, float x, float y )
	{
		text = newText;
		position = new Vector2( x, y );
		alpha = 1f;
	}
	
	// getText
	//========
	public String getText()
	{
		return ( text );
	}
	
	// setPosition
	//============
	/**
	 * @param x the x-position (center).
	 * @param y the y-position (center).
	 */
	public void setPosition( float x, float y )
	{
		position.x = x;
		position.y = y;
	}
	
	// getPosition
	//============
	/**
	 * @return the current position of the popup message. The x-position represents the center, the
	 * y-position represents the bottom of the message.
	 */
	public Vector2 getPosition()
	{
		return ( position );
	}
	
	// setAlpha
	//=========
	/**
	 * @param value the alpha value (opacity) of this {@code MapPopup}.
	 */
	public void setAlpha( float value )
	{
		alpha = value;
	}
	
	// getAlpha
	//=========
	/**
	 * @return the alpha value (opacity) of this {@code MapPopup}.
	 */
	public float getAlpha()
	{
		return ( alpha );
	}
}
