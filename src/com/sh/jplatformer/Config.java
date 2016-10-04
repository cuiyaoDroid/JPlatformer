package com.sh.jplatformer;

import com.badlogic.gdx.Gdx;
import com.sh.jplatformer.util.FileUtils;
import com.sh.jplatformer.util.Lang;
import java.io.*;
import java.util.Locale;
import java.util.Properties;

/**
 * The {@code Config} class stores various global variables, settings and configurations. It
 * provides methods to access, load, save and reset configuration values. This class is a singleton
 * as only one instance of it is needed throughout the application.
 * @author Stefan Hösemann
 */

public class Config
{
	// Properties
	//===========
	public static final String filePath = FileUtils.getRoot() + "resources/settings.ini";
	private static Config instance;
	
	// Settings
	//=========
	public Locale  locale;
	public boolean enableFullscreen;
	public boolean enableVSync;
	public boolean editor_showFps;
	public boolean editor_showPowerInfo;
	public String  tmp_worldPath;
	
	// getInstance
	//============
	/**
	 * Creates a single instance of this class (if there is none yet) and returns it.
	 */
	public static synchronized Config get()
	{
		if ( instance == null )
		{
			instance = new Config();
		}
		return ( instance );
	}
	
	// initDefault
	//============
	/**
	 * Initializes all settings with their default values.
	 */
	public void initDefault()
	{
		// Set values
		//===========
		locale               = Locale.getDefault();
		enableFullscreen     = true;
		enableVSync          = true;
		editor_showFps       = true;
		editor_showPowerInfo = true;
		
		// Language dir
		//=============
		Lang.get().init( FileUtils.getRoot() + "resources/lang", "lang" );
	}
	
	// save
	//=====
	/**
	 * Attempts to save all {@code Config} values to file.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean save()
	{
		// Create directory
		//=================
		FileUtils.createDir( filePath );
		
		// Write to file
		//==============
		try ( PrintStream out = new PrintStream( filePath ) )
		{
			// Write values
			//=============
			out.println( "# Language" );
			out.println( "language = " + locale.getLanguage() );
			out.println( "country = "  + locale.getCountry() );
			out.println();

			out.println( "# Graphics" );
			out.println( "enableFullscreen = " + enableFullscreen );
			out.println( "enableVSync = "      + enableVSync );
			out.println();

			out.println( "# Editor" );
			out.println( "editor_showFps = "       + editor_showFps );
			out.println( "editor_showPowerInfo = " + editor_showPowerInfo );
		}
		catch ( Exception e )
		{
			// Error occurred
			//===============
			System.err.println( "Error reading configuration file!" );
			return ( false );
		}
		return ( true );
	}
	
	// load
	//=====
	/**
	 * Attempts to load the {@code Config} from a user specific file. If an error occurred, a new
	 * {@code Config} file with the default settings is created.
	 * @return {@code true} if the operation was successful, {@code false} otherwise.
	 */
	public boolean load()
	{
		try ( BufferedReader in = new BufferedReader( new FileReader( filePath ) ) )
		{
			// Init properties
			//================
			Properties p = new Properties();
			p.load( in );
			
			// Read properties
			//================
			String lang          = p.getProperty( "language" );
			String ctry          = p.getProperty( "country" );
			enableFullscreen     = Boolean.parseBoolean( p.getProperty( "enableFullscreen" ) );
			enableVSync          = Boolean.parseBoolean( p.getProperty( "enableVSync" ) );
			editor_showFps       = Boolean.parseBoolean( p.getProperty( "editor_showFps" ) );
			editor_showPowerInfo = Boolean.parseBoolean( p.getProperty( "editor_showPowerInfo" ) );
			
			// Apply locale
			//=============
			if ( lang == null ) lang = "";
			if ( ctry == null ) ctry = "";
			
			Lang.get().setLocale( new Locale( lang, ctry ) );
			
			Locale.setDefault( Lang.get().getLocale() );
			
			locale = Locale.getDefault();
		}
		catch ( Exception e )
		{
			// Write default settings
			//=======================
			System.err.println( "Error reading configuration file!" );
			initDefault();
			save();
			return ( false );
		}
		return ( true );
	}
	
	// apply
	//======
	/**
	 * Applies the current graphics configuration.
	 */
	public void applyGraphics()
	{
		// Fullscreen
		//===========
		if ( enableFullscreen == true  )
		{
			Gdx.graphics.setFullscreenMode( Gdx.graphics.getDisplayMode() );
		}
		else
		{
			// Calculate window size
			//======================
			float w = Gdx.graphics.getDisplayMode().width  * 0.8f;
			float h = Gdx.graphics.getDisplayMode().height * 0.8f;
			
			if ( w < 600 ) w = 600;
			if ( h < 600 ) h = 600;
			
			// Set window mode
			//================
			if ( Gdx.graphics.isFullscreen() )
			{
				Gdx.graphics.setFullscreenMode( Gdx.graphics.getDisplayModes()[0] );
			}
			Gdx.graphics.setWindowedMode( (int) w, (int) h );
		}
		
		// VSync
		//======
		Gdx.graphics.setVSync( enableVSync );
	}
}