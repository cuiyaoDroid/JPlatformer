package com.sh.jplatformer.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.stages.MainMenuStage;
import com.sh.jplatformer.util.Lang;

/**
 * The {@code StartMenu} provides is the root of the main menu.
 * @author Stefan Hösemann
 */

public class StartMenu extends Table
{
	// UI components
	//==============
	private MainMenuStage mainMenuStage;
	private Skin skin;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code StartMenu}.
	 * @param newMainMenuStage the {@code MainMenuStage} that is responsible for this menu.
	 */
	public StartMenu( MainMenuStage newMainMenuStage )
	{
		// General values
		//===============
		skin          = Resources.UI.skin;
		mainMenuStage = newMainMenuStage;
		
		// Add components
		//===============
		this.add( createStartButtonsTable() ).center().expand().fill();
	}
	
	// createStartButtonsTable
	//========================
	/**
	 * @return a {@code Table} with the start buttons (start game, open editor, open settings).
	 */
	private Actor createStartButtonsTable()
	{
		// Play button
		//============
		ImageButton btn_play = createHighlightButton();
		btn_play.addListener( new ChangeListener()
		{
			// Switch menu
			//============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				mainMenuStage.callSelectWorldMenu();
				
				Resources.UI.sound_click1.play();
			}
		} );
		
		// Editor button
		//==============
		ImageButton btn_editor = createHighlightButton();
		btn_editor.addListener( new ChangeListener()
		{
			// Call editor
			//============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				JPlatformerGame.get().callScreen( JPlatformerGame.get().editorScreen );
				
				Resources.UI.sound_click1.play();
			}
		} );
		
		// Settings button
		//================
		ImageButton btn_settings = createHighlightButton();
		btn_settings.addListener( new ChangeListener()
		{
			// Call settings
			//==============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				mainMenuStage.callSettingsMenu();
				
				Resources.UI.sound_click1.play();
			}
		} );
		
		// Icon tables
		//============
		Table icon_play     = new Table();
		Table icon_editor   = new Table();
		Table icon_settings = new Table();
		
		icon_play      .background( new SpriteDrawable( Resources.UI.menu_icon_play ) );
		icon_editor    .background( new SpriteDrawable( Resources.UI.menu_icon_editor ) );
		icon_settings  .background( new SpriteDrawable( Resources.UI.menu_icon_settings ) );
		
		// Button stacks
		//==============
		Stack stk_play     = new Stack( btn_play,    icon_play );
		Stack stk_editor   = new Stack( btn_editor,  icon_editor );
		Stack stk_settings = new Stack( btn_settings,icon_settings );

		// Labels setup
		//=============
		Label lbl_play     = new Label( Lang.txt( "menu_start_play"     ), skin, "heading_menu" );
		Label lbl_editor   = new Label( Lang.txt( "menu_start_editor"   ), skin, "heading_menu" );
		Label lbl_settings = new Label( Lang.txt( "menu_start_settings" ), skin, "heading_menu" );
		
		lbl_play    .setAlignment( Align.center );
		lbl_editor  .setAlignment( Align.center );
		lbl_settings.setAlignment( Align.center );
		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.defaults().center().space( 60f );
		
		tbl_main.add( stk_play )    .spaceBottom( 0f );
		tbl_main.add( stk_editor )  .spaceBottom( 0f );
		tbl_main.add( stk_settings ).spaceBottom( 0f );
		
		tbl_main.row();
		
		tbl_main.add( lbl_play )    .spaceTop( 10f ).fillX();
		tbl_main.add( lbl_editor )  .spaceTop( 10f ).fillX();
		tbl_main.add( lbl_settings ).spaceTop( 10f ).fillX();
		
		return ( tbl_main );
	}
	
	// createHighlightButton
	//======================
	private ImageButton createHighlightButton()
	{
		// Create button
		//==============
		ImageButton button = new ImageButton( new SpriteDrawable( Resources.UI.menu_icon_highlight_large ) );
		
		// Click listener
		//===============
		button.addListener( new ClickListener()
		{
			// enter
			//======
			@Override
			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor )
			{
				button.addAction( Actions.alpha( 1f, 0.2f ) );
			}
			
			// exit
			//======
			@Override
			public void exit ( InputEvent event, float x, float y, int pointer, Actor fromActor )
			{
				button.addAction( Actions.alpha( 0f, 0.2f ) );
			}
			
		} );
		button.setColor( 1, 1, 1, 0.f );
		
		// Return
		//=======
		return ( button );
	}
}