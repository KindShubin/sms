package Rotation;

public class Port {

    private int zone;
    private int gatePrefix;

    Port(int zone, int gatePrefix){
        this.zone=zone;
        this.gatePrefix=gatePrefix;
    }

    public int getZone(){
        return this.zone;
    }

    public int getGatePrefix(){
        return this.gatePrefix;
    }

    public void setZone(int zone){
        this.zone=zone;
    }

    public void setGatePrefix(int gatePrefix){
        this.gatePrefix=gatePrefix;
    }

    public int getPort(){
        return Integer.valueOf(String.valueOf(this.gatePrefix).substring(5));
    }


}

