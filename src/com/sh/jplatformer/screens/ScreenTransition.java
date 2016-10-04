package com.sh.jplatformer.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * The {@code ScreenTransition} represents a fade in/fade out transition effect for {@code Screens}.
 * The {@code ScreenTransition} object should be located in the {@code Game} class managing all 
 * game screens. Updates and renderings are typically called and performed in the {@code render}
 * method of the game. A new screen is requested by calling the {@code init} method. A manual call
 * of {@code setScreen} of the {@code Game} class is
 * not necessary.
 * 
 * @author Stefan Hösemann
 */

public class ScreenTransition
{
	// States
	//=======
	public static final int STATE_NONE     = 0;
	public static final int STATE_FADE_OUT = 1;
	public static final int STATE_SWITCH   = 2;
	public static final int STATE_FADE_IN  = 3;
	
	// Properties
	//===========
	private int state;
	private Game game;
	private Screen nextScreen;
	
	// Rendering
	//==========
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private float alpha;
	private int frameCounter;
	
	// Constructor
	//============
	/**
	 * @param newGame the {@code Game} object to set {@code Screens}.
	 */
	public ScreenTransition( Game newGame )
	{
		// Properties
		//===========
		game = newGame;
		
		// Rendering
		//==========
		renderer = new ShapeRenderer();
		camera = new OrthographicCamera();
	}
	
	// update
	//=======
	/**
	 * Updates the transition effect and sets the new screen.
	 */
	public void update()
	{
		// Fade out
		//=========
		if ( state == STATE_FADE_OUT )
		{
			// Increase alpha
			//===============
			alpha += Gdx.graphics.getDeltaTime() * 3f;
			
			// Update state
			//=============
			if ( alpha >= 1f )
			{
				state = STATE_SWITCH;
			}
		}
		
		// Switch screen
		//==============
		else if ( state == STATE_SWITCH )
		{
			// Switch screen
			//==============
			if ( nextScreen != null )
			{
				game.setScreen( nextScreen );
				nextScreen = null;
			}
			state = STATE_FADE_IN;
		}
		
		// Fade in
		//========
		else if ( state == STATE_FADE_IN )
		{
			// Fade in
			//========
			if ( frameCounter >= 2 )
			{
				// Increase alpha
				//===============
				alpha -= Gdx.graphics.getDeltaTime() * 3f;
				
				// Update state
				//=============
				if ( alpha <= 0f )
				{
					state = STATE_NONE;
				}
			}
			else
			{
				// Update frame counter
				//=====================
				frameCounter++;
			}
		}
	}
	
	// draw
	//=====
	/**
	 * Draws the fade effect using a {@code ShapeRenderer}.
	 */
	public void draw()
	{
		// Draw if visible
		//================
		if ( alpha >= 0f )
		{
			// Enable blending
			//================
			Gdx.gl.glEnable( GL20.GL_BLEND );

			// Render shape
			//=============
			renderer.begin( ShapeType.Filled );
			renderer.setColor( 0f, 0f, 0f, alpha );
			renderer.rect( 0, 0, camera.viewportWidth, camera.viewportHeight );
			renderer.end();
			
			// Disable blending
			//=================
			Gdx.gl.glDisable( GL20.GL_BLEND );
		}
	}
	
	// init
	//=====
	/**
	 * Initializes a new screen transition.
	 * @param screen the next {@code Screen}.
	 */
	public void init( Screen screen )
	{	
		if ( state == STATE_NONE )
		{
			state        = STATE_FADE_OUT;
			nextScreen   = screen;
			frameCounter = 0;
			alpha        = 0f;
		}
	}
	
	// getState
	//=========
	public int getState()
	{
		return ( state );
	}
	
	// resize
	//=======
	public void resize( int width, int height )
	{
		// Update camera
		//==============
		camera.position.x     = width  / 2f;
		camera.position.y     = height / 2f;
		camera.viewportWidth  = width;
		camera.viewportHeight = height;
		camera.update();
		
		// Update renderer
		//================
		renderer.setProjectionMatrix( camera.combined );
	}
}