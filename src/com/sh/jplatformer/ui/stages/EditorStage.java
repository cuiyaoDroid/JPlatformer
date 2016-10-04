package com.sh.jplatformer.ui.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.Config;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.components.UiConstants;
import com.sh.jplatformer.ui.editor.sidebar.SidebarWindow;
import com.sh.jplatformer.ui.input.EditorInput;
import com.sh.jplatformer.util.Lang;
import com.sh.jplatformer.world.WorldCamera;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code EditorStage} provides a stage for the editor UI.
 * @author Stefan Hösemann
 */

public class EditorStage extends Stage
{
	// Constants
	//==========
	public static final int DRAG_NONE    = 0;
	public static final int DRAG_PLACE   = 1;
	public static final int DRAG_REMOVE  = 2;
	public static final int DRAG_HOVER   = 3;
	public static final int DRAG_TRIGGER = 4;
	
	public static final int EDIT_NONE        = 0;
	public static final int EDIT_ENVIRONMENT = 1;
	public static final int EDIT_CELLS       = 2;
	public static final int EDIT_TRIGGERS    = 3;
	public static final int EDIT_OBJECTS     = 4;
	public static final int EDIT_ATTRIBUTES  = 5;

	// World properties
	//=================
	private WorldController worldController;
	private WorldCamera     worldCamera;
	
	// UI components
	//==============
	private SidebarWindow sidebarWindow;
	private Label lbl_info;
	private StringBuilder infoText;
	
	// Input values
	//=============
	private Vector2   scrollCenter;
	private Rectangle tmpArea;
	private Rectangle dragArea;
	private int       dragMode;
	private boolean   ctrlPressed;
	private boolean   altPressed;

	// Constructor
	//============
	/**
	 * Constructs a new {@code EditorStage}.
	 * @param newWorldController the {@code WorldController} to modify. This sets
	 * {@code isLive} to {@code false} in the {@code WorldController}.
	 */
	public EditorStage( WorldController newWorldController )
	{
		// Viewport / camera
		//==================
		super( new ScreenViewport() );
		worldCamera = newWorldController.getWorldCamera();
		
		// World controller
		//=================
		worldController = newWorldController;
		worldController.setLive( false );
		
		// Input values
		//=============
		tmpArea  = new Rectangle();
		dragArea = new Rectangle();
		dragMode = 0;
		
		// Sidebar setup
		//==============
		lbl_info       = new Label( "", Resources.UI.skin, "popup" );
		infoText       = new StringBuilder( "" );
		sidebarWindow  = new SidebarWindow( Resources.UI.skin, worldController );
		
		this.addActor( sidebarWindow );		

		// Unselect text fields when mouse clicks
		//=======================================
		this.getRoot().addCaptureListener(new InputListener()
		{
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
			{
				// Release text field
				//===================
				if ( ! ( event.getTarget() instanceof TextField ) )
				{
					setKeyboardFocus(null);
				}
				
				// Release scroll pane
				//====================
				if ( ! ( event.getTarget() instanceof ScrollPane ) )
				{
					if ( getScrollFocus() != null )
					{
						unfocus( getScrollFocus() );
					}
				}
			return ( false );
			}
		} );
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
		
		sidebarWindow.resize( width, height );
	}
	
	// update
	//=======
	/**
	 * Updates all {@code Map} and {@code MapObject} related UI components.
	 */
	public void update()
	{
		sidebarWindow.update();
	}
	
	// draw
	//=====
	@Override
	public void draw()
	{
		// Begin batch
		//============
		this.getBatch().begin();
		{
			// Info displays
			//==============
			this.drawInfoLabel();
			
			// Draw UI if world size > 0x0
			//============================
			if ( worldController.getMap().getColumns() > 0 ||
			     worldController.getMap().getRows()    > 0 )
			{
				// Editing displays
				//=================
				this.drawMapCellDisplays();
				this.drawMapObjectDisplays();
				this.drawTriggerAreas();
				this.drawScrollCenter();
				this.drawZoomLabel();
			}	
		}
		this.getBatch().end();
		
		// Super draw
		//===========
		super.draw();
	}
	
	// drawInfoLabel
	//==============
	private void drawInfoLabel()
	{
		// Clear string
		//=============
		infoText.delete( 0, infoText.length() );
		
		// Pause text
		//===========
		if ( JPlatformerGame.get().isPaused() )
		{
			infoText.append( "Pause" );	
		}
		
		// FPS text
		//=========
		if ( Config.get().editor_showFps == true )
		{	
			// Add new line if paused
			//=======================
			if ( JPlatformerGame.get().isPaused() )
			{
				infoText.insert( 0, Gdx.graphics.getFramesPerSecond() + " FPS" + "\n" );
			}
			else
			{
				infoText.insert( 0, Gdx.graphics.getFramesPerSecond() + " FPS" );
			}
		}
		
		// Draw label
		//===========
		if ( infoText.length() > 0 )
		{
			lbl_info.setText( infoText.toString() );
			lbl_info.pack();
			lbl_info.setX( getWidth()  - lbl_info.getWidth()  * 1f - UiConstants.BORDER_BIG );
			lbl_info.setY( getHeight() - lbl_info.getHeight() * 1f - UiConstants.BORDER_BIG );
			lbl_info.setAlignment( Align.right );
			lbl_info.draw( this.getBatch(), 1f );
		}
	}
	
	// drawZoomLabel
	//==============
	private void drawZoomLabel()
	{
		// Set string
		//===========
		int value = (int) ( ( 1f / worldCamera.zoom ) * 100f );
		
		// Draw label
		//===========
		lbl_info.setText( Lang.txt( "editor_zoom" ) + " " + value + "%" );
		lbl_info.pack();
		lbl_info.setX( getWidth() - lbl_info.getWidth() * 1f - UiConstants.BORDER_BIG );
		lbl_info.setY( UiConstants.BORDER_BIG );
		lbl_info.draw( this.getBatch(), 1f );
	}
	
	// drawMapCellDisplays
	//====================
	/**
	 * Draws all {@code Map} and {@code MapCell} related highlighting and editing graphics.
	 */
	private void drawMapCellDisplays()
	{
		// Temporary values
		//=================
		Vector2 mouse = EditorInput.getMousePosInWorld();
		Map map = worldController.getMap();

		// Map tile highlighting
		//======================
		if ( sidebarWindow.getEditMode() == EditorStage.EDIT_CELLS )
		{
			// Actual drag area
			//=================
			if ( dragMode == EditorStage.DRAG_PLACE || dragMode == EditorStage.DRAG_REMOVE )
			{
				this.drawDragArea( dragArea, dragMode );
			}
			
			// Tile-based highlighting
			//========================
			if ( ctrlPressed == false )
			{
				for ( MapCell cell : worldController.getVisibleCells() )
				{
					// Get hovered cell position
					//==========================
					tmpArea.x      = worldCamera.toUnits( Map.CELL_SIZE ) * cell.x;
					tmpArea.y      = worldCamera.toUnits( Map.CELL_SIZE ) * cell.y;
					tmpArea.width  = worldCamera.toUnits( Map.CELL_SIZE );
					tmpArea.height = worldCamera.toUnits( Map.CELL_SIZE );
					
					// Hover highlight
					//================
					if ( dragMode == EditorStage.DRAG_NONE && tmpArea.contains( mouse ) )
					{
						this.drawDragArea( tmpArea, EditorStage.DRAG_HOVER );
					}
					
					// Drag highlights
					//================
					if ( ( dragMode == EditorStage.DRAG_PLACE ||
					       dragMode == EditorStage.DRAG_REMOVE ) && dragArea.overlaps( tmpArea ) )
					{
						// Placement highlight
						//====================
						if ( dragMode == EditorStage.DRAG_PLACE )
						{
							this.drawDragArea( tmpArea, dragMode );
						}
						
						// Removement highlight
						//=====================
						if ( dragMode == EditorStage.DRAG_REMOVE )
						{
							if ( cell.tileSetId > -1 )
							{
								this.drawDragArea( tmpArea, dragMode );
							}
						}
					}	
				}

				// Mouse label setup
				//==================
				if ( dragMode != EditorStage.DRAG_NONE )
				{
					int x1 = map.getCellAt( dragArea.x, dragArea.y ).x;
					int y1 = map.getCellAt( dragArea.x, dragArea.y ).y;
					
					int x2 = map.getCellAt( mouse.x, mouse.y ).x;
					int y2 = map.getCellAt( mouse.x, mouse.y ).y;
			
					if ( x2 == x1 ) x2 = map.getCellAt( dragArea.x + dragArea.width, dragArea.y + dragArea.height ).x;
					if ( y2 == y1 ) y2 = map.getCellAt( dragArea.x + dragArea.width, dragArea.y + dragArea.height ).y;
					
					int w = -( x1 - x2 ) + 1;
					int h = -( y1 - y2 ) + 1;
					
					// Check if inside map
					//====================
					if ( dragArea.overlaps( worldController.getMapBounds() ) )
					{
						lbl_info.setText( w + " x " + h );
						lbl_info.setPosition( EditorInput.getMousePos().x, EditorInput.getMousePos().y );
						lbl_info.pack();
						lbl_info.draw( this.getBatch(), 1f );
					}
				}
			}
		}
	}
	
	// drawMapObjectDisplays
	//======================
	/**
	 * Draws all {@code MapObject} related highlighting and editing graphics.
	 */
	private void drawMapObjectDisplays()
	{
		// Reset hover
		//============
		worldController.setHoveredMapObject( null );
		
		// Mouse drag area
		//================
		if ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS )
		{		
			if ( dragMode == EditorStage.DRAG_REMOVE )
			{
				this.drawDragArea( dragArea, dragMode );
			}
		}
		
		// Map objects
		//============
		for ( MapObject o : worldController.getMapObjects() )
		{	
			// Removement highlight
			//=====================
			if ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS )
			{
				if ( dragMode == EditorStage.DRAG_REMOVE && dragArea.overlaps( o.getBounds() ) )
				{
					this.drawDragArea( o.getBounds(), dragMode );
				}
			}
				
			// Mouse hover highlight
			//======================
			if ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS ||
			     sidebarWindow.getEditMode() == EditorStage.EDIT_ATTRIBUTES )
			{
				if ( dragMode == EditorStage.DRAG_NONE && ctrlPressed == true )
				{
					if ( o.getBounds().contains( EditorInput.getMousePosInWorld() ) )
					{
						worldController.setHoveredMapObject( o );
							
						this.drawDragArea( o.getBounds(), EditorStage.DRAG_HOVER );
					}
				}
			}
			
			// Highlight marked objects
			//=========================
			if ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS ||
			     sidebarWindow.getEditMode() == EditorStage.EDIT_ATTRIBUTES )
			{
				// Highlight rectangle
				//====================
				if ( worldController.getMarkedMapObjects().contains( o ) )
				{	
					this.drawDragArea( o.getBounds(), EditorStage.DRAG_HOVER );
				}
			}
			
			// Draw object energy label
			//=========================
			if ( Config.get().editor_showPowerInfo )
			{
				if ( o.isPowerSupported() == true )
				{
					// Set values
					//===========
					Rectangle pos = worldCamera.project( o.getBounds() );
					
					infoText.setLength( 0 );
					infoText.append( "ID: " + o.getPowerId() + "\n" );
					
					if ( o.isPowerOn() == true  ) infoText.append( Lang.txt( "game_on" ) );
					if ( o.isPowerOn() == false ) infoText.append( Lang.txt( "game_off" ) );
					
					// Label setup
					//============
					lbl_info.setText( infoText.toString() );
					lbl_info.setX( pos.x );
					lbl_info.setY( pos.y + pos.height );
					lbl_info.setAlignment( Align.left );
					lbl_info.pack();
					lbl_info.draw( this.getBatch(), 1f );
				}
			}
		}
		
		// Draw cursor ghost
		//==================
		if ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS && !ctrlPressed )
		{
			if ( dragMode != EditorStage.DRAG_REMOVE )
			{
				// Temporary values
				//=================
				MapObject obj  = sidebarWindow.getSelectedMapObject();
				Vector2 tmpPos = EditorInput.getMousePosInWorld();
				float tmpSize  = obj.getFrameSize() / worldCamera.zoom;
				
				// Draw ghost
				//===========
				if ( worldController.getMapBounds().contains( tmpPos ) && obj != null )
				{
					// Set ghost coordinates
					//======================
					if ( altPressed == false )
					{
						// Align within tile
						//==================
						float cellX = (int) ( tmpPos.x / Map.CELL_SIZE ) * Map.CELL_SIZE;
						float cellY = (int) ( tmpPos.y / Map.CELL_SIZE ) * Map.CELL_SIZE;
						
						// Horizontal alignment
						//=====================
						if ( obj.getHorizontalAlignment() == MapObject.ALIGN_LEFT )
						{
							obj.getBounds().x = cellX;
						}
						else if ( obj.getHorizontalAlignment() == MapObject.ALIGN_CENTER )
						{
							obj.getBounds().x = cellX + Map.CELL_SIZE / 2f - obj.getBounds().width / 2f;
						}
						else if ( obj.getHorizontalAlignment() == MapObject.ALIGN_RIGHT )
						{
							obj.getBounds().x = cellX + Map.CELL_SIZE - obj.getBounds().width;
						}
						
						// Vertical alignment
						//===================
						if ( obj.getVerticalAlignment() == MapObject.ALIGN_TOP )
						{
							obj.getBounds().y = cellY + Map.CELL_SIZE - obj.getBounds().height;
						}
						else if ( obj.getVerticalAlignment() == MapObject.ALIGN_CENTER )
						{
							obj.getBounds().y = cellY + Map.CELL_SIZE / 2f - obj.getBounds().height / 2f;
						}
						else if ( obj.getVerticalAlignment() == MapObject.ALIGN_BOTTOM )
						{
							obj.getBounds().y = cellY;
						}
						
						// Adapt coords
						//=============
						obj.getBounds().x -= ( obj.getFrameSize() - obj.getBounds().width ) / 2f;
					}
					else
					{
						// Direct mouse coords
						//====================
						obj.getBounds().x = tmpPos.x - ( tmpSize / 2f ) * worldCamera.zoom;
						obj.getBounds().y = tmpPos.y - ( tmpSize / 2f ) * worldCamera.zoom;
					}
					
					// Draw ghost
					//===========
					obj.getFrames()[0].setAlpha( 0.2f );
					obj.getFrames()[0].setX    ( worldCamera.project( obj.getBounds() ).x );
					obj.getFrames()[0].setY    ( worldCamera.project( obj.getBounds() ).y );
					obj.getFrames()[0].setSize ( tmpSize, tmpSize );	
					obj.getFrames()[0].draw    ( this.getBatch() );
				}
			}
		}
		
		// Mark objects cursor
		//====================
		if ( ( sidebarWindow.getEditMode() == EditorStage.EDIT_OBJECTS ||
		     sidebarWindow.getEditMode() == EditorStage.EDIT_ATTRIBUTES ) && ctrlPressed )
		{
			// Draw icon
			//==========
			Vector2 pos = EditorInput.getMousePos();
			
			Resources.UI.editor_icon_markObject.setX( pos.x );
			Resources.UI.editor_icon_markObject.setY( pos.y );
			Resources.UI.editor_icon_markObject.draw( this.getBatch() );
			
			// Draw number label
			//==================
			if ( worldController.getMarkedMapObjects().size() > 0 )
			{
				lbl_info.setText( worldController.getMarkedMapObjects().size() + "" );
				lbl_info.setPosition( pos.x, pos.y );
				lbl_info.pack();
				lbl_info.draw( this.getBatch(), 1f );
			}
		}
	}

	// drawTriggerAreas
	//=================
	/**
	 * Draws all trigger areas (i.e. game start and end triggers).
	 */
	private void drawTriggerAreas()
	{
		// Temporary values
		//=================
		Rectangle start  = worldController.getMap().getStartArea();
		Rectangle finish = worldController.getMap().getFinishArea();
		Rectangle rect   = null;
		
		// Start area
		//===========
		this.drawDragArea( start, DRAG_TRIGGER );
		{
			rect = worldCamera.project( start );
			
			lbl_info.setText( Lang.txt( "editor_start" ) );
			lbl_info.pack   ();
			lbl_info.setX   ( rect.x + 5f );
			lbl_info.setY   ( rect.y - 5f + rect.height - lbl_info.getHeight() );
			lbl_info.draw   ( this.getBatch(), 1f );
		}

		// Finish area
		//============
		this.drawDragArea( finish, DRAG_TRIGGER );
		{
			rect = worldCamera.project( finish );
			
			lbl_info.setText( Lang.txt( "editor_finish" ) );
			lbl_info.pack   ();
			lbl_info.setX   ( rect.x + 5f );
			lbl_info.setY   ( rect.y - 5f + rect.height - lbl_info.getHeight() );
			lbl_info.draw   ( this.getBatch(), 1f );
		}
	}
	
	// drawScrollCenter
	//=================
	private void drawScrollCenter()
	{
		// Scroll center
		//==============
		if ( scrollCenter != null )
		{
			int x = (int) ( scrollCenter.x - Resources.UI.editor_icon_scrollCenter.getWidth()  / 2 );
			int y = (int) ( scrollCenter.y - Resources.UI.editor_icon_scrollCenter.getHeight() / 2 );
			
			Resources.UI.editor_icon_scrollCenter.setPosition( x, y );
			Resources.UI.editor_icon_scrollCenter.draw( this.getBatch() ) ;
		}
	}
	
	// drawDragArea
	//=============
	/**
	 * Draws the drag area rectangle.
	 * @param pos the area in world coordinates.
	 * @param mode the drag mode (i.e. {@code DRAG_REMOVE} or {@code DRAG_PLACE}).
	 */
	private void drawDragArea( Rectangle pos, int mode )
	{
		// Define sprite
		//==============
		Sprite tmp_sprite = null;
		
		if ( mode == EditorStage.DRAG_PLACE   ) tmp_sprite = Resources.UI.editor_rect_green;
		if ( mode == EditorStage.DRAG_REMOVE  ) tmp_sprite = Resources.UI.editor_rect_red;
		if ( mode == EditorStage.DRAG_HOVER   ) tmp_sprite = Resources.UI.editor_rect_white;
		if ( mode == EditorStage.DRAG_TRIGGER ) tmp_sprite = Resources.UI.editor_rect_yellow;
		
		// Render sprite
		//==============
		if ( tmp_sprite != null )
		{
			pos = worldCamera.project( pos );
			
			tmp_sprite.setPosition( pos.x, pos.y );
			tmp_sprite.setSize    ( pos.width, pos.height);
			tmp_sprite.draw       ( this.getBatch() );
		}
	}
	
	// touchDown
	//==========
	/**
	 * Catches mouse clicks if the mouse hovers the UI.
	 */
	@Override
	public boolean touchDown( int x, int y, int pointer, int button )
	{
		// Call super
		//===========
		super.touchDown( x, y, pointer, button);
		Rectangle area = new Rectangle();

		// Check side bar
		//===============
		if ( sidebarWindow.isVisible() )
		{
			area.x      = sidebarWindow.getX();
			area.y      = sidebarWindow.getY();
			area.width  = sidebarWindow.getWidth();
			area.height = sidebarWindow.getHeight();
		}
		return ( area.contains( EditorInput.getMousePos() ) );
	}
	
	// setSidebarVisible
	//==================
	public void setSidebarVisible( boolean value )
	{
		sidebarWindow.setVisible( value );
	}
	
	// setScrollCenter
	//================
	public boolean isSidebarVisible()
	{
		return ( sidebarWindow.isVisible() );
	}
	
	// setScrollCenter
	//================
	public void setScrollCenter( Vector2 newScrollCenter )
	{	
		scrollCenter = newScrollCenter;
	}
	
	// setDragRect
	//============
	public void setDragArea( int mode, Rectangle area )
	{
		dragMode = mode;
		dragArea = area;
	}
	
	// getDragRect
	//============
	public Rectangle getDragArea()
	{
		return ( dragArea );
	}
	
	// setCtrlPressed
	//===============
	public void setCtrlPressed( boolean value )
	{
		ctrlPressed = value;
	}
	
	// setAltPressed
	//==============
	public void setAltPressed( boolean value )
	{
		altPressed = value;
	}
	
	// getWorldController
	//===================
	public WorldController getWorldController()
	{
		return ( worldController );
	}

	// getSidebarWindow
	//=================
	public SidebarWindow getSidebarWindow()
	{
		return ( sidebarWindow );
	}
	
	// getEditMode
	//============
	public int getEditMode()
	{
		return ( sidebarWindow.getEditMode() );
	}
}