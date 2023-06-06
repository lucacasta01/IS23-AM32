package it.polimi.myShelfie.utilities.pojo;

import java.util.ArrayList;
import java.util.List;

public class Ports {
    private List<Integer> ports;
    public Ports(){
        this.ports = new ArrayList<>();
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }
}

