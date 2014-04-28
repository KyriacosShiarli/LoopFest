package imagetoparticles;

import java.awt.Container;

import javax.print.attribute.standard.NumberOfDocuments;

import fisica.FBox;
import fisica.FPoly;
import fisica.FCircle;
import fisica.FWorld;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


public class CircleMover{
	
		
		PApplet parent;
		PVector[] origCoordinates;
		PVector[] altCoordinates;
		FCircle[] particles;	
		FWorld world; 
	
		int particleSize;
		private int numOfDrawable; 
		
	
	// CONSTRUCTOR - - - - -  - -  -  - -  -  - -  - - - 
	public CircleMover(PApplet p, FWorld tempWorld, int tempParticleSize,int numberOfParticles) {
		numOfDrawable = numberOfParticles;
		parent = p;
		particleSize = tempParticleSize;

		origCoordinates = new PVector[numberOfParticles];
		altCoordinates= new PVector[numberOfParticles];
		particles = new FCircle[numberOfParticles];
		world = tempWorld;
			
		for (int i  = 0; i < numberOfParticles;i++){
			
			particles[i]= new FCircle(particleSize);
			particles[i].setPosition(100+i*particleSize/10, 100);
			particles[i].setSensor(false);
			particles[i].setDamping((float)14); // Default damping in 15 TODO -> function that changes all damping
			particles[i].setAngularDamping((float)80); // Same as above;
			particles[i].setDensity(0.4f);
			particles[i].setGroupIndex(-1);
			particles[i].setRestitution(3);
			particles[i].setStroke(255, 250,0,250);
			particles[i].setStrokeWeight(1.5f);
			particles[i].setFill(255,255,0,120);
			origCoordinates[i]=new PVector((int)particles[i].getX(),(int)particles[i].getY());
			altCoordinates[i]=new PVector(origCoordinates[i].x,origCoordinates[i].y);
			world.add(particles[i]);
		}	    
	}
	
	public void changeDamping(int newDamping,String TYPE)
	{
		
		if (TYPE.equals("LINEAR"))
		{
			for (int i = 0 ; i < particles.length;i++)
			{
				particles[i].setDamping(newDamping);
			}
		}
		
		
		if (TYPE.equals("ROTATIONAL"))
		{
			for (int i = 0 ; i < particles.length;i++)
			{
				particles[i].setAngularDamping(newDamping);
			}
		}
		
		
	}
	
	public void attraction(int force) // Provides attraction forces for the particles by defining defining a force as a function of their distance from the original possition
	{
		for (int i = 0;i<particles.length;i++){
			if (altCoordinates[i].x!=particles[i].getX()||altCoordinates[i].y!=particles[i].getX());
			{
			particles[i].addForce((altCoordinates[i].x-particles[i].getX())*force*1.5f,
								  (altCoordinates[i].y-particles[i].getY())*force*1.5f);
			}		
		}
	}
			
	public void defaultPossitions(int force)
	{
		for (int i = 0;i<particles.length;i++){
			altCoordinates[i]=origCoordinates[i];
			
			if (origCoordinates[i].x!=particles[i].getX()||origCoordinates[i].y!=particles[i].getX());
			{
			particles[i].addForce((origCoordinates[i].x-particles[i].getX())*force,
								  (origCoordinates[i].y-particles[i].getY())*force);
			}		
		}
	}
	
	public void	move(int index, PVector newCoordinates)

	{
		altCoordinates[index] = newCoordinates; 
	}
	
	public void	changeSize(int index,int radius)

	{
		if (radius>80){return;}
		else{
			particles[index].setSize(radius);;
			
		}
	}	

	public void unifromMoveParticles(int expansionSize) 
	{
		//Adds to current X and Y coordinates of what is not the screen. Addition is cumulative.
		// Currently expands from the top left particle. TODO From centre + other extreme directions.
		for (int i=0;i<particles.length-1;i++)
		{
			if (i == 0){}
			else{
				altCoordinates[i].x = altCoordinates[i].x+ expansionSize;
				altCoordinates[i].y = altCoordinates[i].y+ expansionSize;}
		}
	}
	
	public void formDice(PImage img,int dist,String CHOOSE) 
	{
		// arranges particles at edges, more like the 4 dice shape
		//
		if (CHOOSE.equals("FOUR")){
			int spacing_width =img.width/4;
			int spacing_height =img.height/4;
			altCoordinates[0].x = spacing_width; altCoordinates[0].y = spacing_height;
			altCoordinates[1].x = 3*spacing_width; altCoordinates[1].y = spacing_height;
			altCoordinates[2].x = spacing_width; altCoordinates[2].y =3*spacing_height;
			altCoordinates[3].x = 3*spacing_width; altCoordinates[3].y = 3*spacing_height;
			if (particles.length>4)
			{
				for (int i =4;i<particles.length;i++)
				{
					particles[i].setDrawable(false);
				}
			}
		}
		
		if (CHOOSE.equals("SIX")){
			int spacing_width =img.width/4;
			int spacing_height =img.height/6;
			altCoordinates[0].x = spacing_width; altCoordinates[0].y = spacing_height;
			altCoordinates[1].x = spacing_width; altCoordinates[1].y = 3*spacing_height;
			altCoordinates[2].x = spacing_width; altCoordinates[2].y = 5*spacing_height;
			altCoordinates[3].x = 3*spacing_width; altCoordinates[3].y = spacing_height;
			altCoordinates[4].x = 3*spacing_width; altCoordinates[4].y = 3*spacing_height;
			altCoordinates[5].x = 3*spacing_width; altCoordinates[5].y = 5*spacing_height;
			
			if (particles.length>6)
			{
				for (int i =6;i<particles.length;i++)
				{
					particles[i].setDrawable(false);
				}
			}
		}
		
	}	

	public void formCircle(int centerX,int centerY,int radius){
		if (numOfDrawable == 0){return;}
		float increment = (2*parent.PI)/numOfDrawable;
		float theta = 0;
		for (int i =0; i<numOfDrawable;i++){
			altCoordinates[i].x =(int)( centerX + radius*PApplet.cos(theta));
			altCoordinates[i].y =(int)( centerY + radius*PApplet.sin(theta));
			theta = theta+increment;
		}
		
		
	}

	public void swap_cw(){
		if (numOfDrawable == 0){return;}
		else{
			
			FCircle temp = particles[numOfDrawable-1];
			for(int i = numOfDrawable; i>0;i--)
			{
				if (i<numOfDrawable)
					particles[i]=particles[i-1];
			}
			particles[0] = temp;
		}
	}
	
	public void swap_random(int times){
		if (numOfDrawable == 0){return;}
		else{
			for (int i =0;i<times;i++){
				int random1=0;
				int random2=0;
				while (random1==random2){
					random1 = (int) parent.random(numOfDrawable-1);
					random2 = (int) parent.random(numOfDrawable-1);
				}
				PVector temp = altCoordinates[random1];
				altCoordinates[random1] = altCoordinates[random2];
				altCoordinates[random2] = temp; 
			}
		}
	}
	
	public void setNumDrawable(int number){
		numOfDrawable = number;
		for (int i = 0;i<particles.length;i++)
		{
			if (i<numOfDrawable){
				particles[i].setDrawable(true);
				particles[i].setSensor(false);
				particles[i].wakeUp();
			}
			else{
				particles[i].setDrawable(false);
				particles[i].setSensor(true);
			}
		}
	}
	
    public void setForce_radial(float force){
    	float theta = 0;
    	float increment = (2*parent.PI)/numOfDrawable;
    	for(int i = 0;i<numOfDrawable;i++){
    		
    		particles[i].addForce(force*parent.cos(theta),force*parent.sin(theta));
    		theta = theta+increment;
    	}
    }
    
    public void setForce_vertical(float force){
    	float theta = 0;
    	float increment = (2*parent.PI)/numOfDrawable;
    	for(int i = 0;i<numOfDrawable;i++){
    		if (theta<parent.PI)
    			particles[i].addForce(0,force);
    		else
    			particles[i].addForce(0,-force);
    		theta = theta+increment;
    	}
	}
    
    public void setForce_horizontal(float force){
    	float theta = 0;
    	float increment = (2*parent.PI)/numOfDrawable;
    	for(int i = 0;i<numOfDrawable;i++){
    		if (theta<parent.PI)
    			particles[i].addForce(force,0);
    		else
    			particles[i].addForce(-force,0);
    		theta = theta+increment;
    	}
	}
    
    public void setForce_cw(float force){
    	for(int i = 0;i<numOfDrawable;i++){
    		if (i ==numOfDrawable-1&&i!=0){
    	        PVector temp = PVector.sub(altCoordinates[i], altCoordinates[0]);
   		        float mag =temp.mag();
    			particles[0].addForce(force*temp.x/mag, force*temp.y/mag);
    		}
    		else{
    			PVector temp = PVector.sub(altCoordinates[i], altCoordinates[i+1]);
   		 		float mag =temp.mag();
   		 		particles[i+1].addForce(force*temp.x/mag, force*temp.y/mag);
    		}
    	}
    }		
    
    public void changeColour(int colour){
    	for(int i = 0;i<numOfDrawable;i++){
    		if (colour==0){
    			particles[i].setStroke(0, 250);
    			particles[i].setStrokeWeight(2);
    			particles[i].setFill(0,0);
    		}
    		else if (colour!=255){
    			particles[i].setStrokeWeight(2);
    			particles[i].setFill(200,colour,0,60);
    			particles[i].setStroke(200,colour,0,235);
    		}
    		else{
    			particles[i].setStroke(255, 250);
    			particles[i].setStrokeWeight(2);
    			particles[i].setFill(255,255,255,0);
    		}
    	} 	
    }
    
    public void sizeOnPossition(){
    	PVector center= new PVector(parent.width/2,parent.height/2);
    	float distance =0;
    	for (int i = 0 ; i<particles.length;i++){
    		PVector current= new PVector(particles[i].getX(),particles[i].getY());
    		distance = current.dist(center);
    		
    		if(current.x<0 || current.x>parent.width+2)
    			changeSize(i,9);
    		else if (current.y<0 || current.y>parent.height+2)
    			changeSize(i,9);
    		else if (distance<20)
    			changeSize(i,9);
    		else
    			changeSize(i,(int)distance/4);
    			
    	}	
    }
}; // END
	