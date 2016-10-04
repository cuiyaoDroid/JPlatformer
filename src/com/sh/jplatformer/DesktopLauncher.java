package com.sh.jplatformer;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sh.jplatformer.JPlatformerGame;

/**
 * This is the main class of the program for the desktop release.
 * @author Stefan H�semann
 */

public class DesktopLauncher
{
	public static void main( String[] args )
	{
		// Config
		//=======
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.addIcon( "resources/images/icon/icon_16px.png",  FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_32px.png",  FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_48px.png",  FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_64px.png",  FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_96px.png",  FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_128px.png", FileType.Internal );
		cfg.addIcon( "resources/images/icon/icon_256px.png", FileType.Internal );
		
		// Create game
		//============
		new LwjglApplication( JPlatformerGame.get(), cfg );
	}
}