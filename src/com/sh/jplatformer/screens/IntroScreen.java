package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;

/**
 * The {@code IntroScreen} plays a short sequence of images when the game starts and then call the
 * {@code MenuScreen}.
 * @author Stefan Hösemann
 */

public class IntroScreen implements Screen
{
	// Properties
	//===========
	private OrthographicCamera camera;
	private long introTimer;
	
	// render
	//=======
	@Override
	public void render( float delta )
	{
		// Draw black background
		//======================
		Gdx.gl20.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT  );
		
		// Draw SHS logo
		//==============
		Batch batch = JPlatformerGame.get().getBatch();
		batch.begin();
		{
			// Set projection matrix
			//======================
			batch.setProjectionMatrix( camera.combined );
			
			// Draw sprite
			//============
			Resources.UI.logo_shs.setX( -Resources.UI.logo_shs.getWidth()  / 2f );
			Resources.UI.logo_shs.setY( -Resources.UI.logo_shs.getHeight() / 2f );
			Resources.UI.logo_shs.draw( batch );
		}
		batch.end();
		
		// Switch to menu
		//===============
		if ( introTimer < System.currentTimeMillis() && introTimer != 0L )
		{
			introTimer = 0L;
			JPlatformerGame.get().callScreen( JPlatformerGame.get().menuScreen );
		}
	}
	
	// pause
	//======
	@Override
	public void pause()
	{
	}
	
	// resume
	//=======
	@Override
	public void resume()
	{
	}
	
	// show
	//=====
	@Override
	public void show()
	{
		// Timer setup
		//============
		introTimer = System.currentTimeMillis() + 3000L;
		
		// Camera setup
		//=============
		camera = new OrthographicCamera();
	}
	
	// hide
	//=====
	@Override
	public void hide()
	{
		this.dispose();
	}
	
	// dispose
	//========
	@Override
	public void dispose()
	{
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		// Update camera
		//==============
		camera.viewportWidth  = width;
		camera.viewportHeight = height;
		camera.update();
	}
}