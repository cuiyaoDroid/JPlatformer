package com.sh.jplatformer.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.sh.jplatformer.JPlatformerGame;
import com.sh.jplatformer.resources.Resources;
import com.sh.jplatformer.world.map.Map;
import com.sh.jplatformer.world.map.MapCell;
import com.sh.jplatformer.world.map.MapObject;

/**
 * The {@code WorldRenderer} renders a world stored in a {@code WorldController}.
 * @author Stefan Hösemann
 */

public class WorldRenderer
{
	// World properties
	//=================
	private WorldController worldController;
	
	// Rendering properties
	//=====================
	private ShapeRenderer shapeRenderer;
	private Rectangle scissors;
	private float waterOffset;
	
	// Constructor
	//============
	/**
	 * Constructs a new {@code MapRenderer}.
	 * @param newWorldController the {@code WorldController} to render.
	 */
	public WorldRenderer( WorldController newWorldController )
	{
		// World properties
		//=================
		worldController = newWorldController;
		
		// Rendering properties
		//=====================
		shapeRenderer = new ShapeRenderer();
		scissors      = new Rectangle();
	}
	
	// render
	//=======
	/**
	 * Renders all {@code Map} components.
	 * @param spriteBatch the {@code SpriteBatch} to render.
	 */
	public void render( SpriteBatch spriteBatch )
	{	
		// Check world size
		//=================
		if ( worldController.getMap().getColumns() == 0 ||
		     worldController.getMap().getRows()    == 0 )
		{
			return;
		}
		
		// Calculate scissors
		//===================
		ScissorStack.calculateScissors( worldController.getWorldCamera(),
		                                spriteBatch.getTransformMatrix(),
		                                worldController.getMapBounds(),
		                                scissors );
		
		// Draw map components
		//====================
		ScissorStack.pushScissors( scissors );
		{
			this.drawBackground( spriteBatch );
			this.drawForeground( spriteBatch );
			this.drawMap       ( spriteBatch );
			this.drawMapObjects( spriteBatch );
			this.drawWater     ( spriteBatch );
			
			spriteBatch.flush();
		}
		ScissorStack.popScissors();
		
		// Draw outline
		//=============
		if ( worldController.isLive() == false )
		{
			this.drawOutline();
		}
	}
	
	// drawOutline
	//============
	/**
	 * Draws the {@code Map} outline with the internal {@code ShapeRenderer}.
	 */
	private void drawOutline()
	{
		// Draw outline
		//=============
		shapeRenderer.setProjectionMatrix( worldController.getWorldCamera().combined );
		shapeRenderer.begin( ShapeType.Line );
		{
			shapeRenderer.setColor( Color.WHITE );
			shapeRenderer.rect( worldController.getMapBounds().x      + 0.5f,
			                    worldController.getMapBounds().y      + 0.5f,
			                    worldController.getMapBounds().width  - 0.5f,
			                    worldController.getMapBounds().height - 0.5f );
		}
		shapeRenderer.end();
	}
	
	// drawWater
	//==========
	/**
	 * Draws the water.
	 * @param spriteBatch the {@code SpriteBatch} to draw.
	 */
	private void drawWater( SpriteBatch spriteBatch )
	{
		// Get map
		//========
		Map map = worldController.getMap();
		
		// Update water offset
		//====================
		if ( map.getWaterHeight() <= 0f )
		{
			return;
		}
		else if ( JPlatformerGame.get().isPaused() == false )
		{
			// Add offset
			//===========
			waterOffset += WorldController.worldDelta * Map.WATER_SPEEDS[ map.getWaterSpeedId() ];

			// Check new offset
			//=================
			if ( waterOffset > Map.CELL_SIZE ) waterOffset = 0f;
			if ( waterOffset < 0f )            waterOffset = Map.CELL_SIZE;
		}
		
		// Draw surface
		//=============
		for ( int i = 0; i < map.getColumns(); i++ )
		{
			// Temporary values
			//=================
			WorldCamera camera = worldController.getWorldCamera();
			float zoom         = camera.zoom;
			float x            = i * Map.CELL_SIZE;
			float y            = map.getWaterHeight() - Map.CELL_SIZE;
			
			// Check visibility and render
			//============================
			if ( ( x + Map.CELL_SIZE ) * zoom > camera.getOffset().x * zoom )
			{
				if ( x < camera.getOffset().x + camera.viewportWidth * zoom )
				{
					spriteBatch.draw( Resources.WORLD.texture_waterTiles,
					                  x, y,
					                  Map.CELL_SIZE, Map.CELL_SIZE,
					                  (int) waterOffset, Map.CELL_SIZE * map.getWaterId(),
					                  Map.CELL_SIZE, Map.CELL_SIZE,
					                  false, false );
				}
			}
		}
		
		// Render color
		//=============
		spriteBatch.draw( Resources.WORLD.texture_waterTiles,
		                  0f, 0f,
		                  Map.CELL_SIZE * map.getColumns(), map.getWaterHeight() - Map.CELL_SIZE,
		                  0, Map.CELL_SIZE * ( map.getWaterId() + 1 ) - 1,
		                  1, 1,
		                  false, false );
	}
	
	// drawMap
	//========
	/**
	 * Draws all visible {@code MapCells}.
	 * @param spriteBatch the {@code SpriteBatch} to render.
	 */
	private void drawMap( SpriteBatch spriteBatch )
	{
		for ( MapCell cell : worldController.getVisibleCells() )
		{
			if ( cell.tileSetId > -1 )
			{
				spriteBatch.draw( Resources.WORLD.mapTiles[cell.tileSetId][cell.tileId],
				                  cell.x * Map.CELL_SIZE,
				                  cell.y * Map.CELL_SIZE );
				
				this.drawTileCorner( cell, spriteBatch );
			}
		}
	}
	
	// drawTileCorner
	//===============
	/**
	 * Draws {@code tileId} specific corner sprites.
	 * @param cell the {@code MapCell} to check.
	 * @param spriteBatch the {@code SpriteBatch} to render.
	 */
	private void drawTileCorner( MapCell cell, SpriteBatch spriteBatch )
	{
		// Temporary values
		//=================
		int tileSetId = cell.tileSetId;
		
		// Right side corners
		//===================
		if ( worldController.compareTiles( tileSetId, cell.x+1, cell.y ) >= +0 )
		{
			// Bottom right corner
			//====================
			if ( worldController.compareTiles( tileSetId, cell.x,   cell.y-1 ) >= +0 &&
			     worldController.compareTiles( tileSetId, cell.x+1, cell.y-1 ) == -1 )
			{
				spriteBatch.draw( Resources.WORLD.mapTiles[tileSetId][19],
				                  cell.x * Map.CELL_SIZE + Map.CELL_SIZE / 2,
				                  cell.y * Map.CELL_SIZE - Map.CELL_SIZE / 2 );
			}
			
			// Top right corner
			//=================
			if ( worldController.compareTiles( tileSetId, cell.x,   cell.y+1 ) >= +0 &&
			     worldController.compareTiles( tileSetId, cell.x+1, cell.y+1 ) == -1 )
			{
				spriteBatch.draw( Resources.WORLD.mapTiles[tileSetId][17],
		                          cell.x * Map.CELL_SIZE + Map.CELL_SIZE / 2,
		                          cell.y * Map.CELL_SIZE + Map.CELL_SIZE / 2 );
			}
		}
			
		// Left side corners
		//==================
		if ( worldController.compareTiles( tileSetId, cell.x-1, cell.y ) >= +0 )
		{
			// Bottom left corner
			//===================
			if ( worldController.compareTiles( tileSetId, cell.x,   cell.y-1 ) >= +0 &&
			     worldController.compareTiles( tileSetId, cell.x-1, cell.y-1 ) == -1 )
			{
				spriteBatch.draw( Resources.WORLD.mapTiles[tileSetId][18],
		                          cell.x * Map.CELL_SIZE - Map.CELL_SIZE / 2,
		                          cell.y * Map.CELL_SIZE - Map.CELL_SIZE / 2 );
			}
			
			// Top left corner
			//================
			if ( worldController.compareTiles( tileSetId, cell.x,   cell.y+1 ) >= +0 &&
			     worldController.compareTiles( tileSetId, cell.x-1, cell.y+1 ) == -1 )
			{
				spriteBatch.draw( Resources.WORLD.mapTiles[tileSetId][16],
		                          cell.x * Map.CELL_SIZE - Map.CELL_SIZE / 2,
		                          cell.y * Map.CELL_SIZE + Map.CELL_SIZE / 2 );
			}
		}
	}
	
	// drawMapObjects
	//===============
	/**
	 * Draws all {@code MapObjects}.
	 * @param batch the {@code SpriteBatch} to draw sprites with.
	 */
	private void drawMapObjects( SpriteBatch batch )
	{
		for ( MapObject object : worldController.getMapObjects() )
		{
			object.draw( batch );
		}
	}
	
	// drawBackground
	//===============
	/**
	 * Draws the static background.
	 * @param spriteBatch {@code SpriteBatch} to render.
	 */
	private void drawBackground( SpriteBatch spriteBatch )
	{
		// Load texture
		//=============
		Resources.WORLD.loadBackground( worldController.getMap().getBackgroundFile() );
		
		// Draw sprite
		//============
		if ( Resources.WORLD.background != null )
		{
			// Update bounds + scale
			//======================
			WorldCamera camera = worldController.getWorldCamera();
			Sprite sprite      = Resources.WORLD.background;
			
			float x = camera.getOffset().x;
			float y = camera.getOffset().y;
			float w = camera.viewportWidth;
			float h = sprite.getHeight() * ( w / sprite.getWidth() );
			
			// Adapt height resize
			//====================
			if ( camera.viewportHeight > h )
			{
				h = camera.viewportHeight;
				w = sprite.getWidth() * ( h / sprite.getHeight() );
			}

			// Draw background sprite
			//=======================
			sprite.setOrigin( 0, 0 );
			sprite.setScale ( camera.zoom );
			sprite.setBounds( x, y, w, h );
			sprite.draw     ( spriteBatch );
		}
	}
	
	// drawForeground
	//===============
	/**
	 * Draws the scrolling foreground.
	 * @param spriteBatch {@code SpriteBatch} to render.
	 */
	private void drawForeground( SpriteBatch spriteBatch )
	{
		// Load texture
		//=============
		Resources.WORLD.loadForeground( worldController.getMap().getForegroundFile() );
		
		// Temporary values
		//=================	
		WorldCamera camera = worldController.getWorldCamera();
		Map map            = worldController.getMap();
		Sprite sprite      = Resources.WORLD.foreground;
		float zoom         = camera.zoom;
		
		// Limit zoom
		//===========
		if ( zoom < 1f )
		{
			zoom = 1f;
		}
		
		// Draw foreground tiles
		//========================
		if ( sprite != null )
		{
			// Iterations
			//===========
			int max = (int) ( ( Map.CELL_SIZE * map.getColumns() * 2 ) / sprite.getWidth() + 1 ) ;
			
			for ( int i = 0; i < max; i++ )
			{
				// Get coordinates
				//================
				float x = i * sprite.getWidth() + camera.getOffset().x / ( 2 * zoom );
				float y = - ( camera.getOffset().y / 6 )  / ( zoom );
				
				if ( y > 0 )
				{
					y = 0;
				}
				
				// Check visibility and render
				//============================
				if ( ( y + sprite.getHeight() ) * zoom > camera.getOffset().y * zoom &&
				     ( x + sprite.getWidth()  ) * zoom > camera.getOffset().x * zoom )
				{
					if ( x < camera.getOffset().x + camera.viewportWidth * zoom )
					{
						spriteBatch.draw( sprite, x, y );
					}
				}
			}
		}
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
		worldController.getWorldCamera().resize( width, height );
	}
}