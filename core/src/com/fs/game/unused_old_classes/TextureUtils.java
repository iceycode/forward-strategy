/**
 * 
 */
package com.fs.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;

/** a simple class for creating a simple pixmap 
 * - mainly for testing components (temporary replacement )
 * TODO: update json files with better images
 * @author Allen
 *
 */
public class TextureUtils {

	public static Pixmap createPixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		
		//pixmap.drawRectangle(200, 200, width, height);
		pixmap.setColor(color);
		pixmap.fill(); //fill with the color
 		
		return pixmap;
	}
	
	public static Texture createPixmapCircle(int radius, Color color) {
		Pixmap circle = new Pixmap(radius, radius, Format.RGBA8888);
		circle.setColor(color);
 
		circle.drawCircle(10, 10, radius);
		
		Texture cirlceTex = new Texture(circle);
 		
		
		circle.dispose();
		
		return cirlceTex;
	}
	
	public static Texture createGoButton(int width, int height, Color color){
		Pixmap goButton = new Pixmap(width, height, Format.RGBA8888);
		goButton.setColor(color);
		goButton.drawRectangle(0, height, width, height);
		
		Texture tex = new Texture(goButton);
		
		return tex;
	}
}
