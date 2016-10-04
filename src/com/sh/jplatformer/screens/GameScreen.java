package com.sh.jplatformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.input.GameInput;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.world.WorldAudio;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldFile;
import com.sh.jplatformer.world.WorldRenderer;
import com.sh.jplatformer.world.map.Map;

/**
 * The {@code GameScreen} renders the play mode.
 * @author Stefan Hösemann
 */

public class GameScreen implements Screen
{
	// World properties
	//=================
	private WorldController worldController; 
	private WorldRenderer worldRenderer;
	
	// UI / Input
	//===========
	private GameStage gameStage;
	private GameInput gameInput;
	private InputMultiplexer inputMultiplexer;
	
	// render
	//=======
	@Override
	public void render( float delta )
	{
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
		
		// Update game
		//============
		this.updateGame();
		
		// Game stage
		//===========
		gameStage.act();
		gameStage.draw();
	}
	
	// startGame
	//==========
	private void startGame( String worldPath )
	{
		// Read world file
		//================
		if ( WorldFile.loadWorld( worldPath, worldController ) == true )
		{
			// Start game
			//===========
			worldController.setLive( true );
			worldController.resetTimer();
			JPlatformerGame.get().setPaused( false );
		}
		else
		{
			// Return to main menu
			//====================
			JPlatformerGame.get().callScreen( JPlatformerGame.get().menuScreen );
		}
	}
	
	// updateGame
	//===========
	/**
	 * Updates the game and processes the world states.
	 */
	private void updateGame()
	{
		// Temporary values
		//=================
		ScreenTransition transition = JPlatformerGame.get().getScreenTransition();
		
		// Update components
		//==================
		worldController.update();
		gameInput.update();
		
		// Restart game
		//=============
		if ( worldController.getWorldState() == WorldController.STATE_RESTART )
		{
			// Init fade out
			//==============
			if ( transition.getState() == ScreenTransition.STATE_NONE )
			{
				transition.init( null );
			}
			
			// Restart game
			//=============
			if ( transition.getState() == ScreenTransition.STATE_SWITCH )
			{
				gameStage.restartGame();
			}
		}
		
		// Records menu
		//=============
		if ( worldController.getWorldState() == WorldController.STATE_VIEW_STATS )
		{
			if ( gameStage.isRecordsMenuVisible() == false )
			{
				gameStage.callRecordsMenu();
				
				Resources.UI.sound_game_win.play();
			}
		}
		
		// Save and return
		//================
		if ( worldController.getWorldState() == WorldController.STATE_BACK_TO_MENU )
		{
			// Init fade out
			//==============
			if ( transition.getState() == ScreenTransition.STATE_NONE )
			{
				transition.init( null );
			}
			
			// End game
			//=========
			if ( transition.getState() == ScreenTransition.STATE_SWITCH )
			{
				// Temporary values
				//=================
				boolean newRecords = false;
				int highScore      = worldController.getMap().getHighScore();
				long bestTime      = worldController.getMap().getBestTime();		
				long currentTime   = worldController.getElapsedTime();
				
				// Check high score
				//=================
				if ( worldController.getScore() > highScore )
				{
					newRecords = true;
					highScore = worldController.getScore();
				}
				
				// Calculate time if countdown
				//============================
				if ( worldController.getMap().getCountdownTime() != Map.COUNTDOWN_DISABLED )
				{
					currentTime = worldController.getMap().getCountdownTime() - currentTime;
				}
				
				// Check best time
				//================
				if ( currentTime < bestTime || bestTime <= 0L )
				{
					newRecords = true;
					bestTime = currentTime;
				}
				
				// Save records
				//=============
				if ( newRecords == true )
				{
					WorldFile.reloadWorld();
					
					gameStage.getWorldController().getMap().setHighscore( highScore );
					gameStage.getWorldController().getMap().setBestTime ( bestTime );
					
					WorldFile.overwriteWorld();
				}
				
				// Return to menu
				//===============
				JPlatformerGame.get().setScreen( JPlatformerGame.get().menuScreen );
			}
		}
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		worldRenderer.resize( width, height );
		gameStage    .resize( width, height );
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
		gameStage     = new GameStage( worldController );
		
		// User input
		//===========
		gameInput        = new GameInput( gameStage );
		inputMultiplexer = new InputMultiplexer();
		
		// Multiplexer setup
		//==================
		inputMultiplexer.addProcessor( gameStage );
		inputMultiplexer.addProcessor( gameInput );
		
		Gdx.input.setInputProcessor( inputMultiplexer );
		
		// Start game
		//===========
		this.startGame( Config.get().tmp_worldPath );
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
			gameStage.dispose();
		}
		catch ( Exception e ) {}
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
}
