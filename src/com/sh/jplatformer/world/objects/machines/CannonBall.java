package com.sh.jplatformer.world.objects.machines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * A {@code MapObject} machine.
 * @author Stefan Hösemann
 */

public class CannonBall extends MapObject
{
	// Properties
	//===========
	private static final long serialVersionUID = 1L;
	
	// Routine
	//========
	public static final int MODE_RIGHT = 0;
	public static final int MODE_LEFT  = 1;
	public static final int MODE_DOWN  = 2;
	public static final int MODE_UP    = 3;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapObject}.
	 * @param newWorldController the {@code WorldController} to interact with.
	 * @param mode the {@code routineMode} for this object.
	 */
	public CannonBall( WorldController newWorldController, int mode )
	{
		// Super constructor
		//==================
		super( newWorldController );
		
		// Properties
		//===========
		name            = "Cannon Ball";
		movementSpeed   = 12f;
		bounds.width    = 32f;
		bounds.height   = 32f;
		jumpHeight      = 0f;
		isBlockingSpace = false;
		ignoreGravity   = true;
		routineMode     = mode;
		
		// Init frames
		//============
		this.initFrames();
	}
	
	// draw
	//=====
	@Override
	public void draw( SpriteBatch batch )
	{
		// Update alpha
		//=============
		updateAlpha();
		
		// Frame setup
		//============
		frames[ currentFrame ].setPosition( bounds.x + bounds.width / 2 - frameSize / 2,
		                                    bounds.y - bounds.height / 2 );
		frames[ currentFrame ].setColor   ( 1f, 1f, 1f, alpha );
		frames[ currentFrame ].draw( batch );
	}
	
	// act
	//====
	@Override
	public void act()
	{
		// Move in direction
		//==================
		boolean moved = false;
		float speed = movementSpeed * WorldController.worldDelta * 10f;
		
		if ( routineMode == MODE_RIGHT ) moved = this.move( +speed, 0 );
		if ( routineMode == MODE_LEFT  ) moved = this.move( -speed, 0 );
		if ( routineMode == MODE_UP    ) moved = this.move( 0, +speed );
		if ( routineMode == MODE_DOWN  ) moved = this.move( 0, -speed );
		
		// Destroy if blocked
		//===================
		if ( moved == false )
		{
			this.setAlive( false );
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
		currentFrame = routineMode;
	}
		
	// setPosition
	//============
	@Override
	public void setPosition( float x, float y, boolean center )
	{
		if ( center == true )
		{
			bounds.x = x - bounds.width / 2f;
			bounds.y = y - frameSize / 2f + 1 + bounds.height / 2;
		}
		else
		{
			bounds.x = x;
			bounds.y = y;
		}
	}
}