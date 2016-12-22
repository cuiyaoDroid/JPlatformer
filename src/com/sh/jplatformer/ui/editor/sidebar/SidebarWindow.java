package com.sh.jplatformer.ui.editor.sidebar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.*;
import com.sh.jplatformer.ui.stages.EditorStage;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code SidebarWindow} provides an user interface to modify a specified {@code WorldController}.
 * @author Stefan H�semann
 */

public class SidebarWindow extends Table
{
	// UI components
	//==============
	public static final float WIDTH = 352f;
	
	private TabPane pne_tabs;
	
	private FileMenu   mnu_fileMenu;
	private MapMenu    mnu_mapMenu;
	private ObjectMenu mnu_objectMenu;
	private SystemMenu mnu_systemMenu;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code SidebarWindow}.
	 * @param worldController the {@code WorldController} to modify.
	 */
	public SidebarWindow( Skin skin, WorldController worldController )
	{
		// Create sub menus
		//=================
		mnu_fileMenu   = new FileMenu( worldController );
		mnu_mapMenu    = new MapMenu( worldController );
		mnu_objectMenu = new ObjectMenu( worldController );
		mnu_systemMenu = new SystemMenu();

		// Create tab pane
		//================
		pne_tabs = new TabPane();

		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_file,    skin, "toggle" ), mnu_fileMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_map,     skin, "toggle" ), mnu_mapMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_objects, skin, "toggle" ), mnu_objectMenu );
		pne_tabs.addTab( new TabPaneButton( Resources.UI.editor_toolbar_system,  skin, "toggle" ), mnu_systemMenu );
		
		// Specify properties
		//===================
		this.add( new FormPane( pne_tabs, skin, "dark" ) ).expand().fill();	
		this.pack();
		this.setVisible( true );
	}
	
	// update
	//=======
	/**
	 * Updates all {@code Map} and {@code MapObject} related UI components.
	 */
	public void update()
	{
		mnu_mapMenu.updateComponents();
		mnu_objectMenu.updateComponents();
	}
	
	// draw
	//=====
	@Override
	public void draw( Batch batch, float parentAlpha )
	{
		super.draw( batch, parentAlpha );
		
		batch.setColor( Color.WHITE );
		
		Resources.UI.skin_shadow.draw( batch,
		                               this.getX() + this.getWidth(),
		                               this.getY(),
		                               Resources.UI.skin_shadow.getRegion().getRegionWidth(),
		                               this.getHeight() );
	}
	
	// getEditMode
	//============
	public int getEditMode()
	{
		// Check for edit tab
		//===================
		if ( pne_tabs.getVisibleContent() == mnu_mapMenu )
		{
			return ( mnu_mapMenu.getEditMode() );
		}
		
		if ( pne_tabs.getVisibleContent() == mnu_objectMenu )
		{
			return ( mnu_objectMenu.getEditMode() );
		}
		
		return ( EditorStage.EDIT_NONE );
	}
	
	// getSelectedWaterTile
	//=====================
	public int getSelectedWaterTile()
	{
		return ( mnu_mapMenu.getSelectedWaterTile() );
	}
	
	// getSelectedWaterSpeedId
	//========================
	public int getSelectedWaterSpeedId()
	{
		return ( mnu_mapMenu.getSelectedWaterSpeedId() );
	}
	
	// getSelectedMapTile
	//===================
	public int getSelectedMapTile()
	{
		return ( mnu_mapMenu.getSelectedMapTile() );
	}
	
	// getSelectedMapObject
	//=====================
	public MapObject getSelectedMapObject()
	{
		return ( mnu_objectMenu.getSelectedMapObject() );
	}
	
	// resize
	//=======
	/**
	 * @param width the new width in pixels.
	 * @param height the new height in pixels.
	 */
	public void resize( int width, int height )
	{
		this.setSize( WIDTH, height);
	}
}