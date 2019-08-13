package Rotation;

public class Sim {

    private int simId;
    private String corp;

    Sim(int simId, String corp){
        this.simId=simId;
        this.corp=corp;
    }

    public int getSimId() {
        return simId;
    }

    public String getCorp() {
        return corp;
    }

    public int getSimbank() {
        return Integer.valueOf(String.valueOf(this.simId).substring(0,3));
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    public void setCorp(String corp) {
        this.corp = corp;
    }



}
