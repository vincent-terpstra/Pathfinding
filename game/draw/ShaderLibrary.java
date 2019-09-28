package com.spacehex.game.draw;

import static com.badlogic.gdx.Gdx.gl20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

public class ShaderLibrary {
	public static ShaderPart Vertex(){ 
		return new ShaderPart(GL20.GL_VERTEX_SHADER, vert );
	}
	public static ShaderPart Fragment(){
		return new ShaderPart(GL20.GL_FRAGMENT_SHADER, frag);
	
	}
	public static ShaderProgram Program(ShaderPart vertexShader, ShaderPart fragmentShader){
		String[] uniforms   = {"u_texture", "u_mat"			}, 
				 attributes = {"a_xyz", "a_uv", "a_color"	};
		return new ShaderProgram(vertexShader, fragmentShader, uniforms, attributes);
	}
	public static final class ShaderProgram {
		ShaderProgram(ShaderPart vertex, ShaderPart fragment, String[] uniforms, String[] attributes ){
			GL20 gl = Gdx.gl20;
			programHandle = gl.glCreateProgram();
			if(programHandle != 0) {
				gl.glAttachShader(programHandle, vertex.shaderHandle);
				gl.glAttachShader(programHandle, fragment.shaderHandle);	
				//Bind attributes
				int idx = 0;
				for(String s : attributes)
			{	gl.glBindAttribLocation(programHandle, idx++, s); }
				gl.glLinkProgram(programHandle);
			final java.nio.IntBuffer linkStatus = BufferUtils.newIntBuffer(1);
				gl.glGetProgramiv(programHandle, GL20.GL_LINK_STATUS, linkStatus);
				if(linkStatus.get(0) == 0){
					gl.glDeleteProgram(programHandle);
					programHandle = 0;
				}
			}
		if(programHandle == 0){
			throw new RuntimeException("Error creating program");
		}
			uniformIDS = new int[uniforms.length];
			for(int i = 0; i < uniformIDS.length; i ++){
				uniformIDS[i] = gl.glGetUniformLocation(programHandle, uniforms[i]);
			}
		}
		int 	programHandle;
		int[] 	uniformIDS;
		public void dispose(){ Gdx.gl.glDeleteProgram(programHandle);};
	}
	public static final class ShaderPart {
		private ShaderPart( int type, final String source ){
			GL20 gl = gl20;
			shaderHandle = gl.glCreateShader(type);
			boolean check = false;
			if (shaderHandle != 0){
				gl.glShaderSource ( shaderHandle, source);
				gl.glCompileShader( shaderHandle );
				final java.nio.IntBuffer compileCheck = BufferUtils.newIntBuffer(1);
				gl.glGetShaderiv(shaderHandle, GL20.GL_COMPILE_STATUS, compileCheck);
				check = compileCheck.get(0) == 0;
			}
			if ( check ) {
				System.out.println(gl.glGetShaderInfoLog(shaderHandle));
				System.out.println(source);
				gl.glDeleteShader(shaderHandle);
			}
		}
		final int shaderHandle;
		final void dispose(){ Gdx.gl.glDeleteShader( shaderHandle ); }
	}
	private static final String 
	vert =
			"attribute vec3 a_xyz;"
		+	"attribute vec2 a_uv;"
		+	"attribute vec4 a_color;"
			
		+	"uniform mat3 u_mat;"	
			
		+	"varying vec2 v_uv;"
		+	"varying vec3 v_color;"	
		
		+	"void main(){"
		+	"	v_uv = a_uv;"
		+	"	v_color = a_color.rgb;"
		+	"   vec3 pos = u_mat * vec3(a_xyz.xy, 1.0);"
		+	"	gl_Position = vec4(pos.xy, a_xyz.z, 1.0);"
		+	"}"
		,
	frag = 
			"varying vec2 v_uv;"
		+	"varying vec3 v_color;"
					
		+	"uniform sampler2D u_texture;"
		
		+	"void main(){"
		/**
		+	"	gl_FragColor = texture2D(u_texture, v_uv.xy);"
		/**/
		+   "   vec4 color = texture2D(u_texture, v_uv.xy);"
		+	"   if(color.a < .1)"
		+ 	"		discard;"
		+	"	gl_FragColor = vec4(v_color, 1.0);"
		/**/
		+	"}"
		;
}
