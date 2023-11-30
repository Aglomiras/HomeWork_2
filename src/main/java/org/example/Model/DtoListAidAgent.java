package org.example.Model;

import lombok.Data;

@Data
public class DtoListAidAgent {
    private String nameAgent;
    private boolean guid;
    private long timeStamp;

    public DtoListAidAgent(String name, boolean guid, long timeStamp) {
        this.nameAgent = name;
        this.guid = guid;
        this.timeStamp = timeStamp;
    }
}
