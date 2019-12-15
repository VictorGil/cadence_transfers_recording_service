package net.devaction.cadence.transfersrecordingservice.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class WorkersConfigValues {

    @JsonProperty("cadence_domain")
    private String cadenceDomain;

    @JsonProperty("cadence_workers")
    private int cadenceWorkers;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " ["
                + "cadenceDomain=" + cadenceDomain
                + ", cadenceWorkers=" + cadenceWorkers
                + "]";
    }

    public String getCadenceDomain() {
        return cadenceDomain;
    }

    public void setCadenceDomain(String cadenceDomain) {
        this.cadenceDomain = cadenceDomain;
    }

    public int getCadenceWorkers() {
        return cadenceWorkers;
    }

    public void setCadenceWorkers(int cadenceWorkers) {
        this.cadenceWorkers = cadenceWorkers;
    }
}
