package com.sh.jplatformer.ui.menu;

import java.util.Locale;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.*;
import com.sh.jplatformer.ui.stages.MainMenuStage;
import com.sh.jplatformer.util.Lang;

/**
 * The {@code SettingsMenu} offers an UI to modify a variety configuration attributes.
 * @author Stefan Hösemann
 */

public class SettingsMenu extends Table
{
	// UI components
	//==============
	private MainMenuStage mainMenuStage;
	private Skin skin;
	
	private FormSelectBox<Locale> slc_language;
	
	private CheckBox chk_enableFullscreen;
	private CheckBox chk_enableVSync;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code SettingsMenu}.
	 * @param newMainMenuStage the {@code MainMenuStage} that is responsible for this menu.
	 */
	public SettingsMenu( MainMenuStage newMainMenuStage )
	{
		// Setup
		//======
		skin          = Resources.UI.skin;
		mainMenuStage = newMainMenuStage;
		Actor pane    = new FormPane( createMenu(), skin, "menu" );
		
		// Add components
		//===============
		this.add( pane ).top();
		
		// Update components
		//==================
		updateComponents();
	}
	
	// createMenu
	//===========
	private Actor createMenu()
	{
		// Language select box
		//====================
		slc_language = createLanguageSelectBox();
		
		// Check boxes
		//============
		chk_enableFullscreen = new FormCheckBox( "", skin );
		chk_enableVSync      = new FormCheckBox( "", skin );
		
		// Table setup
		//============
		Table tbl_main = new Table();
		
		tbl_main.top().pad( UiConstants.BORDER_SMALL );
		tbl_main.columnDefaults( 0 ).padRight( 150f );
		tbl_main.defaults().space( UiConstants.BORDER_SMALL );
		
		// Window heading setup
		//=====================
		Label lbl_heading = new Label( Lang.txt( "menu_settings_heading" ), skin, "heading_menu" );
		lbl_heading.setAlignment( Align.center );

		// Window heading
		//===============
		tbl_main.add( lbl_heading ).colspan( 2 ).top().expandX().fillX().pad( 0f );
		tbl_main.row();
		
		// Language selection
		//===================
		tbl_main.add( new Label( Lang.txt( "menu_settings_selectLanguage" ), skin ) ).left();
		tbl_main.add( slc_language ).fillX().right();
		tbl_main.row();
		
		// Enable fullscreen
		//==================
		tbl_main.add( new Label( Lang.txt( "menu_settings_enableFullscreen" ), skin ) ).left();
		tbl_main.add( chk_enableFullscreen ).fillX().right();
		tbl_main.row();
		
		// Enable VSync
		//=============
		tbl_main.add( new Label( Lang.txt( "menu_settings_enableVSync" ), skin ) ).left();
		tbl_main.add( chk_enableVSync ).fillX().right();
		tbl_main.row();
		
		// Note heading
		//=============
		tbl_main.add( new Label( "\n" + Lang.txt( "menu_settings_note" ), skin, "heading" ) ).colspan( 2 ).left();
		tbl_main.row();
		
		// Note label setup
		//=================
		Label lbl_note = new Label( Lang.txt( "menu_settings_noteText" ) + "\n", skin );
		lbl_note.setWrap( true );
		
		// Note label
		//===========
		tbl_main.add( lbl_note ).left().expandY().fillX().pad( 0f ).colspan( 2 );
		tbl_main.row();
		
		// Buttons
		//========
		tbl_main.add( createButtonBar() ).bottom()
		                                 .right()
		                                 .colspan( 2 )
		                                 .space( 0f )
		                                 .pad( 0f );
		return ( tbl_main );
	}
	
	// createLanguageSelectBox
	//========================
	private FormSelectBox<Locale> createLanguageSelectBox()
	{
		// Create select box
		//==================
		slc_language = new FormSelectBox<Locale>( skin, "menu" )
		{
			// getMinWidth
			//============
			@Override
			public float getMinWidth()
			{
				return ( 200f );
			}
			
			// getPrefHeight
			//==============
			@Override
			public float getPrefHeight()
			{
				return ( super.getPrefHeight() + 5f );
			}
			
			// toString
			//=========
			@Override
			public String toString( Locale item )
			{
				// Determine strings
				//==================
				String text = item.getDisplayLanguage();

				// Add country
				//============
				if ( item.getDisplayCountry().length() > 0 )
				{
					text = text + " (" + item.getDisplayCountry() + ")";
				}
				return ( text );
			}
		};
		
		// Initialize select box
		//======================
		try
		{
			slc_language.setItems( Lang.get().getAvailableLocales() );
		}
		catch ( Exception e ) {}
		
		// Return select box
		//==================
		return ( slc_language );
	}
	
	// createButtonBar
	//================
	private Actor createButtonBar()
	{
		// Button: Apply
		//==============
		FormButton btn_apply = new FormButton( Lang.txt( "menu_settings_apply" ), skin, "menu" );
		btn_apply.addListener( new ChangeListener()
		{
			// Apply settings
			//===============
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				// Apply settings
				//===============
				Config.get().locale           = slc_language.getSelected();
				Config.get().enableFullscreen = chk_enableFullscreen.isChecked();
				Config.get().enableVSync      = chk_enableVSync.isChecked();
				
				// Apply graphics
				//===============
				Config.get().applyGraphics();
				
				// Return to menu
				//===============
				mainMenuStage.callStartMenu();
			}
		} );
		
		// Button: Cancel
		//===============
		FormButton btn_cancel = new FormButton( Lang.txt( "menu_settings_cancel" ), skin, "menu"  );
		btn_cancel.addListener( new ChangeListener()
		{
			// Return to start menu
			//=====================
			@Override
			public void changed( ChangeEvent e, Actor actor )
			{
				mainMenuStage.callStartMenu();
			}
		} );
		
		// Equal size
		//===========
		float w = 0f;
		
		if ( btn_cancel.getPrefWidth() > btn_apply.getPrefWidth()  ) w = btn_cancel.getPrefWidth();
		if ( btn_apply.getPrefWidth()  > btn_cancel.getPrefWidth() ) w = btn_apply.getPrefWidth();
		
		// Create table
		//=============
		Table tbl_buttons = new Table();
		
		tbl_buttons.add( btn_cancel ).padLeft( UiConstants.BORDER_BIG ).minWidth( w );
		tbl_buttons.add( btn_apply ) .padLeft( UiConstants.BORDER_BIG ).minWidth( w );
		tbl_buttons.pack();
		
		return ( tbl_buttons );
	}
	
	// setVisible
	//===========
	@Override
	public void setVisible( boolean value )
	{
		// Update components
		//==================
		if ( value == true )
		{
			updateComponents();
		}
		super.setVisible( value );
	}
	
	// updateComponents
	//=================
	private void updateComponents()
	{
		// Language select box
		//====================
		slc_language.setSelected( Config.get().locale );
		
		// General
		//========
		chk_enableFullscreen.setChecked( Config.get().enableFullscreen );
		chk_enableVSync     .setChecked( Config.get().enableVSync );
	}
}