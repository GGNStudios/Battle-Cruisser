
public class PowerUp{
  
  private short type;
  private int life;
  
  /**
  *Esta función está pensada para ser el constructor para los meteoritos
  */
  public PowerUp(){
    type = (short)random(1,2);
    life = 10000;
  }
  
  /*
  *Esta función está pensada para ser el constructor del Player
  *
  */
  public PowerUp(short type){
    this.type = type;
    life = 0;
  }
  
}
