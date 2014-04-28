package imagetoparticles;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
//This class Involves two input images and methods to mix them around.
public class TwoImageMixer {
	PApplet parent;
	PImage[] inputImages;
	PImage outImage;
	PGraphics outGraphic; // Some images are better made using graphic
	
	public TwoImageMixer(PApplet p,PImage[] tempInputImages)
	{
		parent   = p;
		inputImages = new PImage[tempInputImages.length];
		inputImages = tempInputImages;
	}

	public void verticalStripes(int widthOne,int widthTwo )
	// Creates an image of interchanging  vertical stripes of widths widthOne and widthTwo
	// from the two input images.
	{
		int sections;
		int index;
		
		outImage = inputImages[1].get();
		sections =inputImages[1].width/(widthOne+widthTwo); 
		for (int i =0;i<sections;i++)
		{
			index = (widthOne+widthTwo)*i;
		    outImage.copy(inputImages[2], index+widthOne, 0, widthTwo, outImage.height,  
		    		      index+widthOne, 0, widthTwo, outImage.height);
		}
		
	}
	
	public void horizontalStripes(int widthOne,int widthTwo )
	// Creates an image of interchanging  vertical stripes of widths widthOne and widthTwo
	// from the two input images.
	{
		int sections;
		int index;
		
		outImage = inputImages[1].get();
		sections =inputImages[1].width/(widthOne+widthTwo); 
		for (int i =0;i<sections;i++)
		{
			index = (widthOne+widthTwo)*i;
		    outImage.copy(inputImages[2], 0, index+widthOne, outImage.width, widthTwo, 
		    		        0,index+widthOne,outImage.width,widthTwo);
		}
	}
	
	public void imageRoll_right(PImage imageToRoll,int rollWidth )

	// Makes the image roll forwards or backwards by taking vertical stripes and moving them the the opposite
	// position in the image array. It supports the scenario where the image the image to be rolled is the one modified
	{    
		outGraphic = parent.createGraphics(imageToRoll.width,imageToRoll.height); // Better to write o graphic for some reason.
	    outGraphic.image(imageToRoll.get(imageToRoll.width-rollWidth,0,rollWidth,imageToRoll.height),0,0); 
	    outGraphic.image(imageToRoll.get(0,0,imageToRoll.width-rollWidth,imageToRoll.height),rollWidth,0);
	    outImage = outGraphic.get();
    }
	
	public void imageRoll_left(PImage imageToRoll,int rollWidth )

	// Makes the image roll forwards or backwards by taking vertical stripes and moving them the the opposite
	// position in the image array. It supports the scenario where the image the image to be rolled is the one modified
	{    
		outGraphic = parent.createGraphics(imageToRoll.width,imageToRoll.height); // Better to write o graphic for some reason.
	    outGraphic.image(imageToRoll.get(imageToRoll.width-rollWidth,0,rollWidth,imageToRoll.height),0,0); 
	    outGraphic.image(imageToRoll.get(0,0,imageToRoll.width-rollWidth,imageToRoll.height),rollWidth,0);
	    outImage = outGraphic.get();
    }

	public void imageRoll_up(PImage imageToRoll,int rollWidth ){
		
		outGraphic = parent.createGraphics(imageToRoll.width,imageToRoll.height); // Better to write o graphic for some reason.
	    outGraphic.image(imageToRoll.get(0,0,imageToRoll.width,rollWidth),0,imageToRoll.height-rollWidth); 
	    outGraphic.image(imageToRoll.get(0,rollWidth,imageToRoll.width,imageToRoll.height-rollWidth),0,0);
	    outImage = outGraphic.get();
		
		
	}
	
	public void imageRoll_down(PImage imageToRoll,int rollWidth ){
		
		outGraphic = parent.createGraphics(imageToRoll.width,imageToRoll.height); // Better to write o graphic for some reason.
	    outGraphic.image(imageToRoll.get(0,imageToRoll.height-rollWidth,imageToRoll.width,rollWidth),0,0); 
	    outGraphic.image(imageToRoll.get(0,0,imageToRoll.width,imageToRoll.height-rollWidth),0,rollWidth);
	    outImage = outGraphic.get();
		
		
	}

	public void fade(int fade_index,int imageIndex){
		PImage mask;
		mask = parent.createImage(inputImages[imageIndex].width,inputImages[imageIndex].height,parent.ALPHA);

		mask.loadPixels();
		
		for (int i=0;i<(mask.width*mask.height);i++){ //assuming mask1 and mask 2 are the same size
			mask.pixels[i]=fade_index;
		}
		inputImages[imageIndex+1].mask(mask);
		parent.image(inputImages[imageIndex],0,0);
		parent.image(inputImages[imageIndex+1],0,0);
		
	}
		
	public void tex_zoom(int zoomAmount,int zoomRate,int frameCount,String TYPE){
		float sinusoid;
		if (TYPE.equals("POS"))
	    	sinusoid =PApplet.abs(zoomAmount*PApplet.sin(frameCount*PApplet.PI/zoomRate));
		else if (TYPE.equals("NEG"))
	    	sinusoid =-PApplet.abs(zoomAmount*PApplet.sin(frameCount*PApplet.PI/zoomRate));
		else
	    	sinusoid =zoomAmount*PApplet.sin(frameCount*PApplet.PI/zoomRate);
		parent.noStroke();
		parent.beginShape();	
		parent.texture(outImage);
		parent.vertex(0,0,sinusoid,sinusoid); // up left
		parent.vertex(0,outImage.height,0+sinusoid,outImage.height-sinusoid);// down left
		parent.vertex(outImage.width,outImage.height,outImage.width-sinusoid,outImage.height-sinusoid);// down right
		parent.vertex(outImage.width,0,outImage.width-sinusoid,0+sinusoid); // up right
		parent.endShape();
	}

}





