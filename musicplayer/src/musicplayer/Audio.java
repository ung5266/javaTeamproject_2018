package musicplayer;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio
{
	private Clip clip;
	
	public Audio(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
		AudioFormat baseFormat = ais.getFormat();
		AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
				baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

		AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
		clip = AudioSystem.getClip();
		clip.open(dais);
	}
	
	public void play(double volume, int framePos)
	{
		playClip(volume, framePos);
		clip.start();
		
	}
	
	public void stop()
	{
		if(clip.isRunning())
			clip.stop();
	}
	
	public void close()
	{
		stop();
		clip.close();
	}
	
	public void playAfterPause(int frame)
	{
		clip.setFramePosition(frame);
		clip.start();
	}
	
	public int getFramePosition()
	{
		return clip.getFramePosition();
	}
	public int getFrameLength() {
		return clip.getFrameLength();
	}
	public boolean getIsRunning()
	{
		return clip.isRunning();
	}
	
	public void changeVolume(double volume)
	{
		volumeControl(volume);
	}
	
	private void playClip(double volume, int framePos)
	{
		if(clip == null)
			return;
		
		stop();
		volumeControl(volume);
		
		clip.setFramePosition(framePos);
	}
	
	public void volumeControl(double volume)
	{
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		try
		{
			gainControl.setValue((float)volume);
		}
		catch(IllegalArgumentException e)
		{
			//e.printStackTrace();
			if(volume > gainControl.getMaximum())
				volume = gainControl.getMaximum();
			else if(volume < gainControl.getMinimum())
				volume = gainControl.getMinimum();
		}	
	}
	

}

