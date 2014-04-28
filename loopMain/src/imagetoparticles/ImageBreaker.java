package imagetoparticles;

import java.awt.Container;

import fisica.FBlob;
import fisica.FCircle;
import fisica.FWorld;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


public class ImageBreaker{
	
		PVector sketchDims;
		PApplet parent;
		PVector[] origCoordinates;
		PVector[] altCoordinates;	
		int particleSize;
		FBlob[] particles; 
	
	// CONSTRUCTOR - - - - -  - -  -  - -  -  - -  - - - 
	public ImageBreaker(PApplet p ,PImage imageOne, FWorld world, int tempParticleSize, String tempTYPE) {
		parent = p;
		particleSize = tempParticleSize;	
		sketchDims = new PVector(imageOne.width/particleSize,  // Sketch dimensions
                                 imageOne.height/particleSize);
		
		origCoordinates = new PVector[(int)(sketchDims.x*sketchDims.y)];
		altCoordinates= new PVector[(int)(sketchDims.x*sketchDims.y)];
		
		if (tempTYPE.equals("IMAGE")){
			sketchDims = new PVector(imageOne.width/particleSize,  // Sketch dimensions
	                imageOne.height/particleSize);
			origCoordinates = new PVector[(int)(sketchDims.x*sketchDims.y)];
			altCoordinates= new PVector[(int)(sketchDims.x*sketchDims.y)];
			particles = new FBlob[(int) ((sketchDims.x)*(sketchDims.y))];
			
			for (int j = 0;j<sketchDims.y;j++){
				for (int i = 0;i<sketchDims.x;i++){
					
					int index = i+(int)sketchDims.x*j;
					particles[index]= new FBlob();
					particles[index].setPosition(i*particleSize, j*particleSize);
					particles[index].attachImage(imageOne.get(i*particleSize, j*particleSize, particleSize, particleSize));
				    particles[index].setSensor(true);
				    particles[index].setDamping((float)15); // Default damping in 15 TODO -> function that changes all damping
				    particles[index].setAngularDamping((float)3); // Same as above;
				    origCoordinates[index]=new PVector((int)particles[index].getX(),(int)particles[index].getY());
				    altCoordinates[index]=new PVector(origCoordinates[index].x,origCoordinates[index].y);		   
					world.add(particles[index]);
			
				}
			}			
		} 
		else if (tempTYPE.equals("PARTICLE")){ // Construct array of circles
			sketchDims = new PVector(imageOne.width/particleSize,  // Sketch dimensions
	                imageOne.height/particleSize);
			for (int j = 0;j<sketchDims.y;j++){
				for (int i = 0;i<sketchDims.x;i++){
					int index = i+(int)sketchDims.x*j;
					particles[index]= new FBlob();
					particles[index].setAsCircle(particleSize);
					particles[index].setPosition(i*particleSize +(int)particleSize/2, j*particleSize +(int)particleSize/2);
				    particles[index].setSensor(true);
				    
				    int pixel = imageOne.get(i*particleSize, j*particleSize);
				    particles[index].setFillColor(pixel);
					particles[index].setStrokeColor(pixel);
					
				    particles[index].setDamping((float)15);
				    particles[index].setAngularDamping((float)3);
				    origCoordinates[index]=new PVector((int)particles[index].getX(),(int)particles[index].getY());
				    altCoordinates[index]=new PVector(origCoordinates[index].x,origCoordinates[index].y);		   
					world.add(particles[index]);
				}
			}			
		}
	}
	// END OF CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - -
	
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
			particles[i].addForce((altCoordinates[i].x-particles[i].getX())*force,
								  (altCoordinates[i].y-particles[i].getY())*force);
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
	
	public void	columnExchange()
	{
		// get two random tiles		
		int randomCol1 = (int)parent.random(0,sketchDims.x-1);
		int randomCol2 = (int)parent.random(0,sketchDims.x-1);
		while (randomCol1 == randomCol2)
		{
			randomCol2 = (int)parent.random(0,sketchDims.x-1);
		}
		// Swap the elements
		for (int j =0;j<sketchDims.y;j++){
		altCoordinates[(int) (randomCol1 + sketchDims.x*j)] = altCoordinates[(int) (randomCol2 + sketchDims.x*j)];
		altCoordinates[(int) (randomCol2 + sketchDims.x*j)] = altCoordinates[(int) (randomCol1 + sketchDims.x*j)];
		}
	}
		
	public void	particleExchange(int horizontal,int vertical)

	{		
		// get two random tiles		
		int randomTile1 = (int)parent.random(0,particles.length-1);
		int randomTile2 = (int)parent.random(0,particles.length-1);
		while (randomTile1 == randomTile2)
		{
			randomTile2 = (int)parent.random(0,sketchDims.x-1);
		}

		//swap
		altCoordinates[randomTile1] = altCoordinates[randomTile2];
		altCoordinates[randomTile2] = altCoordinates[randomTile1];
	
		
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
}; // END
	


