import ddf.minim.*;

public class Audio {
  
  private AudioPlayer player;
  private Minim minim;
  private boolean onSound;

  public Audio(Battlecruiser b, String file) {
    minim = new Minim(b);
    player = minim.loadFile(file, 2048);
    onSound = false;
  }

  private void play() {
    if (player != null) {
      player.play();
      onSound = true;
    }
    else {
      print("no se pudo cargar el audio");
    }
  }
  
  private void stop() {
    if (player != null) {
      minim.stop();
      onSound = false;
    }
    else {
      print("no se pudo cargar el audio");
    }
  }

  private boolean isOnsound(){
    return onSound;
  }
  
}

