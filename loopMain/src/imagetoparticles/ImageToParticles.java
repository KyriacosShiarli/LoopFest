package imagetoparticles;

import imagetoparticles.TwoImageMixer;

import java.awt.Color;
import java.util.ArrayList;

import oscP5.OscMessage;
import oscP5.OscP5;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import fisica.util.nonconvex.*;
import fisica.*;
import processing.core.*;



public class ImageToParticles{
PApplet parent;
FWorld world;
FCircle circle;
PImage[] backGroundImages;

TwoImageMixer mixerOne;
PImage objects;
BoxMover boxMover;
CircleMover circleMover;
ParticleMover triangleMover;
//AudioPlayer audioPlay;
public AudioInput audioIn;
Minim minim;
SignalAnalysis analysis;
OscP5 oscP5;
int maxParticles;

// user defined params
int numOfDrawableOne;
int numOfDrawableTwo;
int attForce;
int zoom;
int imageOpacity;
int chooseImage;
float multiply;
int circleSize;
String chooseObject;
int xyInput;
int objectColour;
float theta;
	public ImageToParticles(PApplet p, Minim tempMinim,AudioInput tempAudioIn){
		parent = p;
		minim = tempMinim;
		audioIn = tempAudioIn;
	}
	
	public void setup() {
		
		parent.background(0);
		// MUSIC stuff--------------
		analysis = new SignalAnalysis(parent, audioIn);
		//------------------------------------
		// IMAGE STUFF
		String imag= "Image";
		String ending=".jpg";
		backGroundImages= new PImage[6];
		for (int i = 0;i<backGroundImages.length/2;i++){
			String name = imag+i+ending;
			backGroundImages[2*i] = parent.loadImage(name);
			backGroundImages[2*i].resize(parent.width,parent.height);
			backGroundImages[2*i].filter(parent.BLUR,1);
			//backGroundImages[2*i].filter(GRAY);
			backGroundImages[2*i+1] = parent.loadImage(name);
			backGroundImages[2*i+1].filter(parent.BLUR,50);
			backGroundImages[2*i+1].filter(parent.GRAY);
			backGroundImages[2*i+1].resize(parent.width,parent.height);
		}
		mixerOne = new TwoImageMixer(parent,backGroundImages);
		mixerOne.outImage=mixerOne.inputImages[chooseImage];
		//------------------------------------------------
		//PHYSICS STUFF-------------------
		Fisica.init(parent);
		world = new FWorld();
		world.setEdges();
		
		numOfDrawableOne=20;
		numOfDrawableTwo=40;
		attForce=200;
		chooseObject="CIRCLES";
		xyInput=0;
		chooseImage=0;
		maxParticles=50;
		multiply =5;
		imageOpacity=140;
		circleSize=20;
		boxMover = new BoxMover(parent, world,60,maxParticles+1);// Construct object
		boxMover.setNumDrawable(numOfDrawableOne);
		boxMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
		circleMover = new CircleMover(parent, world,60,maxParticles+1);// Construct object
		circleMover.setNumDrawable(numOfDrawableOne);
		circleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
		triangleMover = new ParticleMover(parent, world,65,maxParticles*3+1);// Construct object
		triangleMover.setNumDrawable(0);
		triangleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
		world.setGravity(0,0);
		world.setEdges(-9000,-9000,5*parent.width,5*parent.height);
	}

	
	public void draw() {
		parent.background(0);	
		//Images
		mixerOne.fade((int)(255*Math.abs(PApplet.sin((float)(Math.PI*parent.frameCount/900)))),chooseImage);
		parent.tint(255,imageOpacity); 
		if (zoom!=0)
			mixerOne.tex_zoom(zoom,900,parent.frameCount,"PO");
		//Signal
		analysis.analyze("LOG",22);
		analysis.bands(5, 5);
		
		map();
		if (chooseObject.equals("TRIANGLES"))
			triangleMover.attraction(attForce);
		if (xyInput>numOfDrawableTwo-1){xyInput=0;}
		else if(chooseObject.equals("CIRCLES")){
			boxMover.attraction(attForce);
			circleMover.attraction(attForce);
			if(parent.frameCount%1==0){
				boxMover.sizeOnPossition();
				circleMover.sizeOnPossition();
			}
			if (xyInput>numOfDrawableOne-1){xyInput=0;}
		}
		world.step();
		world.draw();
		//----------------------------------
	}
		
    public void map()
    {		
    	switch(chooseObject){
    		case "CIRCLES":
	    	//MAP BASS
    			if (analysis.bassMax>25){
    				boxMover.setForce_radial(analysis.bassMax*35*multiply);
    				circleMover.setForce_radial(analysis.bassMax*35*multiply);
    			}
    			if (analysis.midMax>10){
    				boxMover.setForce_horizontal(analysis.midMax*40*multiply);
    				//circleMover.setForce_vertical(analysis.midMax*30*multiply);
	    		
    			}
    			if (analysis.trebMax>7){
    				//boxMover.setForce_horizontal(analysis.trebMax*30*multiply);
    				boxMover.swap_cw();
    				circleMover.swap_cw();
    			}
	    	break;
    		case "TRIANGLES":
    			if (analysis.bassMax>25){
    				triangleMover.setForce_radial(analysis.bassMax*45*multiply);
    			}
    			if (analysis.midMax>10){
    				triangleMover.setForce_horizontal(analysis.midMax*45*multiply);
    			}
    			if (analysis.trebMax>4){
    				triangleMover.swap_cw();
    			}
    			break;
	    
    	}
    }
    
    public void reset(){
	   numOfDrawableOne=20;
	   circleMover.setNumDrawable(numOfDrawableOne);
	   boxMover.setNumDrawable(numOfDrawableOne);
	   numOfDrawableTwo=20;
	   triangleMover.setNumDrawable(0);
	   chooseObject="CIRCLES";
	   attForce=400;
	   zoom=0;
	   imageOpacity=120;
	   chooseImage=0;
	   multiply=5;
	   circleSize=100;
	   objectColour=0;
	   circleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
	   boxMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
	   triangleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,100);
    }
    
    public void oscEvent(OscMessage theOscMessage){
    	float[] fader=new float[10];
		String address = theOscMessage.addrPattern();
		
		if(address.indexOf("/1/push")!=-1){//only one pushbutton
			//resetting
			reset();

		}
		
		if(chooseObject.equals("CIRCLES")){
		
			if(address.indexOf("/1/fader")!=-1){
				String list[]  = PApplet.split(address,'/');
				int xFader = Math.round(list[2].charAt(5) - 0x30);
				fader[xFader] = theOscMessage.get(0).floatValue();
				if (xFader==1){
					if (fader[xFader]>0){
						numOfDrawableOne =(int)PApplet.map(fader[xFader],0,1,3,maxParticles);
						boxMover.setNumDrawable(numOfDrawableOne);
						circleMover.setNumDrawable(numOfDrawableOne);
						boxMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
						circleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
					}
				}
				if (xFader==2){attForce =(int)PApplet.map(fader[xFader],0,1,30,200);}
				if (xFader==3){
					objectColour =(int)PApplet.map(fader[xFader],0,1,0,255);boxMover.changeColour(objectColour);
				}
				if (xFader==4){circleSize =(int)PApplet.map(fader[xFader],0,1,30,150);
					boxMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
					circleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
				}
				if (xFader==5){multiply =(int)PApplet.map(fader[xFader],0,1,0,15);}
			}
			
			if(address.indexOf("/1/rotary")!=-1){
				
				String list[]  = PApplet.split(address,'/');
				int xRotary = Math.round(list[2].charAt(6) - 0x30);
				fader[xRotary] = theOscMessage.get(0).floatValue();
				if (xRotary==2){imageOpacity =(int)PApplet.map(fader[xRotary],0,1,0,255);}
				if (xRotary==1){zoom =(int)PApplet.map(fader[xRotary],0,1,0,2000);}
			}
			if(address.indexOf("/1/toggle")!=-1){
				String list[]  = PApplet.split(address,'/');
				int toggle = Math.round(list[2].charAt(6) - 0x30);
				if (toggle==1){boxMover.swap_random(7);}
				if (toggle==2){
					chooseImage+=2;
					if (chooseImage>backGroundImages.length-1)
						chooseImage=0;
					mixerOne.outImage = mixerOne.inputImages[chooseImage];
				}
				if (toggle==3){
					chooseObject="TRIANGLES";
					boxMover.setNumDrawable(0);
					circleMover.setNumDrawable(0);
					triangleMover.setNumDrawable(numOfDrawableTwo);
				}
			}
			
			if (address.indexOf("/1/xy")!=-1){
				if (xyInput>numOfDrawableOne-1){xyInput=0;theta=0;}
				else{
					int xVal = (int) PApplet.map(theOscMessage.get(1).floatValue(),0,1,0,parent.width);
					int yVal = (int) PApplet.map(1-theOscMessage.get(0).floatValue(),0,1,0,parent.height);
					boxMover.altCoordinates[xyInput].x = xVal;
					boxMover.altCoordinates[xyInput].y = yVal;
					circleMover.altCoordinates[xyInput].x = xVal;
					circleMover.altCoordinates[xyInput].y = yVal;
					xyInput+=1;
				}
				
			}
		}
		else if(chooseObject.equals("TRIANGLES")){

			if(address.indexOf("/1/fader")!=-1){
				String list[]  = PApplet.split(address,'/');
				int xFader = Math.round(list[2].charAt(5) - 0x30);
				fader[xFader] = theOscMessage.get(0).floatValue();
				if (xFader==1){
					numOfDrawableTwo =(int)PApplet.map(fader[xFader],0,1,3,maxParticles*3);
					triangleMover.setNumDrawable(numOfDrawableTwo);
					triangleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
					
				}
				if (xFader==2){attForce =(int)PApplet.map(fader[xFader],0,1,30,200);}
				if (xFader==3){
					objectColour =(int)PApplet.map(fader[xFader],0,1,0,255);triangleMover.changeColour(objectColour);
				}
				if (xFader==4){circleSize =(int)PApplet.map(fader[xFader],0,1,30,150);
					triangleMover.formCircle(mixerOne.outImage.width/2,mixerOne.outImage.height/2,circleSize);
				}
				if (xFader==5){multiply =(int)PApplet.map(fader[xFader],0,1,0,15);}
			}
			
			if(address.indexOf("/1/rotary")!=-1){
				int tempZoom=0;
				String list[]  = PApplet.split(address,'/');
				int xRotary = Math.round(list[2].charAt(6) - 0x30);
				fader[xRotary] = theOscMessage.get(0).floatValue();
				if (xRotary==2){imageOpacity =(int)PApplet.map(fader[xRotary],0,1,0,255);}
				if (xRotary==1){zoom =(int)PApplet.map(fader[xRotary],0,1,0,2000);}
			}
			if(address.indexOf("/1/toggle")!=-1){
				String list[]  = PApplet.split(address,'/');
				int toggle = Math.round(list[2].charAt(6) - 0x30);
				if (toggle==1){triangleMover.swap_random(7);}
				if (toggle==2){
					chooseImage+=2;
					if (chooseImage>backGroundImages.length-1)
						chooseImage=0;
					mixerOne.outImage = mixerOne.inputImages[chooseImage];
				}
				if (toggle==3){
					chooseObject="CIRCLES";
					boxMover.setNumDrawable(numOfDrawableOne);
					circleMover.setNumDrawable(numOfDrawableOne);
					triangleMover.setNumDrawable(0);
				}
			}
			
			if (address.indexOf("/1/xy")!=-1){
				if (xyInput>numOfDrawableTwo-1){xyInput=0;theta=0;}
				else{
					int xVal = (int) PApplet.map(theOscMessage.get(1).floatValue(),0,1,0,parent.width);
					int yVal = (int) PApplet.map(1-theOscMessage.get(0).floatValue(),0,1,0,parent.height);
					triangleMover.altCoordinates[xyInput].x = xVal;
					triangleMover.altCoordinates[xyInput].y = yVal;
					xyInput+=1;
				}
			}
		}	
	}
}
    

	