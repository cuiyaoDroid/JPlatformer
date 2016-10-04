package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.input.EditorInput;
import com.sh.jplatformer.ui.stages.EditorStage;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldRenderer;

/**
 * The {@code EditorScreen} renders the editor mode.
 * @author Stefan Hösemann
 */

public class EditorScreen implements Screen
{
	// World properties
	//=================
	private WorldController worldController; 
	private WorldRenderer worldRenderer;
	
	// UI / Input
	//===========
	private EditorStage editorStage;
	private EditorInput editorInput;
	private InputMultiplexer inputMultiplexer;
	
	// render
	//=======
	@Override
	public void render( float delta )
	{
		// Update world
		//=============
		worldController.update();

		// Get batch
		//==========
		SpriteBatch batch = JPlatformerGame.get().getBatch();
		
		// Render game
		//============
		batch.begin();
		{
			batch.setProjectionMatrix( worldController.getWorldCamera().combined );
			worldRenderer.render( batch );
		}
		batch.end();
		
		// Play world audio
		//=================
		if ( !JPlatformerGame.get().isPaused() )
		{
			WorldAudio.play( worldController );
		}
		else
		{
			WorldAudio.pause();
		}
		
		// Editor stage
		//=============
		editorInput.update();
		editorStage.act();
		editorStage.draw();
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		worldRenderer.resize( width, height );
		editorStage  .resize( width, height );
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
		// World controller
		//=================
		worldController = new WorldController();

		// Rendering / UI
		//===============
		worldRenderer = new WorldRenderer( worldController );
		editorStage   = new EditorStage( worldController );
		
		// User input
		//===========
		editorInput      = new EditorInput( editorStage );
		inputMultiplexer = new InputMultiplexer();
		
		// Multiplexer setup
		//==================
		inputMultiplexer.addProcessor( editorStage );
		inputMultiplexer.addProcessor( editorInput );
		
		Gdx.input.setInputProcessor( inputMultiplexer );
		
		// Prepare editor
		//===============
		JPlatformerGame.get().setPaused( true );
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
		try
		{
			// Pause audio
			//============
			WorldAudio.pause();
			
			// Environment
			//============
			Resources.WORLD.loadBackground( null );
			Resources.WORLD.loadForeground( null );
			Resources.WORLD.loadEnvironmentSound( null );
			
			// UI
			//===
			editorStage.dispose();
			inputMultiplexer.clear();
		}
		catch ( Exception e ) {}
	}
}