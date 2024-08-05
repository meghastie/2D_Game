package Entities;

public abstract class Entity {
    protected float x, y; //prote cted - only subclasses can use
    public Entity(float x, float y) {
        this.x = x;
        this.y =y;
    }
}
