package imagetoparticles;

import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.FourierTransform;
import processing.core.PApplet;

// Class with signal analysis tools that keeps track and updates signal attributes in order to allow actions
public class SignalAnalysis {
	
	AudioInput audioIn;
	//AudioPlayer Play;
	PApplet parent;
	FFT fft;
	float bassMax;
	float midMax;
	float trebMax;
	float sigPower;
	float loudestBand;
	
	public SignalAnalysis (PApplet p,AudioInput  tempPlay) 
	{
		parent=p;	
		audioIn=tempPlay;
		bassMax     = 0;
		midMax      = 0;
		trebMax     = 0;
		sigPower    = 0;
		loudestBand = 0;
		fft = new FFT(audioIn.bufferSize(),audioIn.sampleRate());
		fft.window(FourierTransform.HAMMING);
	}
	public void analyze(String averagesType,int numberOfAverages)
	  {
		
		
		if (averagesType.equals("LINEAR"))
		{
	    fft.linAverages(numberOfAverages);
		}
		
		if (averagesType.equals("LOG"))
		{
		    fft.logAverages(10,2);
		    
		}
		 fft.forward(audioIn.left);
	  }

	public void bands(int bass,int mid){
		if (bass>fft.avgSize()){
			parent.println("Not enough Bands");
			return;
		}
		
		else if (bass+mid>fft.avgSize()){
			parent.println("Not enough Bands");
			return;
		}
		else{
			float bassTemp = 0;
			float addBass = 0;
	    	for (int i=0; i<bass;i++){ // three channels for bass ?
	    		addBass +=fft.getAvg(i); 
				if ( fft.getAvg(i)> bassMax)
					bassTemp = fft.getAvg(i);
	    	}
	    	bassMax=bassTemp;
	    	
	    	float addMid = 0;
			float midTemp = 0;
	    	for (int i=bass; i<(mid+bass);i++){ // three channels for bass ?
	    		addMid +=fft.getAvg(i);
				if ( fft.getAvg(i)> midMax)
					midTemp = fft.getAvg(i);
	    	}
	    	midMax = midTemp;
	    	
	    	float addTreb = 0;
			float trebTemp = 0;	
	    	for (int i=mid+bass; i<fft.avgSize();i++){ // three channels for bass ?
	    		addTreb +=fft.getAvg(i);
				if ( fft.getAvg(i)> trebMax)
					trebTemp = fft.getAvg(i);
			}
	    	trebMax = trebTemp;
		}	
		
	}
	
}
