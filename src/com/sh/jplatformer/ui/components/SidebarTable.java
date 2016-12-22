package com.sh.jplatformer.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
* The {@code SidebarTable} overrides the original libGDX {@code Table} and comes
* with predefined properties adapted for the editor {@code SidebarWindow} UI.
* @author Stefan H�semann
 */

public class SidebarTable extends Table
{
	// Constructor
	//============
	/**
	 * Constructs a new {@code SidebarTable}.
	 */
	public SidebarTable()
	{
		// Call super
		//===========
		super();
		
		// Properties
		//===========
		this.top()
		    .left()
		    .pad( UiConstants.BORDER_SMALL )
		    .padTop( 0f );
		this.defaults()
		    .space( UiConstants.BORDER_SMALL )
		    .expandX()
		    .fillX();
	}
}
