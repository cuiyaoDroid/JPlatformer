package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.stages.MainMenuStage;

/**
 * The {@code MenuScreen} class processes all logic and rendering components for the main menu.
 * @author Stefan H�semann
 */

public class MenuScreen implements Screen
{
	// UI
	//===
	private MainMenuStage mainMenuStage;

	// render
	//=======
	@Override
	public void render( float delta )
	{	
		// Main menu stage
		//================
		mainMenuStage.act();
		mainMenuStage.draw();
		
		// Play menu music
		//================
		if ( !Resources.UI.sound_game_menu.isPlaying() )
		{
			Resources.UI.sound_game_menu.play();
		}
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		mainMenuStage.resize( width, height );
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
		// UI
		//===
		mainMenuStage = new MainMenuStage();
		
		// User input
		//===========
		Gdx.input.setInputProcessor( mainMenuStage );
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
			mainMenuStage.dispose();
			
			Resources.UI.sound_game_menu.stop();
		}
		catch ( Exception e ) {}
	}
}