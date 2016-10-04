package com.sh.jplatformer.world;

import java.io.Serializable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code WorldCamera} class extends {@code OrthographicCamera} and provides a utility for
 * map-related camera operations, such as free movement or targeting and following particular map
 * objects.
 * @author Stefan Hösemann
 */

public class WorldCamera extends OrthographicCamera implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	
	// Zoom steps
	//===========
	private static final float zoomSteps[] =
	{
		0.5f,
		0.66666f,
		1f,
		1.25f,
		1.66666f,
		2.5f,
		3.33333f,
		5f,
		10f
	};
	
	// Properties
	//===========
	private WorldController worldController;
	private MapObject target;
	private int currentZoomStep;
	private transient ScreenViewport viewport;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code WorldCamera}.
	 * @param newWorldController the {@code WorldController} representing the game world.
	 */
	public WorldCamera( WorldController newWorldController )
	{
		worldController = newWorldController;
		resetZoom();
	}
	
	// update
	//=======
	@Override
	public void update()
	{
		// Update zoom
		//============
		this.updateZoom();
		
		// Follow target
		//==============
		if ( target != null )
		{
			// Handle uneven lines
			//====================
			float evenX = 0.0f;
			float evenY = 0.0f;
			
			if ( this.viewportWidth  % 2 != 0 ) evenX = 0.5f;
			if ( this.viewportHeight % 2 != 0 ) evenY = 0.5f;
			
			// Set position
			//=============
			this.position.x = target.getBounds().x + target.getBounds().width  / 2f + evenX;
			this.position.y = target.getBounds().y + target.getBounds().height / 2f + evenY;
		}
		super.update();
	}
	
	// move
	//=====
	/**
	 * Moves the camera by the given vector and disables the current target.
	 * @param movement the movement vector.
	 */
	public void move( Vector2 movement )
	{
		this.move( movement.x, movement.y );
	}
	
	// move
	//=====
	/**
	 * Moves the camera by the given vector and disables the current target.
	 * @param x the movement on the x-axis.
	 * @param y the movement on the y-axis.
	 */
	public void move( float x, float y )
	{
		target = null;

		this.position.x += x;
		this.position.y += y;
	}
	
	// resize
	//=======
	/**
	 * Adapts the camera viewport.
	 * @param width the new width in pixels.
	 * @param height the new height in pixels.
	 */
	public void resize( int width, int height )
	{
		// Apply viewport
		//===============
		if ( viewport == null )
		{
			viewport = new ScreenViewport( this );
			viewport.apply( false);
		}
		
		// Update
		//=======
		viewport.update( width, height );
		this.update();
	}
	
	// center
	//=======
	/**
	 * Centers the camera on the center of the {@code Map} and removes the current target.
	 */
	public void center()
	{
		this.center( worldController.getMap().getColumns() / 2,
		             worldController.getMap().getRows()    / 2 );
	}
	
	// center
	//=======
	/**
	 * Centers the camera on the specified cell and removes the current target.
	 * @param column the column to focus.
	 * @param row the row to focus.
	 */
	public void center( int column, int row )
	{
		target = null;
		
		this.position.x = ( this.toUnits( column * Map.CELL_SIZE + Map.CELL_SIZE / 2 ) );
		this.position.y = ( this.toUnits( row    * Map.CELL_SIZE + Map.CELL_SIZE / 2  ) );
	}
	
	// updateZoom
	//===========
	private void updateZoom()
	{
		// Zoom in
		//========
		if ( zoom > zoomSteps[ currentZoomStep ] )
		{
			// Decrease zoom
			//==============
			zoom -= Gdx.graphics.getDeltaTime() * ( zoom * 3.5f );
			
			// Limit value
			//============
			if ( zoom < zoomSteps[ currentZoomStep ] )
			{
				zoom = zoomSteps[ currentZoomStep ];
			}
		}
		
		// Zoom out
		//=========
		if ( zoom < zoomSteps[ currentZoomStep ] )
		{
			// Decrease zoom
			//==============
			zoom += Gdx.graphics.getDeltaTime() * ( zoom * 3.5f );
			
			// Limit value
			//============
			if ( zoom > zoomSteps[ currentZoomStep ] )
			{
				zoom = zoomSteps[ currentZoomStep ];
			}
		}
	}
	
	// resetZoom
	//==========
	/**
	 * Resets the zoom to 100%.
	 */
	public void resetZoom()
	{
		// Update zoom step
		//=================
		currentZoomStep = 2;
		zoom = zoomSteps[ currentZoomStep ];
	}
	
	// zoomIn
	//=======
	/**
	 * Sets the next zoom step from an array of predefined zoom steps.
	 */
	public void zoomIn()
	{
		// Ignore if invalid
		//==================
		if ( currentZoomStep - 1 < 0 )
		{
			return;
		}
		currentZoomStep--;
	}
	
	// zoomOut
	//========
	/**
	 * Sets the previous zoom step from an array of predefined zoom steps.
	 */
	public void zoomOut()
	{
		// Ignore if invalid
		//==================
		if ( currentZoomStep + 1 >= zoomSteps.length )
		{
			return;
		}
		currentZoomStep++;
	}
	
	// getOffset
	//==========
	/**
	 * @return the position of the camera with an origin in the bottom left corner of the screen.
	 */
	public Vector2 getOffset()
	{
		Vector2 offset = new Vector2();
		
		offset.x = this.position.x - ( this.viewportWidth  * zoom / 2 );
		offset.y = this.position.y - ( this.viewportHeight * zoom / 2 );
		
		return ( offset );
	}
	
	// setPosition
	//============
	/**
	 * Sets the camera position and removes the current target.
	 * @param newPosition the new position.
	 */
	public void setPosition( Vector3 newPosition )
	{
		target = null;
		
		this.position.x = newPosition.x;
		this.position.y = newPosition.y;
		this.position.z = newPosition.z;
	}
	
	// getPosition
	//============
	public Vector3 getPosition()
	{
		return ( position );
	}
	
	// setTarget
	//==========
	public void setTarget( MapObject newTarget )
	{
		target = newTarget;
	}
	
	// hasTarget
	//==========
	public boolean hasTarget()
	{
		return ( target != null );
	}
	
	// project
	//========
	/**
	 * Projects a {@code Vector2} of map coordinates to screen coordinates.
	 * @param area the {@code Vector2} to transform.
	 * @return the projected {@code Vector2}.
	 */
	public Vector2 project( Vector2 vector )
	{
		Vector3 temp = super.project( new Vector3( vector.x, vector.y, 0 ) );
		
		return ( new Vector2( temp.x, temp.y ) );
	}
	
	// project
	//========
	/**
	 * Projects a rectangular area of map coordinates to screen coordinates.
	 * @param area the {@code Rectangle} to transform.
	 * @return the projected {@code Rectangle}.
	 */
	public Rectangle project( Rectangle area )
	{
		// Transform rectangle coords
		//===========================
		Rectangle rect = new Rectangle( area );
		
		rect.x      = this.project( new Vector3( rect.x, rect.y, 0 ) ).x;
		rect.y      = this.project( new Vector3( rect.x, rect.y, 0 ) ).y;
		rect.width  = +rect.width  / zoom;
		rect.height = -rect.height / zoom;
		
		// Adapt size
		//===========
		if ( rect.width < 0 )
		{
			rect.width = -rect.width;
			rect.x     = rect.x - rect.width;
		}
		if ( rect.height < 0 )
		{
			rect.height = -rect.height;
		}
		return ( rect );
	}
	
	// unproject
	//==========
	/**
	 * Unprojects a rectangular area of screen coordinates to world coordinates.
	 * @param area the {@code Rectangle} to unproject with an origin in the bottom left corner.
	 * @return the unprojected {@code Rectangle}.
	 */
	public Rectangle unproject( Rectangle area )
	{
		// Flip y-axis
		//============
		Rectangle rect = new Rectangle( area );
		rect.y = this.viewportHeight - rect.y;
		
		// Transform
		//==========
		rect.x      = this.unproject( new Vector3( rect.x, rect.y, 0 ) ).x;
		rect.y      = this.unproject( new Vector3( rect.x, rect.y, 0 ) ).y;
		rect.width  = +rect.width  * zoom;
		rect.height = -rect.height * zoom;
		
		// Adapt size
		//===========
		if ( rect.width < 0 )
		{
			rect.width = -rect.width;
			rect.x     = rect.x - rect.width;
		}
		if ( rect.height < 0 )
		{
			rect.height = -rect.height;
		}
		
		return ( rect );
	}
	
	// toUnits
	//========
	/**
	 * @param pixels the amount of pixels to convert into camera units.
	 * @return the converted value.
	 */
	public float toUnits( int pixels )
	{
		return ( viewport.getUnitsPerPixel() * pixels );
	}
}
