package com.sh.jplatformer.world.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sh.jplatformer.util.Lang;
import java.io.Serializable;

/**
 * The {@code Map} class provides a model for two-dimensional game maps.
 * @author Stefan Hösemann
 */

public class Map implements Serializable
{
	// Constants
	//==========
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE          = 64;
	public static final int COUNTDOWN_DISABLED = 0;
	
	// Difficulties
	//=============
	public static final String[] DIFFICULTIES =
	{
		 Lang.txt( "game_easy" ), Lang.txt( "game_medium" ), Lang.txt( "game_hard" )
	};
		
	// Water speeds
	//=============
	public static final int[] WATER_SPEEDS =
	{
		 150, 50, 0, -50, -150
	};
	
	// Array properties
	//================
	private MapCell cells[];
	private int rows;
	private int columns;
	private Rectangle mapBounds;
	
	// Description
	//============
	private String title;
	private String creator;
	private int difficultyId;
	
	// Records
	//========
	private int highScore;
	private long bestTime;
	
	// Timer
	//======
	private long countdownTime;
	
	// Environment
	//============
	private String backgroundFile;
	private String foregroundFile;
	private String environmentSoundFile;
	
	// Water
	//======
	private int   waterId;
	private int   waterSpeedId;
	private float waterHeight;
	
	// Trigger areas
	//==============
	private Rectangle startArea;
	private Rectangle finishArea;
	
	// Constructor
	//============
	/**
	 * Creates a new {@code Map}.
	 * @param newColumns the number of columns of the map.
	 * @param newRows the number of rows of the map.
	 * @param newTitle the title of the map.
	 */
	public Map( int newColumns, int newRows )
	{
		this.reset( newColumns, newRows );
	}
	
	// reset
	//======
	/**
	 * Resets all properties and resizes the map.
	 * @param newColumns the number of columns of the map.
	 * @param newRows the number of rows of the map.
	 * @param newTitle the title of the map.
	 */
	public void reset( int newColumns, int newRows )
	{		
		// Description
		//============
		creator      = Lang.txt( "editor_unknown" );
		title        = Lang.txt( "editor_newWorld" );
		difficultyId = 0;
		
		// Records
		//========
		highScore = 0;
		bestTime  = 0;
		
		// Timer
		//======
		countdownTime = Map.COUNTDOWN_DISABLED;
		
		// Environment
		//============
		setForegroundFile( null );
		setBackgroundFile( null );
		setEnvironmentSoundFile( null );
		
		// Water
		//======
		waterId      = 0;
		waterSpeedId = 1;
		waterHeight  = 0f;
		
		// Trigger areas
		//==============
		startArea  = new Rectangle( 0f, CELL_SIZE, CELL_SIZE, CELL_SIZE );
		finishArea = new Rectangle( 0f, 0f, CELL_SIZE, CELL_SIZE );
		
		// Array properties
		//=================
		columns   = newColumns;
		rows      = newRows;
		cells     = new MapCell[columns * rows];
		mapBounds = new Rectangle( 0f, 0f, Map.CELL_SIZE * columns, Map.CELL_SIZE * rows );
		
		// Create cells
		//=============
		if ( cells.length > 0 )
		{
			for ( int i = 0; i < columns * rows; i++ )
			{
				cells[i] = new MapCell();
				cells[i].x = ( (int) ( i / rows ) );
				cells[i].y = i - ( cells[i].x * rows );
			}
		}
	}
	
	// isBlocked
	//==========
	/**
	 * @param position the absolute position to check on the map in units.
	 * @return {@code true} if the cell is blocked, {@code false} otherwise or if an error occurred.
	 */
	public boolean isBlocked( Vector2 position )
	{
		return ( this.isBlocked( this.getCellAt( position ).x,
		                         this.getCellAt( position ).y ) );
	}
	
	// isBlocked
	//==========
	/**
	 * @param x the x-position to check on the map in units.
	 * @param y the y-position to check on the map in units.
	 * @return {@code true} if the cell is blocked, {@code false} otherwise or if an error occurred.
	 */
	public boolean isBlocked( float x, float y )
	{
		return ( this.isBlocked( this.getCellAt( x, y ).x,
		                         this.getCellAt( x, y ).y ) );
	}
	
	// isBlocked
	//===========
	/**
	 * @param col the column of the cell.
	 * @param row the row of the cell.
	 * @return {@code true} if the cell is blocked or if the cell is out of map, {@code false}
	 * otherwise.
	 */
	public boolean isBlocked( int col, int row )
	{
		// Read Data
		//==========
		try
		{
			return ( this.getCellAt( col, row ).tileSetId >= 0 );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading map value at " + "[x=" + col + ", y=" + row + "]!" );
			return ( true );
		}
	}
	
	// getCellAt
	//==========
	/**
	 * @param position the absolute position on the map in screen coordinates.
	 * @return the {@code MapCell} at the specified position. If the specified cell is out of map,
	 * the last valid cell is returned.
	 */
	public MapCell getCellAt( Vector2 position )
	{
		return ( this.getCellAt( position.x, position.y ) );
	}
	
	// getCellAt
	//==========
	/**
	 * @param x the absolute x-position on the map in screen coordinates.
	 * @param y the absolute y-position on the map in screen coordinates.
	 * @return the {@code MapCell} at the specified position. If the specified cell is out of map,
	 * the last valid cell is returned.
	 */
	public MapCell getCellAt( float x, float y )
	{
		// Calculate cell
		//===============
		int col = (int) ( x / CELL_SIZE );
		int row = (int) ( y / CELL_SIZE );
		
		// Set limits
		//===========
		if ( x < 0 ) col = 0;
		if ( y < 0 ) row = 0;
		if ( col > columns-1 ) col = columns-1;
		if ( row > rows-1    ) row = rows-1;
		
		return ( this.getCellAt( col, row ) );
	}
	
	// getCellAt
	//==========
	/**
	 * @param col the column of the cell.
	 * @param row the row of the cell.
	 * @return the specified {@code MapCell}. If an error occurred or if the cell is out of the map,
	 * {@code null} is returned.
	 */
	public MapCell getCellAt( int col, int row )
	{
		// Read Data
		//==========
		try
		{
			return ( cells[col * rows + row] );
		}
		catch ( Exception e )
		{
			//System.err.println( "Error reading map value at " + "[x=" + col + ", y=" + row + "]!" );
			return ( null );
		}
	}
	
	// getMapBounds
	//=============
	public Rectangle getMapBounds()
	{
		return ( mapBounds );
	}
	
	// setCreator
	//===========
	public void setCreator( String name )
	{
		creator = name;
	}
	
	// getCreator
	//===========
	public String getCreator()
	{
		return ( creator );
	}
	
	// setTitle
	//=========
	public void setTitle( String newTitle )
	{
		title = newTitle;
	}
	
	// getTitle
	//=========
	public String getTitle()
	{
		return ( title );
	}
	
	// setDifficulty
	//==============
	public void setDifficultyId( int newDifficultyId )
	{
		difficultyId = newDifficultyId;
	}
	
	// getDifficulty
	//==============
	public int getDifficultyId()
	{
		return ( difficultyId );
	}
	
	// getDifficulty
	//==============
	public String getDifficulty()
	{
		return ( Map.DIFFICULTIES[ difficultyId ] );
	}
	
	// setHighScore
	//=============
	public void setHighscore( int newHighscore )
	{
		highScore = newHighscore;
	}
	
	// getHighScore
	//=============
	public int getHighScore()
	{
		return ( highScore );
	}
	
	// setBestTime
	//============
	public void setBestTime( long newBestTime )
	{
		bestTime = newBestTime;
	}
	
	// getBestTime
	//============
	public long getBestTime()
	{
		return ( bestTime );
	}
	
	// getRows
	//========
	public int getRows()
	{
		return ( rows );
	}
	
	// getColumns
	//===========
	public int getColumns()
	{
		return ( columns );
	}
	
	// getCellsInTotal
	//================
	public MapCell[] getCells()
	{
		return ( cells );
	}
	
	// setCountdownTime
	//=================
	/**
	 * @param time the amount of time in which the player has to reach the finish area. A value of
	 * {@code COUNTDOWN_DISABLED} represents an unlimited amount of time.
	 */
	public void setCountdownTime( long time )
	{
		countdownTime = time;
	}
	
	// getCountdownTime
	//=================
	public long getCountdownTime()
	{
		return ( countdownTime );
	}
	
	// setBackgroundFile
	//==================
	public void setBackgroundFile( String fileName )
	{
		backgroundFile = fileName;
	}
	
	// getBackgroundFile
	//==================
	public String getBackgroundFile()
	{
		return ( backgroundFile );
	}
	
	// setForegroundFile
	//==================
	/**
	 * @param path the path of the foreground image file within the root directory of the application.
	 */
	public void setForegroundFile( String fileName )
	{
		foregroundFile = fileName;
	}
	
	// getForegroundFile
	//==================
	public String getForegroundFile()
	{
		return ( foregroundFile );
	}
	
	// setEnvironmentSoundFile
	//========================
	public void setEnvironmentSoundFile( String fileName )
	{
		environmentSoundFile = fileName;
	}
	
	// getEnvironmentSoundFile
	//========================
	public String getEnvironmentSoundFile()
	{
		return ( environmentSoundFile );
	}
	
	// setWaterId
	//===========
	public void setWaterId( int id )
	{
		waterId = id;
	}
	
	// getWaterId
	//===========
	public int getWaterId()
	{
		return ( waterId );
	}
	
	// setWaterSpeedId
	//================
	public void setWaterSpeedId( int speedId )
	{
		waterSpeedId = speedId;
	}
	
	// getWaterSpeedId
	//================
	public int getWaterSpeedId()
	{
		return ( waterSpeedId );
	}
	
	// setWaterHeight
	//===============
	public void setWaterHeight( float height )
	{
		waterHeight = height;
	}
	
	// getWaterHeight
	//===============
	public float getWaterHeight()
	{
		return ( waterHeight );
	}
	
	// setStartArea
	//=============
	public void setStartArea( Rectangle rectangle )
	{
		startArea.set( rectangle );
	}
	
	// setStartArea
	//=============
	public void setStartArea( float x, float y, float w, float h )
	{
		startArea.set( x, y, w, h );
	}
	
	// getStartArea
	//=============
	public Rectangle getStartArea()
	{
		return ( startArea );
	}
	
	// setFinishArea
	//==============
	public void setFinishArea( Rectangle rectangle )
	{
		finishArea.set( rectangle );
	}
	
	// setFinishArea
	//==============
	public void setFinishArea( float x, float y, float w, float h )
	{
		finishArea.set( x, y, w, h );
	}
	
	// getFinishArea
	//==============
	public Rectangle getFinishArea()
	{
		return ( finishArea );
	}
}