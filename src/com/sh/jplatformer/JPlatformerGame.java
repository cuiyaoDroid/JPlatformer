package com.sh.jplatformer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.screens.EditorScreen;
import com.sh.jplatformer.screens.GameScreen;
import com.sh.jplatformer.screens.IntroScreen;
import com.sh.jplatformer.screens.MenuScreen;
import com.sh.jplatformer.screens.ScreenTransition;

/**
 * This is the main class of the game. This class is a singleton as only one instance of it is
 * needed throughout the application.
 * @author Stefan Hösemann
 */

public class JPlatformerGame extends Game
{
	// Game meta data
	//===============
	public static final String TITLE   = "JPlatformer";
	public static final String AUTHOR  = "Stefan Hösemann";
	public static final String VERSION = "1.01";
	public static final String YEARS   = "2016";
	
	// Properties
	//===========
	private static JPlatformerGame instance;
	private boolean paused;

	// Screens
	//========
	public final IntroScreen  introScreen  = new IntroScreen();
	public final MenuScreen   menuScreen   = new MenuScreen();
	public final EditorScreen editorScreen = new EditorScreen();
	public final GameScreen   gameScreen   = new GameScreen();
	
	// Rendering
	//==========
	private SpriteBatch batch;
	private ScreenTransition screenTransition;
	
	// get
	//====
	/**
	 * Creates a single instance of this class (if there is none yet) and returns it.
	 */
	public static JPlatformerGame get()
	{
		if ( instance == null )
		{
			instance = new JPlatformerGame();
		}
		return ( instance );
	}
	
	// render
	//=======
	@Override
	public void render()
	{
		// Clear screen
		//=============
		Gdx.gl20.glClearColor( 0.07f, 0.075f, 0.08f, 1f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT  );
		
		// Render screen
		//==============
		super.render();
		
		// Update + render transition
		//===========================
		screenTransition.update();
		screenTransition.draw();
	}
	
	// resize
	//=======
	@Override
	public void resize( int width, int height )
	{
		super.resize( width, height );
		screenTransition.resize( width, height );
	}
	
	// create
	//=======
	@Override
	public void create()
	{
		// Set title
		//==========
		Gdx.graphics.setTitle( TITLE + " v" + VERSION );
		
		// Load config
		//============
		Config.get().initDefault();
		Config.get().load();
		Config.get().applyGraphics();
		
		// Rendering
		//==========
		batch = new SpriteBatch();
		screenTransition = new ScreenTransition( this );
		
		// Set screen
		//===========
		callScreen( introScreen );
	}
	
	// dispose
	//========
	@Override
	public void dispose()
	{
		// Save settings
		//==============
		Config.get().save();
		
		// Dispose resources
		//==================
		menuScreen  .dispose();
		editorScreen.dispose();
		gameScreen  .dispose();
		
		batch.dispose();
		Resources.dispose();
	}
	
	// getBatch
	//=========
	public SpriteBatch getBatch()
	{
		return ( batch );
	}
	
	// getScreenTransition
	//====================
	public ScreenTransition getScreenTransition()
	{
		return ( screenTransition );
	}
	
	// callScreen
	//===========
	/**
	 * Initializes a screen switch with a fade in / fade out transition.
	 * @param nextScreen the {@code Screen} to set.
	 */
	public void callScreen( Screen nextScreen )
	{
		screenTransition.init( nextScreen );
	}
	
	// setPaused
	//==========
	public void setPaused( boolean value )
	{
		paused = value;
	}
	
	// isPaused
	//=========
	public boolean isPaused()
	{
		return ( paused );
	}
}