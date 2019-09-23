package com.spacehex.game.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;

import com.spacehex.game.draw.ShaderLibrary.ShaderPart;
import com.spacehex.game.draw.ShaderLibrary.ShaderProgram;

/**
 * @author Vincent Terpstra
 * Mar 6, 2017
 * SpriteBatcher that draws texture regions from a float[] XYUV;
 */
public class SimpleBatch {
	private static float height, width;
	public static float Height(){	return height; }
	public static float Width() {	return width; }
	public void setShiftRatio(float x, float y){
		setShift(x * width, y * height);
	}
	public void setShift(float x, float y){
		this.shiftX = x;
		this.shiftY = y;
	}
	protected float shiftX, shiftY;
	private final static int BYTES_PER_FLOAT = 4, BYTES_PER_SHORT = 2, POINTSPERVERTICE = 6;
	protected final float[] 	drawValues;
	protected int				drawIdx		;
	private final ByteBuffer	vertices	;
	private final ShortBuffer	indices		;
	private final ShaderProgram	shader		;
	private final ShaderPart 	vertexShader, 
								fragmentShader;
	private 	  Texture 		texture;
	
	public void setTexture(Texture texture){
		this.texture = texture;
	}
	public SimpleBatch(float w, float h, SpriteArray array, String s){
		this(w,h,array, s, new float[][] {
			{1.0f,0.7f,0	}, //0 orange
			{.65f,0.4f,1f	}, //  purple
			{	0,1.0f,0.3f	}, //  green
			{	0,0.8f,1	}, //  light blue
			{	1,   0,0.8f	}, //  pink
			{	0,0.4f,1f	}, //5 dark blue
		});
	}
	public SimpleBatch(float w, float h, SpriteArray array, String s, float[][] colors){
		float ratio = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
		width = w;  height = h;
		if(h == 0)	height = w / ratio;
		if(w == 0)	width  = h * ratio;
		cArray = colorToFloat( colors );
		dColours = colorToFloat(new float[][] {
			{ 1,1,1 },				//-1
			{.15f, .165f, .171f},	//-2
			{1,1,1}					//-3
		});
		draw = array.get(s);
		
		this.texture = array.getTexture();
		this.numbers = array.getString("0123456789");
		this.dNum = numbers[0][8];
		int batchSize = 600;
		short[] sArray = new short[batchSize * 6];
		short j = 0;
		for( int i = 0; i< sArray.length; ){
			sArray[i  ] = sArray[i+5] =   j;
			sArray[i+1] = 				++j;
			sArray[i+2] = sArray[i+3] = ++j;
			sArray[i+4] = 				++j;
			++j;
			i+=6;
		}
		drawValues = new float[batchSize * POINTSPERVERTICE * 4];
		vertices = buffer(drawValues.length * BYTES_PER_FLOAT);
		indices  = buffer(batchSize * 6 * BYTES_PER_SHORT).asShortBuffer();
		indices.put(sArray).position( 0 );
		
		vertexShader 	= ShaderLibrary.Vertex();
		fragmentShader 	= ShaderLibrary.Fragment();
		shader 			= ShaderLibrary.Program(vertexShader, fragmentShader);
	}
	private static final ByteBuffer buffer(int size){
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}
	protected final float[] 
		draw, 
		dColours,
		cArray;
	private final float[][] numbers;
	protected final float dNum, space = .09f;
	
	public void drawInt(int num, int clr, float x, float y, float scale, float shift, int size){
		if(size == 0){
			size = 1;
			int n = num;
			while((n /= 10) > 0){
				size++;
			}
		}
		y -= .5f * scale;
		x += (shift * (size * (dNum + space) - space - dNum) + .5f * dNum) * scale;
		for(int i = 0; i < size; i++){
			float[] number = numbers[num % 10];
			setScale(scale);
			draw(number, clr, x - number[8] * scale, y, 0);
			x -= (.1f + dNum) * scale;
			num /= 10;
		}
	}
	
	protected final static float[] colorToFloat(float[][] colors){
		float[] out = new float[colors.length];
		for(int i = 0; i < colors.length; i++){
			float[] color = colors[i];
			out[i] = colorToFloat(color[0], color[1], color[2]);
		}
		return out;
	}
	
	private final static float colorToFloat(float r, float g, float b){
		return Color.toFloatBits(r,g,b, 1.0f);
	}
	
	protected final void setColour(float r, float g, float b){
		dColours[2] = colorToFloat(r, g, b);
	}
	protected float rgb(int clr){
		return  (clr < 0) ? dColours[-clr - 1] : cArray[clr % cArray.length];
	}
	public void draw(int clr, float x, float y, float scale){
		setScale(scale);
		draw(draw, clr, x, y, 0);
	}
	
	public SimpleBatch setScale(float scale){
		this.scale = scale;
		return this;
	}
	
	float scale = 1;
	public void draw(float[] img, int clr,
			float x, float y, float z ) {
		if(img == null) img = draw;
		if(img.length / 4 * POINTSPERVERTICE + drawIdx > drawValues.length) end();
		float rgb = rgb(clr);
		x += shiftX;
		y += shiftY;
		for( int i = 0; i< img.length; ) {
			//XYZ
			drawValues[drawIdx++] = x + img[i++] * scale;
			drawValues[drawIdx++] = y + img[i++] * scale;
			drawValues[drawIdx++] = z;
			//UV
			drawValues[drawIdx++] = img[i++];
			drawValues[drawIdx++] = img[i++];
			//RGBA
			drawValues[drawIdx++] = rgb;
			
		}
		scale = 1; //reset values;
	}
	public final void begin(){
		GL20 gl = Gdx.gl;
		gl.glClearColor (.15f, .165f, .171f, 1 );
		//enable blending
		gl.glEnable	  (	GL20.GL_BLEND | GL20.GL_DEPTH_TEST);
		gl.glBlendFunc( GL20.GL_SRC_ALPHA, 
						GL20.GL_ONE_MINUS_SRC_ALPHA );
		gl.glDepthFunc( GL20.GL_LEQUAL		);
		gl.glDepthMask(true);
		gl.glUseProgram		 ( shader.programHandle	   );
		gl.glUniform1f		 ( shader.uniformIDS[0], 0 );
		gl.glUniformMatrix3fv( shader.uniformIDS[1], 1, false, 
			new float[] {
				2f/width,	 0, 0,
				 0,  2f/height, 0,
				-1, 		-1, 0 }, 0);
		texture.bind();
		gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
	}
	
	public final void end(){
		GL20 gl  = Gdx.gl;
		vertices.position(0);
		BufferUtils.copy(drawValues, 0, vertices, drawIdx);
		
		final int STRIDE = POINTSPERVERTICE * BYTES_PER_FLOAT;
		//X Y Z
		final int[] 
			pos = {0, 12, 20}, 
			size = {3,2,4};
		for(int i = 0; i < 3; i++){
			vertices.position(pos[i]);
			gl.glVertexAttribPointer(i, size[i], 
					i != 2 ? GL20.GL_FLOAT : GL20.GL_UNSIGNED_BYTE, 
					i == 2, STRIDE, vertices);
			gl.glEnableVertexAttribArray(i);
		}
		
		final int totalIndices = (drawIdx / POINTSPERVERTICE / 4) * 6;
		gl.glDrawElements(GL20.GL_TRIANGLES, totalIndices, GL20.GL_UNSIGNED_SHORT, indices.position(0).limit(totalIndices));
		drawIdx = 0; //reset the draw counter
	}
	
	public void dispose(){
		Gdx.gl.glUseProgram(0);
		vertexShader	.dispose();
		fragmentShader	.dispose();
		shader			.dispose();
		texture			.dispose();
	}
}
