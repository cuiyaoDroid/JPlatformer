package com.sh.jplatformer.util;

/**
 * The {@code Time} class provides static time-related utility methods.
 * @author Stefan Hösemann
 */

public class Time
{
	// toString
	//=========
	/**
	 * @param ms the time to transform in milliseconds.
	 * @return the time {@code String} in the format of {@code HH:MM:SS}. If the
	 * hours value is 0, it will not be included in the {@code String}.
	 */
	public static String toString( long ms )
	{
		// Calculate units
		//================
		long sec = ms  / 1000;
		long min = sec / 60;
		long hrs = min / 60;
		
		sec = sec - (min*60);
		min = min - (hrs*60);

		// String builders
		//================
		StringBuilder sb_sec = new StringBuilder( sec +  "" );
		StringBuilder sb_min = new StringBuilder( min + ":" );
		StringBuilder sb_hrs = new StringBuilder( hrs + ":" );
		
		// Insert zero
		//============
		if ( sec < 10 ) sb_sec.insert( 0, "0" );
		if ( min < 10 ) sb_min.insert( 0, "0" );
		if ( hrs < 10 ) sb_hrs.insert( 0, "0" );

		// Return string
		//==============
		if ( hrs > 0 )
		{
			return ( sb_hrs.toString() + sb_min.toString() + sb_sec.toString() );
		}
		return ( sb_min.toString() + sb_sec.toString() );
	}
}
