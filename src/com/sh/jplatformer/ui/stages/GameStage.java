package com.sh.jplatformer.ui.stages;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.menu.PauseMenu;
import com.sh.jplatformer.ui.menu.RecordsMenu;
import com.sh.jplatformer.util.Time;
import com.sh.jplatformer.world.WorldCamera;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.WorldFile;
import com.sh.jplatformer.world.map.MapPopup;

/**
 * The {@code GameStage} provides an interface layer for game mode.
 * @author Stefan Hösemann
 */

public class GameStage extends Stage
{
	// UI Components
	//==============
	private Table tbl_main;
	private Stack stk_menus;
	
	private Actor mnu_pause;
	private Actor mnu_records;
	
	// World properties
	//=================
	private WorldController worldController;
	private WorldCamera     worldCamera;

	// Popup labels
	//=============
	private Label lbl_small;
	private Label lbl_big;
	private Vector2 origin;
	
	// Score board
	//============
	private int oldScore;
	private String scoreString;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code DisplayStage}.
	 * @param newWorldController the {@code WorldController} that contains the world data.
	 */
	public GameStage( WorldController newWorldController )
	{
		// Viewport
		//=========
		super( new ScreenViewport() );

		// Add to stack
		//=============
		stk_menus = new Stack();
		
		// Menu setup
		//===========
		mnu_pause   = new PauseMenu  ( this );
		mnu_records = new RecordsMenu( this );
		
		// Stack setup
		//============
		stk_menus = new Stack();
		stk_menus.add( mnu_pause );
		stk_menus.add( mnu_records );
		
		// Table setup
		//============
		tbl_main = new Table();
		tbl_main.setFillParent( true );
		tbl_main.add( stk_menus );

		this.addActor( tbl_main );
		
		// Hide menus
		//===========
		this.setAllMenusInvisible();
		
		// Popup labels
		//=============
		lbl_small = new Label( "", Resources.UI.skin, "popup" );
		lbl_big   = new Label( "", Resources.UI.skin, "popup_big" );
		origin    = new Vector2();
		
		// World properties
		//=================
		worldController = newWorldController;
		worldCamera     = newWorldController.getWorldCamera();
	}
	
	// draw
	//=====
	@Override
	public void draw()
	{
		// Draw displays
		//==============
		this.getBatch().begin();
		{
			// Popup labels
			//=============
			drawPopupLabels();
			
			// Game displays
			//==============
			if ( worldController.isLive() == true )
			{
				drawScoreBoard();
				drawTime();
			}
		}
		this.getBatch().end();
		super.draw();
	}
	
	// drawPopupLabels
	//================
	private void drawPopupLabels()
	{
		for ( MapPopup p : worldController.getPopups() )
		{
			// Set text + pack
			//================
			lbl_small.setText( p.getText() );
			lbl_small.pack();
			
			// Project position
			//=================
			origin.set( p.getPosition().x - lbl_small.getWidth()  / 2f,
			            p.getPosition().y - lbl_small.getHeight() / 2f );
			origin.x = (int) origin.x;
			origin.y = (int) origin.y;
			origin   = worldCamera.project( origin );
			
			// Draw at position
			//=================
			lbl_small.setX( origin.x );
			lbl_small.setY( origin.y );
			lbl_small.draw( this.getBatch(), p.getAlpha() );
		}
	}
	
	// drawScoreBoard
	//===============
	private void drawScoreBoard()
	{
		// Temporary values
		//=================
		int digits   = 6;
		int newScore = worldController.getScore();
		
		// Apply zero score
		//=================
		if ( oldScore == 0 )
		{
			scoreString = "000000";
		}
		
		// Update score string
		//====================
		if ( oldScore != newScore )
		{
			// Limit score
			//============
			if ( newScore > 999999 )
			{
				newScore = 999999;
			}
			
			// Set values
			//===========
			scoreString = Integer.toString( newScore );
			oldScore    = newScore;
			int loops   = digits - scoreString.length();
			
			// Add leading zeros
			//==================
			if ( scoreString.length() < digits )
			{
				for ( int i = 0; i < loops; i++ )
				{
					scoreString = "0" + scoreString;
				}
			}
		}
		
		// Draw score board
		//=================
		if ( scoreString.length() < 10 && newScore >= 0 )
		{
			float w = Resources.UI.game_scoreBoard[0].getWidth();
			float h = Resources.UI.game_scoreBoard[0].getHeight();
			
			for ( int i = 0; i < scoreString.length(); i++ )
			{
				int idx = Integer.parseInt( scoreString.substring( i, i+1 ) );
				Resources.UI.game_scoreBoard[idx].setX( +UiConstants.BORDER_BIG + i * w );
				Resources.UI.game_scoreBoard[idx].setY( -UiConstants.BORDER_BIG - h + getHeight() );
				Resources.UI.game_scoreBoard[idx].draw( this.getBatch() );
			}
		}
	}
	
	// drawTime
	//=========
	private void drawTime()
	{	
		// Draw label
		//===========
		lbl_big.setText( Time.toString( worldController.getElapsedTime() ) );
		lbl_big.pack();
		lbl_big.getStyle().font = Resources.UI.font_big;
		lbl_big.setX( getWidth()  - lbl_big.getWidth()  * 1f - UiConstants.BORDER_BIG );
		lbl_big.setY( getHeight() - lbl_big.getHeight() * 1f - UiConstants.BORDER_BIG );
		lbl_big.draw( this.getBatch(), 1f );
	}
	
	// setAllMenusInvisible
	//=====================
	private void setAllMenusInvisible()
	{
		// Show content
		//=============
		for ( Actor a : stk_menus.getChildren() )
		{	
			a.setVisible( false );
		}
	}
	
	// callRecordsMenu
	//================
	/**
	 * Sets the records menu visible and pauses the game.
	 */
	public void callRecordsMenu()
	{
		this.setAllMenusInvisible();

		mnu_records.setVisible( true );
		
		JPlatformerGame.get().setPaused( true );
	}
		
	// callPauseMenu
	//==============
	public void callPauseMenu()
	{
		this.setAllMenusInvisible();
		
		JPlatformerGame.get().setPaused( true );
		mnu_pause.setVisible           ( true );
	}
	
	// isPauseMenuVisible
	//===================
	public boolean isPauseMenuVisible()
	{
		return ( mnu_pause.isVisible() );
	}
	
	// isRecordsMenuVisible
	//=====================
	public boolean isRecordsMenuVisible()
	{
		return ( mnu_records.isVisible() );
	}
	
	// resumeGame
	//===========
	public void resumeGame()
	{
		this.setAllMenusInvisible();
	
		JPlatformerGame.get().setPaused( false );
	}
	
	// restartGame
	//============
	public void restartGame()
	{
		WorldFile.reloadWorld();
		resumeGame();
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}
	
	// resize
	//=======
	/**
	 * Updates all camera and viewport data on resize.
	 * @param width the new width in pixels.
	 * @param height the new height in pixels.
	 */
	public void resize( int width, int height )
	{
		this.getViewport().update( width, height, true );
	}
}