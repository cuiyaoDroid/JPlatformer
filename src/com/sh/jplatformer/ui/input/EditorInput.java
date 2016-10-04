package com.sh.jplatformer.ui.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.ui.stages.EditorStage;
import com.sh.jplatformer.world.WorldCamera;
import com.sh.jplatformer.world.WorldController;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

/**
 * The {@code EditorInput} class handles all editor related input events.
 * @author Stefan Hösemann
 */

public class EditorInput extends InputAdapter
{
	// Key values
	//===========
	private boolean ctrlPressed;
	private boolean altPressed;
	
	// Mouse values
	//=============
	private static Vector2 mousePos_screen;
	private static Vector2 mousePos_map;
	private static Vector3 mousePos_tmp;
	private Rectangle dragArea;
	private int dragMode;
	private Vector2 dragAreaOrigin;
	private Vector2 scrollCenter;
	private boolean scrollEnabled;
	
	// UI / map data
	//==============
	private EditorStage editorStage;
	private WorldController worldController;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapEditorInput}
	 * @param newEditorWindow the {@code EditorWindow} to work with.
	 */
	public EditorInput( EditorStage newEditorWindow )
	{	
		// World / UI
		//===========
		editorStage     = newEditorWindow;
		worldController = newEditorWindow.getWorldController();
		
		// Drag area
		//==========
		dragMode       = EditorStage.DRAG_NONE;
		dragArea       = new Rectangle();
		dragAreaOrigin = new Vector2();
		
		// Vectors
		//========
		mousePos_screen       = new Vector2();
		mousePos_map          = new Vector2();
		mousePos_tmp          = new Vector3();
		scrollCenter          = new Vector2();
		
		this.update();
	}
	
	// update
	//=======
	/**
	 * Updates all input values.
	 */
	public void update()
	{
		// Updates
		//========
		updateKeys();
		updateScrolling();
		updateTriggerAreas();
	}
	
	// keyPressed
	//===========
	public void keyPressed()
	{
		// CTRL keys
		//==========
		ctrlPressed = Gdx.input.isKeyPressed( Keys.CONTROL_LEFT ) ||
		              Gdx.input.isKeyPressed( Keys.CONTROL_RIGHT ) ;
		
		// ALT keys
		//=========
		altPressed = Gdx.input.isKeyPressed( Keys.ALT_LEFT ) ||
		             Gdx.input.isKeyPressed( Keys.ALT_RIGHT );
		
		// Set water height
		//=================
		if ( editorStage.getEditMode() == EditorStage.EDIT_ENVIRONMENT &&
		     Gdx.input.isKeyPressed( Keys.W ) && editorStage.getKeyboardFocus() == null )
		{
			Map map = worldController.getMap();
			
			map.setWaterId      ( editorStage.getSidebarWindow().getSelectedWaterTile() );
			map.setWaterSpeedId ( editorStage.getSidebarWindow().getSelectedWaterSpeedId() );
			map.setWaterHeight  ( mousePos_map.y );
		}
	}
		
	// keyDown
	//========
	@Override
	public boolean keyDown( int key )
	{
		// Get camera
		//===========
		WorldCamera camera = worldController.getWorldCamera();
		
		// Pause / resume
		//===============
		if ( key == Keys.ESCAPE || key == Keys.P )
		{
			JPlatformerGame.get().setPaused( !JPlatformerGame.get().isPaused() );
			
			Resources.UI.sound_game_pause.play();
		}
		
		// Show / hide sidebar
		//====================
		if ( ctrlPressed && dragMode == EditorStage.DRAG_NONE )
		{	
			// Hide side bar
			//==============
			if ( key == Keys.H )
			{
				editorStage.setSidebarVisible( !editorStage.isSidebarVisible() );
			}
		}
		
		// Initialize scroll center
		//=========================
		if ( key == Keys.Q )
		{
			this.initScrollCenter( mousePos_screen );
		}
		
		// Zoom in
		//========
		if ( key == Keys.PLUS )
		{
			camera.zoomIn();
		}
		
		// Zoom out
		//=========
		if ( key == Keys.MINUS )
		{
			camera.zoomOut();
		}
		
		return ( false );
	}
	
	// keyDown
	//========
	@Override
	public boolean keyUp( int key )
	{
		// Release scroll center
		//======================
		if ( key == Keys.Q )
		{
			this.clearScrollCenter();
		}
		return ( false );
	}
	
	// scrolled
	//=========
	@Override
	public boolean scrolled( int scroll )
	{
		// Update mouse
		//=============
		this.mouseMoved( Gdx.input.getX(), Gdx.input.getY() );
		
		// Mouse zoom
		//===========
		if ( scroll == -1 )
		{
			worldController.getWorldCamera().zoomIn();
		}
		else
		{
			worldController.getWorldCamera().zoomOut();
		}
		return ( true );
	}
	
	// touchDown
	//==========
	@Override
	public boolean touchDown( int x, int y, int pointer, int button )
	{
		// Update drag area
		//=================
		if ( !ctrlPressed && !scrollEnabled && dragMode == EditorStage.DRAG_NONE )
		{
			// Determine drag mode
			//====================
			if ( button == Buttons.LEFT  ) dragMode = EditorStage.DRAG_PLACE;
			if ( button == Buttons.RIGHT ) dragMode = EditorStage.DRAG_REMOVE;
			
			// Initialize drag area
			//=====================
			dragAreaOrigin.x = mousePos_map.x;
			dragAreaOrigin.y = mousePos_map.y;
			dragArea.x       = mousePos_map.x;
			dragArea.y       = mousePos_map.y;
			dragArea.width   = 1f;
			dragArea.height  = 1f;
			
			editorStage.setDragArea( dragMode, dragArea );
		}
		
		// Initialize scroll center
		//=========================
		if ( !ctrlPressed && button == Buttons.MIDDLE )
		{
			this.initScrollCenter( mousePos_screen );
		}
				
		// Mark map objects
		//=================
		if ( ( editorStage.getEditMode() == EditorStage.EDIT_OBJECTS ||
		       editorStage.getEditMode() == EditorStage.EDIT_ATTRIBUTES )
		     && ctrlPressed && button == Buttons.LEFT )
		{
			worldController.addMarkedMapObject( worldController.getHoveredMapObject() );
			
			editorStage.update();
		}
		return ( true );
	}
	
	// touchUp
	//========
	@Override
	public boolean touchUp( int x, int y, int pointer, int button )
	{
		// Release mouse drag
		//===================
		if ( button == Buttons.LEFT || button == Buttons.RIGHT )
		{
			// Placement mode
			//===============
			if ( dragMode == EditorStage.DRAG_PLACE )
			{
				// Cells
				//======
				if ( editorStage.getEditMode() == EditorStage.EDIT_CELLS )
				{
					// Set cells
					//==========
					worldController.setCells( dragArea,
					                          editorStage.getSidebarWindow().getSelectedMapTile() );
					
					// Play sound
					//===========
					Resources.UI.sound_editor_addTile.play();
				}
				
				// Objects
				//========
				if ( editorStage.getEditMode() == EditorStage.EDIT_OBJECTS )
				{
					// Temporary values
					//=================
					String className    = editorStage.getSidebarWindow().getSelectedMapObject().getClass().getName();
					Rectangle bounds    = editorStage.getSidebarWindow().getSelectedMapObject().getBounds();
					MapObject newObject = MapObject.createFromClassName( className, worldController );
					
					// Add object
					//===========
					worldController.addMapObject( newObject,
					                              bounds.x + newObject.getFrameSize() / 2f,
					                              bounds.y + newObject.getFrameSize() / 2f - 1f,
					                              true );
					
					worldController.resetMapObjectRoutines();
					
					// Register as player
					//===================
					if ( newObject.getClass().getSimpleName().equals( "Player" ) )
					{
						worldController.removeMapObject( worldController.getPlayer() );
						worldController.setPlayer( newObject );
					}
					
					// Play sound
					//===========
					Resources.UI.sound_editor_addObject.play();
				}
			}
			
			// Removement mode
			//================
			if ( dragMode == EditorStage.DRAG_REMOVE )
			{
				// Floor
				//======
				if ( editorStage.getEditMode() == EditorStage.EDIT_CELLS )
				{
					worldController.setCells( dragArea, -1 );
					
					Resources.UI.sound_editor_remove.play();
				}

				// Objects
				//========
				if ( editorStage.getEditMode() == EditorStage.EDIT_OBJECTS )
				{
					worldController.removeMapObjects( dragArea );
					
					Resources.UI.sound_editor_remove.play();
				}
			}
			
			// Reset values
			//=============
			dragMode        = EditorStage.DRAG_NONE;
			dragArea.x      = 0;
			dragArea.y      = 0;
			dragArea.width  = 0;
			dragArea.height = 0;
			
			// Update
			//=======
			editorStage.setDragArea( dragMode, dragArea );
			editorStage.update();
		}
		
		// Release mouse scroll
		//=====================
		if ( button == Buttons.MIDDLE )
		{
			this.clearScrollCenter();
		}
		return ( true );
	}
	
	// touchDragged
	//=============
	@Override
	public boolean touchDragged( int x, int y, int pointer )
	{
		// Update
		//=======
		this.mouseMoved( x, y );
		
		// Update drag area
		//=================
		if ( dragMode != EditorStage.DRAG_NONE )
		{
			// Apply coordinates
			//==================
			dragArea.x      = dragAreaOrigin.x;
			dragArea.y      = dragAreaOrigin.y;
			dragArea.width  = mousePos_map.x - dragArea.x;
			dragArea.height = mousePos_map.y - dragArea.y;
			
			// Flip negative sizes
			//====================
			if ( mousePos_map.x < dragAreaOrigin.x )
			{
				dragArea.x     = mousePos_map.x;
				dragArea.width = ( dragAreaOrigin.x - dragArea.x );
			}
			if ( mousePos_map.y < dragAreaOrigin.y )
			{
				dragArea.y      = mousePos_map.y;
				dragArea.height = ( dragAreaOrigin.y - dragArea.y );
			}
			
			// Pass to editor
			//===============
			editorStage.setDragArea( dragMode, dragArea );
		}
		
		return ( false );
	}
	
	// mouseMoved
	//===========
	@Override
	public boolean mouseMoved( int x, int y )
	{
		// Screen coordinates
		//===================
		mousePos_screen.x = x;
		mousePos_screen.y = Gdx.graphics.getHeight() - y;
		
		// Map coordinates
		//================
		mousePos_tmp.set( x, y, 0 );
		mousePos_tmp = worldController.getWorldCamera().unproject( mousePos_tmp );
		
		mousePos_map.x = mousePos_tmp.x;
		mousePos_map.y = mousePos_tmp.y;
		
		return ( false );
	}
	
	// getMousePos
	//============
	/**
	 * @return the mouse position in screen coordinates.
	 */
	public static Vector2 getMousePos()
	{
		return ( mousePos_screen );
	}
	
	// getMousePosInWorld
	//===================
	/**
	 * @return the mouse position in world coordinates.
	 */
	public static Vector2 getMousePosInWorld()
	{
		return ( mousePos_map );
	}
	
	// initScrollCenter
	//=================
	private void initScrollCenter( Vector2 coordinates )
	{
		scrollEnabled  = true;
		scrollCenter.x = coordinates.x;
		scrollCenter.y = coordinates.y;
		
		editorStage.setScrollCenter( scrollCenter );
	}
	
	// clearScrollCenter
	//==================
	private void clearScrollCenter()
	{
		scrollEnabled = false;
		editorStage.setScrollCenter( null );
	}
	
	// updateScrolling
	//================
	private void updateScrolling()
	{
		// Scroll camera
		//==============
		if ( scrollEnabled == true )
		{
			// Get camera
			//===========
			WorldCamera camera = worldController.getWorldCamera();
			
			// Move camera
			//============
			float f = camera.zoom * 3f;
			float x = ( mousePos_screen.x - scrollCenter.x ) * f * Gdx.graphics.getDeltaTime();
			float y = ( mousePos_screen.y - scrollCenter.y ) * f * Gdx.graphics.getDeltaTime();

			camera.move( x, y );
			
			// Temporary values
			//=================
			Vector3 pos  = camera.position;
			float buffer = camera.zoom * 100f;
			float w      = camera.zoom * camera.viewportWidth;
			float h      = camera.zoom * camera.viewportHeight;
			float worldW = worldController.getMapBounds().width;
			float worldH = worldController.getMapBounds().height;

			// Limit position
			//===============
			if ( pos.x < -w / 2f + buffer )          pos.x = -w / 2f + buffer;
			if ( pos.y < -h / 2f + buffer )          pos.y = -h / 2f + buffer;
			if ( pos.x > +w / 2f - buffer + worldW ) pos.x = +w / 2f - buffer + worldW;
			if ( pos.y > +h / 2f - buffer + worldH ) pos.y = +h / 2f - buffer + worldH;
			
			// Update
			//=======
			camera.setPosition( pos );
			this.touchDragged( Gdx.input.getX(), Gdx.input.getY(), 0 );
		}
	}
	
	// updateKeys
	//===========
	private void updateKeys()
	{
		editorStage.setCtrlPressed( ctrlPressed );
		editorStage.setAltPressed( altPressed );
		this.keyPressed();
	}
	
	// updateTriggerAreas
	//===================
	private void updateTriggerAreas()
	{
		// Update area bounds
		//===================
		if ( editorStage.getEditMode() == EditorStage.EDIT_TRIGGERS )
		{
			// Start area
			//===========
			if ( dragMode == EditorStage.DRAG_PLACE )
			{
				if ( worldController.getMapBounds().contains( mousePos_map ) )
				{
					// Temporary values
					//=================
					float posX = Map.CELL_SIZE * (int) ( mousePos_map.x / Map.CELL_SIZE );
					float posY = Map.CELL_SIZE * (int) ( mousePos_map.y / Map.CELL_SIZE );
					
					worldController.getMap().setStartArea( posX, posY, Map.CELL_SIZE, Map.CELL_SIZE );
				}
			}
			
			// Finish area
			//============
			if ( dragMode == EditorStage.DRAG_REMOVE )
			{
				worldController.getMap().setFinishArea( dragArea.x,
				                                        dragArea.y,
				                                        dragArea.width,
				                                        dragArea.height );
			}
		}
	}
}
