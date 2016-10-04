package com.sh.jplatformer.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.*;
import com.sh.jplatformer.ui.stages.GameStage;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldController;

/**
 * The {@code PauseMenu} shows up if the game is paused in the {@code GameScreen}.
 * @author Stefan Hösemann
 */

public class PauseMenu extends Table
{
	// UI Components
	//==============
	private GameStage gameStage;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code PauseMenu}.
	 * @param newGameStage the {@code GameStage} that is responsible for this menu.
	 */
	public PauseMenu( GameStage newGameStage )
	{
		// Set game stage
		//===============
		gameStage = newGameStage;
		
		// Add components
		//===============
		Actor pane = new FormPane( createMenu(), Resources.UI.skin, "menu" );
		this.add( pane );
	}
	
	// createMenu
	//===========
	private Actor createMenu()
	{
		// Button: Resume
		//===============
		FormButton btn_resume = new FormButton( Lang.txt( "menu_pause_resume" ), Resources.UI.skin, "menu" );
		btn_resume.addListener( new ChangeListener()
		{
			// Resume game
			//============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				gameStage.resumeGame();
			}
		} );
		
		// Button: Restart
		//================
		FormButton btn_restart = new FormButton( Lang.txt( "menu_pause_restart" ), Resources.UI.skin, "menu" );
		btn_restart.addListener( new ChangeListener()
		{
			// Restart game
			//=============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				gameStage.getWorldController().setWorldState( WorldController.STATE_RESTART );
			}
		} );
		
		// Button: Leave
		//==============
		FormButton btn_leave = new FormButton( Lang.txt( "menu_pause_backToMenu" ), Resources.UI.skin, "menu" );
		btn_leave.addListener( new ChangeListener()
		{
			// Call main menu
			//===============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				JPlatformerGame.get().callScreen( JPlatformerGame.get().menuScreen );
			}
		} );
		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.top().pad( UiConstants.BORDER_SMALL );
		tbl_main.defaults().space( UiConstants.BORDER_SMALL );
		
		// Window heading setup
		//=====================
		Label lbl_heading = new Label( Lang.txt( "menu_pause_heading" ), Resources.UI.skin, "heading_menu" )
		{
			@Override
			public float getMinWidth()
			{
				return ( 300f );
			}
		};
		lbl_heading.setAlignment( Align.center );
		
		// Window heading
		//===============
		tbl_main.add( lbl_heading ).top().expandX().fillX();
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( btn_resume  ).expand().fill().height( btn_resume .getPrefHeight() + 8f );
		tbl_main.row();
		tbl_main.add( btn_restart ).expand().fill().height( btn_restart.getPrefHeight() + 8f );
		tbl_main.row();
		tbl_main.add( btn_leave   ).expand().fill().height( btn_leave  .getPrefHeight() + 8f );
		tbl_main.row();
		
		return ( tbl_main );
	}
}