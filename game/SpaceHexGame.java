package com.spacehex.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.draw.SimpleBatch;
import com.spacehex.game.map.HWorld;
import com.spacehex.game.map.HWorldControl;

public class SpaceHexGame extends ApplicationAdapter implements InputProcessor {
	HexBatch 		batch;
	HWorld 			map;
	HWorldControl 	control;
	
	@Override
	public void create () {
		batch   = new HexBatch();
		map     = new HWorld();
		control = new HWorldControl(map);
		time	= System.currentTimeMillis();
		resume(); //activates Touch Listener
	}
	
	private long time;
	private long sum = 0;
	@Override
	public final void render () {
		long next = System.currentTimeMillis();
		sum += next - time;
		time = next;
		while(sum > 10){
			sum -= 10;
			if(!updating)
				map.update();
		}
		renderScreen();
	}
	
	
	private void renderScreen() {
		batch.begin();
		batch.setShift(0, 0);
		control.draw(batch);
		batch.end();
	}
	
	private boolean updating = true;
	
	@Override public void pause(){
		Gdx.input.setInputProcessor(null);
		updating = false;
	}
	@Override public void resume(){
		Gdx.input.setInputProcessor(this);
		updating = true;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	float tx = 0, ty = 0;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		control.touchDown(setX(screenX), setY(screenY), button);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		control.touchUp();
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		control.touchDragged(-tx + setX(screenX), - ty + setY(screenY));
		return true;
	}
	private float setX(int screenX){
		return tx = SimpleBatch.Width() *  ((float)screenX / Gdx.graphics.getWidth());
	}
	private float setY(int screenY){
		return ty = SimpleBatch.Height()*(1-(float)screenY / Gdx.graphics.getHeight());
	}
	
	public boolean keyTyped(char character) { return false; }
	public boolean keyDown (int keycode   ) { return false; }
	public boolean keyUp   (int keycode   ) { return false; }
	public boolean scrolled(int amount    ) { return false; }
	public boolean mouseMoved(int screenX, int screenY) { return false; }
	
}
