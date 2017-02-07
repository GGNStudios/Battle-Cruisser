import saito.objloader.*;

public class Shot{
  
  private PVector pos;
  private PVector dir;
  private int type;
  private boolean isTargeting;
  private Enemy e;
  
  public Shot(PVector position, int t){
    pos = position;
    pos.z = -1400;
    type = t;
    if(type == 1){
      dir = new PVector(0,0,-1);
    }
  }
  
  private void update(){
    if(isTargeting){
      PVector ePos = e.getPosition();
      
    }else{
      if(type == 1){
        pos.z += dir.z*100;
        //print(pos.z + "\n");
      }
    }
  }
  
  private void render(){
    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    scale(0.8);
    shot1.draw();
    popMatrix();
  }
  
  private float getZPosition(){
    return (int)pos.z;
  }
  
  private PVector getPosition(){
    return pos;
  }
  
}
