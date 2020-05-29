package com.zappycode.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import sun.security.jgss.GSSCaller;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int man_state = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;

	Rectangle manRectangle;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();

	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();//helps to shape our objects

	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();

	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();

	Texture bomb;
	int bombCount;

	Random random;

	BitmapFont font; //for displaying the scores
	int score = 0;

	int game_state = 0;

	Texture dizzy;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");

		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(game_state == 1){

			//game is live
			//Bomb
			if (bombCount < 250){
				bombCount++;
			}else {
				bombCount = 0;
				makeBomb();
			}

			//drawing bomb
			bombRectangles.clear();
			for(int i = 0; i < bombXs.size(); i++){

				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 8);

				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(),
						bomb.getHeight()));
			}

			//Coins
			if (coinCount < 100){
				coinCount++;
			}else {
				coinCount = 0;
				makeCoin();
			}

			//drawing coins
			coinRectangles.clear();
			for(int i = 0; i < coinXs.size(); i++){

				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);

				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(),
						coin.getHeight()));
			}

			//goes upward
			if (Gdx.input.justTouched()){
				velocity = -10;
			}

			if (pause < 8){
				pause++;
			}else {
				pause = 0;
				if (man_state < 3) {
					man_state++;
				} else {
					man_state = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			//stops the man from disappearing in ground
			if (manY <= 0){
				manY = 0;
			}

		}else if(game_state == 0){

			//waiting to start
			if (Gdx.input.justTouched()){
				game_state = 1;
			}

		}else if (game_state == 2){
			//Game over
			if (Gdx.input.justTouched()){
				game_state = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;

				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		if(game_state == 2){
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[man_state].getWidth() /2,
					manY);
		}else {
			batch.draw(man[man_state], Gdx.graphics.getWidth() / 2 - man[man_state].getWidth() /2,
					manY);
		}


		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[man_state].getWidth() /2,
                manY, man[man_state].getWidth(), man[man_state].getHeight());

		//detection of collision with coins
		for(int i = 0; i < coinRectangles.size(); i++){
		    if(Intersector.overlaps(manRectangle, coinRectangles.get(i))){

                //Gdx.app.log("Coin!", "Collision!");
                score++;

                //preventing overlapping when in touch with coin making it disappear
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

		//detection of collision with bombs
        for(int i = 0; i < bombRectangles.size(); i++){
            if(Intersector.overlaps(manRectangle, bombRectangles.get(i))){

                //Gdx.app.log("Bomb!", "Collision!");
				game_state = 2;
            }
        }

        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
