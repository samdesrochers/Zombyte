package com.game.jetpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.bag.lib.math.OverlapTester;
import com.bag.lib.math.Vector2;

/*
 * Master class holding all game objects and regulating their interactions
 */

public class World {
	
	// Interface, mostly used to access sound effects
    public interface WorldListener {
          //public void sound();
		  int getTime();
    }

    // World's size
    public static final float WORLD_WIDTH 			= 40;
    public static final float WORLD_HEIGHT 			= 22;
    
    // World's states
    public static final int WORLD_STATE_RUNNING 	= 0;
    public static final int WORLD_STATE_NEXT_LEVEL 	= 1;
    public static final int WORLD_STATE_GAME_OVER 	= 2;
    
    public static final Vector2 friction = new Vector2(-12, 0);

    public final Player player;
    //public final Tank tank;
    public final List<Bullet> bulletArray;
    public final List<Enemy> EnemyArray;
    //public final List<PowerUp> PowerUpArray;
    public Explosion explosion;
    public final WorldListener listener;
    public final Random rand;
    
    public float lastBulletFiredTime;
    
    public int state;
    
    // Tiles map
    public int[][] level;

    public World(WorldListener listener) {
        this.bulletArray = new ArrayList<Bullet>();
        this.EnemyArray = new ArrayList<Enemy>();
        //this.PowerUpArray = new ArrayList<PowerUp>();
    	player = new Player(WORLD_WIDTH/2, 10);
        
    	this.listener = listener;
        
        rand = new Random();
       
        level = new int[(int) WORLD_WIDTH][(int) WORLD_HEIGHT];
        this.state = WORLD_STATE_RUNNING;
        lastBulletFiredTime = 0.0f;
        
        // Initialize tiles map
        for(int i = 0; i < WORLD_WIDTH; i++)
        	for(int j = 0; j < WORLD_HEIGHT; j++)
        		level[i][j] = 0;
        
        explosion = null;
        generateLevel();
        initEnemies();
    }
    
    private void initEnemies()
    {
    	for (int i = 0; i < 20
    			; i++) {
			addEnemy(); 
		}
    }
    
    //Populate the level (tiles map) and adds enemies
	private void generateLevel() {
			
		int tileStyle = 0;
	    for(int i = 0; i < WORLD_WIDTH; i++)
	    	for(int j = 0; j < WORLD_HEIGHT; j++){
	    	
	    		if(j%2 == 0 && i%2 == 0)
	    			tileStyle = 1;
	    		else
	    			tileStyle = 2;
	    	
	    		level[i][j] = tileStyle;
	    	}
	}
	
	public void update(float deltaTime, float speed) {
		updatePlayer(deltaTime, speed);
		updateBullet(deltaTime);
		updateEnemies(deltaTime);
		//updatePowerUp(deltaTime);
		updateExplosions(deltaTime);
		checkCollisions();
		//checkGameOver();
	}

	private void updatePlayer(float deltaTime, float speed) {
	    //if(speed == 0)
	    //	player.state = Player.PLAYER_STATE_IDLE;
	    player.update(deltaTime);
	    if(player.state == Player.PLAYER_STATE_HIT_WALL) {
	    	player.state = player.previousState;
	    }
	}
	
	private void updateBullet(float deltaTime) {
		synchronized (bulletArray) {
			for(int i = 0; i < bulletArray.size(); i ++){
				bulletArray.get(i).update(deltaTime);
				
				if( bulletArray.get(i).state == Bullet.NOT_ACTIVE )
					bulletArray.remove(i);
			}
		}
	}
//	
//	private void updatePowerUp(float deltaTime) {
////		synchronized (PowerUpArray) {
////			for(int i = 0; i < PowerUpArray.size(); i ++){
////				PowerUpArray.get(i).update(deltaTime);
////			}
////		}
//	}
//	
//	
	private void updateEnemies(float deltaTime) {
	    int len = EnemyArray.size();
	    
	    // Add enemies if 2 enemies remaining
	    if(len <= 2)
	    {
	    	for(int i=0; i<10; i++)
	    	{
	    		addEnemy();
	    	}
	    }
	    
	    // Update the enemies
	    for (int i = 0; i < len; i++) {
	        Enemy enemy = EnemyArray.get(i);
	        float distX = player.position.x - enemy.position.x;
	        float distY = player.position.y - enemy.position.y;
	        float angle = (float) Math.atan2(distY, distX);
	        enemy.rotationAngle = angle;
	        enemy.update(deltaTime);
	        
	        if(enemy.state == Enemy.ENEMY_STATE_DEAD){
	        	EnemyArray.remove(enemy);
	        	i = EnemyArray.size();	
	        }
	    }
	}
	private void updateExplosions(float deltaTime) {
		try{	
			explosion.update(deltaTime);
		} catch(Exception e){}
	}
//	
	private void checkCollisions() {
		checkEnemyBulletCollisions();
		checkPlayerEnemyCollisions();
	    //checkAmmoCollisions();
	    //checkPowerUpCollisions();
	}
	
	// Enemy - Player collision
	private void checkPlayerEnemyCollisions() {
	    int len = EnemyArray.size();
	    synchronized (EnemyArray) {
	    	for (int i = 0; i < len; i++) {
		        Enemy enemy = EnemyArray.get(i);

		        if (OverlapTester.overlapRectangles(enemy.bounds, player.bounds)) {
		        	
		        	len = EnemyArray.size();
		        	player.state = Player.PLAYER_STATE_HIT;
		            //listener.hit();
		        }
		    }
		}   
	}
	
	// Enemy - Ammo collision
	private void checkEnemyBulletCollisions() {
	    int elen = EnemyArray.size();
	    int alen = bulletArray.size();
	    synchronized (EnemyArray) {
		    for (int i = 0; i < elen; i++) {
		        Enemy enemy = EnemyArray.get(i);
		        synchronized (enemy) {
		        	for (int j = 0; j < alen; j++){
			        	Bullet bul = bulletArray.get(j);
				        if (OverlapTester.overlapRectangles(bul.bounds, enemy.bounds)) {
				        	bulletArray.remove(bul);
				            alen = bulletArray.size();
				            
				            enemy.life -= 1; 
				            
				            // Add particle effect 
					    	explosion = new Explosion(20, (int)enemy.position.x, (int)enemy.position.y);
				            //enemy.life -= bul.weaponDamage; 
				            //listener.enemyHit();
				        }
			        }
				} 
		    }
		}
	}
//	
//	private void checkGameOver() {  	  	
////    	if (tank.life <=  0) {
////            state = WORLD_STATE_GAME_OVER;
////            listener.gameOver();
////        }
//	}
	
	private void addEnemy(){
		int pos  = rand.nextInt(4);
		int diff = rand.nextInt(5) + 2;
		if(pos == 0)
		{
			EnemyArray.add(new Enemy(WORLD_WIDTH/2+rand.nextInt(5) - 5, -10, Enemy.ENEMY_TYPE_ZOMBIE, diff));
		}
		else if(pos == 1)
		{
			EnemyArray.add(new Enemy(-10, WORLD_HEIGHT/2+rand.nextInt(5) - 5, Enemy.ENEMY_TYPE_ZOMBIE, diff));
		}
		else if(pos == 2)
		{
			EnemyArray.add(new Enemy(WORLD_WIDTH/2+rand.nextInt(5) - 5, WORLD_HEIGHT + 10, Enemy.ENEMY_TYPE_ZOMBIE, diff));
		}
		else
		{
			EnemyArray.add(new Enemy(WORLD_WIDTH + 10, WORLD_HEIGHT/2+rand.nextInt(5) - 5, Enemy.ENEMY_TYPE_ZOMBIE, diff));
		}
	}
	
	public void addBullet(float angle){
		synchronized (bulletArray) {	
			if(lastBulletFiredTime > player.weapon.getFireRate()) {	
				// Condition to regulate the bullets being fired
					if(player.weapon.getType() == Weapon.WEAPON_PISTOL )
					{
						bulletArray.add(new Bullet(player.position.x + (float)(Math.cos(angle/180*3.146)), 
													   player.position.y + (float)(Math.sin(angle/180*3.146)),
													   angle, player.weapon.getBulletSpeed()));
					
					}
					else if (player.weapon.getType() == Weapon.WEAPON_SHOTGUN) 
					{
						bulletArray.add(new Bullet(player.position.x + (float)(Math.cos(angle/180*3.146)), 
								   player.position.y + (float)(Math.sin(angle/180*3.146)),
								   angle + 5,
								   player.weapon.getBulletSpeed()));
						bulletArray.add(new Bullet(player.position.x + (float)(Math.cos((angle)/180*3.146)), 
								   player.position.y + (float)(Math.sin(angle/180*3.146)),
								   angle,
								   player.weapon.getBulletSpeed()));
						bulletArray.add(new Bullet(player.position.x + (float)(Math.cos(angle)/180*3.146), 
								   player.position.y + (float)(Math.sin(angle/180*3.146)),
								   angle - 5,
								   player.weapon.getBulletSpeed()));
						
					}
					lastBulletFiredTime = 0.0f; 
			}
			else {
				lastBulletFiredTime += 0.1f;
			}
		}
	}
	
	public void addPowerUp(int type){
		// add powerup
	}
}

