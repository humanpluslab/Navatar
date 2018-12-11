package com.navatar.pathplanning;

import com.navatar.maps.particles.ParticleState;
import com.navatar.protobufs.LandmarkProto.Landmark;

/**
 * A single step within the path
 *
 * @author Kevin Glass
 */
public class Step {

    private String direction;
    private ParticleState particlestate;
    private Landmark landmark;
    private boolean followLeft;

    /**
     * Create a new step
     *
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     */

    public Step() {
        this.direction = "";
        this.particlestate = new ParticleState();
    }

    public Step(Landmark inlandmark, ParticleState pState) {
        this.direction = "";
        this.particlestate = pState;
        this.landmark = inlandmark;
    }

    public Landmark getlandmark() {
        return this.landmark;
    }

    public void setlandmark(Landmark inlandmark) {
        this.landmark = inlandmark;
    }

    public String getDirectionString() {
        return this.direction;
    }

    public void setDirectionString(String dirString) {
        this.direction = dirString;
    }

    public ParticleState getParticleState() {
        return this.particlestate;
    }

    /* Getters and setters for particle states */
    public void setParticleState(ParticleState pState) {
        this.particlestate = pState;
    }

    /**
     * Get the y coordinate of the new step
     *
     * @return The y coordinate of the new step
     */

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + this.getlandmark().getLocation().hashCode();
        return hash;
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object other) {
        if (other instanceof Step) {
            Step o = (Step) other;
            return (o.particlestate.getX() == this.particlestate.getX())
                    && (o.particlestate.getY() == this.particlestate.getY());
        }
        return false;
    }

    @Override
    public String toString() {
        return getDirectionString();
    }

    public boolean isFollowLeft() {
        return followLeft;
    }

    public void setFollowLeft(boolean followLeft) {
        this.followLeft = followLeft;
    }
}
