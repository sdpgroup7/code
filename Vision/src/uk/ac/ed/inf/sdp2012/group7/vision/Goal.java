public class Goal implements Unmoveable{

    private ObjectPosition position;

    public Goal(Point topLeft,Point topright, Point bottomLeft, Point bottomRight){
        position = new ObjectPosition(topLeft, topRight, bottomLeft, bottomRight);
    }

    public Goal(){
        position = new ObjectPosition();
    }

    public ObjectPosition getPosition(){
        return this.position;
    }

}
