package com.game.zombyte;

import com.bag.lib.DynamicGameObject;

public class Enemy extends DynamicGameObject {

	public static final int ENEMY_TYPE_ZOMBIE 		= 0;
	public static final int ENEMY_TYPE_BOSS 		= 1;

	public static final int ENEMY_STATE_ALIVE 		= 2;
	public static final int ENEMY_STATE_DEAD 		= 3;
	public static final int ENEMY_STATE_COLLIDE 	= 4;

	public static final int ENEMY_STATE_RETARDED	= 9000;
	
    public static final float ENEMY_MOVE_VELOCITY 	= 3.2f;
    public static final float ENEMY_BASIC_WIDTH 	= 1.0f;
    public static final float ENEMY_BASIC_HEIGHT 	= 1.0f;
    public static final float ENEMY_BOSS_WIDTH 		= 4.0f;
    public static final float ENEMY_BOSS_HEIGHT 	= 4.0f;
    
    public static final int ENEMY_SCORE = 50;
    
    public int type;
    public int state;
    public int life;
    
    private float 	speed;
    private int   	difficulty;
    public float 	rotationAngle;
    public int   	score;
    public int 		randomAngleX;
    public int 		randomAngleY;
    public int 		randDiff;
    
    public float stateTime;
	
	public Enemy(float x, float y, int type, int difficulty) {
		super(x, y, ENEMY_BASIC_WIDTH, ENEMY_BASIC_HEIGHT); 
		
		this.state = ENEMY_STATE_ALIVE;
		this.rotationAngle = 0;
		this.life = 20;
		this.difficulty = difficulty;
		this.type = type;
		this.stateTime = 0.0f;
		this.randomAngleX = rndInt(0,360);
		this.randDiff = rndInt(1, 4);
		initialize();
	}
	
    public void initialize()
    {
    	if(type == ENEMY_TYPE_ZOMBIE)
    	{
    		this.score = ENEMY_SCORE;
    		this.speed = ENEMY_MOVE_VELOCITY + (difficulty + randDiff)/8;
	        
    	} 
    	else if (type == ENEMY_TYPE_BOSS)
    	{
    		this.score = (int) (50 * (difficulty * 2));
    		this.life = (int) (30 + (difficulty * 25));
    		this.speed = 2.1f + (difficulty + rndInt(2, 20))/7; 
    		this.bounds.width += ENEMY_BOSS_WIDTH/3;
    		this.bounds.height += ENEMY_BOSS_WIDTH/3;
    	}	
    }
    
    public void update(float deltaTime) {    
    	bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	stateTime += deltaTime;
    	updateVelocity(); 
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        
        if(position.x >= 50)
        	position.x = 50;
        else if(position.x <= -10)
        	position.x = -10;
        if(position.y >= 30)
        	position.y = 30;
        else if(position.y <= -10)
        	position.y = -10;
        
        if(life <= 0)
        {
        	state = ENEMY_STATE_DEAD;
        }
    }
    
    public void updateOnline(float deltaTime) {    
    	bounds.lowerLeft.set(position).sub(bounds.width / 2, bounds.height / 2);
    	stateTime += deltaTime;
    	
        if(life <= 0)
        {
        	state = ENEMY_STATE_DEAD;
        }
    }
    
    private void updateVelocity() 
    {
    	if(state != ENEMY_STATE_RETARDED){
    		velocity.x = (float) (speed*Math.cos(rotationAngle));
    		velocity.y = (float) (speed*Math.sin(rotationAngle));
    	}
    	else{
        	velocity.x = (float) (speed*Math.cos(randomAngleX));
        	velocity.y = (float) (speed*Math.sin(randomAngleX));
        	rotationAngle = randomAngleX;
    	}
    }
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}
}
